package me.shouheng.commons.activity;

import android.annotation.TargetApi;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.Menu;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.shouheng.commons.R;
import me.shouheng.commons.event.RxBus;
import me.shouheng.commons.event.RxMessage;
import me.shouheng.commons.theme.ThemeStyle;
import me.shouheng.commons.theme.ThemeUtils;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.mvvm.base.BaseViewModel;
import me.shouheng.mvvm.base.CommonActivity;
import me.shouheng.utils.app.ResUtils;
import me.shouheng.utils.stability.LogUtils;
import me.shouheng.utils.store.SPUtils;

/**
 * Created by wang shouheng on 2017/12/21.*/
public abstract class ThemedActivity<T extends ViewDataBinding, VM extends BaseViewModel> extends CommonActivity<T, VM> {

    private ThemeStyle themeStyle;

    /**
     * Whether use the theme of preference
     *
     * @return whether use the theme
     */
    protected boolean useColorfulTheme() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        themeStyle = ThemeUtils.getInstance().getThemeStyle();
        if (useColorfulTheme()) {
            setTheme(themeStyle.style);
            ThemeUtils.customStatusBar(this);
        }
        updateNavigationBar();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (useColorfulTheme() && ThemeUtils.getInstance().getThemeStyle() != themeStyle) {
            recreate();
        }
    }

    /**
     * Set the status bar color.
     *
     * @param color the status bar color
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void setStatusBarColor(@ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }

    public ThemeStyle getThemeStyle() {
        return themeStyle;
    }

    protected boolean isDarkTheme(){
        return themeStyle.isDarkTheme;
    }

    /**
     * Update the navigation bar color
     */
    public void updateNavigationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (SPUtils.getInstance().getBoolean(ResUtils.getString(R.string.key_setting_nav_bar_result), false)) {
                getWindow().setNavigationBarColor(ColorUtils.primaryColor());
            } else {
                getWindow().setNavigationBarColor(Color.BLACK);
            }
        }
    }

    @ColorInt
    protected int primaryColor(){
        return ResUtils.getColor(themeStyle.primaryColor);
    }

    @ColorInt
    protected int accentColor(){
        return ResUtils.getColor(themeStyle.accentColor);
    }

    protected Fragment getCurrentFragment(@IdRes int containerId) {
        return getSupportFragmentManager().findFragmentById(containerId);
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
