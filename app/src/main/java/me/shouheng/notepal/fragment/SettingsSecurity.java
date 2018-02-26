package me.shouheng.notepal.fragment;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;

import com.afollestad.materialdialogs.MaterialDialog;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.LockActivity;
import me.shouheng.notepal.listener.OnFragmentDestroyListener;
import me.shouheng.notepal.util.PreferencesUtils;
import me.shouheng.notepal.util.ToastUtils;

/**
 * Created by wang shouheng on 2018/1/12. */
public class SettingsSecurity extends PreferenceFragment {

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
        findPreference(PreferencesUtils.PASSWORD_INPUT_FREEZE_TIME).setOnPreferenceClickListener(preference -> {
            showInputDialog();
            return true;
        });
    }

    private void showInputDialog() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.setting_password_freeze)
                .content(R.string.input_the_freeze_minutes_in_minute)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .inputRange(0, 2)
                .negativeText(R.string.cancel)
                .input(getString(R.string.input_the_freeze_minutes_in_minute), String.valueOf(preferencesUtils.getPasswordFreezeTime()), (materialDialog, charSequence) -> {
                    try {
                        int minutes = Integer.parseInt(charSequence.toString());
                        if (minutes < 0) {
                            ToastUtils.makeToast(getActivity(), R.string.illegal_number);
                            return;
                        }
                        if (minutes > 30) {
                            ToastUtils.makeToast(getActivity(), R.string.freeze_time_too_long);
                            return;
                        }
                        preferencesUtils.setPasswordFreezeTime(minutes);
                    } catch (Exception e) {
                        ToastUtils.makeToast(getActivity(), R.string.wrong_numeric_string);
                    }
                }).show();
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
