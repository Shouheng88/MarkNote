package me.shouheng.notepal.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;

import java.security.PublicKey;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.ActivityLockBinding;
import me.shouheng.notepal.util.ActivityUtils;
import me.shouheng.notepal.util.Base64Utils;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.PreferencesUtils;
import me.shouheng.notepal.util.RSAUtil;
import me.shouheng.notepal.util.ToastUtils;

public class LockActivity extends CommonActivity<ActivityLockBinding> {

    private final static String ACTION_SET_PASSWORD = "action_set_password";
    private final static String ACTION_REQUIRE_PERMISSION = "action_require_password";
    private final static String ACTION_REQUIRE_LAUNCH_APP = "action_require_launch_app";
    private final static String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQD0diKVSZ/U/KHuxZFYac3lLq7K\n" +
            "edqc+uOKSJgq26tgy4wmELCw8gJkempBm8NPf+uSOdWPlPLWijSf3W2KfzMMvZQ2\n" +
            "tfNQPQu+gXgdXuZC+fhqVqNgYtWVRMIspveSm3AK+52AxxzTlfAU1fpCEFOf4AHc\n" +
            "/E33toB493pf9gS2xwIDAQAB";

    private String lastInputPassword, savedPassword;
    private int errorTimes = 0;
    private boolean isPasswordFrozen = false;
    private long psdFreezeLength;

    private PreferencesUtils preferencesUtils;

    public static void setPassword(Activity activity) {
        Intent intent = new Intent(activity, LockActivity.class);
        intent.setAction(ACTION_SET_PASSWORD);
        activity.startActivity(intent);
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
    }

    private void configSystemUI() {
        runOnUiThread(() -> getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_IMMERSIVE));
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
        String encryptedPin = getEncryptPassword(var);

        /**
         * Check the freeze time first. */
        if (preferencesUtils.getLastInputErrorTime() + psdFreezeLength > System.currentTimeMillis()) {
            ToastUtils.makeToast(this, R.string.setting_password_frozen);
            return;
        } else if (isPasswordFrozen) {
            // clear the freeze info
            isPasswordFrozen = false;
            errorTimes = 0;
        }

        if (savedPassword.equals(encryptedPin)) {
            /**
             * If the input password is the same as saved one -> back and record.*/
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            PalmApp.setPasswordChecked(true);
            finish();
        } else {
            /**
             * Input wrong password. */
            getBinding().pinLockView.resetPinLockView();

            if (++errorTimes == 5) {
                /**
                 * Input wrong password for too many times, record last error time and save the frozen state. */
                preferencesUtils.setLastInputErrorTime(System.currentTimeMillis());
                isPasswordFrozen = true;
                String msg = String.format(getString(R.string.setting_password_frozen_minutes), preferencesUtils.getPasswordFreezeTime());
                ToastUtils.makeToast(msg);
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
        String encryptedPin = getEncryptPassword(var);

        if (TextUtils.isEmpty(lastInputPassword)) {
            /**
             * record last input password witch will be used to check twice-input-logic */
            lastInputPassword = encryptedPin;
            getBinding().profileName.setText(R.string.setting_input_password_again);
            getBinding().pinLockView.resetPinLockView();
        } else {
            if (lastInputPassword.equals(encryptedPin)) {
                /**
                 * The password input twice is the same, save it to settings and finish activity. */
                preferencesUtils.setPassword(encryptedPin);
                finish();
            } else {
                /**
                 * Clear last input password, need to input same password twice. */
                lastInputPassword = null;
                getBinding().profileName.setText(R.string.setting_input_password_newly);
                getBinding().pinLockView.resetPinLockView();
            }
        }
    }

    private String getEncryptPassword(String pin) {
        try {
            PublicKey publicKey = RSAUtil.loadPublicKey(PUBLIC_KEY);
            byte[] encryptByte = RSAUtil.encryptData(pin.getBytes(), publicKey);
            String afterEncrypt = Base64Utils.encode(encryptByte);
            LogUtils.d(afterEncrypt);
            return afterEncrypt;
        } catch (Exception e) {
            LogUtils.e(e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        if (ACTION_REQUIRE_LAUNCH_APP.equals(getIntent().getAction())) {
            ActivityUtils.finishAll();
        } else {
            super.onBackPressed();
        }
    }
}
