package me.shouheng.notepal.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;

import com.umeng.analytics.MobclickAgent;

import java.util.Objects;

import me.shouheng.commons.activity.ContainerActivity;
import me.shouheng.commons.event.PageName;
import me.shouheng.commons.event.UMEvent;
import me.shouheng.commons.fragment.CommonFragment;
import me.shouheng.commons.fragment.WebviewFragment;
import me.shouheng.commons.minipay.Config;
import me.shouheng.commons.minipay.MiniPayUtils;
import me.shouheng.commons.utils.IntentUtils;
import me.shouheng.commons.utils.PalmUtils;
import me.shouheng.notepal.BuildConfig;
import me.shouheng.notepal.Constants;
import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.FragmentSupportBinding;

import static me.shouheng.commons.event.UMEvent.SUPPORT_DONATE_ALIPAY;
import static me.shouheng.commons.event.UMEvent.SUPPORT_DONATE_WECHAT;

/**
 * @author WngShhng (shouheng2015@gmail.com)
 * @version $Id: SupportFragment, v 0.1 2018/12/6 11:37 shouh Exp$
 */
@PageName(name = UMEvent.PAGE_SUPPORT)
public class SupportFragment extends CommonFragment<FragmentSupportBinding> {

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_support;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        if (getActivity() != null) {
            ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (ab != null) ab.setTitle(R.string.drawer_menu_donate);
        }

        getBinding().tv1.setText(Html.fromHtml(PalmUtils.getStringCompact(R.string.dialog_notice_content_part1)));
        getBinding().tv2.setText(Html.fromHtml(PalmUtils.getStringCompact(R.string.dialog_notice_content_part2)));
        getBinding().tv3.setText(Html.fromHtml(PalmUtils.getStringCompact(R.string.dialog_notice_content_part3)));
        getBinding().tv4.setText(Html.fromHtml(PalmUtils.getStringCompact(R.string.dialog_notice_content_part4)));
        getBinding().tv5.setText(Html.fromHtml(PalmUtils.getStringCompact(R.string.dialog_notice_content_part5)));
        getBinding().tv6.setText(Html.fromHtml(PalmUtils.getStringCompact(R.string.dialog_notice_content_part6)));

        getBinding().sivGooglePlay.setOnClickListener(v -> IntentUtils.openInMarket(getContext(), BuildConfig.APPLICATION_ID));
        getBinding().sivGithub.setOnClickListener(v ->
                ContainerActivity.open(WebviewFragment.class)
                        .put(WebviewFragment.ARGUMENT_KEY_URL, Constants.PAGE_GITHUB_REPOSITORY)
                        .put(WebviewFragment.ARGUMENT_KEY_USE_PAGE_TITLE, true)
                        .launch(getContext()));
        getBinding().sivGmail.setOnClickListener(v ->
                IntentUtils.sendEmail(getActivity(), Constants.EMAIL_DEVELOPER, "", ""));

        getBinding().sivQq.setOnClickListener(v -> joinQQGroup("0HQ8P6rzoNTwpHWHtkYPolgPAvQltMdt"));
        getBinding().sivTwitter.setOnClickListener(v ->
                ContainerActivity.open(WebviewFragment.class)
                        .put(WebviewFragment.ARGUMENT_KEY_URL, Constants.PAGE_TWITTER)
                        .put(WebviewFragment.ARGUMENT_KEY_USE_PAGE_TITLE, true)
                        .launch(getContext()));
        getBinding().sivWeibo.setOnClickListener(v ->
                ContainerActivity.open(WebviewFragment.class)
                        .put(WebviewFragment.ARGUMENT_KEY_URL, Constants.PAGE_WEIBO)
                        .put(WebviewFragment.ARGUMENT_KEY_USE_PAGE_TITLE, true)
                        .launch(getContext()));

        getBinding().btnAlipay.setOnClickListener(v -> {
            MiniPayUtils.setupPay(Objects.requireNonNull(getContext()),
                    new Config.Builder("a6x09668hybrp0jdxhp732f", R.mipmap.ali_pay, R.mipmap.mm_pay)
                            .setChannel(Config.PAY_CHANNEL_ALIPAY).build());
            MobclickAgent.onEvent(getContext(), SUPPORT_DONATE_ALIPAY);
        });
        getBinding().btnWechat.setOnClickListener(v -> {
            MiniPayUtils.setupPay(Objects.requireNonNull(getContext()),
                    new Config.Builder("a6x09668hybrp0jdxhp732f", R.mipmap.ali_pay, R.mipmap.mm_pay)
                            .setChannel(Config.PAY_CHANNEL_WECHAT).build());
            MobclickAgent.onEvent(getContext(), SUPPORT_DONATE_WECHAT);
        });
    }

    /****************
     *
     * 发起添加群流程。群号：马克笔记(878285438) 的 key 为： 0HQ8P6rzoNTwpHWHtkYPolgPAvQltMdt
     * 调用 joinQQGroup(0HQ8P6rzoNTwpHWHtkYPolgPAvQltMdt) 即可发起手Q客户端申请加群 马克笔记(878285438)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }
}
