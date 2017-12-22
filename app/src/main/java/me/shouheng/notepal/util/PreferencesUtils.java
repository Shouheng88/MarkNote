package me.shouheng.notepal.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.polaric.colorful.Colorful;

import java.util.Set;

/**
 * Created by Wang Shouheng on 2017/12/5. */
public class PreferencesUtils {

    private final String IS_DARK_THEME = "is_dark_theme";
    private final String PRIMARY_COLOR = "primary_color";
    private final String ACCENT_COLOR = "accent_color";

    private final String TOUR_ACTIVITY_SHOWED = "tour_activity_showed";
    private final String COLORED_NAVIGATION_BAR = "colored_navigation_bar";

    private static PreferencesUtils sInstance;

    private static SharedPreferences mPreferences;

    public static PreferencesUtils getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (PreferencesUtils.class) {
                if (sInstance == null){
                    sInstance = new PreferencesUtils(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    protected PreferencesUtils(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public void setDarkTheme(boolean isDarkTheme) {
        putBooleanValue(IS_DARK_THEME, isDarkTheme);
    }

    public boolean isDarkTheme() {
        return getBooleanValue(IS_DARK_THEME, false);
    }

    public void setThemeColor(Colorful.ThemeColor themeColor){
        putStringValue(PRIMARY_COLOR, themeColor.getPrimaryName());
    }

    public Colorful.ThemeColor getThemeColor(){
        return Colorful.ThemeColor.getByPrimaryName(getStringValue(PRIMARY_COLOR, Colorful.ThemeColor.GREEN.getPrimaryName()));
    }

    public Colorful.AccentColor getAccentColor() {
        return Colorful.AccentColor.getByAccentName(getStringValue(ACCENT_COLOR, Colorful.AccentColor.GREEN_700.getColorName()));
    }

    public void setAccentColor(Colorful.AccentColor accentColor){
        putStringValue(ACCENT_COLOR, accentColor.getAccentName());
    }

    public void setTourActivityShowed(boolean showed) {
        putBooleanValue(TOUR_ACTIVITY_SHOWED, showed);
    }

    public boolean isTourActivityShowed() {
        return getBooleanValue(TOUR_ACTIVITY_SHOWED, false);
    }

    public void setColoredNavigationBar(boolean coloredNavigationBar) {
        putBooleanValue(COLORED_NAVIGATION_BAR, coloredNavigationBar);
    }

    public boolean isColoredNavigationBar() {
        return getBooleanValue(TOUR_ACTIVITY_SHOWED, false);
    }

    // region the setters & getters
    private String getStringValue(String key, String defaultValue) {
        return mPreferences.getString(key, defaultValue);
    }

    private void putStringValue(String key, String value) {
        mPreferences.edit().putString(key, value).apply();
    }

    private int getIntValue(String key, int defaultValue) {
        return mPreferences.getInt(key, defaultValue);
    }

    private void putIntValue(String key, int value) {
        mPreferences.edit().putInt(key, value).apply();
    }

    private long getLongValue(String key, long defaultValue) {
        return mPreferences.getLong(key, defaultValue);
    }

    private void putLongValue(String key, long value) {
        mPreferences.edit().putLong(key, value).apply();
    }

    private boolean getBooleanValue(String key, boolean defaultValue) {
        return mPreferences.getBoolean(key, defaultValue);
    }

    private void putBooleanValue(String key, boolean value) {
        mPreferences.edit().putBoolean(key, value).apply();
    }

    private void putStringSetValue(String key, Set<String> stringSet) {
        mPreferences.edit().putStringSet(key, stringSet).apply();
    }

    private Set<String> getStringSetValue(String key, Set<String> defaultStringSet) {
        return mPreferences.getStringSet(key, defaultStringSet);
    }
    // endregion
}
