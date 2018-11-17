package me.shouheng.commons.view.fragment;

import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.StringRes;

import com.umeng.analytics.MobclickAgent;

import me.shouheng.commons.colorful.ThemeStyle;
import me.shouheng.commons.tools.PalmUtils;
import me.shouheng.commons.tools.preferences.BasePrefUtils;

/**
 * @author shouh
 * @version $Id: BasePreference, v 0.1 2018/9/18 21:19 shouh Exp$
 */
public abstract class BasePreference extends PreferenceFragment {

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
    public Preference findPreference(CharSequence key) {
        return super.findPreference(key);
    }

    public Preference findPreference(@StringRes int keyRes) {
        return super.findPreference(PalmUtils.getStringCompact(keyRes));
    }

    public ThemeStyle getThemeStyle() {
        return BasePrefUtils.getInstance().getThemeStyle();
    }
}
