package me.shouheng.notepal.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import me.shouheng.commons.activity.CommonActivity;
import me.shouheng.commons.event.RxMessage;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.utils.PalmUtils;
import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.ActivityBaseListBinding;
import me.shouheng.notepal.fragment.CategoriesFragment;
import me.shouheng.notepal.fragment.NotesFragment;
import me.shouheng.notepal.model.data.Status;
import me.shouheng.notepal.util.FragmentHelper;


/**
 * Created by wangshouheng on 2017/10/10.*/
public abstract class BaseListActivity extends CommonActivity<ActivityBaseListBinding> implements
        NotesFragment.OnNotesInteractListener,
        CategoriesFragment.OnCategoriesInteractListener {

    private Drawer drawer;

    private boolean isListChanged;

    protected abstract CharSequence getActionbarTitle();

    @DrawableRes
    protected abstract int getHeaderDrawable();

    protected abstract Fragment getNotesFragment();

    protected abstract Fragment getCategoryFragment();

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_base_list;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        // config toolbar
        setSupportActionBar(getBinding().toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(ColorUtils.tintDrawable(
                    PalmUtils.getDrawableCompact(R.drawable.ic_menu_black),
                    getThemeStyle().isDarkTheme ? Color.WHITE : Color.BLACK));
        }
        getBinding().toolbar.setTitle(getActionbarTitle());
        getBinding().toolbar.setTitleTextColor(getThemeStyle().isDarkTheme ? Color.WHITE : Color.BLACK);
        if (getThemeStyle().isDarkTheme) {
            getBinding().toolbar.setPopupTheme(R.style.AppTheme_PopupOverlayDark);
        }

        // config drawer
        configDrawer(savedInstanceState);
    }

    private void configDrawer(Bundle savedInstanceState) {
        PrimaryDrawerItem itemMenu = new PrimaryDrawerItem()
                .withName(getActionbarTitle().toString())
                .withIconTintingEnabled(true)
                .withSelectable(false)
                .withIdentifier(1111)
                .withSelectedTextColor(ColorUtils.accentColor())
                .withSelectedIconColor(ColorUtils.accentColor());

        DividerDrawerItem divider = new DividerDrawerItem();

        PrimaryDrawerItem itemNotes = new PrimaryDrawerItem()
                .withName(R.string.drawer_menu_notebooks)
                .withIcon(ColorUtils.tintDrawable(R.drawable.ic_book, isDarkTheme() ? Color.WHITE : Color.BLACK))
                .withIdentifier(0)
                .withIconTintingEnabled(true)
                .withSelectedTextColor(ColorUtils.accentColor())
                .withSelectedIconColor(ColorUtils.accentColor());

        PrimaryDrawerItem itemTags = new PrimaryDrawerItem()
                .withName(R.string.drawer_menu_categories)
                .withIcon(ColorUtils.tintDrawable(R.drawable.ic_view_module_white_24dp, isDarkTheme() ? Color.WHITE : Color.BLACK))
                .withIdentifier(1)
                .withIconTintingEnabled(true)
                .withSelectedTextColor(ColorUtils.accentColor())
                .withSelectedIconColor(ColorUtils.accentColor());

        PrimaryDrawerItem itemBack = new PrimaryDrawerItem()
                .withName(R.string.text_back)
                .withIcon(ColorUtils.tintDrawable(R.drawable.ic_subdirectory_arrow_left_black_24dp, isDarkTheme() ? Color.WHITE : Color.BLACK))
                .withIdentifier(2)
                .withSelectedTextColor(ColorUtils.accentColor())
                .withSelectedIconColor(ColorUtils.accentColor());

        drawer = new DrawerBuilder().withActivity(this)
                .withHasStableIds(true)
                .addDrawerItems(itemMenu, divider, itemNotes, itemTags, divider, itemBack)
                .withMultiSelect(false)
                .withSelectedItem(0)
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    if (drawerItem == null) return false;
                    switch ((int) drawerItem.getIdentifier()) {
                        case 0:
                            drawer.closeDrawer();
                            FragmentHelper.replace(this, getNotesFragment(), R.id.fragment_container);
                            break;
                        case 1:
                            drawer.closeDrawer();
                            FragmentHelper.replace(this, getCategoryFragment(), R.id.fragment_container);
                            break;
                        case 2:
                            if (isListChanged) {
                                postEvent(new RxMessage(RxMessage.CODE_NOTE_DATA_CHANGED, null));
                            }
                            super.onBackPressed();
                            return true;
                    }
                    return true;
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();

        FragmentHelper.replace(this, getNotesFragment(), R.id.fragment_container);
    }

    public void setDrawerLayoutLocked(boolean lock){
        drawer.getDrawerLayout().setDrawerLockMode(lock ?
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    protected Fragment getCurrentFragment(){
        return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                Fragment fragment = getCurrentFragment();
                if (!fragment.onOptionsItemSelected(item)) {
                    drawer.openDrawer();
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen()){
            drawer.closeDrawer();
        } else {
            if (isListChanged) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
            }
            super.onBackPressed();
        }
    }

    @Override
    public void onNoteDataChanged() {
        isListChanged = true;
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
    public void onCategoryLoadStateChanged(Status status) {
        onLoadStateChanged(status);
    }

    @Override
    public void onNoteLoadStateChanged(Status status) {
        onLoadStateChanged(status);
    }

    private void onLoadStateChanged(Status status) {
        switch (status) {
            case SUCCESS:
            case FAILED:
                getBinding().sl.setVisibility(View.GONE);
                break;
            case LOADING:
                getBinding().sl.setVisibility(View.VISIBLE);
                break;
        }
    }
}
