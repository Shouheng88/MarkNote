package me.shouheng.notepal.util.preferences;

import android.content.Context;
import android.text.TextUtils;

import java.util.LinkedList;
import java.util.List;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.model.enums.FabSortItem;
import me.shouheng.notepal.model.enums.Operation;
import me.shouheng.notepal.util.ColorUtils;


/**
 * Created by shouh on 2018/4/9.*/
public class UserPreferences extends BasePreferences {

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

    private static UserPreferences preferences;

    public static UserPreferences getInstance() {
        if (preferences == null) {
            synchronized (UserPreferences.class) {
                if (preferences == null) {
                    preferences = new UserPreferences(PalmApp.getContext());
                }
            }
        }
        return preferences;
    }

    private UserPreferences(Context context) {
        super(context);
    }

    public boolean isImageAutoCompress() {
        return getBoolean(getKey(R.string.key_auto_compress_image), true);
    }

    public boolean listAnimationEnabled() {
        return getBoolean(getKey(R.string.key_list_animation), true);
    }

    public boolean systemAnimationEnabled() {
        return getBoolean(getKey(R.string.key_system_animation), true);
    }

    public List<FabSortItem> getFabSortResult() {
        String fabStr = getString(getKey(R.string.key_fab_sort_result), null);
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

    public void setFabSortResult(List<FabSortItem> fabSortItems) {
        int size = fabSortItems.size();
        StringBuilder fabStr = new StringBuilder();
        for (int i=0;i<size;i++) {
            if (size == size - 1) {
                fabStr.append(fabSortItems.get(i).name());
            } else {
                fabStr.append(fabSortItems.get(i).name()).append(FAB_SORT_SPLIT);
            }
        }
        putString(getKey(R.string.key_fab_sort_result), fabStr.toString());
    }

    public int getTimeLineColor(Operation operation) {
        return getInt(getKey(R.string.key_operation_color_prefix) + operation.name(), defaultTimeLineColor(operation));
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

    public void setSearchConditions(String searchConditions) {
        putString(R.string.key_search_conditions, searchConditions);
    }

    public String getSearchConditions() {
        return getString(R.string.key_search_conditions, null);
    }
}
