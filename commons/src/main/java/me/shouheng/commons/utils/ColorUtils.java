package me.shouheng.commons.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.FloatRange;
import android.support.annotation.MenuRes;
import android.support.annotation.StringRes;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.kennyc.bottomsheet.menu.BottomSheetMenu;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.shouheng.commons.R;
import me.shouheng.commons.theme.ThemeStyle;
import me.shouheng.commons.theme.ThemeUtils;

/**
 * @author shouh
 * @version $Id: ColorUtils, v 0.1 2018/6/6 22:14 shouh Exp$
 */
public class ColorUtils {
    private static ThemeStyle themeStyle;

    private static final int DEFAULT_COLOR_ALPHA = 50;

    public static @ColorInt int fadeColor(@ColorInt int color, @FloatRange(from = 0, to = 1) float rate) {
        return (color & 0x00ffffff) | ((0xff - (int)(0xff * rate)) << 24);
    }

    public static int getBlackWhiteColor(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        if (darkness >= 0.5) {
            return Color.WHITE;
        } else return Color.BLACK;
    }

    public static boolean isDarkTheme() {
        if (themeStyle == null) {
            themeStyle = ThemeUtils.getInstance().getThemeStyle();
        }
        return themeStyle.isDarkTheme;
    }

    public static int primaryColor() {
        if (themeStyle == null) {
            themeStyle = ThemeUtils.getInstance().getThemeStyle();
        }
        return PalmUtils.getColorCompact(themeStyle.primaryColor);
    }

    public static int accentColor() {
        if (themeStyle == null) {
            themeStyle = ThemeUtils.getInstance().getThemeStyle();
        }
        return  PalmUtils.getColorCompact(themeStyle.accentColor);
    }

    /**
     * Update the {@link #themeStyle} filed to force update the theme values persisted.
     * For if the field is singleton and won't change when we changed the theme.
     */
    public static void updateTheme() {
        themeStyle = ThemeUtils.getInstance().getThemeStyle();
    }

    public static Drawable tintDrawable(Drawable drawable, int color) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable.mutate());
        DrawableCompat.setTintList(wrappedDrawable, ColorStateList.valueOf(color));
        return wrappedDrawable;
    }

    public static Drawable tintDrawable(@DrawableRes int drawableRes, @ColorInt int color) {
        Drawable drawable = PalmUtils.getDrawableCompact(drawableRes);
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

    public static void addRipple(View view) {
        Drawable drawable;
        if (PalmUtils.isLollipop() && (drawable = PalmUtils.getDrawableCompact(R.drawable.ripple)) != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                view.setForeground(drawable);
            } else {
                try {
                    Method method = View.class.getMethod("setForeground", Drawable.class);
                    method.invoke(view, drawable);
                } catch (NoSuchMethodException e) {
                    LogUtils.e("NoSuchMethodException" + e);
                } catch (IllegalAccessException e) {
                    LogUtils.e("IllegalAccessException" + e);
                } catch (InvocationTargetException e) {
                    LogUtils.e("InvocationTargetException" + e);
                }
            }
        }
    }

    /**
     * Get the them styled drawer menu item for main activity.
     *
     * @param nameRes menu item name res id
     * @param iconRes menu item icon res id
     * @param id the menu item id
     * @param selectable is menu item selectable
     * @return the final drawer menu item
     */
    public static PrimaryDrawerItem getColoredDrawerMenuItem(@StringRes int nameRes,
                                                             @DrawableRes int iconRes,
                                                             long id,
                                                             boolean selectable) {
        int colorTint = PalmUtils.getColorCompact(isDarkTheme() ? R.color.white54 : R.color.black54);
        return new PrimaryDrawerItem()
                .withName(nameRes)
                .withIcon(ColorUtils.tintDrawable(iconRes, colorTint))
                .withIdentifier(id)
                .withIconColor(colorTint)
                .withTextColorRes(isDarkTheme() ? R.color.white87 : R.color.black87)
                .withSelectable(selectable)
                .withSelectedColor(ColorUtils.fadeColor(isDarkTheme() ? Color.WHITE : Color.BLACK, 0.9f))
                .withIconTintingEnabled(true)
                .withSelectedTextColor(ColorUtils.accentColor())
                .withSelectedIconColor(ColorUtils.accentColor());
    }

    public static BottomSheetMenu getThemedBottomSheetMenu(Context context, @MenuRes int menuRes) {
        int tintColor = PalmUtils.getColorCompact(isDarkTheme() ?
                R.color.dark_theme_image_tint_color : R.color.light_theme_image_tint_color);
        BottomSheetMenu menu = new BottomSheetMenu(context);
        new MenuInflater(context).inflate(menuRes, menu);
        int size = menu.size();
        for (int i=0; i<size; i++) {
            MenuItem menuItem = menu.getItem(i);
            Drawable drawable = menuItem.getIcon();
            if (drawable != null) {
                menuItem.setIcon(ColorUtils.tintDrawable(drawable, tintColor));
            }
        }
        return menu;
    }
}
