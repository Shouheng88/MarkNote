package me.shouheng.notepal.fragment;

import android.app.Activity;
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

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.ContentActivity;
import me.shouheng.notepal.adapter.NotesAdapter;
import me.shouheng.notepal.databinding.FragmentNotesBinding;
import me.shouheng.notepal.dialog.NotebookEditDialog;
import me.shouheng.notepal.dialog.NotebookPickerDialog;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.model.enums.Status;
import me.shouheng.notepal.provider.NotebookStore;
import me.shouheng.notepal.provider.NotesStore;
import me.shouheng.notepal.provider.helper.ArchiveHelper;
import me.shouheng.notepal.provider.helper.NotebookHelper;
import me.shouheng.notepal.provider.helper.TrashHelper;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.widget.tools.CustomItemAnimator;
import me.shouheng.notepal.widget.tools.DividerItemDecoration;


public class NotesFragment extends BaseFragment<FragmentNotesBinding> {

    private final static String ARG_NOTEBOOK = "arg_notebook";
    private final static String ARG_STATUS = "arg_status";

    private final static int REQUEST_NOTE_VIEW = 0x0010;
    private final static int REQUEST_NOTE_EDIT = 0x0011;

    private Status status;
    private Notebook notebook;
    private boolean isTopStack = true;

    private RecyclerView.OnScrollListener scrollListener;

    private NotebookEditDialog dialog;

    private NotesAdapter adapter;

    public static NotesFragment newInstance(@Nullable Notebook notebook, @Nonnull Status status) {
        Bundle args = new Bundle();
        if (notebook != null) args.putSerializable(ARG_NOTEBOOK, notebook);
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
        if (args != null && args.containsKey(ARG_NOTEBOOK)) {
            isTopStack = false;
            notebook = (Notebook) args.get(ARG_NOTEBOOK);
        }
        if (args != null && args.containsKey(ARG_STATUS)) {
            status = (Status) getArguments().get(ARG_STATUS);
        } else {
            throw new IllegalArgumentException("status required");
        }
    }

