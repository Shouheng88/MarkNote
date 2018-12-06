package me.shouheng.commons.minipay;

import android.support.annotation.DrawableRes;

import java.io.Serializable;

import me.shouheng.commons.R;
import me.shouheng.commons.utils.PalmUtils;

/**
 * Created by changxing on 2017/9/20.
 */
public class Config implements Serializable {
    public final static int PAY_CHANNEL_WECHAT = 0;
    public final static int PAY_CHANNEL_ALIPAY = 1;

    private String wechatTip;
    private String aliTip;
    @DrawableRes
    private int wechatQaImage;
    @DrawableRes
    private int aliQaImage;
    private String aliZhiKey;
    private int channel;

    Config(Builder builder) {
        this.wechatQaImage = builder.wechatQaImage;
        this.aliQaImage = builder.aliQaImage;
        this.wechatTip = builder.wechatTip;
        this.aliTip = builder.aliTip;
        this.aliZhiKey = builder.aliZhiKey;
        this.channel = builder.channel;
    }

    private Config() { }

    public String getWechatTip() {
        return wechatTip;
    }

    public String getAliTip() {
        return aliTip;
    }

    public int getWechatQaImage() {
        return wechatQaImage;
    }

    public int getAliQaImage() {
        return aliQaImage;
    }

    public String getAliZhiKey() {
        return aliZhiKey;
    }

    public int getChannel() {
        return channel;
    }

    public static class Builder {
        private String wechatTip = PalmUtils.getStringCompact(R.string.donate_wechat_tips);
        private String aliTip = PalmUtils.getStringCompact(R.string.donate_alipay_tips);
        @DrawableRes
        private int wechatQaImage;
        @DrawableRes
        private int aliQaImage;
        private String aliZhiKey;
        private int channel;

        public Builder(String aliKey, @DrawableRes int qaAli, @DrawableRes int qaWechat) {
            wechatQaImage = qaWechat;
            aliQaImage = qaAli;
            aliZhiKey = aliKey;
        }

        public Builder setWechatTip(String tip) {
            wechatTip = tip;
            return this;
        }

        public Builder setAliTip(String tip) {
            aliTip = tip;
            return this;
        }

        public Builder setChannel(int channel) {
            this.channel = channel;
            return this;
        }

        public Config build() { // 构建，返回一个新对象
            return new Config(this);
        }
    }
}
