package me.shouheng.notepal.activity;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import com.github.clans.fab.FloatingActionButton;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import java.util.Collections;
import java.util.List;

import me.shouheng.commons.activity.CommonActivity;
import me.shouheng.commons.activity.ContainerActivity;
import me.shouheng.commons.event.RxMessage;
import me.shouheng.commons.helper.ActivityHelper;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.utils.IntentUtils;
import me.shouheng.commons.utils.LogUtils;
import me.shouheng.commons.utils.PermissionUtils;
import me.shouheng.commons.utils.PersistData;
import me.shouheng.commons.utils.ToastUtils;
import me.shouheng.commons.widget.recycler.CustomRecyclerScrollViewListener;
import me.shouheng.data.ModelFactory;
import me.shouheng.data.entity.Attachment;
import me.shouheng.data.entity.Category;
import me.shouheng.data.entity.MindSnagging;
import me.shouheng.data.entity.Model;
import me.shouheng.data.entity.Note;
import me.shouheng.data.entity.Notebook;
import me.shouheng.data.model.enums.FabSortItem;
import me.shouheng.data.model.enums.Status;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.databinding.ActivityMainBinding;
import me.shouheng.notepal.dialog.CategoryEditDialog;
import me.shouheng.notepal.dialog.NotebookEditDialog;
import me.shouheng.notepal.dialog.QuickNoteDialog;
import me.shouheng.notepal.fragment.CategoriesFragment;
import me.shouheng.notepal.fragment.NoteViewFragment;
import me.shouheng.notepal.fragment.NotesFragment;
import me.shouheng.notepal.fragment.StatisticsFragment;
import me.shouheng.notepal.fragment.TimeLineFragment;
import me.shouheng.notepal.fragment.setting.SettingsFragment;
import me.shouheng.notepal.util.FragmentHelper;
import me.shouheng.notepal.util.SynchronizeUtils;
import me.shouheng.notepal.util.preferences.PrefUtils;
import me.shouheng.notepal.viewmodel.CategoryViewModel;
import me.shouheng.notepal.viewmodel.NoteViewModel;
import me.shouheng.notepal.viewmodel.NotebookViewModel;
import me.shouheng.notepal.vm.MainViewModel;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