    private void configToolbar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.drawer_menu_notes);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setSubtitle(notebook == null ? null : notebook.getTitle());
        actionBar.setHomeAsUpIndicator(isTopStack ? R.drawable.ic_menu_white : R.drawable.ic_arrow_back_white_24dp);
    }

    // region Config Notes List
    private void configNotesList() {
        getBinding().ivEmpty.setSubTitle(getEmptySubTitle());

        adapter = new NotesAdapter(getContext(), getMultiItems());
        adapter.setOnItemClickListener((adapter, view, position) -> {
            NotesAdapter.MultiItem item = (NotesAdapter.MultiItem) adapter.getData().get(position);
            if (item.itemType == NotesAdapter.MultiItem.ITEM_TYPE_NOTE) {
                ContentActivity.startNoteViewForResult(NotesFragment.this, item.note, null, REQUEST_NOTE_VIEW);
            } else if (item.itemType == NotesAdapter.MultiItem.ITEM_TYPE_NOTEBOOK) {
                if (getActivity() != null && getActivity() instanceof OnNotesInteractListener) {
                    ((OnNotesInteractListener) getActivity()).onNotebookSelected(item.notebook);
                }
            }
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
    }

    private List<NotesAdapter.MultiItem> getMultiItems() {
        List<NotesAdapter.MultiItem> data = new LinkedList<>();
        for (Object obj : getNotesAndNotebooks()) {
            if (obj instanceof Note) {
                data.add(new NotesAdapter.MultiItem((Note) obj));
            } else if (obj instanceof Notebook) {
                data.add(new NotesAdapter.MultiItem((Notebook) obj));
            }
        }
        return data;
    }

    private List getNotesAndNotebooks() {
        return status == Status.ARCHIVED ?
                ArchiveHelper.getNotebooksAndNotes(getContext(), notebook) :
                status == Status.TRASHED ?
                        TrashHelper.getNotebooksAndNotes(getContext(), notebook) :
                        NotebookHelper.getNotesAndNotebooks(getContext(), notebook);
    }

    private String getEmptySubTitle() {
        switch (status) {
            case NORMAL:
                return getString(R.string.notes_list_empty_sub_normal);
            case TRASHED:
                return getString(R.string.notes_list_empty_sub_trashed);
            case ARCHIVED:
                return getString(R.string.notes_list_empty_sub_archived);
        }
        return getString(R.string.notes_list_empty_sub_normal);
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
                    NotesStore.getInstance(getContext()).update(multiItem.note, Status.TRASHED);
                    reload();
                    break;
                case R.id.action_archive:
                    NotesStore.getInstance(getContext()).update(multiItem.note, Status.ARCHIVED);
                    reload();
                    break;
                case R.id.action_move:
                    moveNote(multiItem.note);
                    break;
                case R.id.action_edit:
                    ContentActivity.startNoteEditForResult(this, multiItem.note, null, REQUEST_NOTE_EDIT);
                    break;
                case R.id.action_move_out:
                    NotesStore.getInstance(getContext()).update(multiItem.note, Status.NORMAL);
                    reload();
                    break;
                case R.id.action_delete:
                    NotesStore.getInstance(getContext()).update(multiItem.note, Status.DELETED);
                    reload();
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
                    NotebookStore.getInstance(getContext()).update(notebook, status, Status.TRASHED);
                    reload();
                    break;
                case R.id.action_archive:
                    NotebookStore.getInstance(getContext()).update(notebook, status, Status.ARCHIVED);
                    reload();
                    break;
                case R.id.action_move:
                    moveNotebook(multiItem.notebook);
                    break;
                case R.id.action_edit:
                    editNotebook(position, multiItem.notebook);
                    break;
                case R.id.action_move_out:
                    NotebookStore.getInstance(getContext()).update(notebook, status, Status.NORMAL);
                    reload();
                    break;
                case R.id.action_delete:
                    showDeleteMsgDialog(notebook, position);
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
                    adapter.notifyItemChanged(position);
                    NotebookStore.getInstance(getContext()).update(notebook);
                });
        dialog.show(getFragmentManager(), "Notebook Editor");
    }

    private void moveNote(final Note note) {
        NotebookPickerDialog.newInstance().setOnItemSelectedListener((dialog, toBook, position) -> {
            if (toBook.getCode() == note.getParentCode()) return;

            note.setParentCode(toBook.getCode());
            note.setTreePath(toBook.getTreePath() + "|" + note.getCode());
            NotesStore.getInstance(getContext()).update(note);
            ToastUtils.makeToast(getContext(), R.string.moved_successfully);

            reload();
            dialog.dismiss();
        }).show(getFragmentManager(), "Notebook picker");
    }

    private void moveNotebook(final Notebook nb) {
        NotebookPickerDialog.newInstance().setOnItemSelectedListener((dialog, toBook, position) -> {
            if (toBook.getCode() == nb.getParentCode()) return;

            NotebookStore.getInstance(getContext()).move(nb, toBook);
            ToastUtils.makeToast(getContext(), R.string.moved_successfully);

            reload();
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

    private void showDeleteMsgDialog(final Notebook notebook, final int position) {
        new MaterialDialog.Builder(getContext())
                .title(R.string.text_warning)
                .content(R.string.msg_when_delete_notebook)
                .positiveText(R.string.text_delete_still)
                .negativeText(R.string.text_give_up)
                .onPositive((materialDialog, dialogAction) -> {
                    NotebookStore.getInstance(getContext()).update(notebook, Status.DELETED);
                    reload();
                })
                .show();
    }
    // endregion

    public void setScrollListener(RecyclerView.OnScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    public void reload() {
        adapter.setNewData(getMultiItems());
    }

    public Notebook getNotebook() {
        return notebook;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                LogUtils.d("onOptionsItemSelected");
                if (isTopStack) {
                    return super.onOptionsItemSelected(item);
                } else {
                    getActivity().onBackPressed();
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
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public interface OnNotesInteractListener {
        void onNotebookSelected(Notebook notebook);
        void onActivityAttached(boolean isTopStack);
    }
}
