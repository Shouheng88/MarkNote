package me.shouheng.notepal.activity;

import android.support.v4.app.Fragment;

import me.shouheng.notepal.R;
import me.shouheng.notepal.fragment.NotesFragment;
import me.shouheng.notepal.fragment.SnaggingsFragment;
import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.model.enums.Status;
import me.shouheng.notepal.util.FragmentHelper;

/**
 * Created by wangshouheng on 2017/10/10.*/
public class TrashedActivity extends BaseListActivity implements NotesFragment.OnNotesInteractListener {

    @Override
    protected CharSequence getActionbarTitle() {
        return getString(R.string.drawer_menu_trash);
    }

    @Override
    protected Fragment getNotesFragment() {
        return NotesFragment.newInstance(null, Status.TRASHED);
    }

    @Override
    protected Fragment getSnaggingsFragment() {
        return SnaggingsFragment.newInstance(Status.TRASHED);
    }

    @Override
    public void onNotebookSelected(Notebook notebook) {
        NotesFragment notesFragment = NotesFragment.newInstance(notebook, Status.TRASHED);
        FragmentHelper.replaceWithCallback(this, notesFragment, R.id.fragment_container);
    }

    @Override
    public void onActivityAttached(boolean isTopStack) {
        setDrawerLayoutLocked(!isTopStack);
    }
}