public class MainActivity extends CommonActivity<ActivityMainBinding> implements
        NotesFragment.OnNotesInteractListener,
        CategoriesFragment.OnCategoriesInteractListener {

    private final static int REQUEST_PASSWORD = 0x0006;
    private final static long TIME_INTERVAL_BACK = 2000;

    private RecyclerView.OnScrollListener onScrollListener;

    private FloatingActionButton[] fabs;
    private long onBackPressed;
    private Drawer drawer;

    private NotebookViewModel notebookViewModel;
    private CategoryViewModel categoryViewModel;
    private NoteViewModel noteViewModel;

    private MainViewModel viewModel;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        viewModel = getViewModel(MainViewModel.class);

        // Get view models -- NOT STANDARD THIS WAY! :( TODO
        notebookViewModel = ViewModelProviders.of(this).get(NotebookViewModel.class);
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);

        checkPsdIfNecessary(savedInstanceState);

        addSubscriptions();
    }

    private void checkPsdIfNecessary(Bundle savedInstanceState) {
        boolean psdRequired = PersistData.getBoolean(R.string.key_password_required, false);
        String psd = PersistData.getString(R.string.key_password, null);
        if (psdRequired && !PalmApp.isPasswordChecked() && !TextUtils.isEmpty(psd)) {
            LockActivity.requireLaunch(this, REQUEST_PASSWORD);
        } else {
            everything(savedInstanceState);
        }
    }

    private void addSubscriptions() {
        addSubscription(RxMessage.class, RxMessage.CODE_NOTE_DATA_CHANGED, rxMessage -> {
            if (isNotesFragment()) ((NotesFragment) getCurrentFragment()).reload();
        });
        addSubscription(RxMessage.class, RxMessage.CODE_SORT_FLOAT_BUTTONS, rxMessage -> {
            initFabSortItems();
        });
    }

    private void everything(Bundle savedInstanceState) {
        // Handle intent
        handleIntent(getIntent());

        // Config toolbar
        setSupportActionBar(getBinding().toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(ColorUtils.tintDrawable(R.drawable.ic_menu_black,
                    getThemeStyle().isDarkTheme ? Color.WHITE : Color.BLACK));
        }
        getBinding().toolbar.setTitleTextColor(getThemeStyle().isDarkTheme ? Color.WHITE : Color.BLACK);
        getBinding().toolbar.setSubtitleTextColor(getThemeStyle().isDarkTheme ? Color.WHITE : Color.BLACK);
        if (getThemeStyle().isDarkTheme) {
            getBinding().toolbar.setPopupTheme(R.style.AppTheme_PopupOverlayDark);
        }

        // Custom left drawer
        configDrawer(savedInstanceState);

        // Initialize float action buttons
        initFloatButtons();

        // Initialize the FABs
        initFabSortItems();

        toNotesFragment(true);
    }

    private void configDrawer(Bundle savedInstanceState) {
        AccountHeader header = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .build();

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

        PrimaryDrawerItem itemArchive = new PrimaryDrawerItem()
                .withName(R.string.drawer_menu_archive)
                .withIcon(ColorUtils.tintDrawable(R.drawable.ic_archive_grey, isDarkTheme() ? Color.WHITE : Color.BLACK))
                .withIdentifier(2)
                .withSelectable(false)
                .withIconTintingEnabled(true)
                .withSelectedTextColor(ColorUtils.accentColor())
                .withSelectedIconColor(ColorUtils.accentColor());

        PrimaryDrawerItem itemTrash = new PrimaryDrawerItem()
                .withName(R.string.drawer_menu_trash)
                .withIcon(ColorUtils.tintDrawable(R.drawable.ic_trash_black, isDarkTheme() ? Color.WHITE : Color.BLACK))
                .withIdentifier(3)
                .withSelectable(false)
                .withIconTintingEnabled(true)
                .withSelectedTextColor(ColorUtils.accentColor())
                .withSelectedIconColor(ColorUtils.accentColor());

        PrimaryDrawerItem itemSetting = new PrimaryDrawerItem()
                .withName(R.string.drawer_menu_setting)
                .withIcon(ColorUtils.tintDrawable(R.drawable.ic_settings_black, isDarkTheme() ? Color.WHITE : Color.BLACK))
                .withIdentifier(4)
                .withSelectable(false)
                .withIconTintingEnabled(true)
                .withSelectedTextColor(ColorUtils.accentColor())
                .withSelectedIconColor(ColorUtils.accentColor());

        PrimaryDrawerItem itemDonate = new PrimaryDrawerItem()
                .withName(R.string.drawer_menu_donate)
                .withIcon(ColorUtils.tintDrawable(R.drawable.ic_monetization_on_black_24dp, isDarkTheme() ? Color.WHITE : Color.BLACK))
                .withIdentifier(5)
                .withSelectable(false)
                .withIconTintingEnabled(true)
                .withSelectedTextColor(ColorUtils.accentColor())
                .withSelectedIconColor(ColorUtils.accentColor());

        PrimaryDrawerItem itemStatistic = new PrimaryDrawerItem()
                .withName(R.string.drawer_menu_statistics)
                .withIcon(ColorUtils.tintDrawable(R.drawable.ic_equalizer_grey_24dp, isDarkTheme() ? Color.WHITE : Color.BLACK))
                .withIdentifier(6)
                .withSelectable(false)
                .withIconTintingEnabled(true)
                .withSelectedTextColor(ColorUtils.accentColor())
                .withSelectedIconColor(ColorUtils.accentColor());

        PrimaryDrawerItem itemTimeLine = new PrimaryDrawerItem()
                .withName(R.string.drawer_menu_time_line)
                .withIcon(ColorUtils.tintDrawable(R.drawable.ic_timeline_black_24dp, isDarkTheme() ? Color.WHITE : Color.BLACK))
                .withIdentifier(7)
                .withSelectable(false)
                .withIconTintingEnabled(true)
                .withSelectedTextColor(ColorUtils.accentColor())
                .withSelectedIconColor(ColorUtils.accentColor());

        PrimaryDrawerItem itemShare = new PrimaryDrawerItem()
                .withName(R.string.drawer_menu_share)
                .withIcon(ColorUtils.tintDrawable(R.drawable.ic_share_white, isDarkTheme() ? Color.WHITE : Color.BLACK))
                .withIdentifier(8)
                .withSelectable(false)
                .withIconTintingEnabled(true)
                .withSelectedTextColor(ColorUtils.accentColor())
                .withSelectedIconColor(ColorUtils.accentColor());

        drawer = new DrawerBuilder().withActivity(this)
                .withHasStableIds(true)
                .addDrawerItems(itemNotes, itemTags, itemTimeLine, divider, itemStatistic, itemArchive,
                        itemTrash, divider, itemSetting, itemShare, itemDonate)
                .withMultiSelect(false)
                .withSelectedItem(0)
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    if (drawerItem == null) return false;
                    switch ((int) drawerItem.getIdentifier()) {
                        case 0:
                            drawer.closeDrawer();
                            toNotesFragment(true);
                            break;
                        case 1:
                            drawer.closeDrawer();
                            toCategoriesFragment();
                            break;
                        case 2:
                            ActivityHelper.start(this, ArchiveActivity.class);
                            break;
                        case 3:
                            ActivityHelper.start(this, TrashedActivity.class);
                            break;
                        case 4:
                            SettingsActivity.open(SettingsFragment.class).launch(this);
                            break;
                        case 5:
                            // Donate
                            break;
                        case 6:
                            ContainerActivity.open(StatisticsFragment.class).launch(this);
                            break;
                        case 7:
                            ContainerActivity.open(TimeLineFragment.class).launch(this);
                            break;
                        case 8:
                            // Share
                            break;
                    }
                    return true;
                })
                .withAccountHeader(header)
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();
    }

    // region region : handle intent
    private void handleIntent(Intent intent) {
        String action = intent.getAction();

        // if the action is empty or the activity is recreated for theme change, don;t handle intent
        if (TextUtils.isEmpty(action)) return;

        switch (action) {
            case Constants.ACTION_SHORTCUT:
                intent.setClass(this, ContentActivity.class);
                startActivity(intent);
                break;
            case Constants.ACTION_ADD_NOTE:
                editNote(getNewNote());
                break;
            case Constants.ACTION_ADD_MIND:
                editMindSnagging(ModelFactory.getMindSnagging());
                break;
            case Constants.ACTION_WIDGET_LIST:
                Model model;
                if (intent.hasExtra(Constants.EXTRA_MODEL)
                        && (model = (Model) intent.getSerializableExtra(Constants.EXTRA_MODEL)) != null) {
                    if (model instanceof Note) {
                        ContainerActivity.open(NoteViewFragment.class)
                                .put(NoteViewFragment.ARGS_KEY_NOTE, model)
                                .put(NoteViewFragment.ARGS_KEY_IS_PREVIEW, false)
                                .launch(this);
                    } else if (model instanceof MindSnagging) {
                        editMindSnagging((MindSnagging) model);
                    }
                }
                break;
            case Constants.ACTION_WIDGET_LAUNCH_APP:
                // do nothing just open the app.
                break;
            case Constants.ACTION_TAKE_PHOTO:
                startAddPhoto();
                break;
            case Constants.ACTION_ADD_SKETCH:
                startAddSketch();
                break;
            case Intent.ACTION_SEND:
            case Intent.ACTION_SEND_MULTIPLE:
            case Constants.INTENT_GOOGLE_NOW:
                PermissionUtils.checkStoragePermission(this, this::handleThirdPart);
                break;
            case Constants.ACTION_RESTART_APP:
                // Recreate
                recreate();
                break;
        }
    }

    private void handleThirdPart() {
        Intent i = getIntent();
        if (IntentUtils.checkAction(i,
                Intent.ACTION_SEND,
                Intent.ACTION_SEND_MULTIPLE,
                Constants.INTENT_GOOGLE_NOW) && i.getType() != null) {
            ContentActivity.resolveThirdPart(this, i);
        }
    }

    private void startAddPhoto() {
        PermissionUtils.checkStoragePermission(this, () ->
                ContentActivity.resolveAction(
                        MainActivity.this,
                        getNewNote(),
                        Constants.ACTION_TAKE_PHOTO));
    }

    private void startAddSketch() {
        PermissionUtils.checkStoragePermission(this, () ->
                ContentActivity.resolveAction(
                        MainActivity.this,
                        getNewNote(),
                        Constants.ACTION_ADD_SKETCH));
    }

    private void startAddFile() {
        PermissionUtils.checkStoragePermission(this, () ->
                ContentActivity.resolveAction(
                        MainActivity.this,
                        getNewNote(),
                        Constants.ACTION_ADD_FILES));
    }
    // endregion

    // region region : fab
    private void initFloatButtons() {
        getBinding().menu.setMenuButtonColorNormal(accentColor());
        getBinding().menu.setMenuButtonColorPressed(accentColor());
        getBinding().menu.setOnMenuButtonLongClickListener(v -> {
            ActivityHelper.start(this, FabSortActivity.class);
            return false;
        });
        getBinding().menu.setOnMenuToggleListener(opened -> getBinding().rlMenuContainer.setVisibility(opened ? View.VISIBLE : View.GONE));
        getBinding().rlMenuContainer.setOnClickListener(view -> getBinding().menu.close(true));
        getBinding().rlMenuContainer.setBackgroundResource(isDarkTheme() ? R.color.menu_container_dark : R.color.menu_container_light);

        fabs = new FloatingActionButton[]{getBinding().fab1, getBinding().fab2, getBinding().fab3, getBinding().fab4, getBinding().fab5};

        for (int i=0; i<fabs.length; i++) {
            fabs[i].setColorNormal(accentColor());
            fabs[i].setColorPressed(accentColor());
            int finalI = i;
            fabs[i].setOnClickListener(view -> resolveFabClick(finalI));
        }

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
            List<FabSortItem> fabSortItems = PrefUtils.getInstance().getFabSortResult();
            for (int i=0; i<fabs.length; i++) {
                fabs[i].setImageDrawable(ColorUtils.tintDrawable(getResources().getDrawable(fabSortItems.get(i).iconRes), Color.WHITE));
                fabs[i].setLabelText(getString(fabSortItems.get(i).nameRes));
            }
        } catch (Exception e) {
            LogUtils.d("initFabSortItems, error occurred : " + e);
            PrefUtils.getInstance().setFabSortResult(PrefUtils.defaultFabOrders);
        }
    }

    private void resolveFabClick(int index) {
        getBinding().menu.close(true);
        FabSortItem fabSortItem = PrefUtils.getInstance().getFabSortResult().get(index);
        switch (fabSortItem) {
            case NOTE:
                editNote(getNewNote());
                break;
            case NOTEBOOK:
                editNotebook();
                break;
            case CATEGORY:
                CategoryEditDialog.newInstance(ModelFactory.getCategory(), this::saveCategory)
                        .show(getSupportFragmentManager(), "CATEGORY_EDIT_DIALOG");
                break;
            case MIND_SNAGGING:
                editMindSnagging(ModelFactory.getMindSnagging());
                break;
            case DRAFT:
                startAddSketch();
                break;
            case FILE:
                startAddFile();
                break;
            case CAPTURE:
                startAddPhoto();
                break;
        }
    }

    private void editNote(@NonNull final Note note) {
        PermissionUtils.checkStoragePermission(this, () ->
                ContentActivity.editNote(this, note));
    }

    private Note getNewNote() {
        Note note = ModelFactory.getNote();

        boolean isNotes = isNotesFragment();

        /* Add notebook filed according to current fragment */
        Notebook notebook;
        if (isNotes && (notebook = ((NotesFragment) getCurrentFragment()).getNotebook()) != null) {
            note.setParentCode(notebook.getCode());
            note.setTreePath(notebook.getTreePath() + "|" + note.getCode());
        } else {
            // The default tree path of note is itself
            note.setTreePath(String.valueOf(note.getCode()));
        }

        /* Add category field according to current fragment */
        Category category;
        if (isNotes && (category = ((NotesFragment) getCurrentFragment()).getCategory()) != null) {
            note.setTags(CategoryViewModel.getTags(Collections.singletonList(category)));
        }

        return note;
    }

    private void editNotebook() {
        Notebook notebook = ModelFactory.getNotebook();
        NotebookEditDialog.newInstance(notebook, (notebookName, notebookColor) -> {
            // notebook fields
            notebook.setTitle(notebookName);
            notebook.setColor(notebookColor);
            notebook.setCount(0);
            notebook.setTreePath(String.valueOf(notebook.getCode()));

            // notebook parent fields
            Notebook parent;
            if (isNotesFragment() && (parent = ((NotesFragment) getCurrentFragment()).getNotebook()) != null) {
                notebook.setParentCode(parent.getCode());
                notebook.setTreePath(parent.getTreePath() + "|" + notebook.getCode());
            }

            // do save
            saveNotebook(notebook);
        }).show(getSupportFragmentManager(), "NotebookEditDialog");
    }

    private void saveNotebook(Notebook notebook) {
        notebookViewModel.saveModel(notebook).observe(this, notebookResource -> {
            assert notebookResource != null;
            switch (notebookResource.status) {
                case SUCCESS:
                    ToastUtils.makeToast(R.string.text_save_successfully);
                    Fragment fragment = getCurrentFragment();
                    if (fragment instanceof NotesFragment) {
                        ((NotesFragment) fragment).reload();
                    }
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_error_when_save);
                    break;
            }
        });
    }

    private void editMindSnagging(@NonNull MindSnagging param) {
        QuickNoteDialog.newInstance(param, new QuickNoteDialog.DialogInteraction() {
            @Override
            public void onCancel() { }

            @Override
            public void onDismiss() { }

            @Override
            public void onCancel(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onConfirm(Dialog dialog, MindSnagging mindSnagging, Attachment attachment) {
                dialog.dismiss();
                saveMindSnagging(mindSnagging, attachment);
            }
        }).show(getSupportFragmentManager(), "QUICK NOTE");
    }

    private void saveMindSnagging(MindSnagging mindSnagging, Attachment attachment) {
        noteViewModel.saveSnagging(getNewNote(), mindSnagging, attachment).observe(this, noteResource -> {
            assert noteResource != null;
            switch (noteResource.status) {
                case SUCCESS:
                    ToastUtils.makeToast(R.string.text_save_successfully);
                    Fragment fragment = getCurrentFragment();
                    if (fragment instanceof NotesFragment) {
                        ((NotesFragment) fragment).reload();
                    }
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed_to_modify_data);
                    break;
                case LOADING:break;
            }
        });
    }

    private void saveCategory(Category category) {
        categoryViewModel.saveModel(category).observe(this, categoryResource -> {
            assert categoryResource != null;
            switch (categoryResource.status) {
                case SUCCESS:
                    ToastUtils.makeToast(R.string.text_save_successfully);
                    Fragment fragment = getCurrentFragment();
                    if (fragment instanceof CategoriesFragment) {
                        ((CategoriesFragment) fragment).addCategory(category);
                    }
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_error_when_save);
                    break;
            }
        });
    }
    // endregion

    // region region : drawer
    private void setDrawerLayoutLocked(boolean lockDrawer) {
        drawer.getDrawerLayout().setDrawerLockMode(lockDrawer ?
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    private void toNotesFragment(boolean checkDuplicate) {
        if (checkDuplicate && isNotesFragment()) return;
        NotesFragment notesFragment = NotesFragment.newInstance(Status.NORMAL);
        notesFragment.setScrollListener(onScrollListener);
        FragmentHelper.replace(this, notesFragment, R.id.fragment_container);
    }

    private void toCategoriesFragment() {
        if (getCurrentFragment() instanceof CategoriesFragment) return;
        CategoriesFragment categoriesFragment = CategoriesFragment.newInstance();
        categoriesFragment.setScrollListener(onScrollListener);
        FragmentHelper.replace(this, categoriesFragment, R.id.fragment_container);
    }

    private Fragment getCurrentFragment(){
        return getCurrentFragment(R.id.fragment_container);
    }

    private boolean isNotesFragment(){
        Fragment f = getCurrentFragment();
        return f instanceof NotesFragment;
    }

    private boolean isDashboard() {
        Fragment f = getCurrentFragment();
        return (f instanceof NotesFragment || f instanceof CategoriesFragment);
    }
    // endregion

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
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
            case R.id.action_search:
                ActivityHelper.open(SearchActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        .launch(getContext());
                break;
            case R.id.action_sync:
                SynchronizeUtils.syncOneDrive(this, true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case REQUEST_PASSWORD:
                everything(null);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (isDashboard()){
            if (drawer.isDrawerOpen()){
                drawer.closeDrawer();
            } else {
                if (getBinding().menu.isOpened()) {
                    getBinding().menu.close(true);
                    return;
                }
                if (isNotesFragment()) {
                    if (((NotesFragment) getCurrentFragment()).isTopStack()) {
                        againExit();
                    } else {
                        super.onBackPressed();
                    }
                } else {
                    drawer.setSelection(0);
                    toNotesFragment(true);
                }
            }
        } else {
            super.onBackPressed();
        }
    }

    private void againExit() {
        if (onBackPressed + TIME_INTERVAL_BACK > System.currentTimeMillis()) {
            SynchronizeUtils.syncOneDrive(this);
            super.onBackPressed();
            return;
        } else {
            ToastUtils.makeToast(R.string.text_tab_again_exit);
        }
        onBackPressed = System.currentTimeMillis();
    }

    @Override
    public void onNotebookSelected(Notebook notebook) {
        NotesFragment notesFragment = NotesFragment.newInstance(notebook, Status.NORMAL);
        notesFragment.setScrollListener(onScrollListener);
        FragmentHelper.replaceWithCallback(this, notesFragment, R.id.fragment_container);
    }

    @Override
    public void onCategorySelected(Category category) {
        NotesFragment notesFragment = NotesFragment.newInstance(category, Status.NORMAL);
        notesFragment.setScrollListener(onScrollListener);
        FragmentHelper.replaceWithCallback(this, notesFragment, R.id.fragment_container);
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
    public void onCategoryLoadStateChanged(me.shouheng.commons.model.data.Status status) {
        onLoadStateChanged(status);
    }

    @Override
    public void onNoteLoadStateChanged(me.shouheng.commons.model.data.Status status) {
        onLoadStateChanged(status);
    }

    private void onLoadStateChanged(me.shouheng.commons.model.data.Status status) {
        switch (status) {
            case SUCCESS:
            case FAILED:
                getBinding().topMpb.setVisibility(View.GONE);
                break;
            case LOADING:
                getBinding().topMpb.setVisibility(View.VISIBLE);
                break;
        }
    }
}
