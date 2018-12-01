package me.shouheng.notepal.fragment;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.kennyc.bottomsheet.BottomSheet;
import com.kennyc.bottomsheet.BottomSheetListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.shouheng.commons.activity.ContainerActivity;
import me.shouheng.commons.activity.interaction.BackEventResolver;
import me.shouheng.commons.fragment.CommonFragment;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.utils.PalmUtils;
import me.shouheng.commons.utils.StringUtils;
import me.shouheng.commons.utils.ToastUtils;
import me.shouheng.data.ModelFactory;
import me.shouheng.data.entity.Attachment;
import me.shouheng.data.entity.Category;
import me.shouheng.data.entity.Note;
import me.shouheng.data.model.enums.ModelType;
import me.shouheng.data.store.CategoryStore;
import me.shouheng.easymark.EasyMarkEditor;
import me.shouheng.easymark.tools.Utils;
import me.shouheng.notepal.Constants;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.SettingsActivity;
import me.shouheng.notepal.databinding.FragmentNoteBinding;
import me.shouheng.notepal.dialog.CategoryEditDialog;
import me.shouheng.notepal.dialog.TableInputDialog;
import me.shouheng.notepal.dialog.picker.CategoryPickerDialog;
import me.shouheng.notepal.dialog.picker.NotebookPickerDialog;
import me.shouheng.notepal.fragment.setting.SettingsNote;
import me.shouheng.notepal.util.AppWidgetUtils;
import me.shouheng.notepal.util.FileHelper;
import me.shouheng.notepal.util.ModelHelper;
import me.shouheng.notepal.viewmodel.CategoryViewModel;
import me.shouheng.notepal.vm.NoteViewModel;
import me.shouheng.notepal.widget.MDEditorLayout;

import static me.shouheng.notepal.Constants.SHORTCUT_ACTION_CAPTURE;
import static me.shouheng.notepal.Constants.SHORTCUT_ACTION_CREATE_NOTE;
import static me.shouheng.notepal.Constants.SHORTCUT_ACTION_VIEW_NOTE;

/**
 * Created by WngShhng (shouehng2015@gmail.com) on 2017/5/12.*/
public class NoteFragment extends CommonFragment<FragmentNoteBinding> implements BackEventResolver {

    /**
     * The key for action, used to send a command to this fragment.
     * The MainActivity will directly put the action argument to this fragment if received itself.
     */
    public final static String ARGS_KEY_ACTION = "__args_key_action";

    /**
     * The intent the MainActivity received. This fragment will get the extras from this value,
     * and handle the intent later.
     */
    public final static String ARGS_KEY_INTENT = "__args_key_intent";

    /**
     * The most important argument, the note model, used to get the information of note.
     */
    public final static String ARGS_KEY_NOTE = "__args_key_note";

    private final static String TAB_REPLACEMENT = "    ";

    private NoteViewModel viewModel;
    private EditText etTitle;
    private EasyMarkEditor eme;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_note;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(this).get(NoteViewModel.class);

        /* Add subscriptions AT FIRST! */
        addSubscriptions();

        /* Handle arguments only when first time launch. */
        if (savedInstanceState == null) {
            handleArguments();
        }

