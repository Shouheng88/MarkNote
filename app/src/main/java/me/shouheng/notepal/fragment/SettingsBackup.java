package me.shouheng.notepal.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import me.shouheng.notepal.R;
import me.shouheng.notepal.listener.OnFragmentDestroyListener;
import me.shouheng.notepal.util.PreferencesUtils;

/**
 * Created by wang shouheng on 2018/1/5.*/
public class SettingsBackup extends PreferenceFragment {

    private final static String KEY_EXTERNAL_STORAGE = "backup_to_external_storage";

    private PreferencesUtils preferencesUtils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configToolbar();

        preferencesUtils = PreferencesUtils.getInstance(getActivity());

        addPreferencesFromResource(R.xml.preferences_data_backup);

        setPreferenceClickListeners();
    }

    private void configToolbar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) actionBar.setTitle(R.string.setting_backup);
    }

    private void setPreferenceClickListeners() {
        findPreference(KEY_EXTERNAL_STORAGE).setOnPreferenceClickListener(preference -> {

            return true;
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() instanceof OnFragmentDestroyListener) {
            ((OnFragmentDestroyListener) getActivity()).onFragmentDestroy();
        }
    }
}
