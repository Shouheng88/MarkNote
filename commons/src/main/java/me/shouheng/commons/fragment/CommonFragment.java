package me.shouheng.commons.fragment;

import android.annotation.TargetApi;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.analytics.MobclickAgent;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.shouheng.commons.event.PageName;
import me.shouheng.commons.event.RxBus;
import me.shouheng.commons.event.RxMessage;
import me.shouheng.commons.theme.ThemeStyle;
import me.shouheng.commons.theme.ThemeUtils;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.utils.LogUtils;

/**
 * Created by wang shouheng on 2017/12/23. */
public abstract class CommonFragment<T extends ViewDataBinding> extends Fragment {

    private T binding;
    private View rootView;
    private ThemeStyle themeStyle;
    private String pageName;

    {
        Class<?> clazz = getClass();
        if (clazz.isAnnotationPresent(PageName.class)) {
            pageName = clazz.getAnnotation(PageName.class).name();
        }
    }

    protected abstract int getLayoutResId();

    protected abstract void doCreateView(Bundle savedInstanceState);

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(pageName);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(pageName);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        themeStyle = ThemeUtils.getInstance().getThemeStyle();

        if (rootView != null) return rootView;

        if (getLayoutResId() <= 0 ) {
            throw new AssertionError("Subclass must provide a valid layout resource id");
        }

        binding = DataBindingUtil.inflate(inflater, getLayoutResId(), container, false);

        doCreateView(savedInstanceState);

        return rootView = binding.getRoot();
    }

    protected final T getBinding() {
        return binding;
    }

    protected ThemeStyle getThemeStyle() {
        return themeStyle;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        ThemeUtils.themeMenu(menu, getThemeStyle().isDarkTheme);
    }

    protected <VM extends ViewModel> VM getViewModel(@NonNull Class<VM> modelClass) {
        return ViewModelProviders.of(this).get(modelClass);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && getActivity() != null) {
            getActivity().getWindow().setStatusBarColor(color);
        }
    }

    protected boolean isDarkTheme(){
        return ColorUtils.isDarkTheme();
    }

    protected int primaryColor(){
        return ColorUtils.primaryColor();
    }

    protected int accentColor(){
        return ColorUtils.accentColor();
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

    protected <M extends RxMessage> void addSubscription(Class<M> eventType, int code, Consumer<M> action) {
        Disposable disposable = RxBus.getRxBus().doSubscribe(eventType, code, action, LogUtils::d);
        RxBus.getRxBus().addSubscription(this, disposable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getRxBus().unSubscribe(this);
    }
}
