package me.shouheng.notepal.model.enums;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import me.shouheng.notepal.R;

/**
 * Created by wangshouheng on 2017/8/12. */
public enum Portrait {
    FOLDER(1, R.string.portrait_name_folder, R.drawable.ic_folder_black_24dp),
    LOCK(2, R.string.portrait_name_lock, R.drawable.ic_lock),
    ENVELOPE(3, R.string.portrait_name_envelope, R.drawable.ic_envelope),
    WRITE(4, R.string.portrait_name_write, R.drawable.ic_write),
    TIMELINE(5, R.string.portrait_name_timeline, R.drawable.ic_timeline),
    DEMO(6, R.string.portrait_name_demo, R.drawable.ic_demostrate),

    GROWING(7, R.string.portrait_name_growing, R.drawable.ic_grow),
    CORPORATE(8, R.string.portrait_name_corporate, R.drawable.ic_corporate),
    BOOK(9, R.string.portrait_name_book, R.drawable.ic_book),
    TOURISM_BOOK(10, R.string.portrait_name_tourism_book, R.drawable.ic_tourism),
    GIFT(11, R.string.portrait_name_gift, R.drawable.ic_gift),
    HOUSE(12, R.string.portrait_name_house, R.drawable.ic_house),

    CLOUD(13, R.string.portrait_name_cloud, R.drawable.ic_cloud),
    ATTACHMENT(14, R.string.portrait_name_attachment, R.drawable.ic_pin),
    MUSIC(15, R.string.portrait_name_music, R.drawable.ic_music),
    FLAG(16, R.string.portrait_name_flag, R.drawable.ic_flag),
    STAR(17, R.string.portrait_name_star, R.drawable.ic_star),
    MEDAL(18, R.string.portrait_name_medal, R.drawable.ic_medal),

    LINK(19, R.string.portrait_name_link, R.drawable.ic_link),
    BUG(20, R.string.portrait_name_bug, R.drawable.ic_bug),
    ALBUM(21, R.string.portrait_name_album, R.drawable.ic_albums),
    LOCATION(22, R.string.portrait_name_location, R.drawable.ic_location),
    MESSAGE(23, R.string.portrait_name_message, R.drawable.ic_message),
    LABEL(24, R.string.portrait_name_label, R.drawable.ic_label),

    LOVING_LABEL(26, R.string.portrait_name_loving_label, R.drawable.ic_loving_label),
    LOVING_FOLDER(27, R.string.portrait_name_loving_folder, R.drawable.ic_loving_folder),
    NODE(25, R.string.portrait_name_node, R.drawable.ic_node),
    NODE_CIRCLE(28, R.string.portrait_name_node_circle, R.drawable.ic_node_circle),
    FOOTBALL(29, R.string.portrait_name_football, R.drawable.ic_football),
    COLLECTION(30, R.string.portrait_name_collection, R.drawable.ic_collection);

    public final int id;

    @StringRes
    public final int nameRes;

    @DrawableRes
    public final int iconRes;

    Portrait(int id, int nameRes, int iconRes) {
        this.id = id;
        this.nameRes = nameRes;
        this.iconRes = iconRes;
    }

    public static Portrait getPortraitById(int id) {
        for (Portrait type : values()){
            if (type.id == id){
                return type;
            }
        }
        return FOLDER;
    }
}
