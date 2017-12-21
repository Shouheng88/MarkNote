package me.shouheng.notepal.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;

import org.polaric.colorful.Colorful;

/**
 * Created by wangshouheng on 2017/3/31.*/
public class ColorUtils {

    private static Boolean isDarkTheme;
    private static Integer primaryColor;
    private static Integer accentColor;

    private static final int DEFAULT_COLOR_ALPHA = 50;

    public static boolean isDarkTheme(Context context) {
        if (isDarkTheme == null) {
            isDarkTheme = PreferencesUtils.getInstance(context).isDarkTheme();
        }
        return isDarkTheme;
    }

    public static int primaryColor(Context context){
        if (primaryColor == null) {
            Colorful.ThemeColor primaryColor = PreferencesUtils.getInstance(context).getThemeColor();
            ColorUtils.primaryColor = context.getResources().getColor(primaryColor.getColorRes());
        }
        return primaryColor;
    }

    public static int accentColor(Context context){
        if (accentColor == null) {
            Colorful.AccentColor accentColor = PreferencesUtils.getInstance(context).getAccentColor();
            ColorUtils.accentColor = context.getResources().getColor(accentColor.getColorRes());
        }
        return accentColor;
    }

    public static Colorful.ThemeColor getThemeColor(Context context) {
        return PreferencesUtils.getInstance(context).getThemeColor();
    }

    public static Colorful.AccentColor getAccentColor(Context context) {
        return PreferencesUtils.getInstance(context).getAccentColor();
    }

    public static void forceUpdateThemeStatus(Context context) {
        Colorful.ThemeColor primaryColor = PreferencesUtils.getInstance(context).getThemeColor();
        ColorUtils.primaryColor = context.getResources().getColor(primaryColor.getColorRes());

        Colorful.AccentColor accentColor = PreferencesUtils.getInstance(context).getAccentColor();
        ColorUtils.accentColor = context.getResources().getColor(accentColor.getColorRes());

        isDarkTheme = PreferencesUtils.getInstance(context).isDarkTheme();
    }

    public static String getColorName(int color) {
        return "#" + getHexString(Color.red(color)) + getHexString(Color.green(color)) + getHexString( Color.blue(color));
    }

    private static String getHexString(int i){
        String s = Integer.toHexString(i).toUpperCase();
        return s.length() == 1 ? "0" + s : s;
    }

    public static Drawable tintDrawable(Drawable drawable, int color) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable.mutate());
        DrawableCompat.setTintList(wrappedDrawable, ColorStateList.valueOf(color));
        return wrappedDrawable;
    }

    public static int calStatusBarColor(int color) {
        return calStatusBarColor(color, DEFAULT_COLOR_ALPHA);
    }

    public static int calStatusBarColor(int color, int alpha) {
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }
}
