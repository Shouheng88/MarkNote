package me.shouheng.notepal.util.preferences;

import android.content.Context;

import org.polaric.colorful.Colorful;
import org.polaric.colorful.Defaults;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;

public class ThemePreferences extends BasePreferences {

    private static ThemePreferences sInstance;

    public static ThemePreferences getInstance() {
        if (sInstance == null) {
            synchronized (ThemePreferences.class) {
                if (sInstance == null){
                    sInstance = new ThemePreferences(PalmApp.getContext());
                }
            }
        }
        return sInstance;
    }

    private ThemePreferences(Context context) {
        super(context);
    }

    public void setDarkTheme(boolean isDarkTheme) {
        putBoolean(R.string.key_is_dark_theme, isDarkTheme);
    }

    public boolean isDarkTheme() {
        return getBoolean(R.string.key_is_dark_theme, false);
    }

    public void setThemeColor(Colorful.ThemeColor themeColor) {
        putString(R.string.key_primary_color, themeColor.getIdentifyName());
    }

    public Colorful.ThemeColor getThemeColor() {
        return Colorful.ThemeColor.getByPrimaryName(getString(R.string.key_primary_color, Defaults.primaryColor.getIdentifyName()));
    }

    public Colorful.AccentColor getAccentColor() {
        return Colorful.AccentColor.getByAccentName(getString(R.string.key_accent_color, Defaults.accentColor.getColorName()));
    }

    public void setAccentColor(Colorful.AccentColor accentColor) {
        putString(R.string.key_accent_color, accentColor.getAccentName());
    }

    public void setColoredNavigationBar(boolean coloredNavigationBar)  {
        putBoolean(R.string.key_is_colored_navigation_bar, coloredNavigationBar);
    }

    public boolean isColoredNavigationBar() {
        return getBoolean(R.string.key_is_colored_navigation_bar, false);
    }
}
