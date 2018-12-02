package me.shouheng.data.model.enums;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import me.shouheng.data.R;

/**
 * Created by WngShhng on 2017/8/8.
 */
public enum FabSortItem {
    NOTE(R.string.fab_opt_note, R.drawable.ic_description_black_24dp),
    NOTEBOOK(R.string.fab_opt_notebook, R.drawable.ic_book),
    CATEGORY(R.string.fab_opt_tags, R.drawable.ic_view_module_white_24dp),
    IMAGE(R.string.fab_opt_image, R.drawable.ic_format_image_white_24dp),
    CAPTURE(R.string.fab_opt_capture, R.drawable.ic_add_a_photo_white),
    DRAFT(R.string.fab_opt_draft, R.drawable.ic_gesture_grey_24dp),
    QUICK_NOTE(R.string.fab_quick_note, R.drawable.ic_lightbulb_outline_black_24dp);

    @StringRes
    public final int nameRes;

    @DrawableRes
    public final int iconRes;

    FabSortItem(int nameRes, @DrawableRes int iconRes) {
        this.nameRes = nameRes;
        this.iconRes = iconRes;
    }
}
