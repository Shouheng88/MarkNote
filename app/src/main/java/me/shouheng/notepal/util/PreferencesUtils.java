package me.shouheng.notepal.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.polaric.colorful.Colorful;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.model.enums.FabSortItem;

/**
 * Created by Wang Shouheng on 2017/12/5. */
public class PreferencesUtils {

    private final String IS_DARK_THEME = "is_dark_theme";
    private final String PRIMARY_COLOR = "primary_color";
    private final String ACCENT_COLOR = "accent_color";

    private final String TOUR_ACTIVITY_SHOWED = "tour_activity_showed";
    private final String COLORED_NAVIGATION_BAR = "colored_navigation_bar";
    private final String FIRST_DAY_OF_WEEK = "first_day_of_week";
    private final String VIDEO_SIZE_LIMIT = "VIDEO_SIZE_LIMIT";

    private final String FAB_SORT_RESULT = "fab_sort_result";
    private final String FAB_SORT_SPLIT = ":";
    private final String LIST_ANIMATION = "list_animation";

    public static List<FabSortItem> defaultFabOrders;

    static {
        defaultFabOrders = new LinkedList<>();
        defaultFabOrders.add(FabSortItem.NOTE);
        defaultFabOrders.add(FabSortItem.NOTEBOOK);
        defaultFabOrders.add(FabSortItem.MIND_SNAGGING);
        defaultFabOrders.add(FabSortItem.NOTICE);
        defaultFabOrders.add(FabSortItem.CAPTURE);
    }

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

    // region theme
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
    // endregion

    // region preferences
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
        return getBooleanValue(COLORED_NAVIGATION_BAR, false);
    }

    public void setFirstDayOfWeek(int firstDay){
        putIntValue(FIRST_DAY_OF_WEEK, firstDay);
    }

    public int getFirstDayOfWeek(){
        return getIntValue(FIRST_DAY_OF_WEEK, Calendar.SUNDAY);
    }

    public List<FabSortItem> getFabSortResult(){
        String fabStr = getStringValue(FAB_SORT_RESULT, null);
        if (!TextUtils.isEmpty(fabStr)) {
            String[] fabs = fabStr.split(FAB_SORT_SPLIT);
            List<FabSortItem> fabSortItems = new LinkedList<>();
            for (String fab : fabs) {
                fabSortItems.add(FabSortItem.valueOf(fab));
            }
            return fabSortItems;
        } else {
            return defaultFabOrders;
        }
    }

    public void setFabSortResult(List<FabSortItem> fabSortItems){
        int size = fabSortItems.size();
        StringBuilder fabStr = new StringBuilder();
        for (int i=0;i<size;i++) {
            if (size == size - 1) {
                fabStr.append(fabSortItems.get(i).name());
            } else {
                fabStr.append(fabSortItems.get(i).name()).append(FAB_SORT_SPLIT);
            }
        }
        putStringValue(FAB_SORT_RESULT, fabStr.toString());
    }

    public void enableListAnimation(boolean enable){
        putBooleanValue(LIST_ANIMATION, enable);
    }

    public boolean listAnimationEnabled() {
        return getBooleanValue(LIST_ANIMATION, true);
    }

    public void setVideoSizeLimit(int limit){
        putIntValue(VIDEO_SIZE_LIMIT, limit);
    }

    public int getVideoSizeLimit(){
        return getIntValue(VIDEO_SIZE_LIMIT, 10);
    }
    // endregion

    // region notification
    private final String ALLOW_WAKE_LOCK = "allow_wake_lock";
    private final String LIGHT_COLOR = "light_color";
    private final String ALLOW_VIBRATE = "allow_vibrate";
    private final String SNOOZE_DURATION = "snooze_duration";
    private final String NOTIFICATION_RINGTONE = "notification_ringtone";

    public void setAllowWakeLock(boolean allowWakeLock){
        putBooleanValue(ALLOW_WAKE_LOCK, allowWakeLock);
    }

    public boolean getAllowWakeLock(){
        return getBooleanValue(ALLOW_WAKE_LOCK, false);
    }

    public void setLightColor(int lightColor){
        // 通知灯的颜色：0->绿色, 1->红色, 2->黄色, 3->蓝色, 4->白色
        putIntValue(LIGHT_COLOR, lightColor);
    }

    public int getLightColor(){
        return getIntValue(LIGHT_COLOR, 0);
    }

    public void setAllowVibrate(boolean allowVibrate){
        putBooleanValue(ALLOW_VIBRATE, allowVibrate);
    }

    public boolean isVibrateAllowed(){
        return getBooleanValue(ALLOW_VIBRATE, true);
    }

    public void setSnoozeDuration(int duration){
        putIntValue(SNOOZE_DURATION, duration);
    }

    public int getSnoozeDuration(){
        return getIntValue(SNOOZE_DURATION, 5);
    }

    public void setNotificationRingtone(String notificationRingtone){
        putStringValue(NOTIFICATION_RINGTONE, notificationRingtone);
    }

    public String getNotificationRingtone(){
        return getStringValue(NOTIFICATION_RINGTONE, null);
    }
    // endregion

    // region notes and notebooks
    private final String DEFAULT_NOTE_COLOR = "default_note_color";
    private final String NOTE_FILE_EXTENSION = "note_file_extension";
    private final String DEFAULT_NOTEBOOK_COLOR = "default_notebook_color";

    public void setDefaultNotebookColor(int color) {
        putIntValue(DEFAULT_NOTEBOOK_COLOR, color);
    }

    public int getDefaultNotebookColor() {
        return getIntValue(DEFAULT_NOTEBOOK_COLOR, ColorUtils.primaryColor(PalmApp.getContext()));
    }

    public void setDefaultNoteColor(int color) {
        putIntValue(DEFAULT_NOTE_COLOR, color);
    }

    public int getDefaultNoteColor() {
        return getIntValue(DEFAULT_NOTE_COLOR, ColorUtils.primaryColor(PalmApp.getContext()));
    }

    public String getNoteFileExtension() {
        return getStringValue(NOTE_FILE_EXTENSION, ".md");
    }

    public void setNoteFileExtension(String extension) {
        putStringValue(NOTE_FILE_EXTENSION, extension);
    }
    // endregion

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
