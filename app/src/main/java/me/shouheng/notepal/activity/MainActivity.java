package me.shouheng.notepal.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.color.ColorChooserDialog;

import java.util.List;

import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.ActivityMainBinding;
import me.shouheng.notepal.databinding.ActivityMainNavHeaderBinding;
import me.shouheng.notepal.dialog.NotebookEditDialog;
import me.shouheng.notepal.fragment.NotesFragment;
import me.shouheng.notepal.intro.IntroActivity;
import me.shouheng.notepal.model.ModelFactory;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.model.enums.FabSortItem;
import me.shouheng.notepal.provider.NotebookStore;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.util.FragmentHelper;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.PermissionUtils;
import me.shouheng.notepal.util.PreferencesUtils;
import me.shouheng.notepal.widget.tools.CustomRecyclerScrollViewListener;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

public class MainActivity extends CommonActivity<ActivityMainBinding> {

    private final int REQUEST_FAB_SORT = 0x0001;

    private final int REQUEST_ADD_NOTE = 0x0002;

    private PreferencesUtils preferencesUtils;

    private NotebookEditDialog notebookEditDialog;

    private RecyclerView.OnScrollListener onScrollListener;

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
        preferencesUtils = PreferencesUtils.getInstance(this);

        IntroActivity.launchIfNecessary(this);

        configToolbar();

        initHeaderView();

        initFloatButtons();
        initFabSortItems();

        initDrawerMenu();

        toNotesFragment();
    }

    public void setDrawerLayoutLocked(boolean lockDrawer){
        getBinding().drawerLayout.setDrawerLockMode(lockDrawer ? DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED);
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
        getBinding().menu.setOnMenuButtonLongClickListener(v -> {
            startActivityForResult(FabSortActivity.class, REQUEST_FAB_SORT);
            return false;
        });

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

        View.OnClickListener onFabClickListener = v -> {
            switch (v.getId()) {
                case R.id.fab1:resolveFabClick(0);break;
                case R.id.fab2:resolveFabClick(1);break;
                case R.id.fab3:resolveFabClick(2);break;
                case R.id.fab4:resolveFabClick(3);break;
                case R.id.fab5:resolveFabClick(4);break;
            }
        };

        getBinding().fab1.setOnClickListener(onFabClickListener);
        getBinding().fab2.setOnClickListener(onFabClickListener);
        getBinding().fab3.setOnClickListener(onFabClickListener);
        getBinding().fab4.setOnClickListener(onFabClickListener);
        getBinding().fab5.setOnClickListener(onFabClickListener);

        onScrollListener = new CustomRecyclerScrollViewListener() {
            @Override
            public void show() {
                getBinding().menu.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }

            @Override
            public void hide() {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) getBinding().menu.getLayoutParams();
                int fabMargin = lp.bottomMargin;
                getBinding().menu.animate().translationY(getBinding().menu.getHeight()+fabMargin).setInterpolator(new AccelerateInterpolator(2.0f)).start();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LogUtils.d("onScrollStateChanged: ");
                if (newState == SCROLL_STATE_IDLE){
                    LogUtils.d("onScrollStateChanged: SCROLL_STATE_IDLE");
                }
            }
        };
    }

    private void initFabSortItems() {
        try {
            List<FabSortItem> fabSortItems = preferencesUtils.getFabSortResult();

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

    private void resolveFabClick(int index) {
        getBinding().menu.close(true);
        FabSortItem fabSortItem = preferencesUtils.getFabSortResult().get(index);
        switch (fabSortItem) {
            case NOTE:
                PermissionUtils.checkStoragePermission(this, () -> {
                    Note note = ModelFactory.getNote(this);
                    ContentActivity.startNoteEditForResult(this, note, null, REQUEST_ADD_NOTE);
                });
                break;
            case NOTEBOOK:
                editNotebook();
                break;
        }
    }

    private void editNotebook() {
        Notebook notebook = ModelFactory.getNotebook(this);
        notebookEditDialog = NotebookEditDialog.newInstance(this, notebook, (notebookName, notebookColor) -> {
            notebook.setTitle(notebookName);
            notebook.setColor(notebookColor);
            notebook.setCount(0);
            NotebookStore.getInstance(this).saveModel(notebook);
            Fragment fragment = getCurrentFragment();
            if (fragment != null && fragment instanceof NotesFragment) {
                ((NotesFragment) fragment).reload();
            }
        });
        notebookEditDialog.show(getSupportFragmentManager(), "NotebookEditDialog");
    }

    private void configToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white);
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
        NotesFragment notesFragment = NotesFragment.newInstance(null);
        notesFragment.setScrollListener(onScrollListener);
        FragmentHelper.replace(this, notesFragment, R.id.fragment_container);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_FAB_SORT:
                if (resultCode == RESULT_OK) {
                    initFabSortItems();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, int selectedColor) {
        notebookEditDialog.updateUIBySelectedColor(selectedColor);
    }
}
