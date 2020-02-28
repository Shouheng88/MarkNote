package me.shouheng.commons.fragment;

import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.StringRes;

import com.umeng.analytics.MobclickAgent;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.shouheng.commons.event.PageName;
import me.shouheng.commons.event.RxBus;
import me.shouheng.commons.event.RxMessage;
import me.shouheng.commons.theme.ThemeStyle;
import me.shouheng.commons.theme.ThemeUtils;
import me.shouheng.commons.utils.LogUtils;
import me.shouheng.commons.utils.PalmUtils;

/**
 * @author shouh
 * @version $Id: BPreferenceFragment, v 0.1 2018/9/18 21:19 shouh Exp$
 */
public abstract class BPreferenceFragment extends PreferenceFragment {

    private String pageName;

    {
        Class<?> clazz = getClass();
        if (clazz.isAnnotationPresent(PageName.class)) {
            pageName = clazz.getAnnotation(PageName.class).name();
        }
    }

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
    public Preference findPreference(CharSequence key) {
        return super.findPreference(key);
    }

    public Preference findPreference(@StringRes int keyRes) {
        return super.findPreference(PalmUtils.getStringCompact(keyRes));
    }

    public ThemeStyle getThemeStyle() {
        return ThemeUtils.getInstance().getThemeStyle();
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
