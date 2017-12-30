package me.shouheng.notepal.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.ContentActivity;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.databinding.FragmentNoteBinding;
import me.shouheng.notepal.dialog.AttachmentPickerDialog;
import me.shouheng.notepal.dialog.NotebookPickerDialog;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.model.Location;
import me.shouheng.notepal.model.ModelFactory;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.model.enums.ModelType;
import me.shouheng.notepal.provider.AttachmentsStore;
import me.shouheng.notepal.provider.BaseStore;
import me.shouheng.notepal.provider.LocationsStore;
import me.shouheng.notepal.provider.NotebookStore;
import me.shouheng.notepal.provider.NotesStore;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.util.FileHelper;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.ModelHelper;
import me.shouheng.notepal.util.PreferencesUtils;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.widget.FlowLayout;
import my.shouheng.palmmarkdown.dialog.LinkInputDialog;
import my.shouheng.palmmarkdown.dialog.TableInputDialog;
import my.shouheng.palmmarkdown.tools.MarkdownEffect;

/**
 * Created by wangshouheng on 2017/5/12.*/
public class NoteFragment extends BaseModelFragment<Note, FragmentNoteBinding> {

    private MaterialMenuDrawable materialMenu;

    private Note note;
    private Location location;
    private Attachment noteFile;

    private File file;
    private File tempFile;
    private Notebook notebook;

    private PreferencesUtils preferencesUtils;

    private AttachmentPickerDialog attachmentPickerDialog;

    public static NoteFragment newInstance(Note note, Integer position, Integer requestCode){
        Bundle arg = new Bundle();
        if (note == null) throw new IllegalArgumentException("Note cannot be null");
        arg.putSerializable(Constants.EXTRA_MODEL, note);
        if (position != null) arg.putInt(Constants.EXTRA_POSITION, position);
        if (requestCode != null) arg.putInt(Constants.EXTRA_REQUEST_CODE, requestCode);
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

        if (arguments != null && arguments.containsKey(Constants.EXTRA_MODEL)){
            note = (Note) arguments.getSerializable(Constants.EXTRA_MODEL);
        }

        if (note == null)  {
            if (getActivity() != null) getActivity().finish();
            ToastUtils.makeToast(getContext(), R.string.note_no_such_note);
        }

        noteFile = AttachmentsStore.getInstance(getContext()).get(note.getContentCode());
        location = LocationsStore.getInstance(getContext()).getLocation(note);
        notebook = NotebookStore.getInstance(getContext()).get(note.getParentCode());

        if (noteFile != null) {
            try {
                file = new File(noteFile.getPath());
                String content = FileUtils.readFileToString(file, "utf-8");
                note.setContent(content);
            } catch (IOException e) {
                ToastUtils.makeToast(getContext(), R.string.note_failed_to_read_file);
            }
        } else {
            tempFile = FileHelper.createNewAttachmentFile(getContext(), preferencesUtils.getNoteFileExtension());
            note.setContent("");
        }
    }

    private void configToolbar() {
        materialMenu = new MaterialMenuDrawable(getContext(), primaryColor(), MaterialMenuDrawable.Stroke.THIN);
        materialMenu.setIconState(MaterialMenuDrawable.IconState.ARROW);
        getBinding().main.toolbar.setNavigationIcon(materialMenu);
        ((AppCompatActivity) getActivity()).setSupportActionBar(getBinding().main.toolbar);
        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("");
        setStatusBarColor(getResources().getColor(isDarkTheme() ? R.color.dark_theme_foreground : R.color.md_grey_500));
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
    }

