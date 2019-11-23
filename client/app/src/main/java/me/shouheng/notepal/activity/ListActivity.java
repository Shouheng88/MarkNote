package me.shouheng.notepal.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import me.shouheng.commons.activity.ThemedActivity;
import me.shouheng.commons.helper.FragmentHelper;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.data.entity.Category;
import me.shouheng.data.entity.Notebook;
import me.shouheng.data.model.enums.Status;
import me.shouheng.mvvm.base.anno.ActivityConfiguration;
import me.shouheng.mvvm.comn.EmptyViewModel;
import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.ActivityBaseListBinding;
import me.shouheng.notepal.fragment.CategoriesFragment;
import me.shouheng.notepal.fragment.NotesFragment;

/**
 * List activity. used to mange the categories and notebooks list of for archived and trashed notes.
 *
 * Created by WngShhng (shouheng2015@gmail.com) on 2017/10/10.
 */
@ActivityConfiguration(layoutResId = R.layout.activity_base_list)
public class ListActivity extends ThemedActivity<ActivityBaseListBinding, EmptyViewModel>
        implements NotesFragment.OnNotesInteractListener, CategoriesFragment.CategoriesInteraction {

    /**
     * The argument for list type, should be one of
     * {@link Status#ARCHIVED} and {@link Status#TRASHED}
     */
    public static final String ARGS_KEY_LIST_TYPE = "__args_key_list_type";

    private Drawer drawer;

    /**
     * The status for current notes list.
     */
    private Status status;

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        /* Handle arguments */
        Intent i = getIntent();
        status = (Status) i.getSerializableExtra(ARGS_KEY_LIST_TYPE);

        /* Config toolbar */
        setSupportActionBar(getBinding().toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(ColorUtils.tintDrawable(
                    R.drawable.ic_menu_black, isDarkTheme() ? Color.WHITE : Color.BLACK));
        }
        getBinding().toolbar.setTitle(status == Status.ARCHIVED ?
                R.string.drawer_menu_archive : R.string.drawer_menu_trash);
        getBinding().toolbar.setTitleTextColor(getThemeStyle().isDarkTheme ? Color.WHITE : Color.BLACK);
        if (getThemeStyle().isDarkTheme) {
            getBinding().toolbar.setPopupTheme(R.style.AppTheme_PopupOverlayDark);
        }

        /* Config drawer */
        configDrawer(savedInstanceState);

        /* Load default fragment. */
        NotesFragment fragment = FragmentHelper.open(NotesFragment.class)
                .put(NotesFragment.ARGS_KEY_STATUS, status)
                .get();
        FragmentHelper.replace(this, fragment, R.id.fragment_container, false);
    }

    private void configDrawer(Bundle savedInstanceState) {
        PrimaryDrawerItem itemMenu = ColorUtils.getColoredDrawerMenuItem(
                status == Status.ARCHIVED ? R.string.drawer_menu_archive : R.string.drawer_menu_trash,
                R.drawable.ic_book, -1, false);
        DividerDrawerItem divider = new DividerDrawerItem();
        PrimaryDrawerItem itemNotes = ColorUtils.getColoredDrawerMenuItem(
                R.string.drawer_menu_notebooks, R.drawable.ic_book, 0, true);
        PrimaryDrawerItem itemTags = ColorUtils.getColoredDrawerMenuItem(
                R.string.drawer_menu_categories, R.drawable.ic_view_module_white_24dp, 1, true);
        PrimaryDrawerItem itemBack = ColorUtils.getColoredDrawerMenuItem(
                R.string.text_back, R.drawable.ic_subdirectory_arrow_left_black_24dp, 2, true);

        drawer = new DrawerBuilder().withActivity(this)
                .withHasStableIds(true)
                .addDrawerItems(itemMenu, divider, itemNotes, itemTags, divider, itemBack)
                .withMultiSelect(false)
                .withSelectedItem(0)
                .withSliderBackgroundColorRes(isDarkTheme() ? R.color.dark_theme_background : R.color.light_theme_background)
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    if (drawerItem == null) return false;
                    switch ((int) drawerItem.getIdentifier()) {
                        case 0:
                            NotesFragment notesFragment = FragmentHelper.open(NotesFragment.class)
                                    .put(NotesFragment.ARGS_KEY_STATUS, status)
                                    .get();
                            drawer.closeDrawer();
                            FragmentHelper.replace(this, notesFragment , R.id.fragment_container, false);
                            break;
                        case 1:
                            CategoriesFragment categoriesFragment = FragmentHelper.open(CategoriesFragment.class)
                                    .put(CategoriesFragment.ARGS_KEY_STATUS, status)
                                    .get();
                            drawer.closeDrawer();
                            FragmentHelper.replace(this, categoriesFragment, R.id.fragment_container, false);
                            break;
                        case 2:
                            super.onBackPressed();
                            break;
                        default: // noop
                    }
                    return true;
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();
    }

    private void setDrawerLayoutLocked(boolean lock) {
        drawer.getDrawerLayout().setDrawerLockMode(lock ?
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    protected Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    @Override
    public void onCategorySelected(Category category) {
        NotesFragment fragment = FragmentHelper.open(NotesFragment.class)
                .put(NotesFragment.ARGS_KEY_CATEGORY, category)
                .put(NotesFragment.ARGS_KEY_STATUS, status)
                .get();
        FragmentHelper.replace(this, fragment, R.id.fragment_container, true);
    }

    @Override
    public void onNotebookSelected(Notebook notebook) {
        NotesFragment fragment = FragmentHelper.open(NotesFragment.class)
                .put(NotesFragment.ARGS_KEY_NOTEBOOK, notebook)
                .put(NotesFragment.ARGS_KEY_STATUS, status)
                .get();
        FragmentHelper.replace(this, fragment, R.id.fragment_container, true);
    }

    @Override
    public void onResumeToCategory() {
        setDrawerLayoutLocked(false);
    }

    @Override
    public void onActivityAttached(boolean isTopStack) {
        setDrawerLayoutLocked(!isTopStack);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Fragment fragment = getCurrentFragment();
            if (!fragment.onOptionsItemSelected(item)) {
                drawer.openDrawer();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
}
