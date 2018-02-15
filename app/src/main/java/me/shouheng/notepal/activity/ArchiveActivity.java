package me.shouheng.notepal.activity;

import android.support.v4.app.Fragment;

import me.shouheng.notepal.R;
import me.shouheng.notepal.fragment.NotesFragment;
import me.shouheng.notepal.fragment.SnaggingsFragment;
import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.model.enums.Status;
import me.shouheng.notepal.util.FragmentHelper;

public class ArchiveActivity extends BaseListActivity implements
        NotesFragment.OnNotesInteractListener,
        SnaggingsFragment.OnSnagginsInteractListener{

    @Override
    protected CharSequence getActionbarTitle() {
        return getString(R.string.drawer_menu_archive);
    }

    @Override
    protected Fragment getNotesFragment() {
        return NotesFragment.newInstance(Status.ARCHIVED);
    }

    @Override
    protected Fragment getSnaggingsFragment() {
        return SnaggingsFragment.newInstance(Status.ARCHIVED);
    }

    @Override
    public void onNotebookSelected(Notebook notebook) {
        FragmentHelper.replaceWithCallback(
                this,
                NotesFragment.newInstance(notebook, Status.ARCHIVED),
                R.id.fragment_container);
    }

    @Override
    public void onActivityAttached(boolean isTopStack) {
        setDrawerLayoutLocked(!isTopStack);
    }

    @Override
    public void onNoteListChanged() {
        setListChanged(true);
    }

    @Override
    public void onSnaggingListChanged() {
        setListChanged(true);
    }
}
