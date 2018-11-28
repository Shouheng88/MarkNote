package me.shouheng.data.model.enums;

import android.support.annotation.StringRes;

/**
 * Created by shouh on 2018/3/19.*/
public enum WeatherType {
    SUNNY(0, 0),
    RAIN(1, 0),
    SNOW(2, 0),
    SANDSTORM(3, 0);

    public final int id;

    @StringRes
    public final int nameRes;

    WeatherType(int id, @StringRes int nameRes) {
        this.id = id;
        this.nameRes = nameRes;
    }

    public static WeatherType getTypeById(int id) {
        for (WeatherType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        return SUNNY;
    }
}
