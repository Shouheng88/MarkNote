package me.shouheng.notepal.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import me.shouheng.notepal.R;

/**
 * Created by wang shouheng on 2017/12/21.
 */

public class SettingFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
