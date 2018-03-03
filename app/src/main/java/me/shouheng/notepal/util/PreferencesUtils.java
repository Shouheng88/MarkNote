package me.shouheng.notepal.util;

import android.content.Context;
import android.text.TextUtils;

import org.polaric.colorful.Colorful;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.model.enums.FabSortItem;
import me.shouheng.notepal.model.enums.Operation;
import me.shouheng.notepal.util.base.BasePreferencesUtils;
import me.shouheng.notepal.util.enums.MindSnaggingListType;

/**
 * Created by Wang Shouheng on 2017/12/5. */
public class PreferencesUtils extends BasePreferencesUtils {

    public static List<FabSortItem> defaultFabOrders;

    private final String FAB_SORT_SPLIT = ":";

    static {
        defaultFabOrders = new LinkedList<>();
        defaultFabOrders.add(FabSortItem.NOTE);
        defaultFabOrders.add(FabSortItem.NOTEBOOK);
        defaultFabOrders.add(FabSortItem.MIND_SNAGGING);
        defaultFabOrders.add(FabSortItem.CATEGORY);
        defaultFabOrders.add(FabSortItem.FILE);
    }

    private static PreferencesUtils sInstance;

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
        super(context);
    }

    // region theme values preferences
    public final static String IS_DARK_THEME = "is_dark_theme";
    public final static String PRIMARY_COLOR = "primary_color";
    public final static String ACCENT_COLOR = "accent_color";
    public final static String COLORED_NAVIGATION_BAR = "colored_navigation_bar";

    public void setDarkTheme(boolean isDarkTheme) {
        putBoolean(IS_DARK_THEME, isDarkTheme);
    }

    public boolean isDarkTheme() {
        return getBoolean(IS_DARK_THEME, false);
    }

    public void setThemeColor(Colorful.ThemeColor themeColor){
        putString(PRIMARY_COLOR, themeColor.getPrimaryName());
    }

    public Colorful.ThemeColor getThemeColor(){
        return Colorful.ThemeColor.getByPrimaryName(getString(PRIMARY_COLOR, Colorful.ThemeColor.GREEN.getPrimaryName()));
    }

    public Colorful.AccentColor getAccentColor() {
        return Colorful.AccentColor.getByAccentName(getString(ACCENT_COLOR, Colorful.AccentColor.GREEN_700.getColorName()));
    }

    public void setAccentColor(Colorful.AccentColor accentColor){
        putString(ACCENT_COLOR, accentColor.getAccentName());
    }

    public void setColoredNavigationBar(boolean coloredNavigationBar) {
        putBoolean(COLORED_NAVIGATION_BAR, coloredNavigationBar);
    }

    public boolean isColoredNavigationBar() {
        return getBoolean(COLORED_NAVIGATION_BAR, false);
    }
    // endregion

    // region user preferences
    private final String FIRST_DAY_OF_WEEK = "first_day_of_week";
    private final String VIDEO_SIZE_LIMIT = "video_size_limit";
    private final String FAB_SORT_RESULT = "fab_sort_result";
    private final String MIND_SNAGGING_LIST_TYPE = "mind_snagging_list_type";
    private final String TOUR_ACTIVITY_SHOWED = "tour_activity_showed";
    private final String KEY_LAST_INPUT_ERROR_TIME = "last_input_error_time";
    private final String SEARCH_CONDITIONS = "search_conditions";

    public void setTourActivityShowed(boolean showed) {
        putBoolean(TOUR_ACTIVITY_SHOWED, showed);
    }

    public boolean isTourActivityShowed() {
        return getBoolean(TOUR_ACTIVITY_SHOWED, false);
    }

    public void setFirstDayOfWeek(int firstDay){
        putInt(FIRST_DAY_OF_WEEK, firstDay);
    }

    public int getFirstDayOfWeek(){
        return getInt(FIRST_DAY_OF_WEEK, Calendar.SUNDAY);
    }

    public List<FabSortItem> getFabSortResult(){
        String fabStr = getString(FAB_SORT_RESULT, null);
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
        putString(FAB_SORT_RESULT, fabStr.toString());
    }

    public void setVideoSizeLimit(int limit){
        putInt(VIDEO_SIZE_LIMIT, limit);
    }

    public int getVideoSizeLimit(){
        return getInt(VIDEO_SIZE_LIMIT, 10);
    }

    public MindSnaggingListType getMindSnaggingListType() {
        return MindSnaggingListType.getTypeById(getInt(MIND_SNAGGING_LIST_TYPE, MindSnaggingListType.TWO_COLS.id));
    }

    public void setMindSnaggingListType(MindSnaggingListType type) {
        putInt(MIND_SNAGGING_LIST_TYPE, type.id);
    }

    public void setLastInputErrorTime(long millis) {
        putLong(KEY_LAST_INPUT_ERROR_TIME, millis);
    }

    public long getLastInputErrorTime() {
        return getLong(KEY_LAST_INPUT_ERROR_TIME, 0);
    }

    public void setSearchConditions(String searchConditions) {
        putString(SEARCH_CONDITIONS, searchConditions);
    }

    public String getSearchConditions() {
        return getString(SEARCH_CONDITIONS, null);
    }
    // endregion

    // region universal settings
    private final String LIST_ANIMATION = "list_animation";
    private final String SYSTEM_ANIMATION = "system_animation";
    private final String PASSWORD_REQUIRED = "password_required";
    private final String PASSWORD = "password";
    public static final String PASSWORD_INPUT_FREEZE_TIME = "password_input_freeze_time";
    private final String PASSWORD_QUESTION = "password_question";
    private final String PASSWORD_ANSWER = "password_answer";
    private static final String NOTE_FILE_EXTENSION = "note_file_extension";
    private final String OPERATION_COLOR = "OPERATION_COLOR";

    public int getTimeLineColor(Operation operation) {
        return getInt(OPERATION_COLOR + operation.name(), defaultTimeLineColor(operation));
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
        putInt(OPERATION_COLOR + operation.name(), color);
    }

    public String getNoteFileExtension() {
        return getString(NOTE_FILE_EXTENSION, "md");
    }

    public void setNoteFileExtension(String extension) {
        putString(NOTE_FILE_EXTENSION, extension);
    }

    public void enableListAnimation(boolean enable){
        putBoolean(LIST_ANIMATION, enable);
    }

    public boolean listAnimationEnabled() {
        return getBoolean(LIST_ANIMATION, true);
    }

    public void enableSystemAnimation(boolean enable){
        putBoolean(SYSTEM_ANIMATION, enable);
    }

    public boolean systemAnimationEnabled() {
        return getBoolean(SYSTEM_ANIMATION, true);
    }

    public void setPasswordRequired(boolean isRequired) {
        putBoolean(PASSWORD_REQUIRED, isRequired);
    }

    public boolean isPasswordRequired() {
        return getBoolean(PASSWORD_REQUIRED, false);
    }

    public void setPassword(String password) {
        putString(PASSWORD, password);
    }

    public String getPassword() {
        return getString(PASSWORD, null);
    }

    public int getPasswordFreezeTime() {
        return getInt(PASSWORD_INPUT_FREEZE_TIME, 5);
    }

    public void setPasswordFreezeTime(int time) {
        putInt(PASSWORD_INPUT_FREEZE_TIME, time);
    }

    public void setPasswordQuestion(String question) {
        putString(PASSWORD_QUESTION, question);
    }

    public String getPasswordQuestion() {
        return getString(PASSWORD_QUESTION, null);
    }

    public void setPasswordAnswer(String answer) {
        putString(PASSWORD_ANSWER, answer);
    }

    public String getPasswordAnswer() {
        return getString(PASSWORD_ANSWER, null);
    }
    // endregion

    // region notification settings
    private final String ALLOW_WAKE_LOCK = "allow_wake_lock";
    private final String LIGHT_COLOR = "light_color";
    private final String ALLOW_VIBRATE = "allow_vibrate";
    private final String SNOOZE_DURATION = "snooze_duration";
    private final String NOTIFICATION_RINGTONE = "notification_ringtone";

    public void setAllowWakeLock(boolean allowWakeLock){
        putBoolean(ALLOW_WAKE_LOCK, allowWakeLock);
    }

    public boolean getAllowWakeLock(){
        return getBoolean(ALLOW_WAKE_LOCK, false);
    }

    public void setLightColor(int lightColor){
        // 通知灯的颜色：0->绿色, 1->红色, 2->黄色, 3->蓝色, 4->白色
        putInt(LIGHT_COLOR, lightColor);
    }

    public int getLightColor(){
        return getInt(LIGHT_COLOR, 0);
    }

    public void setAllowVibrate(boolean allowVibrate){
        putBoolean(ALLOW_VIBRATE, allowVibrate);
    }

    public boolean isVibrateAllowed(){
        return getBoolean(ALLOW_VIBRATE, true);
    }

    public void setSnoozeDuration(int duration){
        putInt(SNOOZE_DURATION, duration);
    }

    public int getSnoozeDuration(){
        return getInt(SNOOZE_DURATION, 5);
    }

    public void setNotificationRingtone(String notificationRingtone){
        putString(NOTIFICATION_RINGTONE, notificationRingtone);
    }

    public String getNotificationRingtone(){
        return getString(NOTIFICATION_RINGTONE, null);
    }
    // endregion
}
