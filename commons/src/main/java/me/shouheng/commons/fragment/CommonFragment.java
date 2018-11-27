package me.shouheng.commons.fragment;

import android.annotation.TargetApi;
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
import me.shouheng.commons.event.RxBus;
import me.shouheng.commons.theme.ThemeUtils;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.utils.LogUtils;
import me.shouheng.commons.utils.ThemeStyle;

/**
 * Created by wang shouheng on 2017/12/23. */
public abstract class CommonFragment<T extends ViewDataBinding> extends Fragment {

    private T binding;
    private View rootView;
    private ThemeStyle themeStyle;

    protected abstract int getLayoutResId();

    protected abstract void doCreateView(Bundle savedInstanceState);

    protected abstract String umengPageName();

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(umengPageName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(umengPageName());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && getActivity() != null) {
            getActivity().getWindow().setStatusBarColor(color);
        }
    }

    protected boolean isDarkTheme(){
        return ColorUtils.isDarkTheme(getContext());
    }

    protected int primaryColor(){
        return ColorUtils.primaryColor(getContext());
    }

    protected int accentColor(){
        return ColorUtils.accentColor(getContext());
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
    public void onDestroy() {
        super.onDestroy();
        RxBus.getRxBus().unSubscribe(this);
    }
}
