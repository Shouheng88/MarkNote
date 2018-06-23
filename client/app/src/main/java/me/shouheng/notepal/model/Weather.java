package me.shouheng.notepal.model;

import me.shouheng.notepal.model.enums.WeatherType;
import me.shouheng.notepal.provider.annotation.Column;
import me.shouheng.notepal.provider.annotation.Table;
import me.shouheng.notepal.provider.schema.WeatherSchema;

/**
 * Created by shouh on 2018/3/19.*/
@Table(name = WeatherSchema.TABLE_NAME)
public class Weather extends Model {

    @Column(name = WeatherSchema.WEATHER_TYPE)
    private WeatherType type;

    @Column(name = WeatherSchema.TEMPERATURE)
    private int temperature;

    public WeatherType getType() {
        return type;
    }

    public void setType(WeatherType type) {
        this.type = type;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "type=" + type +
                ", temperature=" + temperature +
                "} " + super.toString();
    }
}
