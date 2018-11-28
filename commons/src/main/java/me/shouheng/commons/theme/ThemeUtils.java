package me.shouheng.commons.theme;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import me.shouheng.commons.R;
import me.shouheng.commons.activity.ThemedActivity;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.utils.PalmUtils;
import me.shouheng.commons.utils.PersistData;

/**
 * @author shouh
 * @version $Id: ThemeUtils, v 0.1 2018/9/4 7:42 shouh Exp$
 */
public class ThemeUtils {

    private static ThemeUtils sInstance = new ThemeUtils();

    private ThemeUtils() { }

    public static ThemeUtils getInstance() {
        return sInstance;
    }

    public ThemeStyle getThemeStyle() {
        return ThemeStyle.getThemeStyleById(PersistData.getInt(R.string.key_theme_style_id, ThemeStyle.LIGHT_BLUE_THEME.id));
    }

    public void setThemeStyle(ThemeStyle themeStyle) {
        PersistData.putInt(R.string.key_theme_style_id, themeStyle.id);
    }

    public static void customStatusBar(ThemedActivity activity) {
        // 6.0 and above
        if (PalmUtils.isMarshmallow()) {
            setStatusBarLightMode(activity.getWindow(), !activity.getThemeStyle().isDarkTheme);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static void setStatusBarLightMode(@NonNull Window window, boolean isLightMode) {
        if (!PalmUtils.isMarshmallow()) return;
        View decorView = window.getDecorView();
        if (decorView != null) {
            int vis = decorView.getSystemUiVisibility();
            if (isLightMode) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            decorView.setSystemUiVisibility(vis);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarColor(Activity activity, @ColorInt int color) {
        if (!PalmUtils.isLollipop()) return;
        activity.getWindow().setStatusBarColor(color);
    }

    public static void hideSystemUI(Activity activity) {
        activity.runOnUiThread(() -> activity.getWindow().getDecorView()
                .setSystemUiVisibility(SystemUiVisibilityUtil.getSystemVisibility()));
    }

    public static void themeMenu(Menu menu, boolean isDarkMode) {
        int size = menu.size();
        for (int i=0; i<size; i++) {
            MenuItem menuItem = menu.getItem(i);
            Drawable icon = menuItem.getIcon();
            if (icon != null) {
                menuItem.setIcon(ColorUtils.tintDrawable(icon, isDarkMode ? Color.WHITE : Color.BLACK));
            }
        }
    }
}
