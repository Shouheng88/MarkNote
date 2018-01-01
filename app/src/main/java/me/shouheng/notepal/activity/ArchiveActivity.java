package me.shouheng.notepal.activity;

import android.support.v4.app.Fragment;

import me.shouheng.notepal.R;
import me.shouheng.notepal.fragment.NotesFragment;
import me.shouheng.notepal.fragment.SnaggingsFragment;
import me.shouheng.notepal.model.enums.Status;

public class ArchiveActivity extends BaseListActivity {

    @Override
    protected CharSequence getActionbarTitle() {
        return getString(R.string.drawer_menu_archive);
    }

    @Override
    protected Fragment getNotesFragment() {
        return NotesFragment.newInstance(null, Status.ARCHIVED);
    }

    @Override
    protected Fragment getSnaggingsFragment() {
        return SnaggingsFragment.newInstance(Status.ARCHIVED);
    }
}
