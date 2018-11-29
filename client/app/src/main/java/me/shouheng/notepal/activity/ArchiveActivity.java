package me.shouheng.notepal.activity;

import android.support.v4.app.Fragment;

import me.shouheng.data.model.enums.Status;
import me.shouheng.notepal.R;
import me.shouheng.notepal.fragment.CategoriesFragment;
import me.shouheng.notepal.fragment.NotesFragment;
import me.shouheng.data.entity.Category;
import me.shouheng.data.entity.Notebook;
import me.shouheng.notepal.util.FragmentHelper;

public class ArchiveActivity extends BaseListActivity {

    @Override
    protected CharSequence getActionbarTitle() {
        return getString(R.string.drawer_menu_archive);
    }

    @Override
    protected int getHeaderDrawable() {
        return R.drawable.ic_archive_grey;
    }

    @Override
    protected Fragment getNotesFragment() {
        return NotesFragment.newInstance(Status.ARCHIVED);
    }

    @Override
    protected Fragment getCategoryFragment() {
        return CategoriesFragment.newInstance(Status.ARCHIVED);
    }

    @Override
    public void onCategorySelected(Category category) {
        NotesFragment notesFragment = NotesFragment.newInstance(category, Status.ARCHIVED);
        FragmentHelper.replaceWithCallback(this, notesFragment, R.id.fragment_container);
    }

    @Override
    public void onNotebookSelected(Notebook notebook) {
        NotesFragment notesFragment = NotesFragment.newInstance(notebook, Status.ARCHIVED);
        FragmentHelper.replaceWithCallback(this, notesFragment, R.id.fragment_container);
    }
}
