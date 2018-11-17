package me.shouheng.commons.tools;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;

/**
 * @author shouh
 * @version $Id: ColorUtils, v 0.1 2018/6/6 22:14 shouh Exp$
 */
public class ColorUtils {

    private static final int DEFAULT_COLOR_ALPHA = 50;

    public static Drawable tintDrawable(@NonNull Drawable drawable, @ColorInt int color) {
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

    public static int parseColor(String colorHex, @ColorInt int defaultValue) {
        try {
            return Color.parseColor(colorHex);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static @ColorInt int fadeColor(@ColorInt int color, @FloatRange(from = 0, to = 1) float rate) {
        return (color & 0x00ffffff) | ((0xff - (int)(0xff * rate)) << 24);
    }

    public static int getBlackWhiteColor(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        if (darkness >= 0.5) {
            return Color.WHITE;
        } else return Color.BLACK;
    }
}
