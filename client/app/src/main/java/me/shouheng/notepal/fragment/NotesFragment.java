package me.shouheng.notepal.fragment;

import android.app.Activity;
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
import android.widget.TextView;

import java.io.Serializable;
import java.util.Collections;
import java.util.Objects;

import javax.annotation.Nullable;

import me.shouheng.commons.activity.ContainerActivity;
import me.shouheng.commons.event.RxMessage;
import me.shouheng.commons.fragment.CustomFragment;
import me.shouheng.commons.helper.FragmentHelper;
import me.shouheng.commons.widget.recycler.DividerItemDecoration;
import me.shouheng.data.entity.Category;
import me.shouheng.data.entity.Note;
import me.shouheng.data.entity.Notebook;
import me.shouheng.data.model.enums.Status;
import me.shouheng.mvvm.base.anno.FragmentConfiguration;
import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.MainActivity;
import me.shouheng.notepal.adapter.NotesAdapter;
import me.shouheng.notepal.databinding.FragmentNotesBinding;
import me.shouheng.notepal.dialog.NotebookEditDialog;
import me.shouheng.notepal.dialog.picker.NotebookPickerDialog;
import me.shouheng.notepal.util.AppWidgetUtils;
import me.shouheng.notepal.vm.NotesViewModel;
import me.shouheng.utils.app.ResUtils;
import me.shouheng.utils.store.SPUtils;
import me.shouheng.utils.ui.ToastUtils;

/**
 * Notes list fragment, used to show the list of notes according to the params.
 *
 * Created by WngShhng (shouheng2015@gmail.com) and
 * refactored by WngShhng (shouheng2015@gmail.com) on 2018/12/2.
 */
@FragmentConfiguration(layoutResId = R.layout.fragment_notes)
public class NotesFragment extends CustomFragment<FragmentNotesBinding, NotesViewModel> {

    /**
     * Argument key for notebook, null if showing the top level notebook
     * or showing the category notes list.
     */
    public static final String ARGS_KEY_NOTEBOOK = "__argument_key_notebook";

    /**
     * Argument key for category, null if showing the notebook.
     */
    public static final String ARGS_KEY_CATEGORY = "__argument_key_category";

    /**
     * REQUIRED: Argument key for status, Might be one of {@link Status#ARCHIVED},
     * {@link Status#DELETED}, {@link Status#NORMAL} or {@link Status#TRASHED}
     */
    public static final String ARGS_KEY_STATUS = "__argument_key_status";

    private RecyclerView.OnScrollListener scrollListener;
    private NotesAdapter adapter;

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        /* Get the view model */
        handleArguments();

        customToolbar();

