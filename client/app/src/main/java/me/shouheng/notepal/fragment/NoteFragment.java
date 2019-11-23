package me.shouheng.notepal.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.kennyc.bottomsheet.BottomSheet.Builder;
import com.kennyc.bottomsheet.BottomSheetListener;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.shouheng.commons.activity.ContainerActivity;
import me.shouheng.commons.activity.interaction.BackEventResolver;
import me.shouheng.commons.event.RxMessage;
import me.shouheng.commons.fragment.CustomFragment;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.utils.PalmUtils;
import me.shouheng.data.ModelFactory;
import me.shouheng.data.entity.Attachment;
import me.shouheng.data.entity.Category;
import me.shouheng.data.entity.Note;
import me.shouheng.data.store.CategoryStore;
import me.shouheng.easymark.EasyMarkEditor;
import me.shouheng.easymark.editor.Format;
import me.shouheng.easymark.tools.Utils;
import me.shouheng.mvvm.base.CommonActivity;
import me.shouheng.mvvm.base.anno.FragmentConfiguration;
import me.shouheng.notepal.Constants;
import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.SettingsActivity;
import me.shouheng.notepal.databinding.FragmentNoteBinding;
import me.shouheng.notepal.dialog.CategoryEditDialog;
import me.shouheng.notepal.dialog.TableInputDialog;
import me.shouheng.notepal.dialog.picker.CategoryPickerDialog;
import me.shouheng.notepal.dialog.picker.NotebookPickerDialog;
import me.shouheng.notepal.fragment.setting.SettingsNote;
import me.shouheng.notepal.manager.FileManager;
import me.shouheng.notepal.manager.NoteManager;
import me.shouheng.notepal.util.AppWidgetUtils;
import me.shouheng.notepal.util.AttachmentHelper;
import me.shouheng.notepal.vm.NoteViewModel;
import me.shouheng.notepal.widget.MDEditorLayout;
import me.shouheng.utils.app.ResUtils;
import me.shouheng.utils.permission.Permission;
import me.shouheng.utils.permission.PermissionUtils;
import me.shouheng.utils.stability.LogUtils;
import me.shouheng.utils.ui.ToastUtils;

import static android.app.Activity.RESULT_OK;
import static me.shouheng.easymark.editor.Format.TABLE;
import static me.shouheng.notepal.Constants.FAB_ACTION_CAPTURE;
import static me.shouheng.notepal.Constants.FAB_ACTION_CREATE_SKETCH;
import static me.shouheng.notepal.Constants.FAB_ACTION_PICK_IMAGE;
import static me.shouheng.notepal.Constants.SHORTCUT_ACTION_CAPTURE;
import static me.shouheng.notepal.Constants.SHORTCUT_ACTION_CREATE_NOTE;
import static me.shouheng.notepal.Constants.SHORTCUT_ACTION_VIEW_NOTE;

/**
 * The note edit fragment.
 *
 * Created by WngShhng (shouehng2015@gmail.com) on 2017/5/12.
 * Refactored by WngShhng (shouheng2015@gmail.com) on 2017/12/2.
 */
