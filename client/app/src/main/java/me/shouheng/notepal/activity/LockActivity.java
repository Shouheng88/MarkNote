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

import me.shouheng.commons.activity.CommonActivity;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.ActivityLockBinding;
import me.shouheng.notepal.util.ActivityUtils;
import me.shouheng.notepal.util.RSAUtil;
import me.shouheng.notepal.util.SystemUiVisibilityUtil;
import me.shouheng.commons.utils.ToastUtils;
import me.shouheng.notepal.util.preferences.LockPreferences;

public class LockActivity extends CommonActivity<ActivityLockBinding> {

    private final static String ACTION_SET_PASSWORD = "action_set_password";
    private final static String ACTION_REQUIRE_PERMISSION = "action_require_password";
    private final static String ACTION_REQUIRE_LAUNCH_APP = "action_require_launch_app";

    private String lastInputPassword, savedPassword;
    private int errorTimes = 0;
    private boolean isPasswordFrozen = false;
    private long psdFreezeLength;

    private LockPreferences lockPreferences;

    public static void setPassword(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), LockActivity.class);
        intent.setAction(ACTION_SET_PASSWORD);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void requirePassword(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), LockActivity.class);
        intent.setAction(ACTION_REQUIRE_PERMISSION);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void requireLaunch(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, LockActivity.class);
        intent.setAction(ACTION_REQUIRE_LAUNCH_APP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_lock;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        lockPreferences = LockPreferences.getInstance();
        psdFreezeLength = lockPreferences.getPasswordFreezeTime() * DateUtils.MINUTE_IN_MILLIS;
        savedPassword = lockPreferences.getPassword();

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
            getBinding().profileName.setText(R.string.setting_lock_new_psd);
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
        if (lockPreferences.getLastInputErrorTime() + psdFreezeLength > System.currentTimeMillis()) {
            if (!TextUtils.isEmpty(lockPreferences.getPasswordQuestion())
                    && !TextUtils.isEmpty(lockPreferences.getPasswordAnswer())) {
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
                lockPreferences.setLastInputErrorTime(System.currentTimeMillis());
                isPasswordFrozen = true;
                showFreezeToast();
            } else {
                ToastUtils.makeToast(String.format(getString(R.string.setting_lock_psd_changes_left), 5 - errorTimes));
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
            getBinding().profileName.setText(R.string.setting_lock_psd_hint);
            getBinding().pinLockView.resetPinLockView();
        } else {
            if (lastInputPassword.equals(encryptedPin)) {
                passSetting(encryptedPin);
            } else {
                /*
                 * Clear last input password, need to input same password twice. */
                lastInputPassword = null;
                getBinding().profileName.setText(R.string.setting_lock_new_psd);
                getBinding().pinLockView.resetPinLockView();
            }
        }
    }

    private void showFreezeToast() {
        String msg = String.format(getString(R.string.setting_lock_psd_frozen),
                lockPreferences.getPasswordFreezeTime());
        ToastUtils.makeToast(msg);
    }

    private void showFreezeDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.text_tips)
                .content(R.string.setting_lock_psd_frozen_tips)
                .positiveText(R.string.text_ok)
                .onPositive((dialog, which) -> showQuestionDialog())
                .negativeText(R.string.text_cancel)
                .build().show();
    }

    private void showQuestionDialog() {
        String question = lockPreferences.getPasswordQuestion();
        String answer = lockPreferences.getPasswordAnswer();
        new MaterialDialog.Builder(this)
                .title(R.string.text_security_question)
                .content(question)
                .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .input(null, null, (dialog, input) -> {
                    String encryptAnswer = RSAUtil.getEncryptString(input.toString());
                    if (answer.equals(encryptAnswer)) {
                        lockPreferences.setPasswordRequired(false);
                        // remove the last input error time
                        lockPreferences.setLastInputErrorTime(0);
                        showDisableDialog();
                    } else {
                        ToastUtils.makeToast(R.string.setting_lock_security_question_wrong);
                    }
                })
                .negativeText(R.string.text_cancel)
                .positiveText(R.string.text_confirm)
                .build().show();
    }

    private void showDisableDialog() {
        MaterialDialog dlg = new MaterialDialog.Builder(this)
                .title(R.string.text_tips)
                .content(R.string.setting_lock_security_question_removed)
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
        lockPreferences.setPassword(encryptedPin);
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
