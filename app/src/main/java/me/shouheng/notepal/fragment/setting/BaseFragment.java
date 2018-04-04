package me.shouheng.notepal.fragment.setting;

import android.preference.PreferenceFragment;
import android.support.annotation.StringRes;

import me.shouheng.notepal.PalmApp;

/**
 * Created by shouh on 2018/4/4.*/
public abstract class BaseFragment extends PreferenceFragment {

    public String getKey(@StringRes int resId) {
        return PalmApp.getStringCompact(resId);
    }
}
