package me.shouheng.notepal.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.kennyc.bottomsheet.BottomSheet;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.DimenHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.shouheng.commons.BaseConstants;
import me.shouheng.commons.activity.CommonActivity;
import me.shouheng.commons.activity.ContainerActivity;
import me.shouheng.commons.event.PageName;
import me.shouheng.commons.event.RxMessage;
import me.shouheng.commons.helper.ActivityHelper;
import me.shouheng.commons.helper.FragmentHelper;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.utils.IntentUtils;
import me.shouheng.commons.utils.LogUtils;
import me.shouheng.commons.utils.PalmUtils;
import me.shouheng.commons.utils.PermissionUtils;
import me.shouheng.commons.utils.PermissionUtils.Permission;
import me.shouheng.commons.utils.PersistData;
import me.shouheng.commons.utils.StringUtils;
import me.shouheng.commons.utils.ToastUtils;
import me.shouheng.commons.widget.recycler.CustomRecyclerScrollViewListener;
import me.shouheng.data.ModelFactory;
import me.shouheng.data.entity.Attachment;
import me.shouheng.data.entity.Category;
import me.shouheng.data.entity.Note;
import me.shouheng.data.entity.Notebook;
import me.shouheng.data.entity.QuickNote;
import me.shouheng.data.model.enums.FabSortItem;
import me.shouheng.data.model.enums.Status;
import me.shouheng.data.store.NotebookStore;
import me.shouheng.data.store.NotesStore;
import me.shouheng.notepal.Constants;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.common.exception.NoteNotFoundException;
import me.shouheng.notepal.common.preferences.UserPreferences;
import me.shouheng.notepal.databinding.ActivityMainBinding;
import me.shouheng.notepal.databinding.LayoutActionViewBottomDialogBinding;
import me.shouheng.notepal.databinding.LayoutHeaderBinding;
import me.shouheng.notepal.dialog.CategoryEditDialog;
import me.shouheng.notepal.dialog.NotebookEditDialog;
import me.shouheng.notepal.dialog.QuickNoteDialog;
import me.shouheng.notepal.fragment.CategoriesFragment;
import me.shouheng.notepal.fragment.NoteFragment;
import me.shouheng.notepal.fragment.NoteViewFragment;
import me.shouheng.notepal.fragment.NotesFragment;
import me.shouheng.notepal.fragment.StatisticsFragment;
import me.shouheng.notepal.fragment.SupportFragment;
import me.shouheng.notepal.fragment.TimeLineFragment;
import me.shouheng.notepal.fragment.setting.SettingsFragment;
import me.shouheng.notepal.manager.FileManager;
import me.shouheng.notepal.util.SynchronizeUtils;
import me.shouheng.notepal.vm.MainViewModel;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static me.shouheng.commons.event.UMEvent.FAB_SORT_ITEM_CAPTURE;
import static me.shouheng.commons.event.UMEvent.FAB_SORT_ITEM_CATEGORY;
import static me.shouheng.commons.event.UMEvent.FAB_SORT_ITEM_DRAFT;
import static me.shouheng.commons.event.UMEvent.FAB_SORT_ITEM_IMAGE;
import static me.shouheng.commons.event.UMEvent.FAB_SORT_ITEM_NOTE;
import static me.shouheng.commons.event.UMEvent.FAB_SORT_ITEM_NOTEBOOK;
import static me.shouheng.commons.event.UMEvent.FAB_SORT_ITEM_QUICK_NOTE;
import static me.shouheng.commons.event.UMEvent.INTENT_ACTION_RESTART_APP;
import static me.shouheng.commons.event.UMEvent.INTENT_ACTION_SEND;
import static me.shouheng.commons.event.UMEvent.INTENT_ACTION_VIEW;
import static me.shouheng.commons.event.UMEvent.INTENT_APP_WIDGET_ACTION_CAPTURE;
import static me.shouheng.commons.event.UMEvent.INTENT_APP_WIDGET_ACTION_CREATE_NOTE;
import static me.shouheng.commons.event.UMEvent.INTENT_APP_WIDGET_ACTION_CREATE_SKETCH;
import static me.shouheng.commons.event.UMEvent.INTENT_APP_WIDGET_ACTION_LAUNCH_APP;
import static me.shouheng.commons.event.UMEvent.INTENT_APP_WIDGET_ACTION_LIST_ITEM_CLICKED;
import static me.shouheng.commons.event.UMEvent.INTENT_SHORTCUT_ACTION_CAPTURE;
import static me.shouheng.commons.event.UMEvent.INTENT_SHORTCUT_ACTION_CREATE_NOTE;
import static me.shouheng.commons.event.UMEvent.INTENT_SHORTCUT_ACTION_SEARCH_NOTE;
import static me.shouheng.commons.event.UMEvent.INTENT_SHORTCUT_ACTION_VIEW_NOTE;
import static me.shouheng.commons.event.UMEvent.MAIN_MENU_ITEM_ARCHIVED;
import static me.shouheng.commons.event.UMEvent.MAIN_MENU_ITEM_CATEGORIES;
import static me.shouheng.commons.event.UMEvent.MAIN_MENU_ITEM_NOTEBOOKS;
import static me.shouheng.commons.event.UMEvent.MAIN_MENU_ITEM_SETTINGS;
import static me.shouheng.commons.event.UMEvent.MAIN_MENU_ITEM_SHARE_APP;
import static me.shouheng.commons.event.UMEvent.MAIN_MENU_ITEM_STATISTIC;
import static me.shouheng.commons.event.UMEvent.MAIN_MENU_ITEM_SUPPORT;
import static me.shouheng.commons.event.UMEvent.MAIN_MENU_ITEM_TIMELINE;
import static me.shouheng.commons.event.UMEvent.MAIN_MENU_ITEM_TRASHED;
import static me.shouheng.commons.event.UMEvent.PAGE_MAIN;
import static me.shouheng.notepal.Constants.FAB_ACTION_CAPTURE;
import static me.shouheng.notepal.Constants.FAB_ACTION_CREATE_SKETCH;
import static me.shouheng.notepal.Constants.FAB_ACTION_PICK_IMAGE;
import static me.shouheng.notepal.Constants.SHARE_IMAGE_ASSETS_NAME_1;
import static me.shouheng.notepal.Constants.SHARE_IMAGE_NAME_1;
import static me.shouheng.notepal.Constants.SHORTCUT_ACTION_CAPTURE;
import static me.shouheng.notepal.Constants.SHORTCUT_ACTION_CREATE_NOTE;
import static me.shouheng.notepal.Constants.SHORTCUT_ACTION_SEARCH_NOTE;
import static me.shouheng.notepal.Constants.SHORTCUT_ACTION_VIEW_NOTE;
import static me.shouheng.notepal.Constants.SHORTCUT_EXTRA_NOTE_CODE;

