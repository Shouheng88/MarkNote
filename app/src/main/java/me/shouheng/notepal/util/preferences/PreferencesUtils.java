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
    private final String FAB_SORT_RESULT = "fab_sort_result";
    private final String TOUR_ACTIVITY_SHOWED = "tour_activity_showed";
    private final String SEARCH_CONDITIONS = "search_conditions";

    private final String KEY_ATTACHMENT_URI = "key_attachment_uri";
    private final String KEY_ATTACHMENT_FILE_PATH = "key_attachment_file_path";

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

    public void setSearchConditions(String searchConditions) {
        putString(SEARCH_CONDITIONS, searchConditions);
    }

    public String getSearchConditions() {
        return getString(SEARCH_CONDITIONS, null);
    }
    // endregion
}
