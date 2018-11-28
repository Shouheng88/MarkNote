package me.shouheng.notepal.fragment.setting;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;

import com.afollestad.materialdialogs.MaterialDialog;

import me.shouheng.commons.fragment.BPreferenceFragment;
import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.LockActivity;
import me.shouheng.notepal.databinding.DialogSecurityQuestionLayoutBinding;
import me.shouheng.notepal.util.RSAUtil;
import me.shouheng.commons.utils.ToastUtils;
import me.shouheng.notepal.util.preferences.LockPreferences;

/**
 * Created by wang shouheng on 2018/1/12. */
public class SettingsSecurity extends BPreferenceFragment {

    private final static int REQUEST_SET_PASSWORD = 0x0010;

    private LockPreferences lockPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lockPreferences = LockPreferences.getInstance();

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) actionBar.setTitle(R.string.setting_data_security);

        addPreferencesFromResource(R.xml.preferences_data_security);

        findPreference(R.string.key_is_password_required).setOnPreferenceClickListener(preference -> {
            if (TextUtils.isEmpty(lockPreferences.getPassword()) && ((SwitchPreference) preference).isChecked() ) {
                toSetPassword();
            } else if (((SwitchPreference) preference).isChecked()){
                /* the password is not empty and the password is required, but the security question is not set */
                showAlertIfNecessary();
            }
            return true;
        });
        findPreference(R.string.key_password).setOnPreferenceClickListener(preference -> {
            toSetPassword();
            return true;
        });
        findPreference(R.string.key_password_freeze_time).setOnPreferenceClickListener(preference -> {
            showInputDialog();
            return true;
        });
        findPreference(R.string.key_password_question).setOnPreferenceClickListener(preference -> {
            showQuestionEditor();
            return true;
        });

        showAlertIfNecessary();
    }

    private void showInputDialog() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.setting_password_freeze)
                .content(R.string.input_the_freeze_minutes_in_minute)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .inputRange(0, 2)
                .negativeText(R.string.cancel)
                .input(getString(R.string.input_the_freeze_minutes_in_minute), String.valueOf(lockPreferences.getPasswordFreezeTime()),
                        (materialDialog, charSequence) -> {
                            try {
                                int minutes = Integer.parseInt(charSequence.toString());
                                if (minutes < 0) {
                                    ToastUtils.makeToast(R.string.illegal_number);
                                    return;
                                }
                                if (minutes > 30) {
                                    ToastUtils.makeToast(R.string.freeze_time_too_long);
                                    return;
                                }
                                lockPreferences.setPasswordFreezeTime(minutes);
                            } catch (Exception e) {
                                ToastUtils.makeToast(R.string.wrong_numeric_string);
                            }
                        }).show();
    }

    private void toSetPassword() {
        LockActivity.setPassword(this, REQUEST_SET_PASSWORD);
    }

    private void showQuestionEditor() {
        DialogSecurityQuestionLayoutBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(getActivity()), R.layout.dialog_security_question_layout, null, false);

        binding.wtvQuestion.bindEditText(binding.etQuestion);
        binding.wtvAnswer.bindEditText(binding.etAnswer);
        binding.wtvConfirmAnswer.bindEditText(binding.etConfirmAnswer);

        /* set default question from the preferences */
        String savedQuestion = lockPreferences.getPasswordQuestion();
        binding.etQuestion.setText(savedQuestion);

        binding.etConfirmAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!binding.etAnswer.getText().toString().equals(s.toString())) {
                    binding.tilConfirmAnswer.setErrorEnabled(true);
                    binding.tilConfirmAnswer.setError(getString(R.string.setting_answer_different));
                } else {
                    binding.tilConfirmAnswer.setErrorEnabled(false);
                }
            }
        });

        new AlertDialog.Builder(getActivity())
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.text_save, (dialog, which) -> {
                    String question = binding.etQuestion.getText().toString();
                    String answer = binding.etAnswer.getText().toString();
                    boolean passed = checkSecurityQuestion(question, answer,
                            binding.etConfirmAnswer.getText().toString());
                    if (passed) {
                        saveSecurityQuestion(question, answer);
                    }
                })
                .setView(binding.getRoot())
                .create().show();
    }

    private boolean checkSecurityQuestion(String question, String answer, String confirmAnswer) {
        if (TextUtils.isEmpty(question)) {
            ToastUtils.makeToast(R.string.setting_question_required);
            return false;
        }
        if (TextUtils.isEmpty(answer)) {
            ToastUtils.makeToast(R.string.setting_answer_required);
            return false;
        }
        if (TextUtils.isEmpty(confirmAnswer)) {
            ToastUtils.makeToast(R.string.setting_confirm_answer_required);
            return false;
        }
        if (!answer.equals(confirmAnswer)) {
            ToastUtils.makeToast(R.string.setting_answer_different);
            return false;
        }
        return true;
    }

    private void saveSecurityQuestion(String question, String answer) {
        lockPreferences.setPasswordQuestion(question);
        String encryptAnswer = RSAUtil.getEncryptString(answer);
        lockPreferences.setPasswordAnswer(encryptAnswer);

        ToastUtils.makeToast(R.string.text_save_successfully);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SET_PASSWORD:
                if (resultCode != Activity.RESULT_OK && TextUtils.isEmpty(lockPreferences.getPassword())) {
                    /* remove the password requirement if the password is not set */
                    lockPreferences.setPasswordRequired(false);
                    ((SwitchPreference) findPreference(R.string.key_is_password_required)).setChecked(false);
                } else {
                    showAlertIfNecessary();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showAlertIfNecessary() {
        if (!TextUtils.isEmpty(lockPreferences.getPasswordQuestion())
                || !lockPreferences.isPasswordRequired()) return;
        new MaterialDialog.Builder(getActivity())
                .title(R.string.text_tips)
                .content(R.string.setting_no_security_question_message)
                .positiveText(R.string.text_ok)
                .onPositive((dialog, which) -> showQuestionEditor())
                .negativeText(R.string.cancel)
                .build()
                .show();
    }

    @Override
    protected String umengPageName() {
        return "Security preferences";
    }
}
