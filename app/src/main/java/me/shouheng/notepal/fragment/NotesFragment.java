package me.shouheng.notepal.fragment;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Collections;

import javax.annotation.Nonnull;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.ContentActivity;
import me.shouheng.notepal.adapter.NotesAdapter;
import me.shouheng.notepal.databinding.FragmentNotesBinding;
import me.shouheng.notepal.dialog.NotebookEditDialog;
import me.shouheng.notepal.dialog.picker.NotebookPickerDialog;
import me.shouheng.notepal.fragment.base.BaseFragment;
import me.shouheng.notepal.model.Category;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.model.enums.Status;
import me.shouheng.notepal.util.AppWidgetUtils;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.PreferencesUtils;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.viewmodel.NoteViewModel;
import me.shouheng.notepal.viewmodel.NotebookViewModel;
import me.shouheng.notepal.widget.tools.CustomItemAnimator;
import me.shouheng.notepal.widget.tools.DividerItemDecoration;


public class NotesFragment extends BaseFragment<FragmentNotesBinding> {

    private final static String ARG_NOTEBOOK = "arg_notebook";
    private final static String ARG_CATEGORY = "arg_category";
    private final static String ARG_STATUS = "arg_status";

    private final static int REQUEST_NOTE_VIEW = 0x0010;
    private final static int REQUEST_NOTE_EDIT = 0x0011;

    private Status status;
    private Notebook notebook;
    private Category category;
    private boolean isTopStack = true;

    private RecyclerView.OnScrollListener scrollListener;

    private NotebookEditDialog dialog;

    private NotesAdapter adapter;
    private NoteViewModel noteViewModel;
    private NotebookViewModel notebookViewModel;