@PageName(name = PAGE_MAIN)
public class MainActivity extends CommonActivity<ActivityMainBinding>
        implements NotesFragment.OnNotesInteractListener, CategoriesFragment.CategoriesInteraction {

    private FloatingActionButton[] fabs;
    private long onBackPressed;
    private Drawer drawer;
    private RecyclerView.OnScrollListener onScrollListener;
    private MainViewModel viewModel;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        viewModel = getViewModel(MainViewModel.class);

        checkPsdIfNecessary(savedInstanceState);

        addSubscriptions();
    }

    private void checkPsdIfNecessary(Bundle savedInstanceState) {
        boolean psdRequired = PersistData.getBoolean(R.string.key_security_psd_required, false);
        String psd = PersistData.getString(R.string.key_security_psd, null);
        if (psdRequired && PalmApp.passwordNotChecked() && !TextUtils.isEmpty(psd)) {
            ActivityHelper.open(LockActivity.class)
                    .setAction(LockActivity.ACTION_REQUIRE_PASSWORD)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    .launch(getContext());
        } else {
            everything(savedInstanceState);
        }
    }

    private void addSubscriptions() {
        addSubscription(RxMessage.class, RxMessage.CODE_SORT_FLOAT_BUTTONS, rxMessage -> configFabSortItems());
        addSubscription(RxMessage.class, RxMessage.CODE_PASSWORD_CHECK_PASSED, rxMessage -> everything(null));
        addSubscription(RxMessage.class, RxMessage.CODE_PASSWORD_CHECK_FAILED, rxMessage -> finish());
        viewModel.getUpdateNotebookLiveData().observe(this, resources -> {
            assert resources != null;
            switch (resources.status) {
                case SUCCESS:
                    postEvent(new RxMessage(RxMessage.CODE_NOTE_DATA_CHANGED, null));
                    ToastUtils.makeToast(R.string.text_save_successfully);
                    break;
                case LOADING:
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed);
                    break;
            }
        });
        viewModel.getSaveNoteLiveData().observe(this, resources -> {
            assert resources != null;
            switch (resources.status) {
                case SUCCESS:
                    postEvent(new RxMessage(RxMessage.CODE_NOTE_DATA_CHANGED, null));
                    ToastUtils.makeToast(R.string.text_save_successfully);
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed);
                    break;
            }
        });
        viewModel.getSaveCategoryLiveData().observe(this, resources -> {
            assert resources != null;
            switch (resources.status) {
                case SUCCESS:
                    Fragment f = getCurrentFragment();
                    if (f instanceof CategoriesFragment) {
                        ((CategoriesFragment) f).addCategory(resources.data);
                    } else {
                        postEvent(new RxMessage(RxMessage.CODE_CATEGORY_DATA_CHANGED, null));
                    }
                    ToastUtils.makeToast(R.string.text_save_successfully);
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed);
                    break;
            }
        });
    }

    private void everything(Bundle savedInstanceState) {
        /* Handle all intents. */
        handleIntent(savedInstanceState);

        /* Config toolbar */
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

        /* Custom left drawer */
        configDrawer(savedInstanceState);

        /* Initialize float action buttons */
        initFloatButtons();

        /* Initialize the FABs */
        configFabSortItems();

        /* Load the notes fragment. */
        toNotesFragment();
    }

    private void configDrawer(Bundle savedInstanceState) {
        DividerDrawerItem divider = new DividerDrawerItem();
        PrimaryDrawerItem itemNotes = ColorUtils.getColoredDrawerMenuItem(
                R.string.drawer_menu_notebooks, R.drawable.ic_book, 0, true);
        PrimaryDrawerItem itemTags = ColorUtils.getColoredDrawerMenuItem(
                R.string.drawer_menu_categories, R.drawable.ic_view_module_white_24dp, 1, true);
        PrimaryDrawerItem itemArchive = ColorUtils.getColoredDrawerMenuItem(
                R.string.drawer_menu_archive, R.drawable.ic_archive_grey, 2, false);
        PrimaryDrawerItem itemTrash = ColorUtils.getColoredDrawerMenuItem(
                R.string.drawer_menu_trash, R.drawable.ic_trash_black, 3, false);
        PrimaryDrawerItem itemSetting = ColorUtils.getColoredDrawerMenuItem(
                R.string.drawer_menu_setting, R.drawable.ic_settings_black, 4, false);
        PrimaryDrawerItem itemDonate = ColorUtils.getColoredDrawerMenuItem(
                R.string.drawer_menu_donate, R.drawable.ic_monetization_on_black_24dp, 5, false);
        PrimaryDrawerItem itemStatistic = ColorUtils.getColoredDrawerMenuItem(
                R.string.drawer_menu_statistics, R.drawable.ic_equalizer_grey_24dp, 6, false);
        PrimaryDrawerItem itemTimeLine = ColorUtils.getColoredDrawerMenuItem(
                R.string.drawer_menu_time_line, R.drawable.ic_timeline_black_24dp, 7, false);
        PrimaryDrawerItem itemShare = ColorUtils.getColoredDrawerMenuItem(
                R.string.drawer_menu_share, R.drawable.ic_share_white, 8, false);

        LayoutHeaderBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.layout_header, null, false);
        Glide.with(getContext()).load(isDarkTheme() ? Constants.IMAGE_HEADER_DARK : Constants.IMAGE_HEADER_LIGHT).into(binding.iv);

        drawer = new DrawerBuilder().withActivity(this)
                .withHasStableIds(true)
                .addDrawerItems(itemNotes, itemTags, itemTimeLine, divider, itemStatistic, itemArchive,
                        itemTrash, divider, itemSetting, itemShare, itemDonate)
                .withMultiSelect(false)
                .withSelectedItem(0)
                .withSliderBackgroundColorRes(isDarkTheme() ? R.color.dark_theme_background : R.color.light_theme_background)
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    if (drawerItem == null) return false;
                    switch ((int) drawerItem.getIdentifier()) {
                        case 0:
                            drawer.closeDrawer();
                            toNotesFragment();
                            MobclickAgent.onEvent(this, MAIN_MENU_ITEM_NOTEBOOKS);
                            break;
                        case 1:
                            drawer.closeDrawer();
                            toCategoriesFragment();
                            MobclickAgent.onEvent(this, MAIN_MENU_ITEM_CATEGORIES);
                            break;
                        case 2:
                            ActivityHelper.open(ListActivity.class)
                                    .put(ListActivity.ARGS_KEY_LIST_TYPE, Status.ARCHIVED)
                                    .launch(getContext());
                            MobclickAgent.onEvent(this, MAIN_MENU_ITEM_ARCHIVED);
                            break;
                        case 3:
                            ActivityHelper.open(ListActivity.class)
                                    .put(ListActivity.ARGS_KEY_LIST_TYPE, Status.TRASHED)
                                    .launch(getContext());
                            MobclickAgent.onEvent(this, MAIN_MENU_ITEM_TRASHED);
                            break;
                        case 4:
                            SettingsActivity.open(SettingsFragment.class).launch(this);
                            MobclickAgent.onEvent(this, MAIN_MENU_ITEM_SETTINGS);
                            break;
                        case 5:
                            ContainerActivity.open(SupportFragment.class).launch(this);
                            MobclickAgent.onEvent(this, MAIN_MENU_ITEM_SUPPORT);
                            break;
                        case 6:
                            ContainerActivity.open(StatisticsFragment.class).launch(this);
                            MobclickAgent.onEvent(this, MAIN_MENU_ITEM_STATISTIC);
                            break;
                        case 7:
                            ContainerActivity.open(TimeLineFragment.class).launch(this);
                            MobclickAgent.onEvent(this, MAIN_MENU_ITEM_TIMELINE);
                            break;
                        case 8: {
                            // Share
                            MobclickAgent.onEvent(this, MAIN_MENU_ITEM_SHARE_APP);
                            PermissionUtils.checkStoragePermission(this, () ->
                                    Observable
                                            .create((ObservableOnSubscribe<Uri>) emitter -> {
                                                Bitmap share = FileManager.getImageFromAssetsFile(getContext(), SHARE_IMAGE_ASSETS_NAME_1);
                                                Uri uri = FileManager.getShareImageUri(share, SHARE_IMAGE_NAME_1);
                                                if (uri != null) {
                                                    emitter.onNext(uri);
                                                } else {
                                                    emitter.onError(new NullPointerException());
                                                }
                                            })
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(uri -> {
                                                boolean isEn = "en".equalsIgnoreCase(PalmUtils.getStringCompact(R.string.language_code));
                                                String download = isEn ? Constants.GOOGLE_PLAY_WEB_PAGE : Constants.COOL_APK_DOWNLOAD_PAGE;
                                                Intent shareIntent = new Intent();
                                                shareIntent.setAction(Intent.ACTION_SEND);
                                                shareIntent.setType(BaseConstants.MIME_TYPE_IMAGE);
                                                if (uri != null) shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                                shareIntent.putExtra(Intent.EXTRA_SUBJECT, PalmUtils.getStringCompact(R.string.share_title));
                                                shareIntent.putExtra(Intent.EXTRA_TEXT, StringUtils.formatString(R.string.share_content, download));
                                                startActivity(Intent.createChooser(shareIntent, PalmUtils.getStringCompact(R.string.text_send_to)));
                                            }, throwable -> ToastUtils.makeToast(throwable.getMessage())));
                            break;
                        }
                    }
                    return true;
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .withHeader(binding.getRoot())
                .withHeaderHeight(DimenHolder.fromDp(180))
                .build();
    }

    private void handleIntent(Bundle savedInstanceState) {
        /* Don't handle the intent again (when the activity recreate). */
        if (savedInstanceState != null) return;

        Intent intent = getIntent();
        String action = intent.getAction();

        /* Do nothing when the action is null. */
        if (action == null) return;

        switch (action) {
            /* Actions shortcuts, check at first and then send the note fragment. */
            case SHORTCUT_ACTION_SEARCH_NOTE:
                MobclickAgent.onEvent(this, INTENT_SHORTCUT_ACTION_SEARCH_NOTE);
                ActivityHelper.open(SearchActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        .launch(getContext());
                break;
            case SHORTCUT_ACTION_CAPTURE:
                MobclickAgent.onEvent(this, INTENT_SHORTCUT_ACTION_CAPTURE);
                PermissionUtils.checkPermissions(this, () ->
                        ContainerActivity.open(NoteFragment.class)
                                .put(NoteFragment.ARGS_KEY_ACTION, SHORTCUT_ACTION_CAPTURE)
                                .launch(getContext()), Permission.CAMERA, Permission.STORAGE);
                break;
            case SHORTCUT_ACTION_CREATE_NOTE:
                MobclickAgent.onEvent(this, INTENT_SHORTCUT_ACTION_CREATE_NOTE);
                PermissionUtils.checkStoragePermission(this, () ->
                        ContainerActivity.open(NoteFragment.class)
                                .put(NoteFragment.ARGS_KEY_ACTION, SHORTCUT_ACTION_CREATE_NOTE)
                                .launch(MainActivity.this));
                break;
            case SHORTCUT_ACTION_VIEW_NOTE:
                MobclickAgent.onEvent(this, INTENT_SHORTCUT_ACTION_VIEW_NOTE);
                if (!intent.hasExtra(SHORTCUT_EXTRA_NOTE_CODE)) {
                    ToastUtils.makeToast(R.string.text_note_not_found);
                    return;
                }
                long code = intent.getLongExtra(SHORTCUT_EXTRA_NOTE_CODE, 0L);
                Observable
                        .create((ObservableOnSubscribe<Note>) emitter -> {
                            Note note = NotesStore.getInstance().get(code);
                            if (note != null) {
                                emitter.onNext(note);
                            } else {
                                emitter.onError(new NoteNotFoundException(code));
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(note -> PermissionUtils.checkStoragePermission(this, () ->
                                        ContainerActivity.open(NoteFragment.class)
                                                .put(NoteFragment.ARGS_KEY_NOTE, (Serializable) note)
                                                .put(NoteFragment.ARGS_KEY_ACTION, action)
                                                .launch(getContext())),
                                throwable -> ToastUtils.makeToast(R.string.text_note_not_found));
                break;

            /* Actions registered in Manifest, check at first and then send to the note fragment. */
            case Intent.ACTION_SEND:
            case Intent.ACTION_SEND_MULTIPLE:
                MobclickAgent.onEvent(this, INTENT_ACTION_SEND);
                PermissionUtils.checkStoragePermission(this, () -> {
                    if (IntentUtils.checkAction(intent, Intent.ACTION_SEND, Intent.ACTION_SEND_MULTIPLE)
                            && intent.getType() != null) {
                        ContainerActivity.open(NoteFragment.class)
                                .put(NoteFragment.ARGS_KEY_ACTION, action)
                                .put(NoteFragment.ARGS_KEY_INTENT, intent)
                                .launch(getContext());
                    }
                });
                break;
            case Intent.ACTION_VIEW:
            case Intent.ACTION_EDIT:
                MobclickAgent.onEvent(this, INTENT_ACTION_VIEW);
                PermissionUtils.checkStoragePermission(this, () -> {
                    if (IntentUtils.checkAction(intent, Intent.ACTION_EDIT, Intent.ACTION_VIEW)
                            && intent.getType() != null) {
                        Uri uri = intent.getData();
                        String path = FileManager.getPath(this, uri);
                        LayoutActionViewBottomDialogBinding binding = DataBindingUtil.inflate(
                                LayoutInflater.from(getContext()), R.layout.layout_action_view_bottom_dialog, null, false);
                        binding.tvPath.setText(path);
                        BottomSheet bottomSheet = new BottomSheet.Builder(this)
                                .setStyle(isDarkTheme() ? R.style.BottomSheet_Dark : R.style.BottomSheet)
                                .setView(binding.getRoot()).create();
                        binding.btnCancel.setOnClickListener(v -> bottomSheet.dismiss());
                        binding.btnCreate.setOnClickListener(v -> {
                            if (Constants.MIME_TYPE_OF_PLAIN_TEXT.equals(intent.getType())) {
                                ContainerActivity.open(NoteFragment.class)
                                        .put(NoteFragment.ARGS_KEY_ACTION, action)
                                        .put(NoteFragment.ARGS_KEY_INTENT, intent)
                                        .launch(getContext());
                                bottomSheet.dismiss();
                            } else {
                                ToastUtils.makeToast(R.string.note_action_view_file_type_not_support);
                            }
                        });
                        new Handler().postDelayed(bottomSheet::show, 500);
                    }
                });
                break;

            /* Actions from AppWidget. */
            case Constants.APP_WIDGET_ACTION_CREATE_NOTE: {
                MobclickAgent.onEvent(this, INTENT_APP_WIDGET_ACTION_CREATE_NOTE);
                PermissionUtils.checkStoragePermission(this, () ->
                        handleAppWidget(intent, pair -> {
                            Note note = ModelFactory.getNote(pair.first, pair.second);
                            ContainerActivity.open(NoteFragment.class)
                                    .put(NoteFragment.ARGS_KEY_NOTE, (Serializable) note)
                                    .launch(getContext());
                        }));
                break;
            }
            case Constants.APP_WIDGET_ACTION_LIST_ITEM_CLICLED: {
                MobclickAgent.onEvent(this, INTENT_APP_WIDGET_ACTION_LIST_ITEM_CLICKED);
                Note note;
                if (intent.hasExtra(Constants.APP_WIDGET_EXTRA_NOTE)
                        && (note = intent.getParcelableExtra(Constants.APP_WIDGET_EXTRA_NOTE)) != null) {
                    ContainerActivity.open(NoteViewFragment.class)
                            .put(NoteViewFragment.ARGS_KEY_NOTE, (Serializable) note)
                            .put(NoteViewFragment.ARGS_KEY_IS_PREVIEW, false)
                            .launch(this);
                }
                break;
            }
            case Constants.APP_WIDGET_ACTION_LAUNCH_APP:
                MobclickAgent.onEvent(this, INTENT_APP_WIDGET_ACTION_LAUNCH_APP);
                /* DO NOTHING JUST LAUNCH THE APP. */
                break;
            case Constants.APP_WIDGET_ACTION_CAPTURE:
                MobclickAgent.onEvent(this, INTENT_APP_WIDGET_ACTION_CAPTURE);
                PermissionUtils.checkPermissions(this, () ->
                        handleAppWidget(intent, pair -> {
                            Note note = ModelFactory.getNote(pair.first, pair.second);
                            ContainerActivity.open(NoteFragment.class)
                                    .put(NoteFragment.ARGS_KEY_ACTION, action)
                                    .put(NoteFragment.ARGS_KEY_NOTE, (Serializable) note)
                                    .launch(getContext());
                        }), Permission.STORAGE, Permission.CAMERA);
                break;
            case Constants.APP_WIDGET_ACTION_CREATE_SKETCH:
                MobclickAgent.onEvent(this, INTENT_APP_WIDGET_ACTION_CREATE_SKETCH);
                PermissionUtils.checkStoragePermission(this, () ->
                        handleAppWidget(intent, pair -> {
                            Note note = ModelFactory.getNote(pair.first, pair.second);
                            ContainerActivity.open(NoteFragment.class)
                                    .put(NoteFragment.ARGS_KEY_ACTION, action)
                                    .put(NoteFragment.ARGS_KEY_NOTE, (Serializable) note)
                                    .launch(getContext());
                        }));
                break;

            /* Notification action handler. */
            case Constants.ACTION_RESTART_APP:
                MobclickAgent.onEvent(this, INTENT_ACTION_RESTART_APP);
                recreate();
                break;
        }
    }

    private void handleAppWidget(Intent intent, OnGetAppWidgetCondition onGetAppWidgetCondition) {
        /* Get notebook first. */
        int widgetId = intent.getIntExtra(Constants.APP_WIDGET_EXTRA_WIDGET_ID, 0);
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences(
                Constants.APP_WIDGET_PREFERENCES_NAME, Context.MODE_MULTI_PROCESS);
        String key = Constants.APP_WIDGET_PREFERENCE_KEY_NOTEBOOK_CODE_PREFIX + String.valueOf(widgetId);
        long notebookCode = sharedPreferences.getLong(key, 0);
        if (notebookCode != 0) {
            Disposable disposable = Observable
                    .create((ObservableOnSubscribe<Notebook>) emitter -> {
                        Notebook notebook = NotebookStore.getInstance().get(notebookCode);
                        emitter.onNext(notebook);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(notebook -> {
                        if (onGetAppWidgetCondition != null) {
                            onGetAppWidgetCondition.onGetCondition(new Pair<>(notebook, null));
                        }
                    }, throwable -> ToastUtils.makeToast(R.string.text_notebook_not_found));
        } else {
            if (onGetAppWidgetCondition != null) {
                onGetAppWidgetCondition.onGetCondition(new Pair<>(null, null));
            }
        }
    }

    private void initFloatButtons() {
        getBinding().menu.setMenuButtonColorNormal(accentColor());
        getBinding().menu.setMenuButtonColorPressed(accentColor());
        getBinding().menu.setOnMenuButtonLongClickListener(v -> {
            ActivityHelper.start(this, FabSortActivity.class);
            return false;
        });
        getBinding().menu.setOnMenuToggleListener(opened ->
                getBinding().rlMenuContainer.setVisibility(opened ? View.VISIBLE : View.GONE));
        getBinding().rlMenuContainer.setOnClickListener(view ->
                getBinding().menu.close(true));
        getBinding().rlMenuContainer.setBackgroundResource(
                isDarkTheme() ? R.color.menu_container_dark : R.color.menu_container_light);

        fabs = new FloatingActionButton[]{getBinding().fab1,
                getBinding().fab2, getBinding().fab3, getBinding().fab4, getBinding().fab5};

        for (int i=0; i<fabs.length; i++) {
            fabs[i].setColorNormal(accentColor());
            fabs[i].setColorPressed(accentColor());
            int finalI = i;
            fabs[i].setOnClickListener(view -> resolveFabClick(finalI));
        }

        onScrollListener = new CustomRecyclerScrollViewListener() {
            @Override
            public void show() {
                getBinding().menu.animate()
                        .translationY(0)
                        .setInterpolator(new DecelerateInterpolator(2))
                        .start();
            }

            @Override
            public void hide() {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) getBinding().menu.getLayoutParams();
                int fabMargin = lp.bottomMargin;
                getBinding().menu.animate()
                        .translationY(getBinding().menu.getHeight() + fabMargin)
                        .setInterpolator(new AccelerateInterpolator(2.0f))
                        .start();
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                LogUtils.d("onScrollStateChanged: ");
                if (newState == SCROLL_STATE_IDLE) {
                    LogUtils.d("onScrollStateChanged: SCROLL_STATE_IDLE");
                }
            }
        };
    }

    private void configFabSortItems() {
        try {
            List<FabSortItem> fabSortItems = UserPreferences.getInstance().getFabSortResult();
            for (int i=0; i<fabs.length; i++) {
                fabs[i].setImageDrawable(ColorUtils.tintDrawable(fabSortItems.get(i).iconRes, Color.WHITE));
                fabs[i].setLabelText(getString(fabSortItems.get(i).nameRes));
            }
        } catch (Exception e) {
            LogUtils.d("configFabSortItems, error occurred : " + e);
            UserPreferences.getInstance().setFabSortResult(UserPreferences.defaultFabOrders);
        }
    }

    private void resolveFabClick(int index) {
        getBinding().menu.close(true);
        FabSortItem fabSortItem = UserPreferences.getInstance().getFabSortResult().get(index);
        switch (fabSortItem) {
            case NOTE:
                MobclickAgent.onEvent(this, FAB_SORT_ITEM_NOTE);
                PermissionUtils.checkStoragePermission(this, () ->
                        ContainerActivity.open(NoteFragment.class)
                                .put(NoteFragment.ARGS_KEY_NOTE, (Serializable) getNewNote())
                                .launch(getContext()));
                break;
            case NOTEBOOK: {
                MobclickAgent.onEvent(this, FAB_SORT_ITEM_NOTEBOOK);
                Notebook notebook = ModelFactory.getNotebook();
                NotebookEditDialog.newInstance(notebook, (notebookName, notebookColor) -> {
                    notebook.setTitle(notebookName);
                    notebook.setColor(notebookColor);
                    notebook.setCount(0);
                    notebook.setTreePath(String.valueOf(notebook.getCode()));
                    Notebook parent;
                    Fragment f = getCurrentFragment();
                    if (f instanceof NotesFragment && (parent = ((NotesFragment) f).getNotebook()) != null) {
                        notebook.setParentCode(parent.getCode());
                        notebook.setTreePath(parent.getTreePath() + "|" + notebook.getCode());
                    }
                    viewModel.saveNotebook(notebook);
                }).show(getSupportFragmentManager(), "NOTEBOOK EDITOR");
                break;
            }
            case CATEGORY:
                MobclickAgent.onEvent(this, FAB_SORT_ITEM_CATEGORY);
                CategoryEditDialog.newInstance(ModelFactory.getCategory(),
                        category -> viewModel.saveCategory(category)
                ).show(getSupportFragmentManager(), "CATEGORY EDITOR");
                break;
            case QUICK_NOTE:
                MobclickAgent.onEvent(this, FAB_SORT_ITEM_QUICK_NOTE);
                PermissionUtils.checkStoragePermission(this, () ->
                        editQuickNote(ModelFactory.getQuickNote()));
                break;
            case DRAFT:
                MobclickAgent.onEvent(this, FAB_SORT_ITEM_DRAFT);
                PermissionUtils.checkStoragePermission(this, () ->
                        ContainerActivity.open(NoteFragment.class)
                                .put(NoteFragment.ARGS_KEY_ACTION, FAB_ACTION_CREATE_SKETCH)
                                .put(NoteFragment.ARGS_KEY_NOTE, (Serializable) getNewNote())
                                .launch(MainActivity.this));
                break;
            case IMAGE:
                MobclickAgent.onEvent(this, FAB_SORT_ITEM_IMAGE);
                PermissionUtils.checkStoragePermission(this, () ->
                        ContainerActivity.open(NoteFragment.class)
                                .put(NoteFragment.ARGS_KEY_ACTION, FAB_ACTION_PICK_IMAGE)
                                .put(NoteFragment.ARGS_KEY_NOTE, (Serializable) getNewNote())
                                .launch(MainActivity.this));
                break;
            case CAPTURE:
                MobclickAgent.onEvent(this, FAB_SORT_ITEM_CAPTURE);
                PermissionUtils.checkPermissions(this, () ->
                        ContainerActivity.open(NoteFragment.class)
                                .put(NoteFragment.ARGS_KEY_ACTION, FAB_ACTION_CAPTURE)
                                .put(NoteFragment.ARGS_KEY_NOTE, (Serializable) getNewNote())
                                .launch(MainActivity.this), Permission.CAMERA, Permission.STORAGE);
                break;
        }
    }

    /**
     * Get a new note according to current circumstance. This method will get the notebook and
     * category field from the NotesFragment if exists.
     *
     * @return the new note model
     */
    private Note getNewNote() {
        Fragment f = getCurrentFragment();
        boolean isNotes = f instanceof NotesFragment;
        Notebook notebook = isNotes ? ((NotesFragment) f).getNotebook() : null;
        Category category = isNotes ? ((NotesFragment) f).getCategory() : null;
        return ModelFactory.getNote(notebook, category);
    }

    private void editQuickNote(@NonNull QuickNote param) {
        QuickNoteDialog.newInstance(param, new QuickNoteDialog.DialogInteraction() {
            @Override
            public void onCancel(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onConfirm(Dialog dialog, QuickNote quickNote, Attachment attachment) {
                viewModel.saveQuickNote(getNewNote(), quickNote, attachment);
                dialog.dismiss();
            }
        }).show(getSupportFragmentManager(), "QUICK NOTE");
    }

    private void setDrawerLayoutLocked(boolean lockDrawer) {
        drawer.getDrawerLayout().setDrawerLockMode(lockDrawer ?
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    private void toNotesFragment() {
        if (getCurrentFragment() instanceof NotesFragment) return;
        NotesFragment notesFragment = FragmentHelper.open(NotesFragment.class)
                .put(NotesFragment.ARGS_KEY_STATUS, Status.NORMAL).get();
        notesFragment.setScrollListener(onScrollListener);
        FragmentHelper.replace(this, notesFragment, R.id.fragment_container, false);
    }

    private void toCategoriesFragment() {
        if (getCurrentFragment() instanceof CategoriesFragment) return;
        CategoriesFragment categoriesFragment = FragmentHelper.open(CategoriesFragment.class).get();
        categoriesFragment.setScrollListener(onScrollListener);
        FragmentHelper.replace(this, categoriesFragment, R.id.fragment_container, false);
    }

    private Fragment getCurrentFragment(){
        return getCurrentFragment(R.id.fragment_container);
    }

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
    public void onBackPressed() {
        Fragment f = getCurrentFragment();
        if (f instanceof NotesFragment || f instanceof CategoriesFragment) {
            if (drawer.isDrawerOpen()) {
                drawer.closeDrawer();
            } else {
                if (getBinding().menu.isOpened()) {
                    getBinding().menu.close(true);
                    return;
                }
                if (f instanceof NotesFragment) {
                    if (((NotesFragment) f).isTopStack()) {
                        againExit();
                    } else {
                        super.onBackPressed();
                    }
                } else {
                    drawer.setSelection(0);
                    toNotesFragment();
                }
            }
        } else {
            super.onBackPressed();
        }
    }

    private void againExit() {
        if (onBackPressed + Constants.AGAIN_EXIT_TIME_INTERVAL > System.currentTimeMillis()) {
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
        NotesFragment notesFragment = FragmentHelper.open(NotesFragment.class)
                .put(NotesFragment.ARGS_KEY_NOTEBOOK, notebook)
                .put(NotesFragment.ARGS_KEY_STATUS, Status.NORMAL)
                .get();
        notesFragment.setScrollListener(onScrollListener);
        FragmentHelper.replace(this, notesFragment, R.id.fragment_container, true);
    }

    @Override
    public void onCategorySelected(Category category) {
        NotesFragment notesFragment = FragmentHelper.open(NotesFragment.class)
                .put(NotesFragment.ARGS_KEY_CATEGORY, category)
                .put(NotesFragment.ARGS_KEY_STATUS, Status.NORMAL)
                .get();
        notesFragment.setScrollListener(onScrollListener);
        FragmentHelper.replace(this, notesFragment, R.id.fragment_container, true);
    }

    @Override
    public void onResumeToCategory() {
        setDrawerLayoutLocked(false);
    }

    @Override
    public void onActivityAttached(boolean isTopStack) {
        setDrawerLayoutLocked(!isTopStack);
    }

    public interface OnGetAppWidgetCondition {

        /**
         * The callback for app widget condition, used for
         * {@link #handleAppWidget(Intent, OnGetAppWidgetCondition)}
         *
         * @param pair the pair contains app widget notebook and category
         */
        void onGetCondition(Pair<Notebook, Category> pair);
    }
}
