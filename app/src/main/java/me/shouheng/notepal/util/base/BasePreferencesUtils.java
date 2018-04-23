package me.shouheng.notepal.util.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;

import java.util.Set;

import me.shouheng.notepal.PalmApp;

/**
 * Created by WangShouheng on 2018/3/3. */
public class BasePreferencesUtils {

    private static SharedPreferences mPreferences;

    public BasePreferencesUtils(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    protected String getKey(@StringRes int resId) {
        return PalmApp.getStringCompact(resId);
    }

    protected String getString(String key, String defaultValue) {
        return mPreferences.getString(key, defaultValue);
    }

    protected void putString(String key, String value) {
        mPreferences.edit().putString(key, value).apply();
    }

    protected int getInt(String key, int defaultValue) {
        return mPreferences.getInt(key, defaultValue);
    }

    protected void putInt(String key, int value) {
        mPreferences.edit().putInt(key, value).apply();
    }

    protected long getLong(String key, long defaultValue) {
        return mPreferences.getLong(key, defaultValue);
    }

    protected void putLong(String key, long value) {
        mPreferences.edit().putLong(key, value).apply();
    }

    protected boolean getBoolean(String key, boolean defaultValue) {
        return mPreferences.getBoolean(key, defaultValue);
    }

    protected void putBoolean(String key, boolean value) {
        mPreferences.edit().putBoolean(key, value).apply();
    }

    protected void putStringSet(String key, Set<String> stringSet) {
        mPreferences.edit().putStringSet(key, stringSet).apply();
    }

    protected Set<String> getStringSet(String key, Set<String> defaultStringSet) {
        return mPreferences.getStringSet(key, defaultStringSet);
    }

    protected String getString(@StringRes int keyResId, String defaultValue) {
        return mPreferences.getString(getKey(keyResId), defaultValue);
    }

    protected void putString(@StringRes int keyResId, String value) {
        mPreferences.edit().putString(getKey(keyResId), value).apply();
    }

    protected int getInt(@StringRes int keyResId, int defaultValue) {
        return mPreferences.getInt(getKey(keyResId), defaultValue);
    }

    protected void putInt(@StringRes int keyResId, int value) {
        mPreferences.edit().putInt(getKey(keyResId), value).apply();
    }

    protected long getLong(@StringRes int keyResId, long defaultValue) {
        return mPreferences.getLong(getKey(keyResId), defaultValue);
    }

    protected void putLong(@StringRes int keyResId, long value) {
        mPreferences.edit().putLong(getKey(keyResId), value).apply();
    }

    protected boolean getBoolean(@StringRes int keyResId, boolean defaultValue) {
        return mPreferences.getBoolean(getKey(keyResId), defaultValue);
    }

    protected void putBoolean(@StringRes int keyResId, boolean value) {
        mPreferences.edit().putBoolean(getKey(keyResId), value).apply();
    }

    protected void putStringSet(@StringRes int keyResId, Set<String> stringSet) {
        mPreferences.edit().putStringSet(getKey(keyResId), stringSet).apply();
    }

    protected Set<String> getStringSet(@StringRes int keyResId, Set<String> defaultStringSet) {
        return mPreferences.getStringSet(getKey(keyResId), defaultStringSet);
    }
}
