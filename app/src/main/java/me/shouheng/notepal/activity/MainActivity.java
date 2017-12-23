package me.shouheng.notepal.activity;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.ActivityMainBinding;
import me.shouheng.notepal.databinding.ActivityMainNavHeaderBinding;
import me.shouheng.notepal.fragment.NotesFragment;
import me.shouheng.notepal.intro.IntroActivity;
import me.shouheng.notepal.model.enums.FabSortItem;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.util.FragmentHelper;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.PreferencesUtils;

public class MainActivity extends CommonActivity<ActivityMainBinding> {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void beforeSetContentView() {
        setTranslucentStatusBar();
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        IntroActivity.launchIfNecessary(this);

        configToolbar();

        initHeaderView();

        initFloatButtons();
        initFabSortItems();

        initDrawerMenu();

        toNotesFragment();
    }

    private void initHeaderView() {
        View header = getBinding().nav.inflateHeaderView(R.layout.activity_main_nav_header);
        ActivityMainNavHeaderBinding headerBinding = DataBindingUtil.bind(header);
        header.setOnClickListener(v -> {});
        header.setOnLongClickListener(v -> true);
    }

    private void initFloatButtons() {
        getBinding().menu.setMenuButtonColorNormal(accentColor());
        getBinding().menu.setMenuButtonColorPressed(accentColor());

        getBinding().fab1.setColorNormal(accentColor());
        getBinding().fab2.setColorNormal(accentColor());
        getBinding().fab3.setColorNormal(accentColor());
        getBinding().fab4.setColorNormal(accentColor());
        getBinding().fab5.setColorNormal(accentColor());

        getBinding().fab1.setColorPressed(accentColor());
        getBinding().fab2.setColorPressed(accentColor());
        getBinding().fab3.setColorPressed(accentColor());
        getBinding().fab4.setColorPressed(accentColor());
        getBinding().fab5.setColorPressed(accentColor());
    }

    private void initFabSortItems() {
        try {
            List<FabSortItem> fabSortItems = PreferencesUtils.getInstance(this).getFabSortResult();

            getBinding().fab1.setImageDrawable(ColorUtils.tintDrawable(getResources().getDrawable(fabSortItems.get(0).iconRes), Color.WHITE));
            getBinding().fab2.setImageDrawable(ColorUtils.tintDrawable(getResources().getDrawable(fabSortItems.get(1).iconRes), Color.WHITE));
            getBinding().fab3.setImageDrawable(ColorUtils.tintDrawable(getResources().getDrawable(fabSortItems.get(2).iconRes), Color.WHITE));
            getBinding().fab4.setImageDrawable(ColorUtils.tintDrawable(getResources().getDrawable(fabSortItems.get(3).iconRes), Color.WHITE));
            getBinding().fab5.setImageDrawable(ColorUtils.tintDrawable(getResources().getDrawable(fabSortItems.get(4).iconRes), Color.WHITE));

            getBinding().fab1.setLabelText(getString(fabSortItems.get(0).nameRes));
            getBinding().fab2.setLabelText(getString(fabSortItems.get(1).nameRes));
            getBinding().fab3.setLabelText(getString(fabSortItems.get(2).nameRes));
            getBinding().fab4.setLabelText(getString(fabSortItems.get(3).nameRes));
            getBinding().fab5.setLabelText(getString(fabSortItems.get(4).nameRes));
        } catch (Exception e) {
            LogUtils.d("initFabSortItems, error occurred : " + e);
            PreferencesUtils.getInstance(this).setFabSortResult(PreferencesUtils.defaultFabOrders);
        }
    }

    private void configToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_main_menu_white);
        if (!isDarkTheme()) toolbar.setPopupTheme(R.style.AppTheme_PopupOverlay);
    }

    private void initDrawerMenu() {
        getBinding().nav.getMenu().findItem(R.id.nav_notes).setChecked(true);
        getBinding().nav.setNavigationItemSelectedListener(menuItem -> {
            getBinding().drawerLayout.closeDrawers();
            switch (menuItem.getItemId()) {
                case R.id.nav_notes:
                case R.id.nav_minds:
                case R.id.nav_notices:
                    menuItem.setChecked(true);
                    break;
            }
            execute(menuItem);
            return true;
        });
    }

    private void execute(final MenuItem menuItem) {
        new Handler().postDelayed(() -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_settings:
                    startActivity(SettingsActivity.class);
                    break;
                case R.id.nav_notes:
                    toNotesFragment();
                    break;
            }
        }, 500);
    }

    private void toNotesFragment() {
        if (isNotesFragment()) return;
        FragmentHelper.replace(this, NotesFragment.newInstance(), R.id.fragment_container);
        new Handler().postDelayed(() -> getBinding().nav.getMenu().findItem(R.id.nav_notes).setChecked(true), 300);
    }

    private Fragment getCurrentFragment(){
        return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    private boolean isNotesFragment(){
        return getCurrentFragment() instanceof NotesFragment;
    }

    private boolean isDashboard() {
        Fragment currentFragment = getCurrentFragment();
        return currentFragment instanceof NotesFragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                getBinding().drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isDashboard()){
            if (getBinding().drawerLayout.isDrawerOpen(GravityCompat.START)){
                getBinding().drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                if (isNotesFragment()){
                    if (getBinding().menu.isOpened()) {
                        getBinding().menu.close(true);
                        return;
                    }
                    super.onBackPressed();
                } else {
                    toNotesFragment();
                }
            }
        } else {
            super.onBackPressed();
        }
    }
}
