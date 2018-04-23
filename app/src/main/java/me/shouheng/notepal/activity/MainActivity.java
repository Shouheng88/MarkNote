package me.shouheng.notepal.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;

import org.polaric.colorful.PermissionUtils;

import java.util.Collections;
import java.util.List;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.base.CommonActivity;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.databinding.ActivityMainBinding;
import me.shouheng.notepal.databinding.ActivityMainNavHeaderBinding;
import me.shouheng.notepal.dialog.AttachmentPickerDialog;
import me.shouheng.notepal.dialog.CategoryEditDialog;
import me.shouheng.notepal.dialog.NotebookEditDialog;
import me.shouheng.notepal.dialog.QuickNoteEditor;
import me.shouheng.notepal.fragment.CategoriesFragment;
import me.shouheng.notepal.fragment.NotesFragment;
import me.shouheng.notepal.intro.IntroActivity;
import me.shouheng.notepal.listener.OnAttachingFileListener;
import me.shouheng.notepal.listener.OnSettingsChangedListener;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.model.Category;
import me.shouheng.notepal.model.MindSnagging;
import me.shouheng.notepal.model.Model;
import me.shouheng.notepal.model.ModelFactory;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.model.enums.FabSortItem;
import me.shouheng.notepal.model.enums.Status;
import me.shouheng.notepal.util.AttachmentHelper;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.util.FragmentHelper;
import me.shouheng.notepal.util.IntentUtils;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.preferences.PreferencesUtils;
import me.shouheng.notepal.util.SynchronizeUtils;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.viewmodel.CategoryViewModel;
import me.shouheng.notepal.viewmodel.NoteViewModel;
import me.shouheng.notepal.viewmodel.NotebookViewModel;
import me.shouheng.notepal.widget.tools.CustomRecyclerScrollViewListener;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

