package me.shouheng.notepal.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.afollestad.materialdialogs.MaterialDialog;
import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.base.CommonActivity;
import me.shouheng.notepal.databinding.ActivityLockBinding;
import me.shouheng.notepal.util.ActivityUtils;
import me.shouheng.notepal.util.PreferencesUtils;
import me.shouheng.notepal.util.RSAUtil;
import me.shouheng.notepal.util.SystemUiVisibilityUtil;
import me.shouheng.notepal.util.ToastUtils;

public class LockActivity extends CommonActivity<ActivityLockBinding> {

    private final static String ACTION_SET_PASSWORD = "action_set_password";
    private final static String ACTION_REQUIRE_PERMISSION = "action_require_password";
    private final static String ACTION_REQUIRE_LAUNCH_APP = "action_require_launch_app";

    private String lastInputPassword, savedPassword;
    private int errorTimes = 0;
    private boolean isPasswordFrozen = false;
    private long psdFreezeLength;

    private PreferencesUtils preferencesUtils;

    public static void setPassword(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), LockActivity.class);
        intent.setAction(ACTION_SET_PASSWORD);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void requirePassword(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, LockActivity.class);
        intent.setAction(ACTION_REQUIRE_PERMISSION);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void requireLaunch(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, LockActivity.class);
        intent.setAction(ACTION_REQUIRE_LAUNCH_APP);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_lock;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        preferencesUtils = PreferencesUtils.getInstance(getParent());
        psdFreezeLength = preferencesUtils.getPasswordFreezeTime() * DateUtils.MINUTE_IN_MILLIS;
        savedPassword = preferencesUtils.getPassword();

        configSystemUI();

        configViews();

        checkPassword();
    }

    private void configSystemUI() {
        runOnUiThread(() -> getWindow().getDecorView().setSystemUiVisibility(
                SystemUiVisibilityUtil.getSystemVisibility()));
    }

    private void configViews() {
        getBinding().pinLockView.attachIndicatorDots(getBinding().indicatorDots);
        getBinding().pinLockView.setPinLockListener(mPinLockListener);

        getBinding().pinLockView.setPinLength(4);
        getBinding().pinLockView.setTextColor(ContextCompat.getColor(this, R.color.white));

        getBinding().indicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FIXED);

        if (ACTION_SET_PASSWORD.equals(getIntent().getAction())) {
            getBinding().profileName.setText(R.string.setting_input_password_newly);
        }
    }

    /*
     * If there is no password saved in preferences, pass the password check directly. */
    private void checkPassword() {
        if (ACTION_REQUIRE_PERMISSION.equals(getIntent().getAction())
                || ACTION_REQUIRE_LAUNCH_APP.equals(getIntent().getAction())) {
            if (TextUtils.isEmpty(savedPassword)) {
                passCheck();
            }
        }
    }

    private PinLockListener mPinLockListener = new PinLockListener() {

        @Override
        public void onComplete(String pin) {
            if (ACTION_REQUIRE_PERMISSION.equals(getIntent().getAction())
                    || ACTION_REQUIRE_LAUNCH_APP.equals(getIntent().getAction())) {
                onCompleteForRequirement(pin);
            } else if (ACTION_SET_PASSWORD.equals(getIntent().getAction())) {
                onCompleteForSetting(pin);
            }
        }

        @Override
        public void onEmpty() {}

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {}
    };

    private void onCompleteForRequirement(String var) {
        String encryptedPin = RSAUtil.getEncryptString(var);

        /*
         * If the saved password is empty, pass the password check logic. */
        if (TextUtils.isEmpty(savedPassword)) {
            passCheck();
            return;
        }

        /*
         * Check the freeze time first. */
        if (preferencesUtils.getLastInputErrorTime() + psdFreezeLength > System.currentTimeMillis()) {
            if (!TextUtils.isEmpty(preferencesUtils.getPasswordQuestion())
                    && !TextUtils.isEmpty(preferencesUtils.getPasswordAnswer())) {
                showFreezeDialog();
            } else {
                showFreezeDialog();
            }
            // rest the pin
            getBinding().pinLockView.resetPinLockView();
            return;
        } else if (isPasswordFrozen) {
            // clear the freeze info
            isPasswordFrozen = false;
            errorTimes = 0;
        }

        if (savedPassword.equals(encryptedPin)) {
            /*
             * If the input password is the same as saved one -> back and record.*/
            passCheck();
        } else {
            /*
             * Input wrong password. */
            getBinding().pinLockView.resetPinLockView();
            if (++errorTimes == 5) {
                /*
                 * Input wrong password for too many times, record last error time and save the frozen state. */
                preferencesUtils.setLastInputErrorTime(System.currentTimeMillis());
                isPasswordFrozen = true;
                showFreezeToast();
            } else {
                ToastUtils.makeToast(String.format(getString(R.string.setting_input_wrong_password), 5 - errorTimes));
            }
        }
    }

    /**
     * On password input completed for setting password.
     * Should input one same password twice before save it to settings.
     *
     * @param var the password numeric string */
    private void onCompleteForSetting(String var) {
        String encryptedPin = RSAUtil.getEncryptString(var);

        if (TextUtils.isEmpty(lastInputPassword)) {
            /*
             * record last input password witch will be used to check twice-input-logic */
            lastInputPassword = encryptedPin;
            getBinding().profileName.setText(R.string.setting_input_password_again);
            getBinding().pinLockView.resetPinLockView();
        } else {
            if (lastInputPassword.equals(encryptedPin)) {
                passSetting(encryptedPin);
            } else {
                /*
                 * Clear last input password, need to input same password twice. */
                lastInputPassword = null;
                getBinding().profileName.setText(R.string.setting_input_password_newly);
                getBinding().pinLockView.resetPinLockView();
            }
        }
    }

    private void showFreezeToast() {
        String msg = String.format(getString(R.string.setting_password_frozen_minutes),
                preferencesUtils.getPasswordFreezeTime());
        ToastUtils.makeToast(msg);
    }

    private void showFreezeDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.text_tips)
                .content(R.string.setting_password_frozen)
                .positiveText(R.string.text_ok)
                .onPositive((dialog, which) -> showQuestionDialog())
                .negativeText(R.string.text_cancel)
                .build().show();
    }

    private void showQuestionDialog() {
        String question = preferencesUtils.getPasswordQuestion();
        String answer = preferencesUtils.getPasswordAnswer();
        new MaterialDialog.Builder(this)
                .title(R.string.setting_answer_question)
                .content(question)
                .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .input(null, null, (dialog, input) -> {
                    String encryptAnswer = RSAUtil.getEncryptString(input.toString());
                    if (answer.equals(encryptAnswer)) {
                        preferencesUtils.setPasswordRequired(false);
                        // remove the last input error time
                        preferencesUtils.setLastInputErrorTime(0);
                        showDisableDialog();
                    } else {
                        ToastUtils.makeToast(R.string.setting_wrong_answer);
                    }
                })
                .negativeText(R.string.cancel)
                .positiveText(R.string.confirm)
                .build().show();
    }

    private void showDisableDialog() {
        MaterialDialog dlg = new MaterialDialog.Builder(this)
                .title(R.string.text_tips)
                .content(R.string.setting_disable_password)
                .positiveText(R.string.text_ok)
                .onPositive((dialog, which) -> passCheck())
                .build();
        dlg.show();
        dlg.setOnDismissListener(dialog -> passCheck());
    }

    private void passCheck() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        PalmApp.setPasswordChecked();
        finish();
    }

    private void passSetting(String encryptedPin) {
        /*
         * The password input twice is the same, save it to settings and finish activity. */
        preferencesUtils.setPassword(encryptedPin);
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (ACTION_REQUIRE_LAUNCH_APP.equals(getIntent().getAction())) {
            ActivityUtils.finishAll();
        } else if (ACTION_SET_PASSWORD.equals(getIntent().getAction())) {
            Intent intent = new Intent();
            setResult(Activity.RESULT_CANCELED, intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