@FragmentConfiguration(layoutResId = R.layout.fragment_note)
public class NoteFragment extends CustomFragment<FragmentNoteBinding, NoteViewModel>
        implements BackEventResolver, AttachmentHelper.OnAttachingFileListener {

    /**
     * The key for action, used to send a command to this fragment.
     * The MainActivity will directly put the action argument to this fragment if received itself.
     */
    public static final String ARGS_KEY_ACTION = "__args_key_action";

    /**
     * The intent the MainActivity received. This fragment will get the extras from this value,
     * and handle the intent later.
     */
    public static final String ARGS_KEY_INTENT = "__args_key_intent";

    /**
     * The most important argument, the note model, used to get the information of note.
     */
    public static final String ARGS_KEY_NOTE = "__args_key_note";

    private static final String TAB_REPLACEMENT = "    ";

    private EditText etTitle;
    private EasyMarkEditor eme;

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
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
            if (format == TABLE) {
                TableInputDialog.getInstance((rowsStr, colsStr) -> {
                    int rows = PalmUtils.parseInteger(rowsStr, 3);
                    int cols = PalmUtils.parseInteger(colsStr, 3);
                    eme.useFormat(format, rows, cols);
                }).show(Objects.requireNonNull(getFragmentManager()), "TABLE EDITOR");
            } else {
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
        mel.getFastScrollView().getFastScrollDelegate().setThumbDrawable(ResUtils.getDrawable(isDarkTheme() ?
                R.drawable.fast_scroll_bar_dark : R.drawable.fast_scroll_bar_light));
        mel.setOnCustomFormatClickListener(formatId -> {
            eme.useFormat(formatId);
        });
    }

    private TextWatcher inputWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // noop
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // noop
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String title = etTitle.getText().toString();
            String content = Objects.requireNonNull(eme.getText()).toString();
            String count = ResUtils.getString(R.string.text_chars) + ":" + (title.length() + content.length());
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
                    // Create a note of default notebook and no category.
                    Note note = ModelFactory.getNote();
                    getVM().notifyNoteChanged(note);
                    break;
                case SHORTCUT_ACTION_VIEW_NOTE:
                    note = (Note) arguments.getSerializable(ARGS_KEY_NOTE);
                    assert note != null;
                    getVM().notifyNoteChanged(note);
                    break;
                case SHORTCUT_ACTION_CAPTURE:
                    note = ModelFactory.getNote();
                    getVM().notifyNoteChanged(note);
                    /* Need to delay few minutes, otherwise the fragment can't get the result. */
                    new Handler().postDelayed(() -> AttachmentHelper.takeAPhoto(NoteFragment.this), 800);
                    break;

                /* Handle the third part actions. */
                case Intent.ACTION_SEND:
                case Intent.ACTION_SEND_MULTIPLE:
                    /* Handle the note title and content. */
                    note = ModelFactory.getNote();
                    Intent intent = arguments.getParcelable(ARGS_KEY_INTENT);
                    assert intent != null;
                    String title = intent.getStringExtra(Intent.EXTRA_SUBJECT);
                    note.setTitle(title);
                    String content = intent.getStringExtra(Intent.EXTRA_TEXT);
                    if (!TextUtils.isEmpty(content)) {
                        content = content.replace("\t", TAB_REPLACEMENT);
                    }
                    note.setContent(content);
                    getVM().notifyNoteChanged(note);
                    /* Handle the attachments. */
                    List<Uri> uris = new LinkedList<>();
                    Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                    if (uri != null) {
                        uris.add(uri);
                    }
                    ArrayList<Uri> list = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                    if (list != null) {
                        uris.addAll(list);
                    }
                    if (!uris.isEmpty()) {
                        AttachmentHelper.handleAttachments(this, uris, note);
                    }
                    break;
                case Intent.ACTION_VIEW:
                case Intent.ACTION_EDIT:
                    note = ModelFactory.getNote();
                    intent = arguments.getParcelable(ARGS_KEY_INTENT);
                    assert intent != null;
                    uri = intent.getData();
                    String path = FileManager.getPath(getContext(), uri);
                    Disposable disposable = Observable
                            .create((ObservableOnSubscribe<String>) emitter -> {
                                try {
                                    if (path == null) {
                                        emitter.onError(new Exception("The file path is null"));
                                        return;
                                    }
                                    File file = new File(path);
                                    String contentLocal = FileUtils.readFileToString(file, Constants.NOTE_FILE_ENCODING);
                                    emitter.onNext(contentLocal);
                                } catch (IOException ex) {
                                    emitter.onError(ex);
                                }
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(s -> {
                                note.setContent(s);
                                getVM().notifyNoteChanged(note);
                            }, throwable -> getVM().notifyNoteChanged(note));
                    LogUtils.d(disposable);
                    break;

                /* FAB actions */
                case FAB_ACTION_CAPTURE:
                    note = (Note) arguments.getSerializable(ARGS_KEY_NOTE);
                    assert note != null;
                    getVM().notifyNoteChanged(note);
                    new Handler().postDelayed(() -> AttachmentHelper.takeAPhoto(NoteFragment.this), 800);
                    break;
                case FAB_ACTION_PICK_IMAGE:
                    note = (Note) arguments.getSerializable(ARGS_KEY_NOTE);
                    assert note != null;
                    getVM().notifyNoteChanged(note);
                    AttachmentHelper.pickFromCustomAlbum(NoteFragment.this);
                    break;
                case FAB_ACTION_CREATE_SKETCH:
                    note = (Note) arguments.getSerializable(ARGS_KEY_NOTE);
                    assert note != null;
                    getVM().notifyNoteChanged(note);
                    AttachmentHelper.createSketch(NoteFragment.this);
                    break;

                /* Handle the AppWidget actions. */
                case Constants.APP_WIDGET_ACTION_CAPTURE:
                    note = (Note) arguments.getSerializable(ARGS_KEY_NOTE);
                    getVM().notifyNoteChanged(Objects.requireNonNull(note));
                    new Handler().postDelayed(() -> AttachmentHelper.takeAPhoto(NoteFragment.this), 800);
                    break;
                case Constants.APP_WIDGET_ACTION_CREATE_SKETCH:
                    note = (Note) arguments.getSerializable(ARGS_KEY_NOTE);
                    getVM().notifyNoteChanged(Objects.requireNonNull(note));
                    AttachmentHelper.createSketch(NoteFragment.this);
                    break;

                default:
                    // noop
            }
        } else {
            Note note;

            /* Start note fragment without action and intent, then the note is necessary. */
            if (!arguments.containsKey(ARGS_KEY_NOTE)
                    || (note = (Note) arguments.getSerializable(ARGS_KEY_NOTE)) == null) {
                ToastUtils.showShort(R.string.text_note_not_found);
                if (getActivity() != null) getActivity().finish();
                return;
            }

            getVM().notifyNoteChanged(note);
        }
    }

    private void addSubscriptions() {
        getVM().getObservable(Note.class).observe(this, resources -> {
            assert resources != null;
            switch (resources.status) {
                case SUCCESS:
                    assert resources.data != null;
                    getVM().fetchNoteContent();
                    break;
                case FAILED:
                    ToastUtils.showShort(resources.errorMessage);
                default:
                    // noop
            }
        });
        getVM().getObservable(String.class).observe(this, resources -> {
            assert resources != null;
            switch (resources.status) {
                case SUCCESS:
                    Note note = getVM().getNote();
                    etTitle.setText(note.getTitle());
                    eme.setText(note.getContent());
                    break;
                case FAILED:
                    ToastUtils.showShort(R.string.text_failed_to_read_note_file);
                    break;
                default:
                    // noop
            }
        });
        getVM().getObservable(Boolean.class).observe(this, resources -> {
            assert resources != null;
            switch (resources.status) {
                case SUCCESS:
                    Activity activity = getActivity();
                    assert resources.data != null;
                    if (resources.data) {
                        AppWidgetUtils.notifyAppWidgets(getContext());
                        postEvent(new RxMessage(RxMessage.CODE_NOTE_DATA_CHANGED, null));
                        if (activity != null) {
                            activity.setResult(RESULT_OK);
                        }
                    }
                    if (activity != null) {
                        activity.finish();
                    }
                    break;
                case FAILED:
                    ToastUtils.showShort(R.string.text_failed_to_save_note);
                    break;
                case LOADING:
                    break;
                default:
                    // noop
            }
        });
    }

    private void showAttachmentPicker() {
        new Builder(Objects.requireNonNull(getContext()))
                .setTitle(R.string.text_pick)
                .setStyle(isDarkTheme() ? R.style.BottomSheet_Dark : R.style.BottomSheet)
                .setMenu(ColorUtils.getThemedBottomSheetMenu(getContext(), R.menu.attachment_picker))
                .setListener(new BottomSheetListener() {
                    @Override
                    public void onSheetShown(@NonNull BottomSheet bottomSheet, @Nullable Object o) {
                        // noop
                    }

                    @Override
                    public void onSheetItemSelected(@NonNull BottomSheet bottomSheet, MenuItem menuItem, @Nullable Object o) {
                        switch (menuItem.getItemId()) {
                            case R.id.item_pick_from_album:
                                Activity activity = getActivity();
                                if (activity != null) {
                                    PermissionUtils.checkStoragePermission((CommonActivity) activity,
                                            () -> AttachmentHelper.pickFromCustomAlbum(NoteFragment.this));
                                }
                                break;
                            case R.id.item_pick_take_a_photo:
                                activity = getActivity();
                                if (activity != null) {
                                    PermissionUtils.checkPermissions((CommonActivity) activity,
                                            () -> AttachmentHelper.takeAPhoto(NoteFragment.this),
                                            Permission.STORAGE, Permission.CAMERA);
                                }
                                break;
                            case R.id.item_pick_create_sketch:
                                activity = getActivity();
                                if (activity != null) {
                                    PermissionUtils.checkStoragePermission((CommonActivity) activity,
                                            () -> AttachmentHelper.createSketch(NoteFragment.this));
                                }
                                break;
                            default:
                                // noop
                        }
                    }

                    @Override
                    public void onSheetDismissed(@NonNull BottomSheet bottomSheet, @Nullable Object o, int i) {
                        // noop
                    }
                })
                .show();
    }

    private void showCategoriesPicker() {
        Disposable disposable = Observable
                .create((ObservableOnSubscribe<List<Category>>) emitter -> {
                    List<Category> categoryList = CategoryStore.getInstance().get(null, null);
                    emitter.onNext(categoryList);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categories -> {
                    for (Category s : getVM().getCategories()) {
                        for (Category a : categories) {
                            if (s.getCode() == a.getCode()) {
                                a.setSelected(true);
                            }
                        }
                    }
                    CategoryPickerDialog dialog = CategoryPickerDialog.newInstance(categories);
                    dialog.setOnConfirmClickListener(selections -> {
                        getVM().setCategories(selections);
                        getVM().getNote().setTags(NoteManager.getCategoriesField(selections));
                    });
                    dialog.setOnAddClickListener(this::showCategoryEditor);
                    dialog.show(getChildFragmentManager(), "CATEGORY_PICKER");
                });
        LogUtils.d(disposable);
    }

    private void showCategoryEditor() {
        CategoryEditDialog.newInstance(ModelFactory.getCategory(), category -> {
            Disposable disposable = Observable
                    .create((ObservableOnSubscribe<Category>) emitter -> {
                        CategoryStore.getInstance().saveModel(category);
                        emitter.onNext(category);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(category1 -> showCategoriesPicker());
            LogUtils.d(disposable);
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
            case R.id.action_preview:
                String title = etTitle.getText().toString();
                String content = Objects.requireNonNull(eme.getText()).toString() + " ";
                getVM().getNote().setTitle(title);
                getVM().getNote().setContent(content);
                ContainerActivity.open(NoteViewFragment.class)
                        .put(NoteViewFragment.ARGS_KEY_NOTE, (Serializable) getVM().getNote())
                        .put(NoteViewFragment.ARGS_KEY_IS_PREVIEW, true)
                        .launch(getActivity());
                break;
            case R.id.action_undo:
                eme.undo();
                break;
            case R.id.action_redo:
                eme.redo();
                break;
            case R.id.action_attachment:
                showAttachmentPicker();
                break;
            case R.id.action_notebook:
                NotebookPickerDialog.newInstance().setOnItemSelectedListener((dialog, value, position) -> {
                    getVM().getNote().setParentCode(value.getCode());
                    getVM().getNote().setTreePath(value.getTreePath() + "|" + value.getCode());
                    dialog.dismiss();
                }).show(Objects.requireNonNull(getFragmentManager()), "NOTEBOOK_PICKER");
                break;
            case R.id.action_category:
                showCategoriesPicker();
                break;
            case R.id.action_send:
                title = etTitle.getText().toString();
                content = Objects.requireNonNull(eme.getText()).toString() + " ";
                NoteManager.send(getContext(), title, content, new ArrayList<>());
                break;
            case R.id.action_copy_title:
                title = etTitle.getText().toString();
                NoteManager.copy(Objects.requireNonNull(getActivity()), title);
                ToastUtils.showShort(R.string.note_copied_success);
                break;
            case R.id.action_copy_content:
                content = Objects.requireNonNull(eme.getText()).toString() + " ";
                NoteManager.copy(Objects.requireNonNull(getActivity()), content);
                ToastUtils.showShort(R.string.note_copied_success);
                break;
            case R.id.action_setting_note:
                SettingsActivity.open(SettingsNote.class).launch(getContext());
                break;
            default:
                // noop
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void resolve() {
        String title = etTitle.getText().toString();
        String content = Objects.requireNonNull(eme.getText()).toString();
        getVM().saveOrUpdateNote(title, content);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AttachmentHelper.onActivityResult(this, requestCode, resultCode, data, getVM().getNote());
    }

    @Override
    public void onAttachingFileErrorOccurred(Attachment attachment) {
        ToastUtils.showShort(R.string.text_failed_to_save_attachment);
    }

    @Override
    public void onAttachingFileFinished(Attachment attachment) {
        String title = FileManager.getNameFromUri(Objects.requireNonNull(getContext()), attachment.getUri());
        if (TextUtils.isEmpty(title)) title = getString(R.string.text_attachment);
        if (Constants.MIME_TYPE_IMAGE.equalsIgnoreCase(attachment.getMineType())
                || Constants.MIME_TYPE_SKETCH.equalsIgnoreCase(attachment.getMineType())) {
            eme.useFormat(Format.IMAGE, title, attachment.getUri().toString());
        } else {
            eme.useFormat(Format.LINK, title, attachment.getUri().toString());
        }
    }
}
