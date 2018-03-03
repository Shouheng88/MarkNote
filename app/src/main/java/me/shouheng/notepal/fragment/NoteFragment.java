package me.shouheng.notepal.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.bumptech.glide.Glide;

import org.apache.commons.io.FileUtils;
import org.polaric.colorful.BaseActivity;
import org.polaric.colorful.PermissionUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.ContentActivity;
import me.shouheng.notepal.async.AttachmentTask;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.databinding.FragmentNoteBinding;
import me.shouheng.notepal.dialog.AttachmentPickerDialog;
import me.shouheng.notepal.dialog.LinkInputDialog;
import me.shouheng.notepal.dialog.NotebookPickerDialog;
import me.shouheng.notepal.dialog.TableInputDialog;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.model.Category;
import me.shouheng.notepal.model.Location;
import me.shouheng.notepal.model.ModelFactory;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.model.enums.ModelType;
import me.shouheng.notepal.provider.AttachmentsStore;
import me.shouheng.notepal.provider.BaseStore;
import me.shouheng.notepal.provider.CategoryStore;
import me.shouheng.notepal.provider.LocationsStore;
import me.shouheng.notepal.provider.NotebookStore;
import me.shouheng.notepal.provider.NotesStore;
import me.shouheng.notepal.util.AttachmentHelper;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.util.FileHelper;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.ModelHelper;
import me.shouheng.notepal.util.PreferencesUtils;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.widget.FlowLayout;
import my.shouheng.palmmarkdown.tools.MarkdownEffect;

/**
 * Created by wangshouheng on 2017/5/12.*/
public class NoteFragment extends BaseModelFragment<Note, FragmentNoteBinding> {

    private MaterialMenuDrawable materialMenu;

    private Note note;
    private Location location;
    private Attachment noteFile, previewImage;

    private File file;
    private File tempFile;
    private Notebook notebook;
    private List<Category> selections;
    private List<Category> allCategories;

    private AttachmentPickerType attachmentPickerType;
    private PreferencesUtils preferencesUtils;

    private final static String EXTRA_IS_THIRD_PART = "extra_is_third_part";
    private final static String EXTRA_ACTION = "extra_action";

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
        preferencesUtils = PreferencesUtils.getInstance(getContext());

        handleArguments();

        configToolbar();

        configMain();

