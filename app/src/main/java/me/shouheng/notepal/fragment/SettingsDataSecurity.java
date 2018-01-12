package me.shouheng.notepal.fragment;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.LockActivity;
import me.shouheng.notepal.listener.OnFragmentDestroyListener;
import me.shouheng.notepal.util.PreferencesUtils;

/**
 * Created by wang shouheng on 2018/1/12. */
public class SettingsDataSecurity extends PreferenceFragment {

    private final static String KEY_SET_PASSWORD = "set_password";
    private final static String KEY_PASSWORD_REQUIRED = "password_required";

    private PreferencesUtils preferencesUtils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencesUtils = PreferencesUtils.getInstance(getActivity());

        configToolbar();

        addPreferencesFromResource(R.xml.preferences_data_security);

        setPreferenceClickListeners();
    }

    private void configToolbar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) actionBar.setTitle(R.string.setting_data_security);
    }

    private void setPreferenceClickListeners() {
        findPreference(KEY_PASSWORD_REQUIRED).setOnPreferenceClickListener(preference -> {
            if (TextUtils.isEmpty(preferencesUtils.getPassword()) && ((CheckBoxPreference) preference).isChecked() ) {
                toSetPassword();
            }
            return true;
        });
        findPreference(KEY_SET_PASSWORD).setOnPreferenceClickListener(preference -> {
            toSetPassword();
            return true;
        });
    }

    private void toSetPassword() {
        LockActivity.setPassword(getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() instanceof OnFragmentDestroyListener) {
            ((OnFragmentDestroyListener) getActivity()).onFragmentDestroy();
        }
    }
}
