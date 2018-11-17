package me.shouheng.commons.view.activity;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.shouheng.commons.tools.event.RxBus;
import me.shouheng.commons.tools.FragmentHelper;
import me.shouheng.commons.tools.LogUtils;

/**
 * Created by WngShhng on 2018/5/18.*/
public abstract class CommonActivity<T extends ViewDataBinding> extends ColorfulActivity {

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

    protected void toFragment(Fragment fragment, @IdRes int containerId) {
        FragmentHelper.replace(this, fragment, containerId);
    }

    protected void toFragmentWithCallback(Fragment fragment, @IdRes int containerId) {
        FragmentHelper.replaceWithCallback(this, fragment, containerId);
    }

    public void startActivity(Class<? extends Activity> activity) {
        startActivity(new Intent(this, activity));
    }

    public void startActivityForResult(Class<? extends Activity> activity, int requestCode) {
        startActivityForResult(new Intent(this, activity), requestCode);
    }

    /**
     * Used to call {@link #onBackPressed()} to avoid override by sub class */
    public void superOnBackPressed() {
        super.onBackPressed();
    }

    protected void postEvent(Object object) {
        RxBus.getRxBus().post(object);
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
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getRxBus().unSubscribe(this);
    }
}
