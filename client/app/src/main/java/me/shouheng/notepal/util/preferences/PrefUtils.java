package me.shouheng.notepal.util.preferences;

import android.content.Context;
import android.text.TextUtils;

import java.util.LinkedList;
import java.util.List;

import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.model.enums.FabSortItem;
import me.shouheng.notepal.model.enums.Operation;
import my.shouheng.palmmarkdown.tools.MarkdownFormat;


/**
 * Created by shouh on 2018/4/9.*/
public class UserPreferences extends BasePreferences {

    public static List<FabSortItem> defaultFabOrders;

    public static List<MarkdownFormat> defaultMarkdownFormats;

    private final String ITEM_SORT_SPLIT = ":";

    static {
        defaultFabOrders = new LinkedList<>();
        defaultFabOrders.add(FabSortItem.NOTE);
        defaultFabOrders.add(FabSortItem.NOTEBOOK);
        defaultFabOrders.add(FabSortItem.MIND_SNAGGING);
        defaultFabOrders.add(FabSortItem.CATEGORY);
        defaultFabOrders.add(FabSortItem.FILE);

        defaultMarkdownFormats = new LinkedList<>();
        defaultMarkdownFormats.add(MarkdownFormat.H1);

        defaultMarkdownFormats.add(MarkdownFormat.NORMAL_LIST);
//        defaultMarkdownFormats.add(MarkdownFormat.NUMBER_LIST);
//        defaultMarkdownFormats.add(MarkdownFormat.CHECKBOX_OUTLINE);
        defaultMarkdownFormats.add(MarkdownFormat.CHECKBOX);

        defaultMarkdownFormats.add(MarkdownFormat.INDENT);
        defaultMarkdownFormats.add(MarkdownFormat.DEDENT);
        defaultMarkdownFormats.add(MarkdownFormat.QUOTE);
        defaultMarkdownFormats.add(MarkdownFormat.XML);
        defaultMarkdownFormats.add(MarkdownFormat.CODE_BLOCK);

        defaultMarkdownFormats.add(MarkdownFormat.STRIKE);
        defaultMarkdownFormats.add(MarkdownFormat.HORIZONTAL_LINE);

        defaultMarkdownFormats.add(MarkdownFormat.ITALIC);
        defaultMarkdownFormats.add(MarkdownFormat.BOLD);
        defaultMarkdownFormats.add(MarkdownFormat.MARK); // not standard markdown

        defaultMarkdownFormats.add(MarkdownFormat.MATH_JAX);
        defaultMarkdownFormats.add(MarkdownFormat.SUB_SCRIPT);
        defaultMarkdownFormats.add(MarkdownFormat.SUPER_SCRIPT);
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
            String[] fabs = fabStr.split(ITEM_SORT_SPLIT);
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
            if (i == size - 1) {
                fabStr.append(fabSortItems.get(i).name());
            } else {
                fabStr.append(fabSortItems.get(i).name()).append(ITEM_SORT_SPLIT);
            }
        }
        putString(getKey(R.string.key_fab_sort_result), fabStr.toString());
    }

    public List<MarkdownFormat> getMarkdownFormats() {
        String mdStr = getString(R.string.key_note_editor_menu_sort, null);
        if (!TextUtils.isEmpty(mdStr)) {
            String[] mds = mdStr.split(ITEM_SORT_SPLIT);
            List<MarkdownFormat> markdownFormats = new LinkedList<>();
            for (String md : mds) {
                markdownFormats.add(MarkdownFormat.valueOf(md));
            }
            return markdownFormats;
        } else {
            return defaultMarkdownFormats;
        }
    }

    public void setMarkdownFormats(List<MarkdownFormat> markdownFormats) {
        int size = markdownFormats.size();
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<size; i++) {
            if (i == size - 1) {
                sb.append(markdownFormats.get(i).name());
            } else {
                sb.append(markdownFormats.get(i).name()).append(ITEM_SORT_SPLIT);
            }
        }
        putString(R.string.key_note_editor_menu_sort, sb.toString());
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

    public boolean fastScrollerEnabled() {
        return getBoolean(R.string.key_fast_scroller, false);
    }
}
