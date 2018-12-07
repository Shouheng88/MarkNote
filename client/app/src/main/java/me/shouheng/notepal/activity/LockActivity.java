package me.shouheng.notepal.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;

import com.afollestad.materialdialogs.MaterialDialog;
import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.facebook.stetho.common.LogUtil;
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify;
import com.wei.android.lib.fingerprintidentify.base.BaseFingerprint;

import me.shouheng.commons.activity.CommonActivity;
import me.shouheng.commons.event.PageName;
import me.shouheng.commons.event.RxMessage;
import me.shouheng.commons.theme.SystemUiVisibilityUtil;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.utils.LogUtils;
import me.shouheng.commons.utils.Md5Utils;
import me.shouheng.commons.utils.PalmUtils;
import me.shouheng.commons.utils.PersistData;
import me.shouheng.commons.utils.StringUtils;
import me.shouheng.commons.utils.ToastUtils;
import me.shouheng.commons.utils.ViewUtils;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.ActivityLockBinding;

import static me.shouheng.commons.event.UMEvent.*;

/**
 * Lock Activity, used to set password, check password.
 *
 * Created by WngShhng.
 */
@PageName(name = PAGE_LOCK)
public class LockActivity extends CommonActivity<ActivityLockBinding> {

    public final static String ACTION_SET_PASSWORD = "__action_set_password";
    public final static String ACTION_REQUIRE_PASSWORD = "__action_require_password";