        configDrawer();
    }

    private void handleArguments() {
        Bundle arguments = getArguments();

        if (arguments == null
                || !arguments.containsKey(Constants.EXTRA_MODEL)
                || (note = (Note) arguments.getSerializable(Constants.EXTRA_MODEL)) == null) {
            ToastUtils.makeToast(getContext(), R.string.text_no_such_note);
            if (getActivity() != null) getActivity().finish();
            return;
        }

        noteFile = AttachmentsStore.getInstance(getContext()).get(note.getContentCode());
        previewImage = AttachmentsStore.getInstance(getContext()).get(note.getPreviewCode());
        location = LocationsStore.getInstance(getContext()).getLocation(note);
        notebook = NotebookStore.getInstance(getContext()).get(note.getParentCode());
        selections = CategoryStore.getInstance(getContext()).getCategories(note);

        if (noteFile != null) {
            try {
                file = new File(noteFile.getPath());
                String content = FileUtils.readFileToString(file, "utf-8");
                note.setContent(content);
            } catch (IOException e) {
                ToastUtils.makeToast(getContext(), R.string.note_failed_to_read_file);
            }
        } else {
            tempFile = FileHelper.createNewAttachmentFile(getContext(),
                    "." + preferencesUtils.getNoteFileExtension());
            note.setContent("");
        }

        // handle arguments for intent from third part
        if (arguments.getBoolean(EXTRA_IS_THIRD_PART) && getActivity() instanceof OnNoteInteractListener) {
            Intent intent = ((OnNoteInteractListener) getActivity()).getIntentForThirdPart();

            String title = intent.getStringExtra(Intent.EXTRA_SUBJECT);
            note.setTitle(title);

            String content = intent.getStringExtra(Intent.EXTRA_TEXT);
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
        } else if(Constants.ACTION_ADD_SKETCH.equals(arguments.getString(EXTRA_ACTION))) {
            assert getActivity() != null;
            PermissionUtils.checkStoragePermission((BaseActivity) getActivity(),
                    () -> AttachmentHelper.sketch(this));
        } else if (Constants.ACTION_TAKE_PHOTO.equals(arguments.getString(EXTRA_ACTION))) {
            assert getActivity() != null;
            PermissionUtils.checkStoragePermission((BaseActivity) getActivity(),
                    () -> AttachmentHelper.capture(this));
        } else if (Constants.ACTION_ADD_FILES.equals(arguments.getString(EXTRA_ACTION))) {
            assert getActivity() != null;
            PermissionUtils.checkStoragePermission((BaseActivity) getActivity(),
                    () -> AttachmentHelper.pickFiles(this));
        }
    }

    private void configToolbar() {
        assert getContext() != null;
        assert getActivity() != null;
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

        if (getArguments() != null && getArguments().getBoolean(EXTRA_IS_THIRD_PART)) {
            setContentChanged();
        }
    }

    private void configMain() {
        getBinding().main.etTitle.setText(TextUtils.isEmpty(note.getTitle()) ? "" : note.getTitle());
        getBinding().main.etTitle.setTextColor(primaryColor());
        getBinding().main.etTitle.addTextChangedListener(textWatcher);

        getBinding().main.etContent.setText(note.getContent());
        getBinding().main.etContent.addTextChangedListener(textWatcher);

        if (notebook != null) getBinding().main.tvFolder.setText(notebook.getTitle());
        getBinding().main.llFolder.setOnClickListener(v -> showNotebookPicker());

        getBinding().main.rlBottomEditors.setVisibility(View.GONE);

        int[] ids = new int[] {R.id.iv_h1, R.id.iv_h2, R.id.iv_h3, R.id.iv_h4, R.id.iv_h5, R.id.iv_h6,
                R.id.iv_bold, R.id.iv_italic, R.id.iv_stroke, R.id.iv_format_list, R.id.iv_number_list,
                R.id.iv_line, R.id.iv_code, R.id.iv_xml, R.id.iv_quote,
                R.id.iv_insert_picture, R.id.iv_insert_link, R.id.iv_insert_table,
                R.id.iv_redo, R.id.iv_undo};
        for (int id : ids) getRoot().findViewById(id).setOnClickListener(this::addEffect);

        getBinding().main.ivEnableFormat.setOnClickListener(v -> switchFormat());

        getBinding().main.fssv.getDelegate().setThumbSize(8, 32);
        getBinding().main.fssv.getDelegate().setThumbDynamicHeight(false);
        getBinding().main.fssv.getDelegate().setThumbDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recyclerview_fastscroller_handle));
    }

    private void configDrawer() {
        getBinding().drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        getBinding().drawer.drawerToolbar.setNavigationOnClickListener(v ->
                getBinding().drawerLayout.closeDrawer(GravityCompat.END));
        if (isDarkTheme()){
            getBinding().drawer.drawerToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            getBinding().drawer.getRoot().setBackgroundColor(getResources().getColor(R.color.dark_theme_background));
        }

        updateCharsInfo();
        getBinding().drawer.tvTimeInfo.setText(ModelHelper.getTimeInfo(note));

        getBinding().drawer.flLabels.setOnClickListener(v -> showLabelsPicker());
        getBinding().drawer.tvAddLabels.setOnClickListener(v -> showLabelsPicker());
        addTagsToLayout(CategoryStore.getTagsName(selections));

        getBinding().drawer.tvAddLocation.setOnClickListener(v -> tryToLocate());
        showLocationInfo();

        getBinding().drawer.tvCopyLink.setOnClickListener(v -> ModelHelper.copyLink(getActivity(), note));

        getBinding().drawer.tvCopyText.setOnClickListener(v -> {
            note.setContent(getBinding().main.etContent.getText().toString());
            ModelHelper.copyToClipboard(getActivity(), getBinding().main.etContent.getText().toString());
            ToastUtils.makeToast(getContext(), R.string.content_was_copied_to_clipboard);
        });

        getBinding().drawer.tvAddToHomeScreen.setOnClickListener(v -> addShortcut());

        getBinding().drawer.tvStatistics.setOnClickListener(v -> showStatistics());

        getBinding().drawer.ivAddPreview.setOnClickListener(v -> showAttachmentPicker(AttachmentPickerType.PREVIEW_IMAGE));
        loadPreviewImage();

        getBinding().drawer.tvSettings.setVisibility(View.GONE);
        getBinding().drawer.tvSettings.setOnClickListener(view -> {
            String content = getBinding().main.etContent.getText().toString();
            if (!TextUtils.isEmpty(content)) {
                content = content.replace("\t", "    ");
                getBinding().main.etContent.setText(content);
            }
        });
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            setContentChanged();
            updateCharsInfo();
        }
    };

    private void updateCharsInfo() {
        String charsInfo = getString(R.string.text_chars_number) + " : " + getBinding().main.etContent.getText().toString().length();
        getBinding().drawer.tvCharsInfo.setText(charsInfo);
    }

    private void showStatistics() {
        note.setContent(getBinding().main.etContent.getText().toString());
        ModelHelper.showStatistic(getContext(), note);
    }

    private void showLabelsPicker() {
        if (allCategories == null) {
            allCategories = CategoryStore.getInstance(getContext()).get(null, null);
        }
        showCategoriesPicker(allCategories, selections);
    }

    private void showNotebookPicker() {
        NotebookPickerDialog.newInstance().setOnItemSelectedListener((dialog, notebook1, position) -> {
            note.setParentCode(notebook1.getCode());
            note.setTreePath(notebook1.getTreePath() + "|" + notebook1.getCode());
            getBinding().main.tvFolder.setText(notebook1.getTitle());
            getBinding().main.tvFolder.setTextColor(notebook1.getColor());
            setContentChanged();
            dialog.dismiss();
        }).show(getFragmentManager(), "NOTEBOOK_PICKER");
    }

    @Override
    protected FlowLayout getTagsLayout() {
        return getBinding().drawer.flLabels;
    }

    @Override
    protected void onGetSelectedCategories(List<Category> categories) {
        String tagsName = CategoryStore.getTagsName(categories);
        selections = categories;
        note.setTags(CategoryStore.getTags(categories));
        note.setTagsName(tagsName);
        addTagsToLayout(tagsName);
        setContentChanged();
    }

    @Override
    protected void onGetLocation(Location location) {
        this.location = location;
        location.setModelCode(note.getCode());
        location.setModelType(ModelType.NOTE);
        LocationsStore.getInstance(getContext()).saveModel(location);
        showLocationInfo();
    }

    private void showLocationInfo(){
        if (location == null) return;
        String strLocation = location.getCountry() + "|" + location.getProvince() + "|" + location.getCity() + "|" + location.getDistrict();
        getBinding().drawer.tvLocationInfo.setVisibility(View.VISIBLE);
        getBinding().drawer.tvLocationInfo.setText(strLocation);
    }

    private void addEffect(View v) {
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
            case R.id.iv_insert_picture:showAttachmentPicker(AttachmentPickerType.CONTENT_IMAGE);break;
            case R.id.iv_insert_link:showLinkEditor();break;
            case R.id.iv_insert_table:showTableEditor();break;
        }
        if (effect != null) getBinding().main.etContent.setEffect(effect);
    }

    private void switchFormat() {
        if (getBinding().main.rlBottomEditors.getVisibility() == View.VISIBLE) {
            getBinding().main.rlBottomEditors.setVisibility(View.GONE);
            getBinding().main.ivEnableFormat.setImageDrawable(ColorUtils.tintDrawable(
                    getResources().getDrawable(R.drawable.ic_text_format_black_24dp), Color.WHITE));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, getBinding().main.ivEnableFormat.getHeight());
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            getBinding().main.rlBottom.setLayoutParams(params);
        } else {
            getBinding().main.rlBottomEditors.setVisibility(View.VISIBLE);
            getBinding().main.ivEnableFormat.setImageDrawable(ColorUtils.tintDrawable(
                    getResources().getDrawable(R.drawable.ic_text_format_black_24dp), primaryColor()));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, getBinding().main.ivEnableFormat.getHeight() * 2);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            getBinding().main.rlBottom.setLayoutParams(params);
        }
    }

    private void addImageLink() {
        LinkInputDialog.getInstance((title, link) ->
                getBinding().main.etContent.setEffect(MarkdownEffect.IMAGE, title, link)
        ).show(getFragmentManager(), "Link Image");
    }

    private void showTableEditor() {
        TableInputDialog.getInstance((rowsStr, colsStr) -> {
            int rows, cols;
            try {
                rows = TextUtils.isEmpty(rowsStr) ? 3 : Integer.parseInt(rowsStr);
            } catch (NumberFormatException e) {
                rows = 3;
            }
            try {
                cols = TextUtils.isEmpty(colsStr) ? 3 : Integer.parseInt(colsStr);
            } catch (NumberFormatException e) {
                cols = 3;
            }
            getBinding().main.etContent.setEffect(MarkdownEffect.TABLE, rows, cols);
        }).show(getFragmentManager(), "TABLE INPUT");
    }

    private void showLinkEditor() {
        LinkInputDialog.getInstance((title, link) ->
                getBinding().main.etContent.setEffect(MarkdownEffect.LINK, title, link)
        ).show(getFragmentManager(), "LINK INPUT");
    }

    private void showAttachmentPicker(AttachmentPickerType attachmentPickerType) {
        this.attachmentPickerType = attachmentPickerType;
        new AttachmentPickerDialog.Builder(this)
                .setRecordVisible(false)
                .setVideoVisible(false)
                .setAddLinkVisible(attachmentPickerType != AttachmentPickerType.PREVIEW_IMAGE)
                .setFilesVisible(attachmentPickerType != AttachmentPickerType.PREVIEW_IMAGE)
                .setOnAddNetUriSelectedListener(this::addImageLink)
                .build().show(getFragmentManager(), "Attachment picker");
    }

    @Override
    protected void onFailedGetAttachment(Attachment attachment) {
        ToastUtils.makeToast(getContext(), R.string.failed_to_save_attachment);
    }

    @Override
    protected void onGetAttachment(Attachment attachment) {
        attachment.setModelCode(note.getCode());
        attachment.setModelType(ModelType.NOTE);
        LogUtils.d(attachment);
        // can't get the file
        if (TextUtils.isEmpty(attachment.getUri().toString())) return;
        AttachmentsStore.getInstance(getContext()).saveModel(attachment);

        if (attachmentPickerType == AttachmentPickerType.PREVIEW_IMAGE) {
            previewImage = attachment;
            setContentChanged();
            loadPreviewImage();
        } else {
            if (Constants.MIME_TYPE_IMAGE.equalsIgnoreCase(attachment.getMineType())
                    || Constants.MIME_TYPE_SKETCH.equalsIgnoreCase(attachment.getMineType())) {
                getBinding().main.etContent.setEffect(MarkdownEffect.IMAGE,
                        "image" ,
                        attachment.getUri().toString());
            } else {
                getBinding().main.etContent.setEffect(MarkdownEffect.LINK,
                        getAttachmentTitle(attachment.getPath()),
                        attachment.getUri().toString());
            }
        }
    }

    private String getAttachmentTitle(String path) {
        if (!TextUtils.isEmpty(path) && path.contains("/")) {
            return path.substring(path.lastIndexOf('/') + 1, path.length());
        }
        return getString(R.string.text_attachment);
    }

    private void loadPreviewImage() {
        if (previewImage == null) return;
        note.setPreviewCode(previewImage.getCode());
        Uri thumbnailUri = FileHelper.getThumbnailUri(getContext(), previewImage.getUri());
        Glide.with(PalmApp.getContext())
                .load(thumbnailUri)
                .centerCrop()
                .crossFade()
                .into(getBinding().drawer.ivPreview);
    }

    @Override
    protected boolean checkInputInfo() {
        if (TextUtils.isEmpty(getBinding().main.etTitle.getText().toString())) {
            ToastUtils.makeToast(getContext(), R.string.title_required);
            return false;
        }
        return true;
    }

    @Override
    protected void saveModel() {
        NotesStore.getInstance(getActivity()).saveModel(note);
    }

    @Override
    protected void updateModel() {
        NotesStore.getInstance(getActivity()).update(note);
    }

    @Override
    protected void beforeSaveOrUpdate() {
        if (noteFile == null) {
            try {
                FileUtils.writeStringToFile(tempFile, getBinding().main.etContent.getText().toString(), "utf-8");
            } catch (IOException e) {
                LogUtils.d("onClick: " + e);
            }
            noteFile = ModelFactory.getAttachment(getContext());
            noteFile.setUri(FileHelper.getUriFromFile(getContext(), tempFile));
            noteFile.setSize(FileUtils.sizeOf(tempFile));
            noteFile.setPath(tempFile.getPath());
            noteFile.setName(tempFile.getName());
            noteFile.setLength(noteFile.getLength());
            AttachmentsStore.getInstance(getContext()).saveModel(noteFile);
        } else {
            try {
                FileUtils.writeStringToFile(file, getBinding().main.etContent.getText().toString(), "utf-8", false);
            } catch (IOException e) {
                LogUtils.d("onClick: " + e);
            }
        }

        note.setContentCode(noteFile.getCode());
        note.setTitle(getBinding().main.etTitle.getText().toString());
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
    protected BaseStore<Note> getStoreOfModel() {
        return NotesStore.getInstance(getContext());
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
                if (isContentChanged()) saveOrUpdateData();
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
        getContext().sendBroadcast(intent);
    }

    private enum AttachmentPickerType {
        PREVIEW_IMAGE, CONTENT_IMAGE;
    }

    public interface OnNoteInteractListener {
        Intent getIntentForThirdPart();
    }
}
