package me.shouheng.notepal.util.preferences;

import android.content.Context;
import android.text.TextUtils;

import java.util.LinkedList;
import java.util.List;

import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.easymark.editor.Format;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.model.enums.FabSortItem;
import me.shouheng.notepal.model.enums.Operation;

/**
 * Created by shouh on 2018/4/9.*/
public class PrefUtils extends BasePreferences {

    private static PrefUtils instance = new PrefUtils(PalmApp.getContext());

    public static List<FabSortItem> defaultFabOrders;

    public static List<Format> defaultMarkdownFormats;

    static {
        // prepare default fab orders
        defaultFabOrders = new LinkedList<>();
        defaultFabOrders.add(FabSortItem.NOTE);
        defaultFabOrders.add(FabSortItem.NOTEBOOK);
        defaultFabOrders.add(FabSortItem.MIND_SNAGGING);
        defaultFabOrders.add(FabSortItem.CATEGORY);
        defaultFabOrders.add(FabSortItem.FILE);

        // prepare default markdown formats
        defaultMarkdownFormats = new LinkedList<>();
        defaultMarkdownFormats.add(Format.H1);

        defaultMarkdownFormats.add(Format.NORMAL_LIST);
//        defaultMarkdownFormats.add(MarkdownFormat.NUMBER_LIST);
//        defaultMarkdownFormats.add(MarkdownFormat.CHECKBOX_OUTLINE);
        defaultMarkdownFormats.add(Format.CHECKBOX);

        defaultMarkdownFormats.add(Format.INDENT);
        defaultMarkdownFormats.add(Format.DEDENT);
        defaultMarkdownFormats.add(Format.QUOTE);
        defaultMarkdownFormats.add(Format.CODE_INLINE);
        defaultMarkdownFormats.add(Format.CODE_BLOCK);

        defaultMarkdownFormats.add(Format.STRIKE);
        defaultMarkdownFormats.add(Format.HORIZONTAL_LINE);

        defaultMarkdownFormats.add(Format.ITALIC);
        defaultMarkdownFormats.add(Format.BOLD);
        defaultMarkdownFormats.add(Format.MARK); // not standard markdown

        defaultMarkdownFormats.add(Format.MATH_JAX);
        defaultMarkdownFormats.add(Format.SUB_SCRIPT);
        defaultMarkdownFormats.add(Format.SUPER_SCRIPT);
    }

    private final String ITEM_SORT_SPLIT = ":";

    public static PrefUtils getInstance() {
        return instance;
    }

    private PrefUtils(Context context) {
        super(context);
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

    public List<Format> getMarkdownFormats() {
        String mdStr = getString(R.string.key_note_editor_menu_sort, null);
        if (!TextUtils.isEmpty(mdStr)) {
            String[] mds = mdStr.split(ITEM_SORT_SPLIT);
            List<Format> markdownFormats = new LinkedList<>();
            for (String md : mds) {
                markdownFormats.add(Format.valueOf(md));
            }
            return markdownFormats;
        } else {
            return defaultMarkdownFormats;
        }
    }

    public void setMarkdownFormats(List<Format> markdownFormats) {
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
