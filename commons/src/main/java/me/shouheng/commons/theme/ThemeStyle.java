package me.shouheng.commons.theme;

import android.support.annotation.ColorRes;
import android.support.annotation.StyleRes;

import me.shouheng.commons.R;

/**
 * @author shouh
 * @version $Id: ThemeStyle, v 0.1 2018/8/29 21:24 shouh Exp$
 */
public enum ThemeStyle {
    LIGHT_BLUE_THEME(0, false, R.style.LightThemeBlue, R.color.colorPrimary, R.color.colorAccentBlue),
    LIGHT_RED_THEME(1, false, R.style.LightThemeRed, R.color.colorPrimary, R.color.colorAccentRed),
    DARK_THEME(2, true, R.style.AppThemeDark, R.color.colorDarkPrimary, R.color.colorAccentBlue);

    public final int id;

    @StyleRes
    public final int style;

    public final boolean isDarkTheme;

    @ColorRes
    public final int accentColor;

    @ColorRes
    public final int primaryColor;

    ThemeStyle(int id, boolean isDarkTheme, @StyleRes int style, @ColorRes int primaryColor, @ColorRes int accentColor) {
        this.id = id;
        this.isDarkTheme = isDarkTheme;
        this.style = style;
        this.primaryColor = primaryColor;
        this.accentColor = accentColor;
    }

    public static ThemeStyle getThemeStyleById(int id) {
        for (ThemeStyle type : values()) {
            if (type.id == id){
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid ThemeStyle id : " + id);
    }
}
