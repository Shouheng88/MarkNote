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
import me.shouheng.notepal.R;
import me.shouheng.notepal.model.enums.FabSortItem;
import me.shouheng.notepal.model.enums.Operation;
import me.shouheng.notepal.util.enums.MindSnaggingListType;

/**
 * Created by Wang Shouheng on 2017/12/5. */
public class PreferencesUtils {

    public static List<FabSortItem> defaultFabOrders;

    static {
        defaultFabOrders = new LinkedList<>();
        defaultFabOrders.add(FabSortItem.NOTE);
        defaultFabOrders.add(FabSortItem.NOTEBOOK);
        defaultFabOrders.add(FabSortItem.MIND_SNAGGING);
        defaultFabOrders.add(FabSortItem.CATEGORY);
        defaultFabOrders.add(FabSortItem.FILE);
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
    public final static String IS_DARK_THEME = "is_dark_theme";
    public final static String PRIMARY_COLOR = "primary_color";
    public final static String ACCENT_COLOR = "accent_color";
    public final static String COLORED_NAVIGATION_BAR = "colored_navigation_bar";

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

    public void setColoredNavigationBar(boolean coloredNavigationBar) {
        putBooleanValue(COLORED_NAVIGATION_BAR, coloredNavigationBar);
    }

    public boolean isColoredNavigationBar() {
        return getBooleanValue(COLORED_NAVIGATION_BAR, false);
    }
    // endregion

    // region preferences
    private final String FIRST_DAY_OF_WEEK = "first_day_of_week";
    private final String VIDEO_SIZE_LIMIT = "video_size_limit";
    private final String FAB_SORT_RESULT = "fab_sort_result";
    private final String FAB_SORT_SPLIT = ":";
    private final String MIND_SNAGGINGS_LIST_TYPE = "mind_snaggings_list_type";
    private final String TOUR_ACTIVITY_SHOWED = "tour_activity_showed";
    private final String KEY_LAST_INPUT_ERROR_TIME = "last_input_error_time";
    private final String SEARCH_CONDITIONS = "search_conditions";

    public void setTourActivityShowed(boolean showed) {
        putBooleanValue(TOUR_ACTIVITY_SHOWED, showed);
    }

    public boolean isTourActivityShowed() {
        return getBooleanValue(TOUR_ACTIVITY_SHOWED, false);
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

    public void setVideoSizeLimit(int limit){
        putIntValue(VIDEO_SIZE_LIMIT, limit);
    }

    public int getVideoSizeLimit(){
        return getIntValue(VIDEO_SIZE_LIMIT, 10);
    }

    public MindSnaggingListType getMindSnaggingListType() {
        return MindSnaggingListType.getTypeById(getIntValue(MIND_SNAGGINGS_LIST_TYPE, MindSnaggingListType.TWO_COLS.id));
    }

    public void setMindSnaggingListType(MindSnaggingListType type) {
        putIntValue(MIND_SNAGGINGS_LIST_TYPE, type.id);
    }

    public void setLastInputErrorTime(long millis) {
        putLongValue(KEY_LAST_INPUT_ERROR_TIME, millis);
    }

    public long getLastInputErrorTime() {
        return getLongValue(KEY_LAST_INPUT_ERROR_TIME, 0);
    }

    public void setSearchConditions(String searchConditions) {
        putStringValue(SEARCH_CONDITIONS, searchConditions);
    }

    public String getSearchConditions() {
        return getStringValue(SEARCH_CONDITIONS, null);
    }
    // endregion

    // region universal
    private final String LIST_ANIMATION = "list_animation";
    private final String SYSTEM_ANIMATION = "system_animation";
    private final String PASSWORD_REQUIRED = "password_required";
    private final String PASSWORD = "password";
    public static final String PASSWORD_INPUT_FREEZE_TIME = "password_input_freeze_time";
    private final String PASSWORD_QUESTION = "password_question";
    private final String PASSWORD_ANSWER = "password_answer";

    public void enableListAnimation(boolean enable){
        putBooleanValue(LIST_ANIMATION, enable);
    }

    public boolean listAnimationEnabled() {
        return getBooleanValue(LIST_ANIMATION, true);
    }

    public void enableSystemAnimation(boolean enable){
        putBooleanValue(SYSTEM_ANIMATION, enable);
    }

    public boolean systemAnimationEnabled() {
        return getBooleanValue(SYSTEM_ANIMATION, true);
    }

    public void setPasswordRequired(boolean isRequired) {
        putBooleanValue(PASSWORD_REQUIRED, isRequired);
    }

    public boolean isPasswordRequired() {
        return getBooleanValue(PASSWORD_REQUIRED, false);
    }

    public void setPassword(String password) {
        putStringValue(PASSWORD, password);
    }

    public String getPassword() {
        return getStringValue(PASSWORD, null);
    }

    public int getPasswordFreezeTime() {
        return getIntValue(PASSWORD_INPUT_FREEZE_TIME, 5);
    }

    public void setPasswordFreezeTime(int time) {
        putIntValue(PASSWORD_INPUT_FREEZE_TIME, time);
    }

    public void setPasswordQuestion(String question) {
        putStringValue(PASSWORD_QUESTION, question);
    }

    public String getPasswordQuestion() {
        return getStringValue(PASSWORD_QUESTION, null);
    }

    public void setPasswordAnswer(String answer) {
        putStringValue(PASSWORD_ANSWER, answer);
    }

    public String getPasswordAnswer() {
        return getStringValue(PASSWORD_ANSWER, null);
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
    public static final String NOTE_FILE_EXTENSION = "note_file_extension";

    public String getNoteFileExtension() {
        return getStringValue(NOTE_FILE_EXTENSION, ".md");
    }

    public void setNoteFileExtension(String extension) {
        putStringValue(NOTE_FILE_EXTENSION, extension);
    }
    // endregion

    // region time line color
    private final String OPERATION_COLOR = "OPERATION_COLOR";

    public int getTimeLineColor(Operation operation) {
        return getIntValue(OPERATION_COLOR + operation.name(), defaultTimeLineColor(operation));
    }

    private int defaultTimeLineColor(Operation operation) {
        switch (operation) {
            case DELETE: return PalmApp.getContext().getResources().getColor(R.color.md_red_500);
            case TRASH: return PalmApp.getContext().getResources().getColor(R.color.md_deep_orange_500);
            case ARCHIVE: return PalmApp.getContext().getResources().getColor(R.color.md_pink_500);
            case COMPLETE: return PalmApp.getContext().getResources().getColor(R.color.md_purple_500);
            case SYNCED: return PalmApp.getContext().getResources().getColor(R.color.md_light_green_900);
            case ADD: return PalmApp.getContext().getResources().getColor(R.color.md_green_500);
            case UPDATE: return PalmApp.getContext().getResources().getColor(R.color.md_light_green_700);
            case INCOMPLETE: return PalmApp.getContext().getResources().getColor(R.color.md_blue_500);
            case RECOVER: return PalmApp.getContext().getResources().getColor(R.color.md_light_blue_600);
        }
        return ColorUtils.accentColor(PalmApp.getContext());
    }

    public void setTimeLineColor(Operation operation, int color) {
        putIntValue(OPERATION_COLOR + operation.name(), color);
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
