package me.shouheng.data.model.enums;

import android.support.annotation.DrawableRes;

import me.shouheng.data.R;

/**
 * Created by wangshouheng on 2017/8/12. */
public enum Portrait {
    FOLDER(1, R.drawable.ic_folder_black_24dp),
    LOCK(2, R.drawable.ic_lock),
    ENVELOPE(3, R.drawable.ic_envelope),
    WRITE(4, R.drawable.ic_description_black_24dp),
    TIMELINE(5, R.drawable.ic_timeline),
    DEMO(6, R.drawable.ic_demostrate),

    GROWING(7, R.drawable.ic_grow),
    CORPORATE(8, R.drawable.ic_corporate),
    BOOK(9, R.drawable.ic_book),
    TOURISM_BOOK(10, R.drawable.ic_tourism),
    GIFT(11, R.drawable.ic_gift),
    HOUSE(12, R.drawable.ic_house),

    CLOUD(13, R.drawable.ic_cloud),
    ATTACHMENT(14, R.drawable.ic_attach_file_grey),
    MUSIC(15, R.drawable.ic_music),
    FLAG(16, R.drawable.ic_flag),
    STAR(17, R.drawable.ic_star),
    MEDAL(18, R.drawable.ic_medal),

    LINK(19, R.drawable.ic_insert_link_grey_24dp),
    BUG(20, R.drawable.ic_bug),
    ALBUM(21, R.drawable.ic_albums),
    LOCATION(22, R.drawable.ic_location),
    MESSAGE(23, R.drawable.ic_message),
    LABEL(24, R.drawable.ic_label),

    LOVING_LABEL(26, R.drawable.ic_loving_label),
    LOVING_FOLDER(27, R.drawable.ic_loving_folder),
    NODE(25, R.drawable.ic_node),
    NODE_CIRCLE(28, R.drawable.ic_node_circle),
    FOOTBALL(29, R.drawable.ic_football),
    COLLECTION(30, R.drawable.ic_collection);

    public final int id;

    @DrawableRes
    public final int iconRes;

    Portrait(int id, int iconRes) {
        this.id = id;
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
