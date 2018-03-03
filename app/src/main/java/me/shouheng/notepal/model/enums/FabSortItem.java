package me.shouheng.notepal.model.enums;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import me.shouheng.notepal.R;


/**
 * Created by wangshouheng on 2017/8/8. */
public enum FabSortItem {
    NOTE(R.string.fab_opt_note, R.drawable.ic_note),
    NOTEBOOK(R.string.fab_opt_notebook, R.drawable.ic_folder_black_24dp),
    CATEGORY(R.string.fab_opt_tags, R.drawable.ic_labels_grey_24dp),
    FILE(R.string.fab_opt_file, R.drawable.ic_attach_file_white),
    CAPTURE(R.string.fab_opt_capture, R.drawable.ic_add_a_photo_white),
    DRAFT(R.string.fab_opt_draft, R.drawable.ic_gesture_black_24dp),
//    RECORD(R.string.fab_opt_record, R.drawable.ic_mic_white_24dp),
//    NOTICE(R.string.fab_opt_notice, R.drawable.ic_access_alarm_white),
    MIND_SNAGGING(R.string.fab_mind_snagging, R.drawable.ic_lightbulb_outline_black_24dp);

    @StringRes
    public final int nameRes;

    @DrawableRes
    public final int iconRes;

    FabSortItem(int nameRes, @DrawableRes int iconRes) {
        this.nameRes = nameRes;
        this.iconRes = iconRes;
    }
}
