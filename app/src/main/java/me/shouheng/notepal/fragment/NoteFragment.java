package me.shouheng.notepal.fragment;

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
import com.bumptech.glide.Glide;

import org.apache.commons.io.FileUtils;
import org.polaric.colorful.BaseActivity;
import org.polaric.colorful.PermissionUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.ContentActivity;
import me.shouheng.notepal.async.AttachmentTask;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.databinding.FragmentNoteBinding;
import me.shouheng.notepal.dialog.AttachmentPickerDialog;
import me.shouheng.notepal.dialog.LinkInputDialog;
import me.shouheng.notepal.dialog.picker.NotebookPickerDialog;
import me.shouheng.notepal.dialog.TableInputDialog;
import me.shouheng.notepal.fragment.base.BaseModelFragment;
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
    private Attachment atNoteFile, atPreviewImage;

    private File noteFile, tempFile;
    private Notebook notebook;
    private List<Category> selections;

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

        // Notify that the content is changed if the note fragment is called from sharing and other third part
        if (getArguments() != null && getArguments().getBoolean(EXTRA_IS_THIRD_PART)) setContentChanged();

        configMain();

        configDrawer();
    }

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

        // Get information from database
        atNoteFile = AttachmentsStore.getInstance(getContext()).get(note.getContentCode());
        atPreviewImage = AttachmentsStore.getInstance(getContext()).get(note.getPreviewCode());
        location = LocationsStore.getInstance(getContext()).getLocation(note);
        notebook = NotebookStore.getInstance(getContext()).get(note.getParentCode());
        selections = CategoryStore.getInstance(getContext()).getCategories(note);

        // Read content from file system
        if (atNoteFile != null) {
            try {
                noteFile = new File(atNoteFile.getPath());
                LogUtils.d(atNoteFile);
                String content = FileUtils.readFileToString(noteFile, "utf-8");
                note.setContent(content);
            } catch (IOException e) {
                ToastUtils.makeToast(R.string.note_failed_to_read_file);
            }
        } else {
            // Create a temporary file
            tempFile = FileHelper.createNewAttachmentFile(getContext(), "." + preferencesUtils.getNoteFileExtension());
            note.setContent("");
        }

        // Handle arguments for intent from third part
        if (arguments.getBoolean(EXTRA_IS_THIRD_PART) && getActivity() instanceof OnNoteInteractListener) {
            Intent intent = ((OnNoteInteractListener) getActivity()).getIntentForThirdPart();

            String title = intent.getStringExtra(Intent.EXTRA_SUBJECT);
            note.setTitle(title);

            String content = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (!TextUtils.isEmpty(content)) {
                content = content.replace("\t", "    ");
            }
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
            if (getActivity() != null) {
                PermissionUtils.checkStoragePermission((BaseActivity) getActivity(), () ->
                        AttachmentHelper.sketch(this));
            }
        } else if (Constants.ACTION_TAKE_PHOTO.equals(arguments.getString(EXTRA_ACTION))) {
            if (getActivity() != null) {
                PermissionUtils.checkStoragePermission((BaseActivity) getActivity(), () ->
                        AttachmentHelper.capture(this));
            }
        } else if (Constants.ACTION_ADD_FILES.equals(arguments.getString(EXTRA_ACTION))) {
            if (getActivity() != null) {
                PermissionUtils.checkStoragePermission((BaseActivity) getActivity(), () ->
                        AttachmentHelper.pickFiles(this));
            }
        }
    }

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

    // region Config main board
    private void configMain() {
        getBinding().main.etTitle.setText(TextUtils.isEmpty(note.getTitle()) ? "" : note.getTitle());
        getBinding().main.etTitle.setTextColor(primaryColor());
        getBinding().main.etTitle.addTextChangedListener(textWatcher);

        getBinding().main.etContent.setText(note.getContent());
        getBinding().main.etContent.addTextChangedListener(textWatcher);

        if (notebook != null) {
            getBinding().main.tvFolder.setText(notebook.getTitle());
            getBinding().main.tvFolder.setTextColor(notebook.getColor());
        }
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
            setContentChanged();
            updateCharsInfo();
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
            case R.id.iv_insert_picture:showAttachmentPicker(AttachmentPickerType.CONTENT_IMAGE);break;
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

    private void loadPreviewImage() {
        if (atPreviewImage == null) return;
        note.setPreviewCode(atPreviewImage.getCode());
        Uri thumbnailUri = FileHelper.getThumbnailUri(getContext(), atPreviewImage.getUri());
        Glide.with(PalmApp.getContext())
                .load(thumbnailUri)
                .centerCrop()
                .crossFade()
                .into(getBinding().drawer.ivPreview);
    }
    // endregion

    // region Config drawer board
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

        getBinding().drawer.flLabels.setOnClickListener(v -> showCategoriesPicker(selections));
        getBinding().drawer.tvAddLabels.setOnClickListener(v -> showCategoriesPicker(selections));
        addTagsToLayout(CategoryStore.getTagsName(selections));

        getBinding().drawer.tvAddLocation.setOnClickListener(v -> tryToLocate());
        showLocationInfo();

        getBinding().drawer.tvCopyLink.setOnClickListener(v -> ModelHelper.copyLink(getActivity(), note));

        getBinding().drawer.tvCopyText.setOnClickListener(v -> {
            note.setContent(getBinding().main.etContent.getText().toString());
            ModelHelper.copyToClipboard(getActivity(), getBinding().main.etContent.getText().toString());
            ToastUtils.makeToast(R.string.content_was_copied_to_clipboard);
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

    private void showLocationInfo(){
        if (location == null) return;
        getBinding().drawer.tvLocationInfo.setVisibility(View.VISIBLE);
        getBinding().drawer.tvLocationInfo.setText(ModelHelper.getFormatedLocation(location));
    }
    // endregion

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

    @Override
    protected void onFailedGetAttachment(Attachment attachment) {
        ToastUtils.makeToast(R.string.failed_to_save_attachment);
    }

    @Override
    protected void onGetAttachment(@NonNull Attachment attachment) {
        attachment.setModelCode(note.getCode());
        attachment.setModelType(ModelType.NOTE);
        AttachmentsStore.getInstance(getContext()).saveModel(attachment);

        if (attachmentPickerType == AttachmentPickerType.PREVIEW_IMAGE) {
            atPreviewImage = attachment;
            setContentChanged();
            loadPreviewImage();
        } else {
            String title;
            title = TextUtils.isEmpty(title = FileHelper.getNameFromUri(getContext(), attachment.getUri())) ?
                    getString(R.string.text_attachment) : title;
            if (Constants.MIME_TYPE_IMAGE.equalsIgnoreCase(attachment.getMineType())
                    || Constants.MIME_TYPE_SKETCH.equalsIgnoreCase(attachment.getMineType())) {
                getBinding().main.etContent.setEffect(MarkdownEffect.IMAGE, title , attachment.getUri().toString());
            } else {
                getBinding().main.etContent.setEffect(MarkdownEffect.LINK, title, attachment.getUri().toString());
            }
        }
    }

    @Override
    protected boolean checkInputInfo() {
        if (TextUtils.isEmpty(getBinding().main.etTitle.getText().toString())) {
            ToastUtils.makeToast(R.string.title_required);
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
        String noteContent = getBinding().main.etContent.getText().toString();

        if (atNoteFile == null) {
            try {
                FileUtils.writeStringToFile(tempFile, noteContent, "utf-8");
            } catch (IOException e) {
                LogUtils.d("onClick: " + e);
            }
            atNoteFile = ModelFactory.getAttachment(getContext());
            atNoteFile.setUri(FileHelper.getUriFromFile(getContext(), tempFile));
            atNoteFile.setSize(FileUtils.sizeOf(tempFile));
            atNoteFile.setPath(tempFile.getPath());
            atNoteFile.setName(tempFile.getName());
            atNoteFile.setLength(atNoteFile.getLength());
            AttachmentsStore.getInstance(getContext()).saveModel(atNoteFile);
        } else {
            try {
                LogUtils.d(noteFile);
                FileUtils.writeStringToFile(noteFile, noteContent, "utf-8", false);
                // Whenever the attachment file is updated, remember to update its attachment.
                atNoteFile.setLastModifiedTime(new Date());
                AttachmentsStore.getInstance(getContext()).update(atNoteFile);
            } catch (IOException e) {
                LogUtils.d("onClick: " + e);
            }
        }

        note.setContentCode(atNoteFile.getCode());
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
