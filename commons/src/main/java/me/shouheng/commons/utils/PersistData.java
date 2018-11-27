package me.shouheng.commons.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import java.util.Set;

import me.shouheng.commons.BaseApplication;

/**
 * @author shouh
 * @version $Id: PersistData, v 0.1 2018/8/29 21:07 shouh Exp$
 */
public class PersistData {
    
    private static SharedPreferences mPreferences =
            PreferenceManager.getDefaultSharedPreferences(BaseApplication.getContext());

    public static void setOnSharedPreferenceChangeListener(
            SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterOnSharedPreferenceChangeListener(
            SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public static int getInt(String key, int defaultValue) {
        return mPreferences.getInt(key, defaultValue);
    }

    public static void putInt(String key, int value) {
        mPreferences.edit().putInt(key, value).apply();
    }

    public static int getInt(@StringRes int keyResId, int defaultValue) {
        return mPreferences.getInt(BaseApplication.getContext().getString(keyResId), defaultValue);
    }

    public static void putInt(@StringRes int keyResId, int value) {
        mPreferences.edit().putInt(BaseApplication.getContext().getString(keyResId), value).apply();
    }

    public static long getLong(String key, long defaultValue) {
        return mPreferences.getLong(key, defaultValue);
    }

    public static void putLong(String key, long value) {
        mPreferences.edit().putLong(key, value).apply();
    }

    public static long getLong(@StringRes int keyResId, long defaultValue) {
        return mPreferences.getLong(BaseApplication.getContext().getString(keyResId), defaultValue);
    }

    public static void putLong(@StringRes int keyResId, long value) {
        mPreferences.edit().putLong(BaseApplication.getContext().getString(keyResId), value).apply();
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return mPreferences.getBoolean(key, defaultValue);
    }

    public static void putBoolean(String key, boolean value) {
        mPreferences.edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(@StringRes int keyResId, boolean defaultValue) {
        return mPreferences.getBoolean(BaseApplication.getContext().getString(keyResId), defaultValue);
    }

    public static void putBoolean(@StringRes int keyResId, boolean value) {
        mPreferences.edit().putBoolean(BaseApplication.getContext().getString(keyResId), value).apply();
    }

    public static String getString(String key, String defaultValue) {
        return mPreferences.getString(key, defaultValue);
    }

    public static String getString(@StringRes int keyResId, String defaultValue) {
        return mPreferences.getString(BaseApplication.getContext().getString(keyResId), defaultValue);
    }

    public static void putString(String key, String value) {
        mPreferences.edit().putString(key, value).apply();
    }

    public static void putString(@StringRes int keyResId, String value) {
        mPreferences.edit().putString(BaseApplication.getContext().getString(keyResId), value).apply();
    }

    public static void putStringSet(String key, Set<String> stringSet) {
        mPreferences.edit().putStringSet(key, stringSet).apply();
    }

    public static Set<String> getStringSet(String key, Set<String> defaultStringSet) {
        return mPreferences.getStringSet(key, defaultStringSet);
    }

    public static void putStringSet(@StringRes int keyResId, Set<String> stringSet) {
        mPreferences.edit().putStringSet(BaseApplication.getContext().getString(keyResId), stringSet).apply();
    }

    public static Set<String> getStringSet(@StringRes int keyResId, Set<String> defaultStringSet) {
        return mPreferences.getStringSet(BaseApplication.getContext().getString(keyResId), defaultStringSet);
    }

    public static void remove(String key) {
        mPreferences.edit().remove(key).apply();
    }
}