        /* Config toolbar */
        if (getContext() == null || getActivity() == null) return;
        AppCompatActivity app = (AppCompatActivity) getActivity();
        ActionBar actionBar = app.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }

        /* Config the edit layout */
        MDEditorLayout mel = getBinding().mel;
        mel.setOverHeight(Utils.dp2px(getContext(), 50));
        mel.setOnFormatClickListener(format -> {
            switch (format) {
                case TABLE:
                    TableInputDialog.getInstance((rowsStr, colsStr) -> {
                        int rows = StringUtils.parseInteger(rowsStr, 3);
                        int cols = StringUtils.parseInteger(colsStr, 3);
                        eme.useFormat(format, rows, cols);
                    }).show(Objects.requireNonNull(getFragmentManager()), "TABLE EDITOR");
                    break;
                default:
                    eme.useFormat(format);
            }
        });
        eme = mel.getEditText();
        eme.setFormatPasteEnable(true);
        etTitle = mel.getTitleEditor();
        etTitle.addTextChangedListener(inputWatcher);
        eme.addTextChangedListener(inputWatcher);
        mel.getFastScrollView().getFastScrollDelegate().setThumbSize(16, 40);
        mel.getFastScrollView().getFastScrollDelegate().setThumbDynamicHeight(false);
        mel.getFastScrollView().getFastScrollDelegate().setThumbDrawable(PalmApp.getDrawableCompact(isDarkTheme() ?
                R.drawable.fast_scroll_bar_dark : R.drawable.fast_scroll_bar_light));
        mel.setOnCustomFormatClickListener(formatId -> {
            switch (formatId) {
                case MDEditorLayout.FORMAT_ID_ATTACHMENT:
                    showAttachmentPicker();
                    break;
                default:
                    eme.useFormat(formatId);
            }
        });
    }

    private TextWatcher inputWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void afterTextChanged(Editable editable) {
            String title = etTitle.getText().toString();
            String content = eme.getText().toString();
            String count = PalmUtils.getStringCompact(R.string.text_chars) + ":" + (title.length() + content.length());
            getBinding().tvCount.setText(count);
        }
    };

    private void handleArguments() {
        Bundle arguments = getArguments();
        assert arguments != null;

        /* Check the action at first. */
        String action;
        if (arguments.containsKey(ARGS_KEY_ACTION)
                && (action = arguments.getString(ARGS_KEY_ACTION)) != null) {
            switch (action) {
                /* Handle the shortcut actions. */
                case SHORTCUT_ACTION_CREATE_NOTE:
                    break;
                case SHORTCUT_ACTION_VIEW_NOTE:
                    break;
                case SHORTCUT_ACTION_CAPTURE:
                    break;

                /* Handle the third part actions. */
                case Intent.ACTION_SEND:
                case Intent.ACTION_SEND_MULTIPLE:
                    break;
                case Intent.ACTION_VIEW:
                case Intent.ACTION_EDIT:
                    break;

                 /* Handle the AppWidget actions. */

            }
        } else {
            Note note;

            /* Start note fragment without action and intent, then the note is necessary. */
            if (!arguments.containsKey(ARGS_KEY_NOTE)
                    || (note = (Note) arguments.getSerializable(ARGS_KEY_NOTE)) == null) {
                ToastUtils.makeToast(R.string.text_note_not_found);
                if (getActivity() != null) getActivity().finish();
                return;
            }

            viewModel.notifyNoteChanged(note);

            // Handle arguments for intent from third part
//            if (arguments.getBoolean(EXTRA_IS_THIRD_PART)) {
//                handleThirdPart();
//            } else if(Constants.ACTION_ADD_SKETCH.equals(arguments.getString(EXTRA_ACTION))) {
//                if (getActivity() != null) {
//                    PermissionUtils.checkStoragePermission((PermissionActivity) getActivity(), () -> AttachmentHelper.sketch(this));
//                }
//            } else if (Constants.ACTION_TAKE_PHOTO.equals(arguments.getString(EXTRA_ACTION))) {
//                if (getActivity() != null) {
//                    PermissionUtils.checkStoragePermission((PermissionActivity) getActivity(), () -> AttachmentHelper.capture(this));
//                }
//            } else if (Constants.ACTION_ADD_FILES.equals(arguments.getString(EXTRA_ACTION))) {
//                if (getActivity() != null) {
//                    PermissionUtils.checkStoragePermission((PermissionActivity) getActivity(), () -> AttachmentHelper.pickFiles(this));
//                }
//            } else {
                // The cases above is new model, don't need to fetch data.
//            }
        }
    }

    private void addSubscriptions() {
        viewModel.getNoteObservable().observe(this, resources -> {
            assert resources != null;
            switch (resources.status) {
                case SUCCESS:
                    assert resources.data != null;
                    viewModel.fetchNoteContent();
                    break;
            }
        });
        viewModel.getNoteContentObservable().observe(this, resources -> {
            assert resources != null;
            switch (resources.status) {
                case SUCCESS:
                    Note note = viewModel.getNote();
                    etTitle.setText(note.getTitle());
                    eme.setText(note.getContent());
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed_to_read_note_file);
                    break;
            }
        });
        viewModel.getSaveOrUpdateObservable().observe(this, resources -> {
            assert resources != null;
            switch (resources.status) {
                case SUCCESS:
                    AppWidgetUtils.notifyAppWidgets(getContext());
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed_to_save_note);
                    break;
                case LOADING:
                    break;
            }
        });
    }

    private void handleThirdPart() {
        Intent intent = null;

//        = ((OnNoteInteractListener) getActivity()).getIntentForThirdPart();

        String title = intent.getStringExtra(Intent.EXTRA_SUBJECT);
//        note.setTitle(title);

        String content = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (!TextUtils.isEmpty(content)) {
            content = content.replace("\t", TAB_REPLACEMENT);
        }
//        note.setContent(content);

        // Single attachment data
        Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);

        // Due to the fact that Google Now passes intent as text but with
        // audio recording attached the case must be handled in specific way
        if (uri != null) {
//            new CreateAttachmentTask(this, uri, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        // Multiple attachment data
        ArrayList<Uri> uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (uris != null) {
            for (Uri uriSingle : uris) {
//                new CreateAttachmentTask(this, uriSingle, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }
    // endregion

    private void showAttachmentPicker() {
        new BottomSheet.Builder(Objects.requireNonNull(getContext()))
                .setStyle(isDarkTheme() ? R.style.BottomSheet_Dark : R.style.BottomSheet)
                .setMenu(ColorUtils.getThemedBottomSheetMenu(getContext(), R.menu.attachment_picker))
                .setListener(new BottomSheetListener() {
                    @Override
                    public void onSheetShown(@NonNull BottomSheet bottomSheet, @Nullable Object o) {}

                    @Override
                    public void onSheetItemSelected(@NonNull BottomSheet bottomSheet, MenuItem menuItem, @Nullable Object o) {
                        switch (menuItem.getItemId()) {
                            case R.id.item_pick_from_album:
                                break;
                            case R.id.item_pick_a_file:
                                break;
                            case R.id.item_pick_take_a_photo:
                                break;
                            case R.id.item_pick_create_sketch:
                                break;
                        }
                    }

                    @Override
                    public void onSheetDismissed(@NonNull BottomSheet bottomSheet, @Nullable Object o, int i) {}
                })
                .show();
    }

    // endregion

//    @Override
    protected void onFailedGetAttachment(Attachment attachment) {
        ToastUtils.makeToast(R.string.text_failed_to_save_attachment);
    }

//    @Override
    protected void onGetAttachment(@NonNull Attachment attachment) {
//        attachment.setModelCode(note.getCode());
        attachment.setModelType(ModelType.NOTE);
//        attachmentViewModel.saveModel(attachment).observe(this, LogUtils::d);

        String title = FileHelper.getNameFromUri(getContext(), attachment.getUri());
        if (TextUtils.isEmpty(title)) title = getString(R.string.text_attachment);

        // TODO the attachment location
        if (Constants.MIME_TYPE_IMAGE.equalsIgnoreCase(attachment.getMineType())
                || Constants.MIME_TYPE_SKETCH.equalsIgnoreCase(attachment.getMineType())) {
//            getBinding().etContent.useFormat(Format.IMAGE);
        } else {
//            getBinding().etContent.useFormat(Format.IMAGE);
        }
    }

    private void showCategoriesPicker() {
        Disposable disposable = Observable.create((ObservableOnSubscribe<List<Category>>) emitter -> {
            List<Category> categoryList = CategoryStore.getInstance().get(null, null);
            emitter.onNext(categoryList);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Category>>() {
            @Override
            public void accept(List<Category> categories) {
                for (Category s : viewModel.getCategories()) {
                    for (Category a : categories) {
                        if (s.getCode() == a.getCode()) {
                            a.setSelected(true);
                        }
                    }
                }
                CategoryPickerDialog dialog = CategoryPickerDialog.newInstance(categories);
                dialog.setOnConfirmClickListener(selections -> {
                    viewModel.setCategories(selections);
                    viewModel.getNote().setTags(CategoryViewModel.getTags(selections));
                });
                dialog.setOnAddClickListener(() -> showCategoryEditor());
                dialog.show(getChildFragmentManager(), "CATEGORY_PICKER");
            }
        });
    }

    private void showCategoryEditor() {
        CategoryEditDialog.newInstance(ModelFactory.getCategory(), category -> {
            Disposable disposable = Observable.create((ObservableOnSubscribe<Category>) emitter -> {
                CategoryStore.getInstance().saveModel(category);
                emitter.onNext(category);
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(category1 -> showCategoriesPicker());
        }).show(getChildFragmentManager(), "CATEGORY PICKER");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.note_editor_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_preview: {
                String title = etTitle.getText().toString();
                String content = eme.getText().toString() + " ";
                viewModel.getNote().setTitle(title);
                viewModel.getNote().setContent(content);
                ContainerActivity.open(NoteViewFragment.class)
                        .put(NoteViewFragment.ARGS_KEY_NOTE, (Serializable) viewModel.getNote())
                        .put(NoteViewFragment.ARGS_KEY_IS_PREVIEW, true)
                        .launch(getActivity());
                break;
            }
            case R.id.action_undo:
                eme.undo();
                break;
            case R.id.action_redo:
                eme.redo();
                break;
            case R.id.action_notebook:
                NotebookPickerDialog.newInstance().setOnItemSelectedListener((dialog, value, position) -> {
                    viewModel.getNote().setParentCode(value.getCode());
                    viewModel.getNote().setTreePath(value.getTreePath() + "|" + value.getCode());
                    dialog.dismiss();
                }).show(Objects.requireNonNull(getFragmentManager()), "NOTEBOOK_PICKER");
                break;
            case R.id.action_category:
                showCategoriesPicker();
                break;
            case R.id.action_send: {
                String title = etTitle.getText().toString();
                String content = eme.getText().toString() + " ";
                ModelHelper.send(getContext(), title, content, new ArrayList<>());
                break;
            }
            case R.id.action_copy_title: {
                String title = etTitle.getText().toString();
                ModelHelper.copy(getActivity(), title);
                ToastUtils.makeToast(R.string.note_copied_success);
                break;
            }
            case R.id.action_copy_content: {
                String content = eme.getText().toString() + " ";
                ModelHelper.copy(getActivity(), content);
                ToastUtils.makeToast(R.string.note_copied_success);
                break;
            }
            case R.id.action_setting_note: {
                SettingsActivity.open(SettingsNote.class).launch(getContext());
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void resolve() {
        String title = etTitle.getText().toString();
        String content = eme.getText().toString();
        viewModel.saveOrUpdateNote(title, content);
    }
}