public class MainActivity extends CommonActivity<ActivityMainBinding> implements
        NotesFragment.OnNotesInteractListener,
        OnAttachingFileListener,
        CategoriesFragment.OnCategoriesInteractListener {

    // region request codes
    private final int REQUEST_FAB_SORT = 0x0001;
    private final int REQUEST_ADD_NOTE = 0x0002;
    private final int REQUEST_ARCHIVE = 0x0003;
    private final int REQUEST_TRASH = 0x0004;
    private final int REQUEST_USER_INFO = 0x0005;
    private final int REQUEST_PASSWORD = 0x0006;
    private final int REQUEST_SEARCH = 0x0007;
    private final int REQUEST_NOTE_VIEW = 0x0008;
    private final int REQUEST_SETTING = 0x0009;
    private final int REQUEST_SETTING_BACKUP = 0x000A;
    // endregion

    private final static long TIME_INTERVAL_BACK = 2000;

    private long onBackPressed;

    private PreferencesUtils preferencesUtils;

    private QuickNoteEditor quickNoteEditor;
    private NotebookEditDialog notebookEditDialog;
    private CategoryEditDialog categoryEditDialog;

    private RecyclerView.OnScrollListener onScrollListener;
    private NotesChangedReceiver notesChangedReceiver;

    private FloatingActionButton[] fabs;

    private NotebookViewModel notebookViewModel;
    private CategoryViewModel categoryViewModel;
    private NoteViewModel noteViewModel;

    private ActivityMainNavHeaderBinding headerBinding;

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

        checkPassword();

        regNoteChangeReceiver();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void checkPassword() {
        if (preferencesUtils.isPasswordRequired()
                && !PalmApp.isPasswordChecked()
                && !TextUtils.isEmpty(preferencesUtils.getPassword())) {
            LockActivity.requireLaunch(this, REQUEST_PASSWORD);
        } else {
            init();
        }
    }

    private void init() {
        handleIntent(getIntent());

        configToolbar();

        initViewModels();

        initHeaderView();

        // init float action buttons
        initFloatButtons();

        initFabSortItems();

        initDrawerMenu();

        toNotesFragment(true);
    }

    private void initViewModels() {
        notebookViewModel = ViewModelProviders.of(this).get(NotebookViewModel.class);
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);
    }

    private void configToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
        }
        if (!isDarkTheme()) toolbar.setPopupTheme(R.style.AppTheme_PopupOverlay);
    }

    private void initHeaderView() {
        if (headerBinding == null) {
            View header = getBinding().nav.inflateHeaderView(R.layout.activity_main_nav_header);
            headerBinding = DataBindingUtil.bind(header);
        }
        setupHeader();
        headerBinding.getRoot().setOnLongClickListener(v -> true);
        headerBinding.getRoot().setOnClickListener(
                view -> startActivityForResult(UserInfoActivity.class, REQUEST_USER_INFO));
    }

    private void setupHeader() {
        headerBinding.userMotto.setText(preferencesUtils.getUserMotto());

        boolean enabled = preferencesUtils.isUserInfoBgEnable();
        headerBinding.userBg.setVisibility(enabled ? View.VISIBLE : View.GONE);
        if (enabled) {
            Uri customUri = preferencesUtils.getUserInfoBG();
            Glide.with(PalmApp.getContext())
                    .load(customUri)
                    .centerCrop()
                    .crossFade()
                    .into(headerBinding.userBg);
        }
    }

    // region handle intent
    private void handleIntent(Intent intent) {
        String action = intent.getAction();

        // if the action is empty or the activity is recreated for theme change, don;t handle intent
        if (TextUtils.isEmpty(action) || recreateForThemeChange) return;

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
                        ContentActivity.viewNote(this, (Note) model, REQUEST_NOTE_VIEW);
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
            ContentActivity.resolveThirdPart(this, i, REQUEST_ADD_NOTE);
        }
    }

    private void startAddPhoto() {
        PermissionUtils.checkStoragePermission(this, () ->
                ContentActivity.resolveAction(
                        MainActivity.this,
                        getNewNote(),
                        Constants.ACTION_TAKE_PHOTO,
                        0));
    }

    private void startAddSketch() {
        PermissionUtils.checkStoragePermission(this, () ->
                ContentActivity.resolveAction(
                        MainActivity.this,
                        getNewNote(),
                        Constants.ACTION_ADD_SKETCH,
                        0));
    }

    private void startAddFile() {
        PermissionUtils.checkStoragePermission(this, () ->
                ContentActivity.resolveAction(
                        MainActivity.this,
                        getNewNote(),
                        Constants.ACTION_ADD_FILES,
                        0));
    }
    // endregion

    // region fab
    private void initFloatButtons() {
        getBinding().menu.setMenuButtonColorNormal(accentColor());
        getBinding().menu.setMenuButtonColorPressed(accentColor());
        getBinding().menu.setOnMenuButtonLongClickListener(v -> {
            startActivityForResult(FabSortActivity.class, REQUEST_FAB_SORT);
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
            List<FabSortItem> fabSortItems = preferencesUtils.getFabSortResult();
            for (int i=0; i<fabs.length; i++) {
                fabs[i].setImageDrawable(ColorUtils.tintDrawable(getResources().getDrawable(fabSortItems.get(i).iconRes), Color.WHITE));
                fabs[i].setLabelText(getString(fabSortItems.get(i).nameRes));
            }
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
                editNote(getNewNote());
                break;
            case NOTEBOOK:
                editNotebook();
                break;
            case CATEGORY:
                editCategory();
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
                ContentActivity.editNote(this, note, REQUEST_ADD_NOTE));
    }

    private Note getNewNote() {
        Note note = ModelFactory.getNote();

        boolean isNotes = isNotesFragment();

        /*
         * Add notebook filed according to current fragment */
        Notebook notebook;
        if (isNotes && (notebook = ((NotesFragment) getCurrentFragment()).getNotebook()) != null) {
            note.setParentCode(notebook.getCode());
            note.setTreePath(notebook.getTreePath() + "|" + note.getCode());
        } else {
            // The default tree path of note is itself
            note.setTreePath(String.valueOf(note.getCode()));
        }

        /*
         * Add category field according to current fragment */
        Category category;
        if (isNotes && (category = ((NotesFragment) getCurrentFragment()).getCategory()) != null) {
            note.setTags(CategoryViewModel.getTags(Collections.singletonList(category)));
        }

        return note;
    }

    private void editNotebook() {
        Notebook notebook = ModelFactory.getNotebook();
        notebookEditDialog = NotebookEditDialog.newInstance(this, notebook, (notebookName, notebookColor) -> {
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
        });
        notebookEditDialog.show(getSupportFragmentManager(), "NotebookEditDialog");
    }

    private void saveNotebook(Notebook notebook) {
        notebookViewModel.saveModel(notebook).observe(this, notebookResource -> {
            if (notebookResource == null) {
                ToastUtils.makeToast(R.string.text_error_when_save);
                return;
            }
            switch (notebookResource.status) {
                case SUCCESS:
                    ToastUtils.makeToast(R.string.text_save_successfully);
                    Fragment fragment = getCurrentFragment();
                    if (fragment != null && fragment instanceof NotesFragment) {
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
        quickNoteEditor = new QuickNoteEditor.Builder()
                .setMindSnagging(param)
                .setOnAddAttachmentListener(mindSnagging -> showAttachmentPicker())
                .setOnAttachmentClickListener(this::resolveAttachmentClick)
                .setOnConfirmListener(this::saveMindSnagging)
                .build();
        quickNoteEditor.show(getSupportFragmentManager(), "mind snagging");
    }

    private void resolveAttachmentClick(Attachment attachment) {
        AttachmentHelper.resolveClickEvent(
                this,
                attachment,
                Collections.singletonList(attachment),
                attachment.getName());
    }

    private void saveMindSnagging(MindSnagging mindSnagging, Attachment attachment) {
        noteViewModel.saveSnagging(getNewNote(), mindSnagging, attachment).observe(this, noteResource -> {
            if (noteResource == null) {
                ToastUtils.makeToast(R.string.text_failed_to_modify_data);
                return;
            }
            switch (noteResource.status) {
                case SUCCESS:
                    ToastUtils.makeToast(R.string.text_save_successfully);
                    Fragment fragment = getCurrentFragment();
                    if (fragment != null && fragment instanceof NotesFragment) {
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

    private void showAttachmentPicker() {
        new AttachmentPickerDialog.Builder()
                .setAddLinkVisible(false)
                .setRecordVisible(false)
                .setVideoVisible(false)
                .build().show(getSupportFragmentManager(), "Attachment picker");
    }

    private void editCategory() {
        categoryEditDialog = CategoryEditDialog.newInstance(ModelFactory.getCategory(), this::saveCategory);
        categoryEditDialog.show(getSupportFragmentManager(), "CATEGORY_EDIT_DIALOG");
    }

    private void saveCategory(Category category) {
        categoryViewModel.saveModel(category).observe(this, categoryResource -> {
            if (categoryResource == null) {
                ToastUtils.makeToast(R.string.text_error_when_save);
                return;
            }
            switch (categoryResource.status) {
                case SUCCESS:
                    ToastUtils.makeToast(R.string.text_save_successfully);
                    Fragment fragment = getCurrentFragment();
                    if (fragment != null && fragment instanceof CategoriesFragment) {
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

    // region drawer
    private void setDrawerLayoutLocked(boolean lockDrawer){
        getBinding().drawerLayout.setDrawerLockMode(lockDrawer ?
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    private void initDrawerMenu() {
        getBinding().nav.getMenu().findItem(R.id.nav_notes).setChecked(true);
        getBinding().nav.setNavigationItemSelectedListener(menuItem -> {
            getBinding().drawerLayout.closeDrawers();
            switch (menuItem.getItemId()) {
                case R.id.nav_notes:
                case R.id.nav_notices:
                case R.id.nav_labels:
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
                case R.id.nav_sync:
                    SynchronizeUtils.syncOneDrive(this, REQUEST_SETTING_BACKUP, true);
                    break;
                case R.id.nav_settings:
                    SettingsActivity.start(this, REQUEST_SETTING);
                    break;
                case R.id.nav_notes:
                    toNotesFragment(true);
                    break;
                case R.id.nav_labels:
                    toCategoriesFragment();
                    break;
                case R.id.nav_archive:
                    startActivityForResult(ArchiveActivity.class, REQUEST_ARCHIVE);
                    break;
                case R.id.nav_trash:
                    startActivityForResult(TrashedActivity.class, REQUEST_TRASH);
                    break;
            }
        }, 500);
    }

    private void toNotesFragment(boolean checkDuplicate) {
        if (checkDuplicate && isNotesFragment()) return;
        NotesFragment notesFragment = NotesFragment.newInstance(Status.NORMAL);
        notesFragment.setScrollListener(onScrollListener);
        FragmentHelper.replace(this, notesFragment, R.id.fragment_container);
        new Handler().postDelayed(() -> getBinding().nav.getMenu().findItem(R.id.nav_notes).setChecked(true), 300);
    }

    private void toCategoriesFragment() {
        if (getCurrentFragment() instanceof CategoriesFragment) return;
        CategoriesFragment categoriesFragment = CategoriesFragment.newInstance();
        categoriesFragment.setScrollListener(onScrollListener);
        FragmentHelper.replace(this, categoriesFragment, R.id.fragment_container);
        new Handler().postDelayed(() -> getBinding().nav.getMenu().findItem(R.id.nav_labels).setChecked(true), 300);
    }

    private Fragment getCurrentFragment(){
        return getCurrentFragment(R.id.fragment_container);
    }

    private boolean isNotesFragment(){
        Fragment f = getCurrentFragment();
        return f != null && f instanceof NotesFragment;
    }

    private boolean isCategoryFragment() {
        Fragment f = getCurrentFragment();
        return f != null && f instanceof CategoriesFragment;
    }

    private boolean isDashboard() {
        Fragment f = getCurrentFragment();
        return f != null && (f instanceof NotesFragment
                || f instanceof CategoriesFragment);
    }
    // endregion

    // region notes change receiver
    private void regNoteChangeReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_NOTE_CHANGE_BROADCAST);
        notesChangedReceiver = new NotesChangedReceiver();
        registerReceiver(notesChangedReceiver, filter);
    }

    private class NotesChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNotesFragment()) ((NotesFragment) getCurrentFragment()).reload();
        }
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
                    getBinding().drawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
            }
            case R.id.action_search:
                SearchActivity.start(this, REQUEST_SEARCH);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.d("requestCode:" + requestCode + ", resultCode:" + resultCode);

        if (resultCode != RESULT_OK) return;

        handleAttachmentResult(requestCode, data);

        switch (requestCode) {
            case REQUEST_FAB_SORT:
                initFabSortItems();
                break;
            case REQUEST_NOTE_VIEW:
            case REQUEST_ADD_NOTE:
                if (isNotesFragment()) ((NotesFragment) getCurrentFragment()).reload();
                break;
            case REQUEST_TRASH:
                updateListIfNecessary();
                break;
            case REQUEST_ARCHIVE:
                updateListIfNecessary();
                break;
            case REQUEST_SEARCH:
                updateListIfNecessary();
                break;
            case REQUEST_PASSWORD:
                init();
                break;
            case REQUEST_SETTING:
                int[] changedTypes = data.getIntArrayExtra(SettingsActivity.KEY_CONTENT_CHANGE_TYPES);
                boolean drawerUpdated = false, listUpdated = false;
                for (int changedType : changedTypes) {
                    if (changedType == OnSettingsChangedListener.ChangedType.DRAWER_CONTENT.id && !drawerUpdated) {
                        setupHeader();
                        drawerUpdated = true;
                    }
                    if (changedType == OnSettingsChangedListener.ChangedType.NOTE_LIST_TYPE.id && !listUpdated) {
                        if (isNotesFragment()) {
                            toNotesFragment(false);
                        }
                        listUpdated = true;
                    }
                }
                break;
        }
    }

    private void updateListIfNecessary() {
        if (isNotesFragment()) ((NotesFragment) getCurrentFragment()).reload();
        if (isCategoryFragment()) ((CategoriesFragment) getCurrentFragment()).reload();
    }

    private void handleAttachmentResult(int requestCode, Intent data) {
        AttachmentHelper.resolveResult(this, requestCode, data);
    }

    @Override
    public void onBackPressed() {
        if (isDashboard()){
            if (getBinding().drawerLayout.isDrawerOpen(GravityCompat.START)){
                getBinding().drawerLayout.closeDrawer(GravityCompat.START);
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
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(notesChangedReceiver);
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, int selectedColor) {
        if (notebookEditDialog != null) {
            notebookEditDialog.updateUIBySelectedColor(selectedColor);
        }
        if (categoryEditDialog != null) {
            categoryEditDialog.updateUIBySelectedColor(selectedColor);
        }
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment instanceof NotesFragment) {
            ((NotesFragment) currentFragment).setSelectedColor(selectedColor);
        }
        if (currentFragment instanceof CategoriesFragment) {
            ((CategoriesFragment) currentFragment).setSelectedColor(selectedColor);
        }
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
    public void onAttachingFileErrorOccurred(Attachment attachment) {
        ToastUtils.makeToast(R.string.failed_to_save_attachment);
    }

    @Override
    public void onAttachingFileFinished(Attachment attachment) {
        if (AttachmentHelper.checkAttachment(attachment)) {
            if (quickNoteEditor != null) {
                quickNoteEditor.setAttachment(attachment);
            }
        } else {
            ToastUtils.makeToast(R.string.failed_to_save_attachment);
        }
    }

    @Override
    public void onCategoryLoadStateChanged(me.shouheng.notepal.model.data.Status status) {
        onLoadStateChanged(status);
    }

    @Override
    public void onNoteLoadStateChanged(me.shouheng.notepal.model.data.Status status) {
        onLoadStateChanged(status);
    }

    private void onLoadStateChanged(me.shouheng.notepal.model.data.Status status) {
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
