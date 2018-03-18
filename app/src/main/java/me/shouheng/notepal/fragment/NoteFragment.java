package me.shouheng.notepal.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.balysv.materialmenu.MaterialMenuDrawable;

import org.polaric.colorful.BaseActivity;
import org.polaric.colorful.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.ContentActivity;
import me.shouheng.notepal.async.AttachmentTask;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.databinding.FragmentNoteBinding;
import me.shouheng.notepal.dialog.AttachmentPickerDialog;
import me.shouheng.notepal.dialog.LinkInputDialog;
import me.shouheng.notepal.dialog.TableInputDialog;
import me.shouheng.notepal.dialog.picker.NotebookPickerDialog;
import me.shouheng.notepal.fragment.base.BaseModelFragment;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.model.Category;
import me.shouheng.notepal.model.Location;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.model.enums.ModelType;
import me.shouheng.notepal.util.AttachmentHelper;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.util.FileHelper;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.ModelHelper;
import me.shouheng.notepal.util.StringUtils;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.viewmodel.AttachmentViewModel;
import me.shouheng.notepal.viewmodel.BaseViewModel;
import me.shouheng.notepal.viewmodel.CategoryViewModel;
import me.shouheng.notepal.viewmodel.LocationViewModel;
import me.shouheng.notepal.viewmodel.NoteViewModel;
import me.shouheng.notepal.viewmodel.NotebookViewModel;
import me.shouheng.notepal.widget.FlowLayout;
import my.shouheng.palmmarkdown.tools.MarkdownEffect;

/**
 * Created by wangshouheng on 2017/5/12.*/
public class NoteFragment extends BaseModelFragment<Note, FragmentNoteBinding> {

    private final static String EXTRA_IS_THIRD_PART = "extra_is_third_part";
    private final static String EXTRA_ACTION = "extra_action";
    private final static String TAB_REPLACEMENT = "    ";

    private MaterialMenuDrawable materialMenu;

    private Note note;
    private List<Category> selections;

    private NoteViewModel noteViewModel;
    private AttachmentViewModel attachmentViewModel;
    private LocationViewModel locationViewModel;
    private CategoryViewModel categoryViewModel;
    private NotebookViewModel notebookViewModel;

    public static NoteFragment newInstance(Note note, Integer requestCode, boolean isThirdPart, String action) {
        Bundle arg = new Bundle();
        arg.putBoolean(EXTRA_IS_THIRD_PART, isThirdPart);
        if (note == null) throw new IllegalArgumentException("Note cannot be null");
        arg.putSerializable(Constants.EXTRA_MODEL, note);
        if (requestCode != null) arg.putInt(Constants.EXTRA_REQUEST_CODE, requestCode);
        if (action != null) arg.putString(EXTRA_ACTION, action);
        NoteFragment fragment = new NoteFragment();
        fragment.setArguments(arg);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_note;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        initViewModels();

        handleArguments();

        configToolbar();

        // Notify that the content is changed if the note fragment is called from sharing and other third part
        // The code must be here since the material menu might be null.
        if (getArguments() != null && getArguments().getBoolean(EXTRA_IS_THIRD_PART)) {
            setContentChanged();
        }

        // Sync methods. Note that the other data may not be fetched for current.
        configMain(note);

        configDrawer(note);
    }