        /* Config the notes list view. */
        adapter = new NotesAdapter(getContext(), Collections.emptyList());
        adapter.setOnItemClickListener((quickAdapter, view, position) -> {
            NotesAdapter.MultiItem item = (NotesAdapter.MultiItem) quickAdapter.getData().get(position);
            if (item.itemType == NotesAdapter.MultiItem.ITEM_TYPE_NOTE) {
                ContainerActivity.open(NoteViewFragment.class)
                        .put(NoteViewFragment.ARGS_KEY_NOTE, (Serializable) item.note)
                        .put(NoteViewFragment.ARGS_KEY_IS_PREVIEW, false)
                        .launch(getContext());
            } else if (item.itemType == NotesAdapter.MultiItem.ITEM_TYPE_NOTEBOOK) {
                if (getActivity() instanceof OnNotesInteractListener) {
                    ((OnNotesInteractListener) getActivity()).onNotebookSelected(item.notebook);
                }
            }
        });
        adapter.setOnItemLongClickListener((quickAdapter, view, position) -> {
            NotesAdapter.MultiItem item = (NotesAdapter.MultiItem) quickAdapter.getData().get(position);
            if (item.itemType == NotesAdapter.MultiItem.ITEM_TYPE_NOTE) {
                popNoteMenu(view, item);
            } else if (item.itemType == NotesAdapter.MultiItem.ITEM_TYPE_NOTEBOOK) {
                popNotebookMenu(view, item, position);
            }
            return true;
        });
        adapter.setOnItemChildClickListener((quickAdapter, view, position) -> {
            NotesAdapter.MultiItem item = (NotesAdapter.MultiItem) quickAdapter.getData().get(position);
            if (view.getId() == R.id.iv_more) {
                if (item.itemType == NotesAdapter.MultiItem.ITEM_TYPE_NOTE) {
                    popNoteMenu(view, item);
                } else if (item.itemType == NotesAdapter.MultiItem.ITEM_TYPE_NOTEBOOK) {
                    popNotebookMenu(view, item, position);
                }
            }
        });
        getBinding().rvNotes.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()),
                DividerItemDecoration.VERTICAL_LIST, isDarkTheme()));
        getBinding().rvNotes.setLayoutManager(new LinearLayoutManager(getContext()));
        if (scrollListener != null) getBinding().rvNotes.addOnScrollListener(scrollListener);
        ((TextView) getBinding().ev.findViewById(R.id.tv_empty_detail)).setText(getVM().getEmptySubTitle());
        getBinding().rvNotes.setEmptyView(getBinding().ev);
        getBinding().rvNotes.setAdapter(adapter);

        addSubscriptions();

        getVM().fetchMultiItems();
    }

    private void handleArguments() {
        Bundle args = getArguments();
        assert args != null;
        if (args.containsKey(ARGS_KEY_NOTEBOOK)) {
            Notebook notebook = (Notebook) args.get(ARGS_KEY_NOTEBOOK);
            getVM().setNotebook(notebook);
            getVM().setTopStack(false);
        }
        if (args.containsKey(ARGS_KEY_CATEGORY)) {
            Category category = (Category) args.get(ARGS_KEY_CATEGORY);
            getVM().setCategory(category);
            getVM().setTopStack(false);
        }
        if (args.containsKey(ARGS_KEY_STATUS)) {
            Status status = (Status) getArguments().get(ARGS_KEY_STATUS);
            getVM().setStatus(status);
        } else {
            throw new IllegalArgumentException("The status is required!");
        }
    }

    private void customToolbar() {
        Activity activity = getActivity();
        if (activity == null) return;
        ActionBar ab = ((AppCompatActivity) activity).getSupportActionBar();
        if (ab != null) {
            ab.setTitle(getVM().getTitle());
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setSubtitle(getVM().getSubTitle());
            ab.setHomeAsUpIndicator(getVM().getHomeAsUpIndicator());
        }
    }

    private void addSubscriptions() {
        getVM().getMutableLiveData().observe(this, resources -> {
            assert resources != null;
            switch (resources.status) {
                case SUCCESS:
                    adapter.setNewData(resources.data);
                    getBinding().ev.showEmpty();
                    break;
                case LOADING:
                    getBinding().ev.showLoading();
                    break;
                case FAILED:
                    ToastUtils.showShort(R.string.text_failed);
                    getBinding().ev.showEmpty();
                    break;
            }
        });
        getVM().getNoteUpdateLiveData().observe(this, resources -> {
            assert resources != null;
            switch (resources.status) {
                case SUCCESS:
                    notifyDataChanged();
                    break;
                case LOADING:
                    break;
                case FAILED:
                    ToastUtils.showShort(R.string.text_failed_to_modify_data);
                    break;
            }
        });
        getVM().getNotebookUpdateLiveData().observe(this, resources -> {
            assert resources != null;
            switch (resources.status) {
                case SUCCESS:
                    notifyDataChanged();
                    break;
                case LOADING:
                    break;
                case FAILED:
                    ToastUtils.showShort(R.string.text_failed_to_modify_data);
                    break;
            }
        });
        addSubscription(RxMessage.class, RxMessage.CODE_NOTE_DATA_CHANGED, rxMessage -> loadNotesAndNotebooks());
    }

    private void popNoteMenu(View v, NotesAdapter.MultiItem multiItem) {
        PopupMenu popupM = new PopupMenu(Objects.requireNonNull(getContext()), v);
        popupM.inflate(R.menu.pop_menu);
        getVM().configPopMenu(popupM);
        popupM.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.action_trash:
                    getVM().updateNoteStatus(multiItem.note, Status.TRASHED);
                    break;
                case R.id.action_archive:
                    getVM().updateNoteStatus(multiItem.note, Status.ARCHIVED);
                    break;
                case R.id.action_move:
                    Note note = multiItem.note;
                    NotebookPickerDialog.newInstance().setOnItemSelectedListener((dialog, toBook, position) -> {
                        if (toBook.getCode() == note.getParentCode()) return;
                        note.setParentCode(toBook.getCode());
                        note.setTreePath(toBook.getTreePath() + "|" + note.getCode());
                        getVM().updateNote(note);
                        dialog.dismiss();
                    }).show(getChildFragmentManager(), "NOTEBOOK PICKER");
                    break;
                case R.id.action_edit:
                    FragmentHelper.open(NoteFragment.class)
                            .put(NoteFragment.ARGS_KEY_NOTE, (Serializable) multiItem.note)
                            .launch(getContext());
                    break;
                case R.id.action_move_out:
                    getVM().updateNoteStatus(multiItem.note, Status.NORMAL);
                    break;
                case R.id.action_delete:
                    getVM().updateNoteStatus(multiItem.note, Status.DELETED);
                    break;
                default:
                    // noop
            }
            return true;
        });
        popupM.show();
    }

    private void popNotebookMenu(View v, NotesAdapter.MultiItem multiItem, int position) {
        PopupMenu popupM = new PopupMenu(Objects.requireNonNull(getContext()), v);
        popupM.inflate(R.menu.pop_menu);
        getVM().configPopMenu(popupM);
        popupM.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.action_trash:
                    getVM().updateNotebookStatus(multiItem.notebook, Status.TRASHED);
                    break;
                case R.id.action_archive:
                    getVM().updateNotebookStatus(multiItem.notebook, Status.ARCHIVED);
                    break;
                case R.id.action_move:
                    moveNotebook(multiItem.notebook);
                    break;
                case R.id.action_edit:
                    Notebook notebook = multiItem.notebook;
                    NotebookEditDialog dialog = NotebookEditDialog.newInstance(notebook,
                            (categoryName, notebookColor) -> {
                                notebook.setTitle(categoryName);
                                notebook.setColor(notebookColor);
                                getVM().setNotebookUpdatePosition(position);
                                getVM().updateNotebook(notebook);
                            });
                    dialog.show(getChildFragmentManager(), "NOTEBOOK EDITOR");
                    break;
                case R.id.action_move_out:
                    getVM().updateNotebookStatus(multiItem.notebook, Status.NORMAL);
                    break;
                case R.id.action_delete:
                    getVM().updateNotebookStatus(multiItem.notebook, Status.DELETED);
                    break;
                default:
                    // noop
            }
            return true;
        });
        popupM.show();
    }

    /**
     * Need to ignore:
     * 1. The notebook to move to is the selected notebooks's parent;
     * 2. The notebook to move to is the selected notebook;
     * 3. The notebook to move to is the selected notebook's child.
     */
    private void moveNotebook(final Notebook nb) {
        NotebookPickerDialog.newInstance().setOnItemSelectedListener((dialog, toBook, position) -> {
            if (toBook.getCode() == nb.getParentCode()
                    || toBook.getCode() == nb.getCode()
                    || toBook.getTreePath().contains(nb.getTreePath())) return;
            getVM().moveNotebook(nb, toBook);
            dialog.dismiss();
        }).show(getChildFragmentManager(), "NOTEBOOK PICKER");
    }

    /**
     * Set the scroll listener to the notes list.
     *
     * @param scrollListener the scroll listener
     */
    public void setScrollListener(RecyclerView.OnScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    /**
     * Get the notebook of the notes list
     *
     * @return notebook or null
     */
    @Nullable
    public Notebook getNotebook() {
        return getVM().getNotebook();
    }

    /**
     * Get the category of notes list
     *
     * @return category or null
     */
    @Nullable
    public Category getCategory() {
        return getVM().getCategory();
    }

    /**
     * Is the fragment top.
     *
     * @return is top
     * @see NotesViewModel#isTopStack
     * @see MainActivity#onBackPressed()
     */
    public boolean isTopStack() {
        return getVM().isTopStack();
    }

    /**
     * Load notes and notebooks
     */
    private void loadNotesAndNotebooks() {
        getVM().fetchMultiItems();
    }

    private void notifyDataChanged() {
        /* Send the note change broadcast. */
        postEvent(new RxMessage(RxMessage.CODE_NOTE_DATA_CHANGED, null));
        /* Notify app widget that the list is changed. */
        AppWidgetUtils.notifyAppWidgets(getContext());
    }

    @Override
    public void onActivityCreated(@android.support.annotation.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        Activity activity = getActivity();
        if (!getVM().isTopStack() && activity instanceof OnNotesInteractListener) {
            ((OnNotesInteractListener) activity).onActivityAttached(getVM().isTopStack());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.capture, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        boolean isExpanded = SPUtils.getInstance().getBoolean(ResUtils.getString(R.string.key_note_expanded_note), true);
        menu.findItem(R.id.action_capture).setVisible(!isExpanded);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getVM().isTopStack()) {
                return super.onOptionsItemSelected(item);
            } else {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        Activity activity = getActivity();
        if (activity instanceof OnNotesInteractListener) {
            ((OnNotesInteractListener) getActivity()).onActivityAttached(getVM().isTopStack());
        }
        customToolbar();
    }

    public interface OnNotesInteractListener {

        /**
         * On the notebook is selected, this method will be called.
         *
         * @param notebook notebook selected
         */
        default void onNotebookSelected(Notebook notebook) {

        }

        /**
         * When the fragment is attached to the activity, will call this method to lock the drawer
         *
         * @param isTopStack whether current fragment is the top stack of all fragments
         */
        default void onActivityAttached(boolean isTopStack) {

        }
    }
}
