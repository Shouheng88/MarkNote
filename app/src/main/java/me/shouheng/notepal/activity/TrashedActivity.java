package me.shouheng.notepal.activity;

import android.support.v4.app.Fragment;

import me.shouheng.notepal.R;
import me.shouheng.notepal.fragment.NotesFragment;
import me.shouheng.notepal.fragment.SnaggingsFragment;
import me.shouheng.notepal.model.enums.Status;

/**
 * Created by wangshouheng on 2017/10/10.*/
public class TrashedActivity extends BaseListActivity {

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
}
