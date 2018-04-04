package me.shouheng.notepal.util.enums;

import android.support.annotation.StringRes;

import me.shouheng.notepal.R;

/**
 * Created by shouh on 2018/4/4.*/
public enum  SyncTimeInterval {
    EVERY_15_MINUTES(0, R.string.sync_every_15_minutes),
    EVERY_30_MINUTES(1, R.string.sync_every_30_minutes),
    EVERY_HOUR(2, R.string.sync_every_hour),
    EVERY_2_HOURS(3, R.string.sync_every_2_hours),
    EVERY_6_HOURS(4, R.string.sync_every_6_hours),
    EVERY_12_HOURS(5, R.string.sync_every_12_hours),
    DAILY(6, R.string.sync_every_day);

    public final int id;

    @StringRes
    public final int resName;

    SyncTimeInterval(int id, int resName) {
        this.id = id;
        this.resName = resName;
    }

    public static SyncTimeInterval getTypeById(int id) {
        for (SyncTimeInterval type : values()){
            if (type.id == id){
                return type;
            }
        }
        throw new IllegalArgumentException("invalid id");
    }
}