    private FingerprintIdentify mFingerprintIdentify;
    private static final int MAX_AVAILABLE_TIMES = 5;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_lock;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        /* Config views. */
        getBinding().pinLockView.attachIndicatorDots(getBinding().indicatorDots);
        getBinding().pinLockView.setPinLockListener(mPinLockListener);
        getBinding().pinLockView.setPinLength(4);
        getBinding().pinLockView.setTextColor(ContextCompat.getColor(this, R.color.white));
        getBinding().pinLockView.setFingerButtonDrawable(ColorUtils.tintDrawable(
                PalmUtils.getDrawableCompact(R.drawable.ic_fingerprint_black_24dp), Color.WHITE));
        getBinding().pinLockView.setShowFingerButton(PersistData.getBoolean(
                R.string.key_security_finger_print_enable, false));
        getBinding().pinLockView.setFingereButtonSize(ViewUtils.dp2Px(this, 16f));
        getBinding().indicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FIXED);

        /* Get saved results. */
        String savedPsd = PersistData.getString(R.string.key_security_psd, "");

        /* Handle intent. */
        Intent intent = getIntent();
        String action = intent.getAction();
        assert action != null;
        switch (action) {
            case ACTION_REQUIRE_PASSWORD:
                /* Request password check. */
                if (TextUtils.isEmpty(savedPsd)) {
                    passPasswordCheck();
                } else {
                    if (PersistData.getBoolean(R.string.key_security_finger_print_enable, false)) {
                        initFingerprintIdentify();
                    }
                }
                break;
            case ACTION_SET_PASSWORD:
                /* If the action is to set password that means the user has ever passed the password check. */
                getBinding().profileName.setText(R.string.setting_lock_new_psd);
                break;
            default:
                throw new IllegalStateException("The action must be specified!");
        }
    }

    private void initFingerprintIdentify() {
        mFingerprintIdentify = new FingerprintIdentify(getApplicationContext(), LogUtils::e);
        if (!mFingerprintIdentify.isFingerprintEnable()) {
            LogUtils.e("Fingerprint Identify Not Enable!");
            getBinding().pinLockView.setShowFingerButton(false);
        }
        LogUtil.d("initFingerprintIdentify: " + this);
        mFingerprintIdentify.startIdentify(MAX_AVAILABLE_TIMES, listener);
    }

    private BaseFingerprint.FingerprintIdentifyListener listener = new BaseFingerprint.FingerprintIdentifyListener() {
        @Override
        public void onSucceed() {
            passPasswordCheck();
        }

        @Override
        public void onNotMatch(int availableTimes) {
            ToastUtils.makeToast(StringUtils.formatString(R.string.security_finger_not_match, availableTimes));
        }

        @Override
        public void onFailed(boolean isDeviceLocked) {
            ToastUtils.makeToast(R.string.security_finger_failed);
        }

        @Override
        public void onStartFailedByDeviceLocked() {
            ToastUtils.makeToast(R.string.security_finger_locked);
        }
    };

    private PinLockListener mPinLockListener = new PinLockListener() {

        String savedPsd = PersistData.getString(R.string.key_security_psd, "");

        private String lastInputPassword;

        private int errorTimes = 0;

        @Override
        public void onComplete(String pin) {
            if (ACTION_REQUIRE_PASSWORD.equals(getIntent().getAction())) {
                onCompleteForRequirement(pin);
            } else if (ACTION_SET_PASSWORD.equals(getIntent().getAction())) {
                onCompleteForSetting(pin);
            }
        }

        /**
         * Complete input password for requirement.
         *
         * @param pin password string
         */
        private void onCompleteForRequirement(String pin) {
            String md5 = Md5Utils.md5(pin);
            if (md5.equals(savedPsd)) {
                passPasswordCheck();
            } else {
                getBinding().pinLockView.resetPinLockView();
                ToastUtils.makeToast(R.string.setting_lock_psd_changes_left);
                if (errorTimes == 10) {
                    errorTimes = 0;
                    showQuestionDialog();
                }
            }
        }

        /**
         * Complete when set password.
         *
         * @param pin the password string
         */
        private void onCompleteForSetting(String pin) {
            String md5 = Md5Utils.md5(pin);
            if (TextUtils.isEmpty(lastInputPassword)) {
                lastInputPassword = md5;
                getBinding().profileName.setText(R.string.setting_lock_psd_hint);
                getBinding().pinLockView.resetPinLockView();
            } else {
                if (lastInputPassword.equals(md5)) {
                    passSetting(md5);
                } else {
                    lastInputPassword = null;
                    getBinding().profileName.setText(R.string.setting_lock_new_psd);
                    getBinding().pinLockView.resetPinLockView();
                    ToastUtils.makeToast(R.string.setting_lock_psd_set_differ);
                }
            }
        }

        @Override
        public void onEmpty() {

        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {

        }
    };

    private void showQuestionDialog() {
        String question = PersistData.getString(R.string.key_security_psd_question, "");
        String answer = PersistData.getString(R.string.key_security_psd_answer, "");
        if (TextUtils.isEmpty(question) && TextUtils.isEmpty(answer)) return;

        new MaterialDialog.Builder(this)
                .title(R.string.text_security_question)
                .content(question)
                .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .input(null, null, (dialog, input) -> {
                    String encryptAnswer = Md5Utils.md5(input.toString());
                    if (answer.equals(encryptAnswer)) {
                        PersistData.putBoolean(R.string.key_security_psd_required, false);
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
                .onPositive((dialog, which) -> passPasswordCheck())
                .build();
        dlg.show();
        dlg.setOnDismissListener(dialog -> passPasswordCheck());
    }

    private void passPasswordCheck() {
        postEvent(new RxMessage(RxMessage.CODE_PASSWORD_CHECK_PASSED, null));
        PalmApp.setPasswordChecked();
        finish();
    }

    private void passSetting(String md5) {
        PersistData.putString(R.string.key_security_psd, md5);
        postEvent(new RxMessage(RxMessage.CODE_PASSWORD_SET_SUCCEED, md5));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d("onResume: " + this);
        runOnUiThread(() -> getWindow().getDecorView().setSystemUiVisibility(SystemUiVisibilityUtil.getSystemVisibility()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d("onPause: " + this);
        if (mFingerprintIdentify != null) {
            mFingerprintIdentify.cancelIdentify();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d("onDestroy: " + this);
        if (mFingerprintIdentify != null) {
            mFingerprintIdentify.cancelIdentify();
        }
    }

    @Override
    public void onBackPressed() {
        if (ACTION_REQUIRE_PASSWORD.equals(getIntent().getAction())) {
            postEvent(new RxMessage(RxMessage.CODE_PASSWORD_CHECK_FAILED, null));
        } else if (ACTION_SET_PASSWORD.equals(getIntent().getAction())) {
            postEvent(new RxMessage(RxMessage.CODE_PASSWORD_SET_FAILED, null));
        }
        super.onBackPressed();
    }
}
