package me.shouheng.commons.activity;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Menu;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.shouheng.commons.event.RxBus;
import me.shouheng.commons.event.RxMessage;
import me.shouheng.commons.theme.ThemeUtils;
import me.shouheng.commons.utils.LogUtils;

/**
 * Created by WngShhng on 2018/5/18.*/
public abstract class CommonActivity<T extends ViewDataBinding> extends ThemedActivity {

    private T binding;

    protected abstract int getLayoutResId();

    protected abstract void doCreateView(Bundle savedInstanceState);

    protected void beforeCreate(Bundle savedInstanceState) { }

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        beforeCreate(savedInstanceState);

        if (getLayoutResId() <= 0 ) {
            throw new AssertionError("Subclass must provide a valid layout resource id");
        }

        binding = DataBindingUtil.inflate(getLayoutInflater(), getLayoutResId(), null, false);

        setContentView(binding.getRoot());

        doCreateView(savedInstanceState);
    }

    protected final T getBinding() {
        return binding;
    }

    protected <VM extends ViewModel> VM getViewModel(@NonNull Class<VM> modelClass) {
        return ViewModelProviders.of(this).get(modelClass);
    }

    protected Fragment getCurrentFragment(@IdRes int containerId) {
        return getSupportFragmentManager().findFragmentById(containerId);
    }

    /**
     * Provide to the Fragment to call its Activity's {@link super#onBackPressed()} directly.
     * Mainly used to avoid the call to the override {@link #onBackPressed()} method.
     */
    public void superOnBackPressed() {
        super.onBackPressed();
    }

    protected void postEvent(Object object) {
        RxBus.getRxBus().post(object);
    }

    protected <M extends RxMessage> void addSubscription(Class<M> eventType, int code, Consumer<M> action) {
        Disposable disposable = RxBus.getRxBus().doSubscribe(eventType, code, action, LogUtils::d);
        RxBus.getRxBus().addSubscription(this, disposable);
    }

    protected <M> void addSubscription(Class<M> eventType, Consumer<M> action) {
        Disposable disposable = RxBus.getRxBus().doSubscribe(eventType, action, LogUtils::d);
        RxBus.getRxBus().addSubscription(this, disposable);
    }

    protected <M> void addSubscription(Class<M> eventType, Consumer<M> action, Consumer<Throwable> error) {
        Disposable disposable = RxBus.getRxBus().doSubscribe(eventType, action, error);
        RxBus.getRxBus().addSubscription(this, disposable);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (autoCustomMenu()) {
            ThemeUtils.themeMenu(menu, getThemeStyle().isDarkTheme);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    protected boolean autoCustomMenu() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getRxBus().unSubscribe(this);
    }
}
