package me.shouheng.notepal.util.preferences;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.model.enums.FabSortItem;
import me.shouheng.notepal.model.enums.Operation;
import me.shouheng.notepal.util.ColorUtils;

/**
 * Created by Wang Shouheng on 2017/12/5. */
public class PreferencesUtils extends BasePreferences {

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

    public static PreferencesUtils getInstance() {
        if (sInstance == null) {
            synchronized (PreferencesUtils.class) {
                if (sInstance == null){
                    sInstance = new PreferencesUtils(PalmApp.getContext());
                }
            }
        }
        return sInstance;
    }

    protected PreferencesUtils(Context context) {
        super(context);
    }

    // region user preferences
    private final String FIRST_DAY_OF_WEEK = "first_day_of_week";
    private final String VIDEO_SIZE_LIMIT = "video_size_limit";
    private final String FAB_SORT_RESULT = "fab_sort_result";
    private final String TOUR_ACTIVITY_SHOWED = "tour_activity_showed";
    private final String KEY_LAST_INPUT_ERROR_TIME = "last_input_error_time";
    private final String SEARCH_CONDITIONS = "search_conditions";

    private final String KEY_ATTACHMENT_URI = "key_attachment_uri";
    private final String KEY_ATTACHMENT_FILE_PATH = "key_attachment_file_path";

    private final String KEY_USER_INFO_BG = "key_user_info_background_image";
    private final String KEY_USER_INFO_BG_ENABLE = "key_user_info_bg_visible";
    private final String KEY_USER_INFO_MOTTO = "key_user_info_motto";

    /**
     * The user info background in main activity. */
    public void setUserInfoBG(@Nullable Uri uri) {
        putString(KEY_USER_INFO_BG, uri == null ? "" : uri.toString());
    }

    public Uri getUserInfoBG() {
        String bgUri = getString(KEY_USER_INFO_BG, null);
        if (!TextUtils.isEmpty(bgUri)) {
            return Uri.parse(bgUri);
        }
        return Uri.parse(Constants.DEFAULT_USER_INFO_BG);
    }

    public void setUserInfoBGEnable(boolean enable) {
        putBoolean(KEY_USER_INFO_BG_ENABLE, enable);
    }

    public boolean isUserInfoBgEnable() {
        return getBoolean(KEY_USER_INFO_BG_ENABLE, true);
    }

    public void setUserMotto(String motto) {
        putString(KEY_USER_INFO_MOTTO, motto);
    }

    public String getUserMotto() {
        return getString(KEY_USER_INFO_MOTTO, PalmApp.getStringCompact(R.string.setting_dashboard_user_motto_default));
    }

    public void setAttachmentUri(@NonNull Uri uri) {
        putString(KEY_ATTACHMENT_URI, uri.toString());
    }

    public String getAttachmentUri() {
        return getString(KEY_ATTACHMENT_URI, "");
    }

    public void setAttachmentFilePath(String filePath) {
        putString(KEY_ATTACHMENT_FILE_PATH, filePath);
    }

    public String getAttachmentFilePath() {
        return getString(KEY_ATTACHMENT_FILE_PATH, "");
    }

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
    public static final String PASSWORD_INPUT_FREEZE_TIME = "password_input_freeze_time";
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
    // endregion
}
