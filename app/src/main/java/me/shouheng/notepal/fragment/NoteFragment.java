package me.shouheng.notepal.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.ContentActivity;
import me.shouheng.notepal.config.Constants;
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
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.widget.FlowLayout;
import my.shouheng.palmmarkdown.MarkdownEditor;
import my.shouheng.palmmarkdown.dialog.LinkInputDialog;
import my.shouheng.palmmarkdown.dialog.TableInputDialog;
import my.shouheng.palmmarkdown.tools.MarkdownEffect;

/**
 * Created by wangshouheng on 2017/5/12.*/
public class NoteFragment extends BaseModelFragment<Note> {

    private EditText etTitle;
    private MarkdownEditor markdownEditor;

    private DrawerLayout drawerLayout;
    private ImageView ivEnableFormat;
    private RelativeLayout rlBottomEditor, rlBottom;
    private FlowLayout flLabels;
    private TextView tvLocationInfo, tvSetAs, tvPurpose, tvNotebook;
    private MaterialMenuDrawable materialMenu;

    private Note note;
    private Location location;
    private Attachment noteFile;

    private File file;
    private File tempFile;
    private Notebook notebook;

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

    private void prepareValues(){
        Bundle arguments = getArguments();
        if (arguments.containsKey(Constants.EXTRA_MODEL)){
            note = (Note) arguments.getSerializable(Constants.EXTRA_MODEL);
        }

        // 当笔记实体没有被找到的时候，关闭活动并且弹出通知提示
        if (note == null)  {
            getActivity().finish();
            ToastUtils.makeToast(getContext(), R.string.no_such_note);
        }

        noteFile = AttachmentsStore.getInstance(getContext()).get(note.getContentCode());
        location = LocationsStore.getInstance(getContext()).getLocation(note);
        notebook = NotebookStore.getInstance(getContext()).get(note.getParentCode());
        purpose = PurposeStore.getInstance(getContext()).get(note.getPurposeCode());

        if (noteFile != null) {
            try {
                file = new File(noteFile.getPath());
                String content = FileUtils.readFileToString(file, "utf-8");
                note.setContent(content);
            } catch (IOException e) {
                ToastUtils.makeToast(getContext(), R.string.failed_to_read_file);
            }
        } else {
            tempFile = FileHelper.createNewAttachmentFile(getContext(), PreferUtils.getInstance(getContext()).getNoteFileExtension());
            note.setContent("");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepareValues();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_note, container, false);

        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        materialMenu = new MaterialMenuDrawable(getContext(), primaryColor(), MaterialMenuDrawable.Stroke.THIN);
        materialMenu.setIconState(MaterialMenuDrawable.IconState.ARROW);
        toolbar.setNavigationIcon(materialMenu);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("");
        setStatusBarColor(isDarkTheme() ? getResources().getColor(R.color.dark_theme_foreground) : getResources().getColor(R.color.md_grey_500));

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                setContentChanged();
            }
        };

        etTitle = (EditText) rootView.findViewById(R.id.et_title);
        etTitle.setText(TextUtils.isEmpty(note.getTitle()) ? "" : note.getTitle());
        etTitle.addTextChangedListener(textWatcher);

        markdownEditor = (MarkdownEditor) rootView.findViewById(R.id.et_content);
        markdownEditor.setText(note.getContent());
        markdownEditor.addTextChangedListener(textWatcher);

        tvNotebook = (TextView) rootView.findViewById(R.id.tv_folder);
        if (notebook != null) tvNotebook.setText(notebook.getTitle());
        rootView.findViewById(R.id.ll_folder).setOnClickListener(v -> showNotebookPicker());

        rlBottom = rootView.findViewById(R.id.rl_bottom);
        rlBottomEditor = rootView.findViewById(R.id.rl_bottom_editors);
        rlBottomEditor.setVisibility(View.GONE);

        int[] ids = new int[] {R.id.iv_h1, R.id.iv_h2, R.id.iv_h3, R.id.iv_h4, R.id.iv_h5, R.id.iv_h6,
                R.id.iv_bold, R.id.iv_italic, R.id.iv_stroke, R.id.iv_format_list, R.id.iv_number_list,
                R.id.iv_line, R.id.iv_code, R.id.iv_xml, R.id.iv_quote,
                R.id.iv_insert_picture, R.id.iv_insert_link, R.id.iv_insert_table,
                R.id.iv_redo, R.id.iv_undo};
        for (int id : ids) {
            rootView.findViewById(id).setOnClickListener(this::addEffect);
        }

        ivEnableFormat = rootView.findViewById(R.id.iv_enable_format);
        ivEnableFormat.setOnClickListener(v -> switchFormat());

        drawerLayout = rootView.findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        Toolbar drawerToolbar = rootView.findViewById(R.id.drawer_toolbar);
        drawerToolbar.setNavigationOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.END));
        if (isDarkTheme()){
            drawerToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            rootView.findViewById(R.id.drawer).setBackgroundColor(getResources().getColor(R.color.dark_theme_background));
            drawerToolbar.setBackgroundColor(getResources().getColor(R.color.dark_theme_background));
        }

        ((TextView) rootView.findViewById(R.id.tv_time_info)).setText(ModelHelper.getTimeInfo(note));

        flLabels = rootView.findViewById(R.id.fl_labels);
        flLabels.setOnClickListener(v -> showTagsEditDialog());
        rootView.findViewById(R.id.tv_add_labels).setOnClickListener(v -> showTagEditDialog());
        addTagsToLayout(note.getTags());

        tvLocationInfo = rootView.findViewById(R.id.tv_location_info);
        rootView.findViewById(R.id.tv_add_location).setOnClickListener(v -> tryToLocate());
        showLocationInfo();

        rootView.findViewById(R.id.tv_copy_link).setOnClickListener(v -> ModelHelper.copyLink(getActivity(), note));

        rootView.findViewById(R.id.tv_copy_text).setOnClickListener(v -> {
            note.setContent(markdownEditor.getText().toString());
            ModelHelper.copyToClipboard(getActivity(), markdownEditor.getText().toString());
        });

        rootView.findViewById(R.id.tv_add_to_home_screen).setOnClickListener(v -> addShortcut());

        rootView.findViewById(R.id.tv_statistics).setOnClickListener(v -> showStatisticsDialog());

        tvSetAs = rootView.findViewById(R.id.tv_attach_to_class);
        tvSetAs.setOnClickListener(v -> showClassPicker());
        Clazz clazz = ClassesStore.getInstance(getActivity()).get(note.getClassCode());
        if (clazz != null) {
            showNoteAttachInfo(clazz);
        }

        tvPurpose = rootView.findViewById(R.id.tv_set_as_purpose);
        showPurposeInfo(purpose, tvPurpose);
        tvPurpose.setOnClickListener(v -> showPurposePicker());

        return rootView;
    }

    private void showNotebookPicker() {
        NotebookPickerDialog.newInstance(getContext())
                .setOnItemSelectedListener((dialog, notebook1, position) -> {
                    note.setParentCode(notebook1.getCode());
                    tvNotebook.setText(notebook1.getTitle());
                    tvNotebook.setTextColor(notebook1.getColor());
                    setContentChanged();
                    dialog.dismiss();
                })
                .show(getFragmentManager(), "NOTEBOOK_PICKER");
    }

    @Override
    protected FlowLayout getTagsLayout() {
        return flLabels;
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
    protected void onGetPurpose(Purpose purpose) {
        super.onGetPurpose(purpose);
        note.setPurposeCode(purpose.getCode());
        setContentChanged();
        showPurposeInfo(purpose, tvPurpose);
    }

    @Override
    protected void onGetLocation(BDLocation bdLocation) {
        super.onGetLocation(bdLocation);
        if (bdLocation != null && !TextUtils.isEmpty(bdLocation.getCity())){
            if (location != null){
                location.setLongitude(bdLocation.getLongitude());
                location.setLatitude(bdLocation.getLatitude());
                location.setCountry(bdLocation.getCountry());
                location.setProvince(bdLocation.getProvince());
                location.setCity(bdLocation.getCity());
                location.setDistrict(bdLocation.getDistrict());
                LocationsStore.getInstance(getContext()).update(location);
            } else {
                location = ModelFactory.getLocation(getContext());
                location.setLongitude(bdLocation.getLongitude());
                location.setLatitude(bdLocation.getLatitude());
                location.setCountry(bdLocation.getCountry());
                location.setProvince(bdLocation.getProvince());
                location.setCity(bdLocation.getCity());
                location.setDistrict(bdLocation.getDistrict());
                location.setModelCode(note.getCode());
                location.setModelType(ModelType.NOTE);
                LocationsStore.getInstance(getContext()).saveModel(location);
            }
            showLocationInfo();
        } else {
            ToastUtils.makeToast(getContext(), R.string.failed_to_get_location);
        }
    }

    private void showLocationInfo(){
        if (location != null) {
            String strLocation = location.getCountry() + "|" + location.getProvince() + "|" + location.getCity() + "|" + location.getDistrict();
            tvLocationInfo.setVisibility(View.VISIBLE);
            tvLocationInfo.setText(strLocation);
        }
    }

    @Override
    protected void onGetClassAttached(Clazz clazz) {
        super.onGetClassAttached(clazz);
        setContentChanged();
        note.setClassCode(clazz.getCode());
        showNoteAttachInfo(clazz);
    }

    @Override
    protected void onFailedToGetClassAttached() {
        super.onFailedToGetClassAttached();
        ToastUtils.makeToast(getActivity(), R.string.no_class_specified);
    }

    private void showNoteAttachInfo(final Clazz clazz) {
        int clsColor = clazz.getColor();

        String clsTitle = clazz.getTitle();
        SpannableString spTitle = new SpannableString(clsTitle);
        spTitle.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // 单击时跳转到课程信息浏览界面
                ContentActivity.startClassForResult(NoteFragment.this, clazz.getCode(), 0, 1);
            }
        }, 0, clsTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spTitle.setSpan(new ColorSpan(clsColor, Color.TRANSPARENT, false), 0, clsTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        String clsSuffix = getString(R.string.note_of);

        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(spTitle);
        sb.append(clsSuffix);

        tvSetAs.setMovementMethod(LinkMovementMethod.getInstance());
        tvSetAs.setText(sb);
    }

    private void addEffect(View v) {
        MarkdownEffect effect = null;
        switch (v.getId()){
            case R.id.iv_undo:markdownEditor.undo();break;
            case R.id.iv_redo:markdownEditor.redo();break;
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
                LinkInputDialog.getInstance((title, link) -> markdownEditor.setEffect(MarkdownEffect.LINK, title, link)).show(getFragmentManager(), "LINK INPUT");
            } break;
            case R.id.iv_insert_table: {
                TableInputDialog.getInstance((rowsStr, colsStr) -> {
                    int rows, cols;

                    try {rows = TextUtils.isEmpty(rowsStr) ? 3 : Integer.parseInt(rowsStr);
                    } catch (NumberFormatException e) {rows = 3;}

                    try {cols = TextUtils.isEmpty(colsStr) ? 3 : Integer.parseInt(colsStr);
                    } catch (NumberFormatException e) {cols = 3;}

                    markdownEditor.setEffect(MarkdownEffect.TABLE, rows, cols);
                }).show(getFragmentManager(), "TABLE INPUT");
            } break;
        }
        if (effect != null) markdownEditor.setEffect(effect);
    }

    private void switchFormat() {
        if (rlBottomEditor.getVisibility() == View.VISIBLE) {
            rlBottomEditor.setVisibility(View.GONE);
            ivEnableFormat.setImageDrawable(ColorUtils.tintDrawable(
                    getResources().getDrawable(R.drawable.ic_text_format_black_24dp), Color.WHITE));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ivEnableFormat.getHeight());
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            rlBottom.setLayoutParams(params);
        } else {
            rlBottomEditor.setVisibility(View.VISIBLE);
            ivEnableFormat.setImageDrawable(ColorUtils.tintDrawable(
                    getResources().getDrawable(R.drawable.ic_text_format_black_24dp), primaryColor()));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ivEnableFormat.getHeight() * 2);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            rlBottom.setLayoutParams(params);
        }
    }

    @Override
    protected void onFailedGetAttachment(Attachment attachment) {
        ToastUtils.makeToast(getContext(), R.string.failed_to_save_attachment);
    }

    @Override
    protected void onGetAttachment(Attachment attachment) {
        markdownEditor.setEffect(MarkdownEffect.IMAGE, "image" , attachment.getUri().toString());
        // 将添加的附件保存到附件数据库中
        attachment.setModelCode(note.getCode());
        attachment.setModelType(ModelType.NOTE);
        AttachmentsStore.getInstance(getContext()).saveModel(attachment);
    }

    @Override
    protected void personalizeAttachmentPicker(AttachmentPickerDialog attachmentPickerDialog) {
        attachmentPickerDialog.setVideoVisible(false);
        attachmentPickerDialog.setAlbumVisible(true);
        attachmentPickerDialog.setFilesVisible(false);
        attachmentPickerDialog.setRecordVisible(false);
        attachmentPickerDialog.setAddLinkVisible(true);
        attachmentPickerDialog.setOnAddNetUriSelectedListener(() -> addImageLink());
    }

    private void addImageLink() {
        LinkInputDialog.getInstance((title, link) -> markdownEditor.setEffect(MarkdownEffect.IMAGE, title, link)).show(getFragmentManager(), "Link Image");
    }

    @Override
    protected String getStatisticsToShow() {
        return ModelHelper.getStatistics(getActivity(), note, new ArrayList<Model>());
    }

    private View.OnClickListener onSetAsClickListener = v -> setAs();

    private void setAs(){
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_set_assignment_type_picker_layout, null, false);

        final Dialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.associate_to_class)
                .setView(rootView)
                .setPositiveButton(R.string.cancel, null)
                .create();
        dialog.show();

        View.OnClickListener onItemClickListener = v -> {
            switch (v.getId()){
//                    case R.id.tv_set_as_task:
//                        assignment.setType(AssignmentType.TASK);
//                        SearchActivity.startActivityForClass(AssignmentFragment.this, REQUEST_CODE_FOR_CLASS);
//                        if (dialog.isShowing()){
//                            dialog.dismiss();
//                        }
//                        break;
//                    case R.id.tv_set_as_exam:
//                        assignment.setType(AssignmentType.EXAM);
//                        SearchActivity.startActivityForClass(AssignmentFragment.this, REQUEST_CODE_FOR_CLASS);
//                        if (dialog.isShowing()){
//                            dialog.dismiss();
//                        }
//                        break;
            }
        };

        rootView.findViewById(R.id.tv_set_as_exam).setOnClickListener(onItemClickListener);
        rootView.findViewById(R.id.tv_set_as_task).setOnClickListener(onItemClickListener);
    }
    // endregion

    @Override
    protected boolean checkInputInfo() {
        if (TextUtils.isEmpty(etTitle.getText().toString())) {
            ToastUtils.makeToast(getContext(), String.format(getString(R.string.title_required), getString(R.string.notes)));
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
        super.beforeSaveOrUpdate();

        // 保存笔记对应的文件
        if (noteFile == null) { // 如果笔记文件为空，说明之前没有添加过该文件
            try {
                FileUtils.writeStringToFile(tempFile, markdownEditor.getText().toString(), "utf-8");
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
            try { // 将内容重新写入到文件当中，不要从笔记对象上取值
                FileUtils.writeStringToFile(file, markdownEditor.getText().toString(), "utf-8", false);
            } catch (IOException e) {
                LogUtils.d("onClick: " + e);
            }
        }

        // 将笔记对应的文件附件的编号设置为笔记的内容编号
        note.setContentCode(noteFile.getCode());
        note.setTitle(etTitle.getText().toString());
    }

    @Override
    protected void afterSaveOrUpdate() {
        super.afterSaveOrUpdate();
        materialMenu.animateIconState(MaterialMenuDrawable.IconState.ARROW);
        // 更新笔记的内容到content字段上，该字段用于在返回的碎片中重新展示编辑的内容
        String content = markdownEditor.getText().toString();
        if (TextUtils.isEmpty(content)) {
            content = "  "; // 不要使用空字符串，防止在浏览界面中去读取文件中的内容
        }
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
    protected void setContentChanged(){
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
                // 如果内容发生变化就进行保存，否则退出
                if (isContentChanged()) {
                    saveOrUpdateData();
                } else {
                    setResult();
                }
                break;
            case R.id.action_more:
                drawerLayout.openDrawer(GravityCompat.END, true);
                break;
            case R.id.action_preview:
                note.setTitle(TextUtils.isEmpty(etTitle.getText().toString()) ?
                        "" : etTitle.getText().toString());
                String content = markdownEditor.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    content = "  "; // 不要使用空字符串，防止在浏览界面中去读取文件中的内容
                }
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
