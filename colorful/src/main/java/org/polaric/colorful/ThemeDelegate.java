package org.polaric.colorful;

import android.content.Context;
import android.support.annotation.StyleRes;
import android.util.Log;

/**
 * 该类的作用是根据传入的主题等对象的信息，从资源中获取相关的style资源等信息
 * 然后当需要将主题信息应用到程序的时候，应该都是从这里获取的数据 */
public class ThemeDelegate {

    // region 【主题配置对象和资源信息】
    /* 传入的主题对象信息 */
    private Colorful.ThemeColor primaryColor;
    private Colorful.AccentColor accentColor;
    private boolean translucent;
    private boolean dark;

    /* 解析出的主题资源等信息 */
    @StyleRes private int styleResPrimary;
    @StyleRes private int styleResAccent;
    @StyleRes private int styleResBase;
    // endregion

    ThemeDelegate(Context context, Colorful.ThemeColor primary, Colorful.AccentColor accent, boolean translucent, boolean dark) {
        this.primaryColor = primary;
        this.accentColor = accent;
        this.translucent = translucent;
        this.dark = dark;

        long curTime = System.currentTimeMillis();

        /* 根据传入的对象获取对应的style资源等信息 */
        styleResPrimary = context.getResources().getIdentifier(primaryColor.getPrimaryName(), "style", context.getPackageName());
        styleResAccent = context.getResources().getIdentifier(accentColor.getAccentName(), "style", context.getPackageName());
        styleResBase = dark ? R.style.Colorful_Dark : R.style.Colorful_Light;

        Log.d(Util.LOG_TAG, "ThemeDelegate fetched theme in " + (System.currentTimeMillis()-curTime) + " milliseconds");
    }

    // region 【Setter和Getter方法】
    public @StyleRes int getStyleResPrimary() {
        return styleResPrimary;
    }

    public @StyleRes int getStyleResAccent() {
        return styleResAccent;
    }

    public @StyleRes int getStyleResBase() {
        return styleResBase;
    }

    public Colorful.ThemeColor getPrimaryColor() {
        return primaryColor;
    }

    public Colorful.AccentColor getAccentColor() {
        return accentColor;
    }

    public boolean isTranslucent() {
        return translucent;
    }

    public boolean isDark() {
        return dark;
    }
    // endregion
}
