package me.shouheng.notepal.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;

import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.ActivityLockBinding;
import me.shouheng.notepal.util.LogUtils;

public class LockActivity extends CommonActivity<ActivityLockBinding> {

    private final static String ACTION_SET_PASSWORD = "action_set_password";
    private final static String ACTION_REQUIRE_PERMISSION = "action_require_password";

    public static void setPassword(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, LockActivity.class);
        intent.setAction(ACTION_SET_PASSWORD);
        activity.startActivityForResult(intent, requestCode);
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
            LogUtils.d("Pin complete: " + pin);
        }

        @Override
        public void onEmpty() {}

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {}
    };
}
