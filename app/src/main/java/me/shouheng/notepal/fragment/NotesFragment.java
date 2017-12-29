package me.shouheng.notepal.fragment;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.ContentActivity;
import me.shouheng.notepal.activity.MainActivity;
import me.shouheng.notepal.adapter.NotesAdapter;
import me.shouheng.notepal.databinding.FragmentNotesBinding;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.provider.helper.NotebookHelper;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.widget.tools.CustomItemAnimator;
import me.shouheng.notepal.widget.tools.DividerItemDecoration;


public class NotesFragment extends CommonFragment<FragmentNotesBinding> {

    private final static String ARG_NOTEBOOK = "arg_notebook";

    private final static int REQUEST_CODE_FOR_NOTE_VIEW = 0x0010;

    private Notebook notebook;
    private boolean isTopStack = true;

    private RecyclerView.OnScrollListener scrollListener;

    private NotesAdapter adapter;

    public static NotesFragment newInstance(@Nullable Notebook notebook) {
        Bundle args = new Bundle();
        if (notebook != null) args.putSerializable(ARG_NOTEBOOK, notebook);
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
    }

    private void configToolbar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.drawer_menu_notes);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setSubtitle(notebook == null ? null : notebook.getTitle());
        actionBar.setHomeAsUpIndicator(isTopStack ? R.drawable.ic_menu_white : R.drawable.ic_arrow_back_white_24dp);
    }

    private void configNotesList() {
        adapter = new NotesAdapter(getContext(), getMultiItems());
        adapter.setOnItemClickListener((adapter, view, position) -> {
            NotesAdapter.MultiItem item = (NotesAdapter.MultiItem) adapter.getData().get(position);
            if (item.itemType == NotesAdapter.MultiItem.ITEM_TYPE_NOTE) {
                ContentActivity.startNoteViewForResult(NotesFragment.this, item.note, null, REQUEST_CODE_FOR_NOTE_VIEW);
            } else if (item.itemType == NotesAdapter.MultiItem.ITEM_TYPE_NOTEBOOK) {
                if (getActivity() != null && getActivity() instanceof OnNotebookSelectedListener) {
                    ((OnNotebookSelectedListener) getActivity()).onNotebookSelected(item.notebook);
                }
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
        List data = new LinkedList();
        data.addAll(NotebookHelper.getNotebooks(getContext(), notebook));
        data.addAll(NotebookHelper.getNotes(getContext(), notebook));
        return data;
    }

    public void setScrollListener(RecyclerView.OnScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    public void reload() {
        adapter.setNewData(getMultiItems());
    }

    @Override
    public void onActivityCreated(@android.support.annotation.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        if (!isTopStack && getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setDrawerLayoutLocked(true);
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
//                createScreenCapture(mRecyclerView);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public interface OnNotebookSelectedListener {
        void onNotebookSelected(Notebook notebook);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setDrawerLayoutLocked(!isTopStack);
        }
    }
}
