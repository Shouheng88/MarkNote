package me.shouheng.notepal.activity;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Pair;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.shouheng.commons.activity.PermissionActivity;
import me.shouheng.commons.event.PageName;
import me.shouheng.commons.event.RxBus;
import me.shouheng.commons.event.RxMessage;
import me.shouheng.commons.helper.ActivityHelper;
import me.shouheng.commons.utils.LogUtils;
import me.shouheng.commons.utils.PermissionUtils;
import me.shouheng.commons.utils.PersistData;
import me.shouheng.commons.utils.ToastUtils;
import me.shouheng.data.ModelFactory;
import me.shouheng.data.entity.Attachment;
import me.shouheng.data.entity.Category;
import me.shouheng.data.entity.Note;
import me.shouheng.data.entity.Notebook;
import me.shouheng.data.entity.QuickNote;
import me.shouheng.data.store.NotebookStore;
import me.shouheng.notepal.Constants;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.dialog.QuickNoteDialog;
import me.shouheng.notepal.util.AppWidgetUtils;
import me.shouheng.notepal.vm.QuickViewModel;

import static me.shouheng.commons.event.UMEvent.*;

@PageName(name = PAGE_QUICK)
public class QuickActivity extends PermissionActivity {

    private QuickViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(QuickViewModel.class);
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
                    .launch(this);
        } else {
            handleIntent(savedInstanceState);
        }
    }

    private void addSubscriptions() {
        addSubscription(RxMessage.class, RxMessage.CODE_PASSWORD_CHECK_PASSED, rxMessage -> handleIntent(null));
        addSubscription(RxMessage.class, RxMessage.CODE_PASSWORD_CHECK_FAILED, rxMessage -> finish());
        viewModel.getSaveNoteLiveData().observe(this, resources -> {
            assert resources != null;
            switch (resources.status) {
                case SUCCESS:
                    RxBus.getRxBus().post(new RxMessage(RxMessage.CODE_NOTE_DATA_CHANGED, null));
                    ToastUtils.makeToast(R.string.text_save_successfully);
                    AppWidgetUtils.notifyAppWidgets(this);
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed);
                    break;
            }
        });
    }

    private void handleIntent(Bundle savedInstanceState) {
        if (savedInstanceState != null) return;

        Intent intent = getIntent();
        String action = intent.getAction();
        assert action != null;

        switch (action) {
            case Constants.SHORTCUT_ACTION_QUICK_NOTE:
                PermissionUtils.checkStoragePermission(this, () ->
                        editQuickNote(null, ModelFactory.getQuickNote()));
                break;
            case Constants.APP_WIDGET_ACTION_QUICK_NOTE:
                PermissionUtils.checkStoragePermission(this, () ->
                        handleAppWidget(intent, pair -> editQuickNote(pair, ModelFactory.getQuickNote())));
                break;
        }
    }

    private void editQuickNote(Pair<Notebook, Category> pair, @NonNull QuickNote param) {
        QuickNoteDialog.newInstance(param, new QuickNoteDialog.DialogInteraction() {
            @Override
            public void onCancel() {
                finish();
            }

            @Override
            public void onDismiss() {
                finish();
            }

            @Override
            public void onCancel(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onConfirm(Dialog dialog, QuickNote quickNote, Attachment attachment) {
                Note note = pair == null ? ModelFactory.getNote() : ModelFactory.getNote(pair.first, pair.second);
                viewModel.saveQuickNote(note, quickNote, attachment);
                dialog.dismiss();
            }
        }).show(getSupportFragmentManager(), "QUICK NOTE");
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

    private  <M extends RxMessage> void addSubscription(Class<M> eventType, int code, Consumer<M> action) {
        Disposable disposable = RxBus.getRxBus().doSubscribe(eventType, code, action, LogUtils::d);
        RxBus.getRxBus().addSubscription(this, disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getRxBus().unSubscribe(this);
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
