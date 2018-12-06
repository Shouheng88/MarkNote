package me.shouheng.commons.minipay;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.AccelerateDecelerateInterpolator;

import me.shouheng.commons.R;
import me.shouheng.commons.activity.CommonActivity;
import me.shouheng.commons.databinding.ActivityDonateBinding;

/**
 * Created by WngShhng on 2017/9/8.
 */
public class DonateActivity extends CommonActivity<ActivityDonateBinding> {
    private Config config;
    private boolean isAliPay = false;

    @Override
    protected boolean useColorfulTheme() {
        return false;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_donate;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        config = (Config) getIntent().getSerializableExtra(MiniPayUtils.EXTRA_KEY_PAY_CONFIG);
        if (!checkLegal()) throw new IllegalStateException("MiniPay Config illegal!!!");
        isAliPay = config.getChannel() == Config.PAY_CHANNEL_ALIPAY;
        switchTo(isAliPay);
        initEvents();
        initAnimator();
    }

    private void initAnimator() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(getBinding().tip, "alpha", 0, 0.66f, 1.0f, 0);
        animator.setDuration(2888);
        animator.setRepeatCount(6);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();
    }

    private void initEvents() {
        getBinding().zhiBg.setOnClickListener(v -> switchTo(!isAliPay));
        getBinding().zhiBtn.setOnClickListener(v -> {
            if (isAliPay) {
                AliZhi.startAlipayClient(this, config.getAliZhiKey());
            } else {
                WeZhi.startWeZhi(this, getBinding().qaImageView);
            }
        });
    }

    private boolean checkLegal() {
        return config.getWechatQaImage() != 0
                && config.getAliQaImage() != 0
                && !TextUtils.isEmpty(config.getAliZhiKey());
    }

    private void switchTo(boolean isAliPay) {
        this.isAliPay = isAliPay;
        getBinding().zhiBg.setBackgroundResource(isAliPay ? R.color.common_alipay_bg : R.drawable.donate_wechat_bg);
        getBinding().zhiTitle.setText(isAliPay ? R.string.donate_alipay_title : R.string.donate_wechat_title);
        getBinding().zhiSummery.setText(isAliPay ? config.getAliTip() : config.getWechatTip());
        getBinding().qaImageView.setImageResource(isAliPay ? config.getAliQaImage() : config.getWechatQaImage());
    }
}
