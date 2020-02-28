package me.shouheng.notepal.fragment.setting;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;

import me.shouheng.commons.event.PageName;
import me.shouheng.commons.event.RxMessage;
import me.shouheng.commons.event.*;
import me.shouheng.commons.fragment.BPreferenceFragment;
import me.shouheng.commons.helper.ActivityHelper;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.utils.Md5Utils;
import me.shouheng.commons.utils.PersistData;
import me.shouheng.commons.utils.ToastUtils;
import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.LockActivity;
import me.shouheng.notepal.databinding.DialogSecurityQuestionLayoutBinding;

/**
 * Created by WngShhng on 2018/1/12.
 */
@PageName(name = UMEvent.PAGE_SETTING_SECURITY)
public class SettingsSecurity extends BPreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) actionBar.setTitle(R.string.setting_category_universal_security);

        addPreferencesFromResource(R.xml.preferences_data_security);

        findPreference(R.string.key_security_psd_required).setOnPreferenceClickListener(preference -> {
            String psd = PersistData.getString(R.string.key_security_psd, null);
            if (TextUtils.isEmpty(psd) && ((SwitchPreference) preference).isChecked() ) {
                ActivityHelper.open(LockActivity.class)
                        .setAction(LockActivity.ACTION_SET_PASSWORD)
                        .launch(getActivity());
            } else if (((SwitchPreference) preference).isChecked()){
                /* the password is not empty and the password is required, but the security question is not set */
                showAlertIfNecessary();
            }
            return true;
        });
        findPreference(R.string.key_security_psd).setOnPreferenceClickListener(preference -> {
            ActivityHelper.open(LockActivity.class)
                    .setAction(LockActivity.ACTION_SET_PASSWORD)
                    .launch(getActivity());
            return true;
        });
        findPreference(R.string.key_security_psd_question).setOnPreferenceClickListener(preference -> {
            showQuestionEditor();
            return true;
        });

        Preference finger = findPreference(R.string.key_security_finger_print_enable);
        finger.setIcon(ColorUtils.tintDrawable(R.drawable.ic_fingerprint_black_24dp,
                getThemeStyle().isDarkTheme ? Color.WHITE : Color.BLACK));

        showAlertIfNecessary();

        addSubscription(RxMessage.class, RxMessage.CODE_PASSWORD_SET_SUCCEED, rxMessage -> showAlertIfNecessary());
        addSubscription(RxMessage.class, RxMessage.CODE_PASSWORD_SET_FAILED, rxMessage -> {
            String password = PersistData.getString(R.string.key_security_psd, "");
            if (TextUtils.isEmpty(password)) {
                PersistData.putBoolean(R.string.key_security_psd_required, false);
                ((SwitchPreference) findPreference(R.string.key_security_psd_required)).setChecked(false);
            }
        });
    }

    private void showQuestionEditor() {
        DialogSecurityQuestionLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()),
                R.layout.dialog_security_question_layout, null, false);

        binding.wtvQuestion.bindEditText(binding.etQuestion);
        binding.wtvAnswer.bindEditText(binding.etAnswer);
        binding.wtvConfirmAnswer.bindEditText(binding.etConfirmAnswer);

        /* Set default question from the preferences */
        String savedQuestion = PersistData.getString(R.string.key_security_psd_question, "");
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
                    binding.tilConfirmAnswer.setError(getString(R.string.setting_security_question_answer_differ));
                } else {
                    binding.tilConfirmAnswer.setErrorEnabled(false);
                }
            }
        });

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setNegativeButton(R.string.text_cancel, null)
                .setPositiveButton(R.string.text_save, null)
                .setView(binding.getRoot())
                .setCancelable(false)
                .create();

        alertDialog.setOnShowListener(dialog -> {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String question = binding.etQuestion.getText().toString();
                String answer = binding.etAnswer.getText().toString();
                String cfmAnswer = binding.etConfirmAnswer.getText().toString();
                boolean passed = checkSecurityQuestion(question, answer, cfmAnswer);
                if (passed) {
                    PersistData.putString(R.string.key_security_psd_question, question);
                    PersistData.putString(R.string.key_security_psd_answer, Md5Utils.md5(answer));
                    ToastUtils.makeToast(R.string.text_save_successfully);
                    dialog.dismiss();
                }
            });
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> {
                String question = PersistData.getString(R.string.key_security_psd_question, "");
                boolean psdRequired = PersistData.getBoolean(R.string.key_security_psd_required, false);
                boolean requireInput = TextUtils.isEmpty(question) && psdRequired;
                if (!requireInput) {
                    dialog.dismiss();
                } else {
                    ToastUtils.makeToast(R.string.setting_security_question_required);
                }
            });
        });

        alertDialog.show();
    }

    private boolean checkSecurityQuestion(String question, String answer, String confirmAnswer) {
        if (TextUtils.isEmpty(question)) {
            ToastUtils.makeToast(R.string.setting_security_question_required);
            return false;
        }
        if (TextUtils.isEmpty(answer)) {
            ToastUtils.makeToast(R.string.setting_security_question_answer_required);
            return false;
        }
        if (TextUtils.isEmpty(confirmAnswer)) {
            ToastUtils.makeToast(R.string.setting_security_question_confirm_answer_required);
            return false;
        }
        if (!answer.equals(confirmAnswer)) {
            ToastUtils.makeToast(R.string.setting_security_question_answer_differ);
            return false;
        }
        return true;
    }

    private void showAlertIfNecessary() {
        boolean psdRequired = PersistData.getBoolean(R.string.key_security_psd_required, false);
        String question = PersistData.getString(R.string.key_security_psd_question, "");
        if (!TextUtils.isEmpty(question) || !psdRequired) return;
        showQuestionEditor();
    }
}
