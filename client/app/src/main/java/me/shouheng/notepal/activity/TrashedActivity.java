package me.shouheng.notepal.activity;

import android.support.v4.app.Fragment;

import me.shouheng.commons.helper.FragmentHelper;
import me.shouheng.data.entity.Category;
import me.shouheng.data.entity.Notebook;
import me.shouheng.data.model.enums.Status;
import me.shouheng.notepal.R;
import me.shouheng.notepal.fragment.CategoriesFragment;
import me.shouheng.notepal.fragment.NotesFragment;

/**
 * Created by wangshouheng on 2017/10/10.*/
public class TrashedActivity extends BaseListActivity {

    @Override
    protected CharSequence getActionbarTitle() {
        return getString(R.string.drawer_menu_trash);
    }

    @Override
    protected int getHeaderDrawable() {
        return R.drawable.ic_trash_black;
    }

    @Override
    protected Fragment getNotesFragment() {
        return FragmentHelper.open(NotesFragment.class)
                .put(NotesFragment.ARGS_KEY_STATUS, Status.TRASHED)
                .get();
    }

    @Override
    protected Fragment getCategoryFragment() {
        return CategoriesFragment.newInstance(Status.TRASHED);
    }

    @Override
    public void onCategorySelected(Category category) {
        NotesFragment notesFragment = NotesFragment.newInstance(category, Status.TRASHED);
        FragmentHelper.replace(this, notesFragment, R.id.fragment_container, true);
    }

    @Override
    public void onNotebookSelected(Notebook notebook) {
        NotesFragment notesFragment = NotesFragment.newInstance(notebook, Status.TRASHED);
        FragmentHelper.replace(this, notesFragment, R.id.fragment_container, true);
    }
}