    private void configDrawer() {
        getBinding().drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        getBinding().drawer.drawerToolbar.setNavigationOnClickListener(v ->
                getBinding().drawerLayout.closeDrawer(GravityCompat.END));
        if (isDarkTheme()){
            getBinding().drawer.drawerToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            getBinding().drawer.getRoot().setBackgroundColor(getResources().getColor(R.color.dark_theme_background));
            getBinding().drawer.drawerToolbar.setBackgroundColor(getResources().getColor(R.color.dark_theme_background));
        }

        getBinding().drawer.tvTimeInfo.setText(ModelHelper.getTimeInfo(note));

        getBinding().drawer.flLabels.setOnClickListener(v -> showTagsEditDialog());
        getBinding().drawer.tvAddLabels.setOnClickListener(v -> showTagEditDialog());
        addTagsToLayout(note.getTags());

        getBinding().drawer.tvAddLocation.setOnClickListener(v -> tryToLocate());
        showLocationInfo();

        getBinding().drawer.tvCopyLink.setOnClickListener(v -> ModelHelper.copyLink(getActivity(), note));

        getBinding().drawer.tvCopyText.setOnClickListener(v -> {
            note.setContent(getBinding().main.etContent.getText().toString());
            ModelHelper.copyToClipboard(getActivity(), getBinding().main.etContent.getText().toString());
        });

        getBinding().drawer.tvAddToHomeScreen.setOnClickListener(v -> addShortcut());

        getBinding().drawer.tvStatistics.setOnClickListener(v -> showStatisticsDialog());
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            setContentChanged();
        }
    };

    private void showStatisticsDialog(){}

    private void showNotebookPicker() {
        NotebookPickerDialog.newInstance()
                .setOnItemSelectedListener((dialog, notebook1, position) -> {
                    note.setParentCode(notebook1.getCode());
                    note.setTreePath(notebook1.getTreePath() + "|" + notebook1.getCode());
                    getBinding().main.tvFolder.setText(notebook1.getTitle());
                    getBinding().main.tvFolder.setTextColor(notebook1.getColor());
                    setContentChanged();
                    dialog.dismiss();
                })
                .show(getFragmentManager(), "NOTEBOOK_PICKER");
    }

    @Override
    protected FlowLayout getTagsLayout() {
        return getBinding().drawer.flLabels;
    }

    @Override
    protected String getTags() {
        return note.getTags();
    }

    @Override
    protected void onGetTags(String tags) {
        super.onGetTags(tags);
        note.setTags(tags);
        setContentChanged();
    }

    @Override
    protected void onGetLocation(Location location) {
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
            case R.id.iv_insert_picture:
                showAttachmentPicker();
                break;
            case R.id.iv_insert_link: {
                LinkInputDialog.getInstance(
                        (title, link) -> getBinding().main.etContent.setEffect(MarkdownEffect.LINK, title, link)
                ).show(getFragmentManager(), "LINK INPUT");
            } break;
            case R.id.iv_insert_table: {
                TableInputDialog.getInstance((rowsStr, colsStr) -> {
                    int rows, cols;
                    try {rows = TextUtils.isEmpty(rowsStr) ? 3 : Integer.parseInt(rowsStr);}
                    catch (NumberFormatException e) {rows = 3;}
                    try {cols = TextUtils.isEmpty(colsStr) ? 3 : Integer.parseInt(colsStr);}
                    catch (NumberFormatException e) {cols = 3;}
                    getBinding().main.etContent.setEffect(MarkdownEffect.TABLE, rows, cols);
                }).show(getFragmentManager(), "TABLE INPUT");
            } break;
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
        LinkInputDialog.getInstance((title, link) -> getBinding().main.etContent.setEffect(MarkdownEffect.IMAGE, title, link))
                .show(getFragmentManager(), "Link Image");
    }

    @Override
    protected AttachmentPickerDialog getAttachmentPickerDialog() {
        return attachmentPickerDialog;
    }

    private void showAttachmentPicker() {
        attachmentPickerDialog = new AttachmentPickerDialog.Builder(this)
                .setRecordVisible(false)
                .setVideoVisible(false)
                .setOnAddNetUriSelectedListener(this::addImageLink)
                .build();
        attachmentPickerDialog.show(getFragmentManager(), "Attachment picker");
    }

    @Override
    protected void onFailedGetAttachment(Attachment attachment) {
        ToastUtils.makeToast(getContext(), R.string.failed_to_save_attachment);
    }

    @Override
    protected void onGetAttachment(Attachment attachment) {
        getBinding().main.etContent.setEffect(MarkdownEffect.IMAGE, "image" , attachment.getUri().toString());
        attachment.setModelCode(note.getCode());
        attachment.setModelType(ModelType.NOTE);
        AttachmentsStore.getInstance(getContext()).saveModel(attachment);
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
            noteFile.setUri(FileHelper.getAttachmentUriFromFile(getContext(), tempFile));
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
    }

    @Override
    protected BaseStore getStoreOfModel() {
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
                note.setTitle(TextUtils.isEmpty(getBinding().main.etTitle.getText().toString()) ? "" :
                        getBinding().main.etTitle.getText().toString());
                String content = getBinding().main.etContent.getText().toString();
                if (TextUtils.isEmpty(content)) content = "  ";
                note.setContent(content);
                ContentActivity.startNoteViewForResult(this, note, null, 0);
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
}
