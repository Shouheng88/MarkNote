package me.shouheng.commons.fragment;

import android.annotation.TargetApi;
import android.databinding.ViewDataBinding;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.shouheng.commons.event.RxBus;
import me.shouheng.commons.event.RxMessage;
import me.shouheng.commons.theme.ThemeStyle;
import me.shouheng.commons.theme.ThemeUtils;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.mvvm.base.BaseViewModel;
import me.shouheng.mvvm.base.CommonFragment;
import me.shouheng.utils.stability.LogUtils;

/**
 * Created by wang shouheng on 2017/12/23. */
public abstract class CustomFragment<T extends ViewDataBinding, U extends BaseViewModel> extends CommonFragment<T, U> {

    private ThemeStyle themeStyle;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        themeStyle = ThemeUtils.getInstance().getThemeStyle();
        return super.onCreateView(getLayoutInflater(), container, savedInstanceState);
    }

    protected ThemeStyle getThemeStyle() {
        return themeStyle;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        ThemeUtils.themeMenu(menu, getThemeStyle().isDarkTheme);
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