    private void initViewModels() {
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel.class);
        attachmentViewModel = ViewModelProviders.of(this).get(AttachmentViewModel.class);
        categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);
        notebookViewModel = ViewModelProviders.of(this).get(NotebookViewModel.class);
    }

    // region handle arguments
    private void handleArguments() {
        Bundle arguments = getArguments();

        // Check arguments
        if (arguments == null
                || !arguments.containsKey(Constants.EXTRA_MODEL)
                || (note = (Note) arguments.getSerializable(Constants.EXTRA_MODEL)) == null) {
            ToastUtils.makeToast(R.string.text_no_such_note);
            if (getActivity() != null) getActivity().finish();
            return;
        }

        // Handle arguments for intent from third part
        if (arguments.getBoolean(EXTRA_IS_THIRD_PART)) {
            handleThirdPart();
        } else if(Constants.ACTION_ADD_SKETCH.equals(arguments.getString(EXTRA_ACTION))) {
            if (getActivity() != null) {
                PermissionUtils.checkStoragePermission((BaseActivity) getActivity(), () -> AttachmentHelper.sketch(this));
            }
        } else if (Constants.ACTION_TAKE_PHOTO.equals(arguments.getString(EXTRA_ACTION))) {
            if (getActivity() != null) {
                PermissionUtils.checkStoragePermission((BaseActivity) getActivity(), () -> AttachmentHelper.capture(this));
            }
        } else if (Constants.ACTION_ADD_FILES.equals(arguments.getString(EXTRA_ACTION))) {
            if (getActivity() != null) {
                PermissionUtils.checkStoragePermission((BaseActivity) getActivity(), () -> AttachmentHelper.pickFiles(this));
            }
        } else {
            // The cases above is new model, don't need to fetch data.
            fetchData(note);
        }
    }

    private void handleThirdPart() {
        if (!(getActivity() instanceof OnNoteInteractListener)) return;

        Intent intent = ((OnNoteInteractListener) getActivity()).getIntentForThirdPart();

        String title = intent.getStringExtra(Intent.EXTRA_SUBJECT);
        note.setTitle(title);

        String content = intent.getStringExtra(Intent.EXTRA_TEXT);
        // todo check and show dialog tips
        if (!TextUtils.isEmpty(content)) content = content.replace("\t", TAB_REPLACEMENT);
        note.setContent(content);

        // Single attachment data
        Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);

        // Due to the fact that Google Now passes intent as text but with
        // audio recording attached the case must be handled in specific way
        if (uri != null && !Constants.INTENT_GOOGLE_NOW.equals(intent.getAction())) {
            String name = FileHelper.getNameFromUri(getContext(), uri);
            new AttachmentTask(this, uri, name, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        // Multiple attachment data
        ArrayList<Uri> uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (uris != null) {
            for (Uri uriSingle : uris) {
                String name = FileHelper.getNameFromUri(getContext(), uriSingle);
                new AttachmentTask(this, uriSingle, name, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }
    // endregion

    private void configToolbar() {
        if (getContext() == null || getActivity() == null) return;

        materialMenu = new MaterialMenuDrawable(getContext(), primaryColor(), MaterialMenuDrawable.Stroke.THIN);
        materialMenu.setIconState(MaterialMenuDrawable.IconState.ARROW);
        getBinding().main.toolbar.setNavigationIcon(materialMenu);
        ((AppCompatActivity) getActivity()).setSupportActionBar(getBinding().main.toolbar);
        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle("");
            setStatusBarColor(getResources().getColor(isDarkTheme() ? R.color.dark_theme_foreground : R.color.md_grey_500));
        }
    }

    // region fetch data
    private void fetchData(Note note) {
        fetchNoteContent(note);

        fetchCategories(note);

        fetchLocation(note);

        fetchNotebook(note);
    }

    private void fetchNoteContent(Note note) {
        attachmentViewModel.readNoteContent(note).observe(this, contentResource -> {
            if (contentResource == null) {
                ToastUtils.makeToast(R.string.text_failed_to_load_data);
                return;
            }
            switch (contentResource.status) {
                case SUCCESS:
                    note.setContent(contentResource.data);
                    getBinding().main.etContent.setTag(true);
                    getBinding().main.etContent.setText(note.getContent());
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.note_failed_to_read_file);
                    break;
            }
        });
    }

    private void fetchCategories(Note note) {
        categoryViewModel.getCategories(note).observe(this, listResource -> {
            if (listResource == null) {
                ToastUtils.makeToast(R.string.text_failed_to_load_data);
                return;
            }
            switch (listResource.status) {
                case SUCCESS:
                    selections = listResource.data;
                    addTagsToLayout(CategoryViewModel.getTagsName(listResource.data));
                    break;
            }
        });
    }

    private void fetchLocation(Note note) {
        locationViewModel.getLocation(note).observe(this, locationResource -> {
            if (locationResource == null) {
                ToastUtils.makeToast(R.string.text_failed_to_load_data);
                return;
            }
            switch (locationResource.status) {
                case SUCCESS:
                    showLocationInfo(locationResource.data);
                    break;
            }
        });
    }

    private void fetchNotebook(Note note) {
        notebookViewModel.get(note.getParentCode()).observe(this, notebookResource -> {
            if (notebookResource == null) {
                ToastUtils.makeToast(R.string.text_failed_to_load_data);
                return;
            }
            switch (notebookResource.status) {
                case SUCCESS:
                    if (notebookResource.data != null) {
                        getBinding().main.tvFolder.setText(notebookResource.data.getTitle());
                        getBinding().main.tvFolder.setTextColor(notebookResource.data.getColor());
                    }
                    break;
            }
        });
    }
    // endregion

    // region Config main board
    private void configMain(Note note) {
        getBinding().main.etTitle.setText(TextUtils.isEmpty(note.getTitle()) ? "" : note.getTitle());
        getBinding().main.etTitle.setTextColor(primaryColor());
        getBinding().main.etTitle.addTextChangedListener(textWatcher);

        getBinding().main.etContent.setText(note.getContent());
        getBinding().main.etContent.addTextChangedListener(textWatcher);

        getBinding().main.llFolder.setOnClickListener(v -> showNotebookPicker());

        getBinding().main.rlBottomEditors.setVisibility(View.GONE);

        int[] ids = new int[] {R.id.iv_h1, R.id.iv_h2, R.id.iv_h3, R.id.iv_h4, R.id.iv_h5, R.id.iv_h6,
                R.id.iv_bold, R.id.iv_italic, R.id.iv_stroke, R.id.iv_format_list, R.id.iv_number_list,
                R.id.iv_line, R.id.iv_code, R.id.iv_xml, R.id.iv_quote,
                R.id.iv_insert_picture, R.id.iv_insert_link, R.id.iv_insert_table,
                R.id.iv_redo, R.id.iv_undo};
        for (int id : ids) getRoot().findViewById(id).setOnClickListener(this::onFormatClick);

        getBinding().main.ivEnableFormat.setOnClickListener(v -> switchFormat());

        getBinding().main.fssv.getDelegate().setThumbSize(8, 32);
        getBinding().main.fssv.getDelegate().setThumbDynamicHeight(false);
        if (getContext() != null) {
            getBinding().main.fssv.getDelegate().setThumbDrawable(
                    ContextCompat.getDrawable(getContext(), R.drawable.recyclerview_fastscroller_handle));
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            // Ignore the text change if the tag is true
            if (getBinding().main.etContent.getTag() != null ||
                    (getBinding().main.etContent.getTag() instanceof Boolean
                            && ((boolean) getBinding().main.etContent.getTag()))) {
                getBinding().main.etContent.setTag(null);
            } else {
                setContentChanged();
                updateCharsInfo();
            }
        }
    };

    private void onFormatClick(View v) {
        MarkdownEffect effect = null;
        switch (v.getId()){
            case R.id.iv_undo:getBinding().main.etContent.undo();break;
            case R.id.iv_redo:getBinding().main.etContent.redo();break;
            case R.id.iv_h1:effect = MarkdownEffect.H1;break;
            case R.id.iv_h2:effect = MarkdownEffect.H2;break;
            case R.id.iv_h3:effect = MarkdownEffect.H3;break;
            case R.id.iv_h4:effect = MarkdownEffect.H4;break;
            case R.id.iv_h5:effect = MarkdownEffect.H5;break;
            case R.id.iv_h6:effect = MarkdownEffect.H6;break;
            case R.id.iv_format_list:effect = MarkdownEffect.NORMAL_LIST;break;
            case R.id.iv_number_list:effect = MarkdownEffect.NUMBER_LIST;break;
            case R.id.iv_quote:effect = MarkdownEffect.QUOTE;break;
            case R.id.iv_italic:effect = MarkdownEffect.ITALIC;break;
            case R.id.iv_bold:effect = MarkdownEffect.BOLD;break;
            case R.id.iv_code:effect = MarkdownEffect.CODE_BLOCK;break;
            case R.id.iv_stroke:effect = MarkdownEffect.STRIKE;break;
            case R.id.iv_line:effect = MarkdownEffect.H_LINE;break;
            case R.id.iv_xml:effect = MarkdownEffect.XML;break;
            case R.id.iv_insert_picture:showAttachmentPicker();break;
            case R.id.iv_insert_link:showLinkEditor();break;
            case R.id.iv_insert_table:showTableEditor();break;
        }
        if (effect != null) getBinding().main.etContent.setEffect(effect);
    }

    private void switchFormat() {
        boolean rlBottomVisible = getBinding().main.rlBottomEditors.getVisibility() == View.VISIBLE;
        getBinding().main.rlBottomEditors.setVisibility(rlBottomVisible ? View.GONE : View.VISIBLE);
        getBinding().main.ivEnableFormat.setImageDrawable(ColorUtils.tintDrawable(
                getResources().getDrawable(R.drawable.ic_text_format_black_24dp),
                rlBottomVisible ? Color.WHITE : primaryColor()));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                getBinding().main.ivEnableFormat.getHeight() * (rlBottomVisible ? 1 : 2));
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        getBinding().main.rlBottom.setLayoutParams(params);
    }

    private void addImageLink() {
        LinkInputDialog.getInstance((title, link) ->
                getBinding().main.etContent.setEffect(MarkdownEffect.IMAGE, title, link)
        ).show(getFragmentManager(), "Link Image");
    }

    private void showTableEditor() {
        TableInputDialog.getInstance((rowsStr, colsStr) -> {
            int rows = StringUtils.parseInteger(rowsStr, 3);
            int cols = StringUtils.parseInteger(colsStr, 3);
            getBinding().main.etContent.setEffect(MarkdownEffect.TABLE, rows, cols);
        }).show(getFragmentManager(), "TABLE INPUT");
    }

    private void showLinkEditor() {
        LinkInputDialog.getInstance((title, link) ->
                getBinding().main.etContent.setEffect(MarkdownEffect.LINK, title, link)
        ).show(getFragmentManager(), "LINK INPUT");
    }

    private void showAttachmentPicker() {
        new AttachmentPickerDialog.Builder(this)
                .setRecordVisible(false)
                .setVideoVisible(false)
                .setAddLinkVisible(true)
                .setFilesVisible(true)
                .setOnAddNetUriSelectedListener(this::addImageLink)
                .build().show(getFragmentManager(), "Attachment picker");
    }

    // endregion

    // region Config drawer board
    private void configDrawer(Note note) {
        getBinding().drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        getBinding().drawer.drawerToolbar.setNavigationOnClickListener(v ->
                getBinding().drawerLayout.closeDrawer(GravityCompat.END));
        if (isDarkTheme()){
            getBinding().drawer.drawerToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            getBinding().drawer.getRoot().setBackgroundColor(getResources().getColor(R.color.dark_theme_background));
        }

        updateCharsInfo();
        getBinding().drawer.tvTimeInfo.setText(ModelHelper.getTimeInfo(note));

        getBinding().drawer.flLabels.setOnClickListener(v -> showCategoriesPicker(selections));
        getBinding().drawer.tvAddLabels.setOnClickListener(v -> showCategoriesPicker(selections));

        getBinding().drawer.tvAddLocation.setOnClickListener(v -> tryToLocate());

        getBinding().drawer.tvCopyLink.setOnClickListener(v -> ModelHelper.copyLink(getActivity(), note));

        getBinding().drawer.tvCopyText.setOnClickListener(v -> {
            note.setContent(getBinding().main.etContent.getText().toString());
            ModelHelper.copyToClipboard(getActivity(), getBinding().main.etContent.getText().toString());
            ToastUtils.makeToast(R.string.content_was_copied_to_clipboard);
        });

        getBinding().drawer.tvAddToHomeScreen.setOnClickListener(v -> addShortcut());

        getBinding().drawer.tvStatistics.setOnClickListener(v -> showStatistics());
    }

    private void updateCharsInfo() {
        String charsInfo = getString(R.string.text_chars_number) + " : " + getBinding().main.etContent.getText().toString().length();
        getBinding().drawer.tvCharsInfo.setText(charsInfo);
    }

    private void showStatistics() {
        note.setContent(getBinding().main.etContent.getText().toString());
        ModelHelper.showStatistic(getContext(), note);
    }

    private void showNotebookPicker() {
        NotebookPickerDialog.newInstance().setOnItemSelectedListener((dialog, value, position) -> {
            note.setParentCode(value.getCode());
            note.setTreePath(value.getTreePath() + "|" + value.getCode());
            getBinding().main.tvFolder.setText(value.getTitle());
            getBinding().main.tvFolder.setTextColor(value.getColor());
            setContentChanged();
            dialog.dismiss();
        }).show(getFragmentManager(), "NOTEBOOK_PICKER");
    }

    /**
     * Show location information, if the location is null, hide the widget else show it.
     *
     * @param location location info */
    private void showLocationInfo(@Nullable Location location){
        if (location == null) {
            getBinding().drawer.tvLocationInfo.setVisibility(View.GONE);
            return;
        }
        getBinding().drawer.tvLocationInfo.setVisibility(View.VISIBLE);
        getBinding().drawer.tvLocationInfo.setText(ModelHelper.getFormatLocation(location));
    }
    // endregion

    @Override
    protected FlowLayout getTagsLayout() {
        return getBinding().drawer.flLabels;
    }

    @Override
    protected void onGetSelectedCategories(List<Category> categories) {
        String tagsName = CategoryViewModel.getTagsName(categories);
        selections = categories;
        note.setTags(CategoryViewModel.getTags(categories));
        note.setTagsName(tagsName);
        addTagsToLayout(tagsName);
        setContentChanged();
    }

    @Override
    protected void onGetLocation(Location location) {
        location.setModelCode(note.getCode());
        location.setModelType(ModelType.NOTE);
        showLocationInfo(location);
        locationViewModel.saveModel(location);
    }

    @Override
    protected void onFailedGetAttachment(Attachment attachment) {
        ToastUtils.makeToast(R.string.failed_to_save_attachment);
    }

    @Override
    protected void onGetAttachment(@NonNull Attachment attachment) {
        attachment.setModelCode(note.getCode());
        attachment.setModelType(ModelType.NOTE);
        attachmentViewModel.saveModel(attachment).observe(this, LogUtils::d);

        String title = FileHelper.getNameFromUri(getContext(), attachment.getUri());
        if (TextUtils.isEmpty(title)) title = getString(R.string.text_attachment);

        if (Constants.MIME_TYPE_IMAGE.equalsIgnoreCase(attachment.getMineType())
                || Constants.MIME_TYPE_SKETCH.equalsIgnoreCase(attachment.getMineType())) {
            getBinding().main.etContent.setEffect(MarkdownEffect.IMAGE, title , attachment.getUri().toString());
        } else {
            getBinding().main.etContent.setEffect(MarkdownEffect.LINK, title, attachment.getUri().toString());
        }
    }

    @Override
    protected void beforeSaveOrUpdate(BeforePersistEventHandler handler) {
        String noteContent = getBinding().main.etContent.getText().toString();
        note.setContent(noteContent);

        // Use the default title or the input title
        String title = getBinding().main.etTitle.getText().toString();
        note.setTitle(TextUtils.isEmpty(title) ? getString(R.string.note_default_name) : title);

        attachmentViewModel.writeNoteContent(note).observe(this, attachmentResource -> {
            if (attachmentResource == null) {
                ToastUtils.makeToast(R.string.text_error_when_save);
                return;
            }
            switch (attachmentResource.status) {
                case SUCCESS:
                    if (attachmentResource.data != null) {
                        note.setContentCode(attachmentResource.data.getCode());
                    }
                    handler.onGetEventResult(true);
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_error_when_save);
                    handler.onGetEventResult(false);
                    break;
            }
        });
    }

    @Override
    protected void afterSaveOrUpdate() {
        super.afterSaveOrUpdate();
        materialMenu.animateIconState(MaterialMenuDrawable.IconState.ARROW);
        String content = getBinding().main.etContent.getText().toString();
        if (TextUtils.isEmpty(content)) content = "  ";
        note.setContent(content);

        Bundle args = getArguments();
        if (args != null && (args.getBoolean(EXTRA_IS_THIRD_PART)
                || Constants.ACTION_ADD_SKETCH.equals(args.getString(EXTRA_ACTION))
                || Constants.ACTION_ADD_FILES.equals(args.getString(EXTRA_ACTION))
                || Constants.ACTION_TAKE_PHOTO.equals(args.getString(EXTRA_ACTION)))) {
            sendNoteChangeBroadcast();
        }
    }

    @Override
    protected BaseViewModel<Note> getViewModel() {
        return noteViewModel;
    }

    @Override
    protected Note getModel() {
        return note;
    }

    @Override
    protected void setContentChanged() {
        if (!isContentChanged()) {
            super.setContentChanged();
            materialMenu.animateIconState(MaterialMenuDrawable.IconState.CHECK);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (isDarkTheme()) {
            menu.findItem(R.id.action_more).setIcon(R.drawable.ic_more_vert_white);
            menu.findItem(R.id.action_preview).setIcon(R.drawable.ic_visibility_white_24dp);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.note_editor_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (isContentChanged()) saveOrUpdateData(null);
                else setResult();
                break;
            case R.id.action_more:
                getBinding().drawerLayout.openDrawer(GravityCompat.END, true);
                break;
            case R.id.action_preview:
                note.setTitle(TextUtils.isEmpty(getBinding().main.etTitle.getText().toString()) ?
                        "" : getBinding().main.etTitle.getText().toString());
                String content = getBinding().main.etContent.getText().toString();
                if (TextUtils.isEmpty(content)) content = "  ";
                note.setContent(content);
                ContentActivity.viewNote(this, note, 0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    private void sendNoteChangeBroadcast() {
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_NOTE_CHANGE_BROADCAST);
        if (getContext() != null) getContext().sendBroadcast(intent);
    }

    public interface OnNoteInteractListener {
        Intent getIntentForThirdPart();
    }
}
