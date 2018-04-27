package me.shouheng.notepal.fragment.setting;

import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.StringRes;

import me.shouheng.notepal.PalmApp;

/**
 * Created by shouh on 2018/4/4.*/
public abstract class BaseFragment extends PreferenceFragment {

    public Preference findPreference(@StringRes int keyRes) {
        return super.findPreference(PalmApp.getStringCompact(keyRes));
    }
}
