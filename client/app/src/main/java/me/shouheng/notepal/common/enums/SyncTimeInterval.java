package me.shouheng.notepal.common.enums;

import android.support.annotation.StringRes;
import android.text.format.DateUtils;

import me.shouheng.notepal.R;

/**
 * Created by shouh on 2018/4/4.*/
public enum  SyncTimeInterval {
    EVERY_15_MINUTES(0, R.string.setting_backup_interval_sync_every_15_minutes, 15 * DateUtils.MINUTE_IN_MILLIS),
    EVERY_30_MINUTES(1, R.string.setting_backup_interval_sync_every_30_minutes, 30 * DateUtils.MINUTE_IN_MILLIS),
    EVERY_HOUR(2, R.string.setting_backup_interval_sync_every_hour, DateUtils.HOUR_IN_MILLIS),
    EVERY_2_HOURS(3, R.string.setting_backup_interval_sync_every_2_hours, 2 * DateUtils.HOUR_IN_MILLIS),
    EVERY_6_HOURS(4, R.string.setting_backup_interval_sync_every_6_hours, 6 * DateUtils.HOUR_IN_MILLIS),
    EVERY_12_HOURS(5, R.string.setting_backup_interval_sync_every_12_hours, 12 * DateUtils.HOUR_IN_MILLIS),
    DAILY(6, R.string.setting_backup_interval_sync_every_day, DateUtils.DAY_IN_MILLIS);

    public final int id;

    @StringRes
    public final int resName;

    public final long millis;

    SyncTimeInterval(int id, int resName, long millis) {
        this.id = id;
        this.resName = resName;
        this.millis = millis;
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
