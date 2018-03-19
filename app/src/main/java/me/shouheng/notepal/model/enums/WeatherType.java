package me.shouheng.notepal.model.enums;

/**
 * Created by shouh on 2018/3/19.*/
public enum WeatherType {
    SUNNY(0),
    RAIN(1),
    SNOW(2),
    SANDSTORM(3);

    public final int id;

    WeatherType(int id) {
        this.id = id;
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
