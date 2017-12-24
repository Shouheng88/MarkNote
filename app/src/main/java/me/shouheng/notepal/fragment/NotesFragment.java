package me.shouheng.notepal.fragment;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import javax.annotation.Nullable;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.MainActivity;
import me.shouheng.notepal.databinding.FragmentNotesBinding;
import me.shouheng.notepal.model.Notebook;


public class NotesFragment extends CommonFragment<FragmentNotesBinding> {

    private final static String ARG_NOTEBOOK = "arg_notebook";

    private Notebook notebook;
    private boolean isTopStack = true;

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
    }

    private void configToolbar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.drawer_menu_notes);
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (notebook != null) actionBar.setSubtitle(notebook.getTitle());
        if (isTopStack) actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
    }

    private void handleArguments() {
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_NOTEBOOK)) {
            isTopStack = false;
            notebook = (Notebook) args.get(ARG_NOTEBOOK);
        }
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
            case R.id.action_capture:
//                createScreenCapture(mRecyclerView);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