    public static NotesFragment newInstance(@Nonnull Status status) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_STATUS, status);
        NotesFragment fragment = new NotesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static NotesFragment newInstance(@Nonnull Notebook notebook, @Nonnull Status status) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_NOTEBOOK, notebook);
        args.putSerializable(ARG_STATUS, status);
        NotesFragment fragment = new NotesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static NotesFragment newInstance(@Nonnull Category category, @Nonnull Status status) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CATEGORY, category);
        args.putSerializable(ARG_STATUS, status);
        NotesFragment fragment = new NotesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_notes;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        handleArguments();

        configToolbar();

        configNotesList();
    }

    private void handleArguments() {
        Bundle args = getArguments();
        if (args == null) return;
        if (args.containsKey(ARG_NOTEBOOK)) {
            isTopStack = false;
            notebook = (Notebook) args.get(ARG_NOTEBOOK);
        }
        if (args.containsKey(ARG_CATEGORY)) {
            isTopStack = false;
            category = (Category) args.get(ARG_CATEGORY);
        }
        if (args.containsKey(ARG_STATUS)) {
            status = (Status) getArguments().get(ARG_STATUS);
        } else {
            throw new IllegalArgumentException("status required");
        }
    }

    private void configToolbar() {
        if (getActivity() != null) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(R.string.drawer_menu_notes);
                actionBar.setDisplayHomeAsUpEnabled(true);
                String subTitle = notebook != null ? notebook.getTitle() : category != null ? category.getName() : null;
                actionBar.setSubtitle(subTitle);
                actionBar.setHomeAsUpIndicator(isTopStack ? R.drawable.ic_menu_white : R.drawable.ic_arrow_back_white_24dp);
            }
        }
    }

    // region Config Notes List
    private void configNotesList() {
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        notebookViewModel = ViewModelProviders.of(this).get(NotebookViewModel.class);

        getBinding().ivEmpty.setSubTitle(noteViewModel.getEmptySubTitle(status));

        adapter = new NotesAdapter(getContext(), Collections.emptyList());
        adapter.setOnItemClickListener((adapter, view, position) -> {
            NotesAdapter.MultiItem item = (NotesAdapter.MultiItem) adapter.getData().get(position);
            if (item.itemType == NotesAdapter.MultiItem.ITEM_TYPE_NOTE) {
                ContentActivity.viewNote(NotesFragment.this, item.note, REQUEST_NOTE_VIEW);
            } else if (item.itemType == NotesAdapter.MultiItem.ITEM_TYPE_NOTEBOOK) {
                if (getActivity() != null && getActivity() instanceof OnNotesInteractListener) {
                    ((OnNotesInteractListener) getActivity()).onNotebookSelected(item.notebook);
                }
            }
        });
        adapter.setOnItemLongClickListener((adapter, view, position) -> {
            NotesAdapter.MultiItem item = (NotesAdapter.MultiItem) adapter.getData().get(position);
            if (item.itemType == NotesAdapter.MultiItem.ITEM_TYPE_NOTE) {
                popNoteMenu(view, item);
            } else if (item.itemType == NotesAdapter.MultiItem.ITEM_TYPE_NOTEBOOK) {
                popNotebookMenu(view, item, position);
            }
            return true;
        });
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            NotesAdapter.MultiItem item = (NotesAdapter.MultiItem) adapter.getData().get(position);
            switch (view.getId()) {
                case R.id.iv_more:
                    if (item.itemType == NotesAdapter.MultiItem.ITEM_TYPE_NOTE) {
                        popNoteMenu(view, item);
                    } else if (item.itemType == NotesAdapter.MultiItem.ITEM_TYPE_NOTEBOOK) {
                        popNotebookMenu(view, item, position);
                    }
                    break;
            }
        });

        getBinding().rvNotes.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST, isDarkTheme()));
        getBinding().rvNotes.setItemAnimator(new CustomItemAnimator());
        getBinding().rvNotes.setLayoutManager(new LinearLayoutManager(getContext()));
        if (scrollListener != null) getBinding().rvNotes.addOnScrollListener(scrollListener);
        getBinding().rvNotes.setEmptyView(getBinding().ivEmpty);
        getBinding().rvNotes.setAdapter(adapter);

        getBinding().fastscroller.setRecyclerView(getBinding().rvNotes);

        reload();
    }
    // endregion

    // region Note & Notebook Pop Menus
    private void popNoteMenu(View v, NotesAdapter.MultiItem multiItem) {
        PopupMenu popupM = new PopupMenu(getContext(), v);
        popupM.inflate(R.menu.pop_menu);
        configPopMenu(popupM);
        popupM.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.action_trash:
                    update(multiItem.note, Status.TRASHED);
                    break;
                case R.id.action_archive:
                    update(multiItem.note, Status.ARCHIVED);
                    break;
                case R.id.action_move:
                    moveNote(multiItem.note);
                    break;
                case R.id.action_edit:
                    ContentActivity.editNote(this, multiItem.note, REQUEST_NOTE_EDIT);
                    break;
                case R.id.action_move_out:
                    update(multiItem.note, Status.NORMAL);
                    break;
                case R.id.action_delete:
                    update(multiItem.note, Status.DELETED);
                    break;
            }
            return true;
        });
        popupM.show();
    }

    private void popNotebookMenu(View v, NotesAdapter.MultiItem multiItem, int position) {
        PopupMenu popupM = new PopupMenu(getContext(), v);
        popupM.inflate(R.menu.pop_menu);
        configPopMenu(popupM);
        popupM.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.action_trash:
                    update(multiItem.notebook, status, Status.TRASHED);
                    break;
                case R.id.action_archive:
                    update(multiItem.notebook, status, Status.ARCHIVED);
                    break;
                case R.id.action_move:
                    moveNotebook(multiItem.notebook);
                    break;
                case R.id.action_edit:
                    editNotebook(position, multiItem.notebook);
                    break;
                case R.id.action_move_out:
                    update(multiItem.notebook, status, Status.NORMAL);
                    break;
                case R.id.action_delete:
                    showDeleteMsgDialog(multiItem.notebook, position);
                    break;
            }
            return true;
        });
        popupM.show();
    }

    private void editNotebook(final int position, final Notebook notebook) {
        dialog = NotebookEditDialog.newInstance(getContext(), notebook,
                (categoryName, notebookColor) -> {
                    notebook.setTitle(categoryName);
                    notebook.setColor(notebookColor);
                    update(notebook, position);
                });
        dialog.show(getFragmentManager(), "Notebook Editor");
    }

    private void moveNote(final Note note) {
        NotebookPickerDialog.newInstance().setOnItemSelectedListener((dialog, toBook, position) -> {
            if (toBook.getCode() == note.getParentCode()) return;
            note.setParentCode(toBook.getCode());
            note.setTreePath(toBook.getTreePath() + "|" + note.getCode());
            update(note);
            dialog.dismiss();
        }).show(getFragmentManager(), "Notebook picker");
    }

    private void moveNotebook(final Notebook nb) {
        NotebookPickerDialog.newInstance().setOnItemSelectedListener((dialog, toBook, position) -> {
            if (toBook.getCode() == nb.getParentCode()) return;
            move(nb, toBook);
            dialog.dismiss();
        }).show(getFragmentManager(), "Notebook picker");
    }

    public void setSelectedColor(int color) {
        if (dialog != null) dialog.updateUIBySelectedColor(color);
    }

    private void configPopMenu(PopupMenu popupMenu) {
        popupMenu.getMenu().findItem(R.id.action_move_out).setVisible(status == Status.ARCHIVED || status == Status.TRASHED);
        popupMenu.getMenu().findItem(R.id.action_edit).setVisible(status == Status.ARCHIVED || status == Status.NORMAL);
        popupMenu.getMenu().findItem(R.id.action_move).setVisible(status == Status.NORMAL);
        popupMenu.getMenu().findItem(R.id.action_trash).setVisible(status == Status.NORMAL || status == Status.ARCHIVED);
        popupMenu.getMenu().findItem(R.id.action_archive).setVisible(status == Status.NORMAL);
        popupMenu.getMenu().findItem(R.id.action_delete).setVisible(status == Status.TRASHED);
    }

    private void showDeleteMsgDialog(final Notebook nb, final int position) {
        new MaterialDialog.Builder(getContext())
                .title(R.string.text_warning)
                .content(R.string.msg_when_delete_notebook)
                .positiveText(R.string.text_delete_still)
                .negativeText(R.string.text_give_up)
                .onPositive((materialDialog, dialogAction) -> update(nb, status, Status.DELETED))
                .show();
    }
    // endregion

    public void setScrollListener(RecyclerView.OnScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    // region ViewModel interaction
    public void reload() {
        if (getActivity() instanceof OnNotesInteractListener) {
            ((OnNotesInteractListener) getActivity()).onNoteLoadStateChanged(
                    me.shouheng.notepal.model.data.Status.LOADING);
        }

        noteViewModel.getMultiItems(category, status, notebook).observe(this, multiItemResource -> {
            if (multiItemResource == null) {
                ToastUtils.makeToast(R.string.text_failed_to_load_data);
                return;
            }
            if (getActivity() instanceof OnNotesInteractListener) {
                ((OnNotesInteractListener) getActivity()).onNoteLoadStateChanged(multiItemResource.status);
            }
            switch (multiItemResource.status) {
                case SUCCESS:
                    adapter.setNewData(multiItemResource.data);
                    break;
                case LOADING:
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed_to_load_data);
                    break;
            }
        });
    }

    private void update(Note note) {
        noteViewModel.update(note).observe(this, noteResource -> {
            if (noteResource == null) {
                ToastUtils.makeToast(R.string.text_failed_to_modify_data);
                return;
            }
            switch (noteResource.status) {
                case SUCCESS:
                    ToastUtils.makeToast(R.string.moved_successfully);
                    reload();
                    notifyDataChanged();
                    break;
                case LOADING:
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed_to_modify_data);
                    break;
            }
        });
    }

    private void update(Note note, Status toStatus) {
        noteViewModel.update(note, toStatus).observe(this, noteResource -> {
            if (noteResource == null) {
                ToastUtils.makeToast(R.string.text_failed_to_modify_data);
                return;
            }
            switch (noteResource.status) {
                case SUCCESS:
                    reload();
                    notifyDataChanged();
                    break;
                case LOADING:
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed_to_modify_data);
                    break;
            }
        });
    }

    private void update(Notebook notebook, int position) {
        notebookViewModel.update(notebook).observe(this, notebookResource -> {
            if (notebookResource == null) {
                ToastUtils.makeToast(R.string.text_failed_to_modify_data);
                return;
            }
            switch (notebookResource.status) {
                case SUCCESS:
                    adapter.notifyItemChanged(position);
                    ToastUtils.makeToast(R.string.moved_successfully);
                    break;
                case LOADING:
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed_to_modify_data);
                    break;
            }
        });
    }

    private void move(Notebook notebook, Notebook toNotebook) {
        notebookViewModel.move(notebook, toNotebook).observe(this, notebookResource -> {
            if (notebookResource == null) {
                ToastUtils.makeToast(R.string.text_failed_to_modify_data);
                return;
            }
            switch (notebookResource.status) {
                case SUCCESS:
                    ToastUtils.makeToast(R.string.moved_successfully);
                    reload();
                    notifyDataChanged();
                    break;
                case LOADING:
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed_to_modify_data);
                    break;
            }
        });
    }

    private void update(Notebook notebook, Status fromStatus, Status toStatus) {
        notebookViewModel.update(notebook, fromStatus, toStatus).observe(this, notebookResource -> {
            if (notebookResource == null) {
                ToastUtils.makeToast(R.string.text_failed_to_modify_data);
                return;
            }
            switch (notebookResource.status) {
                case SUCCESS:
                    reload();
                    notifyDataChanged();
                    break;
                case LOADING:
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed_to_modify_data);
                    break;
            }
        });
    }
    // endregion

    private void notifyDataChanged() {

        /*
         * Notify app widget that the list is changed. */
        AppWidgetUtils.notifyAppWidgets(getContext());

        /*
         * Notify the attached activity that the list is changed. */
        if (getActivity() != null && getActivity() instanceof OnNotesInteractListener) {
            ((OnNotesInteractListener) getActivity()).onNoteDataChanged();
        }
    }

    public Notebook getNotebook() {
        return notebook;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isTopStack() {
        return isTopStack;
    }

    @Override
    public void onActivityCreated(@android.support.annotation.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        if (!isTopStack && getActivity() != null && getActivity() instanceof OnNotesInteractListener) {
            ((OnNotesInteractListener) getActivity()).onActivityAttached(isTopStack);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.capture, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (PreferencesUtils.getInstance(getContext()).isNoteExpanded()) {
            // disable list capture when the note list is expanded
            menu.findItem(R.id.action_capture).setVisible(false);
        } else {
            menu.findItem(R.id.action_capture).setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                LogUtils.d("onOptionsItemSelected");
                if (isTopStack) {
                    return super.onOptionsItemSelected(item);
                } else {
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                    return true;
                }
            case R.id.action_capture:
                createScreenCapture(getBinding().rvNotes);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity() instanceof OnNotesInteractListener) {
            ((OnNotesInteractListener) getActivity()).onActivityAttached(isTopStack);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_NOTE_VIEW:
                case REQUEST_NOTE_EDIT:
                    reload();
                    notifyDataChanged();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public interface OnNotesInteractListener {

        /**
         * On the notebook is selected, this method will be called.
         *
         * @param notebook notebook selected */
        default void onNotebookSelected(Notebook notebook){}

        /**
         * When the fragment is attached to the activity, will call this method to lock the drawer
         *
         * @param isTopStack whether current fragment is the top stack of all fragments */
        default void onActivityAttached(boolean isTopStack){}

        default void onNoteDataChanged(){}

        default void onNoteLoadStateChanged(me.shouheng.notepal.model.data.Status status) {}
    }
}
