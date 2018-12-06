package me.shouheng.notepal.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import java.util.Objects;

import me.shouheng.commons.fragment.CommonFragment;
import me.shouheng.commons.minipay.Config;
import me.shouheng.commons.minipay.MiniPayUtils;
import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.FragmentSupportBinding;

/**
 * @author WngShhng (shouheng2015@gmail.com)
 * @version $Id: SupportFragment, v 0.1 2018/12/6 11:37 shouh Exp$
 */
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

        getBinding().btnAlipay.setOnClickListener(v -> MiniPayUtils.setupPay(Objects.requireNonNull(getContext()),
                new Config.Builder("a6x09668hybrp0jdxhp732f", R.mipmap.ali_pay, R.mipmap.mm_pay)
                        .setChannel(Config.PAY_CHANNEL_ALIPAY).build()));
        getBinding().btnWechat.setOnClickListener(v -> MiniPayUtils.setupPay(Objects.requireNonNull(getContext()),
                new Config.Builder("a6x09668hybrp0jdxhp732f", R.mipmap.ali_pay, R.mipmap.mm_pay)
                        .setChannel(Config.PAY_CHANNEL_WECHAT).build()));
        getBinding().sivQq.setOnClickListener(v -> joinQQGroup("0HQ8P6rzoNTwpHWHtkYPolgPAvQltMdt"));
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
