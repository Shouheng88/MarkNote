package me.shouheng.notepal.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.view.View;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.ActivityLockBinding;
import me.shouheng.notepal.util.ActivityUtils;
import me.shouheng.notepal.util.MD5Util;
import me.shouheng.notepal.util.PreferencesUtils;
import me.shouheng.notepal.util.ToastUtils;

public class LockActivity extends CommonActivity<ActivityLockBinding> {

    private final static String ACTION_SET_PASSWORD = "action_set_password";
    private final static String ACTION_REQUIRE_PERMISSION = "action_require_password";

    private String lastInputPassword;

    private int errorTimes = 0;

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

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_lock;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        preferencesUtils = PreferencesUtils.getInstance(getParent());

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

        getBinding().indicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);
    }

    private PinLockListener mPinLockListener = new PinLockListener() {

        @Override
        public void onComplete(String pin) {
            if (getIntent().getAction().equals(ACTION_REQUIRE_PERMISSION)) {
                onCompleteForRequirement(pin);
            } else if (getIntent().getAction().equals(ACTION_SET_PASSWORD)) {
                onCompleteForSetting(pin);
            }
        }

        @Override
        public void onEmpty() {}

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {}
    };

    private void onCompleteForRequirement(String pin) {
        pin = MD5Util.MD5(pin);
        if (preferencesUtils.getLastInputErrorTime()
                + preferencesUtils.getPasswordFreezeTime() * DateUtils.MINUTE_IN_MILLIS > System.currentTimeMillis()) {
            ToastUtils.makeToast(this, R.string.setting_password_frozen);
            return;
        }
        if (pin.equals(preferencesUtils.getPassword())) {
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();
            PalmApp.setPasswordChecked(true);
        } else {
            errorTimes++;
            getBinding().pinLockView.resetPinLockView();
            ToastUtils.makeToast(this, String.format(getString(R.string.setting_input_wrong_password), 5 - errorTimes));
            if (errorTimes == 5) {
                preferencesUtils.setLastInputErrorTime(System.currentTimeMillis());
                ToastUtils.makeToast(this, String.format(getString(R.string.setting_password_frozen_minutes),
                        preferencesUtils.getPasswordFreezeTime()));
            }
        }
    }

    private void onCompleteForSetting(String pin) {
        pin = MD5Util.MD5(pin);
        if (lastInputPassword == null) {
            lastInputPassword = pin;
            getBinding().profileName.setText(R.string.setting_input_password_again);
            getBinding().pinLockView.resetPinLockView();
        } else {
            if (lastInputPassword.equals(pin)) {
                preferencesUtils.setPassword(pin);
                finish();
            } else {
                lastInputPassword = null;
                getBinding().profileName.setText(R.string.setting_input_password_newly);
                getBinding().pinLockView.resetPinLockView();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getAction().equals(ACTION_REQUIRE_PERMISSION)) {
            ActivityUtils.finishAll();
        } else {
            super.onBackPressed();
        }
    }
}
