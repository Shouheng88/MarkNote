package me.shouheng.data.model.enums;

import android.support.annotation.StringRes;

import me.shouheng.data.R;
import me.shouheng.data.entity.Alarm;
import me.shouheng.data.entity.Attachment;
import me.shouheng.data.entity.Category;
import me.shouheng.data.entity.Location;
import me.shouheng.data.entity.Model;
import me.shouheng.data.entity.Note;
import me.shouheng.data.entity.Notebook;
import me.shouheng.data.entity.QuickNote;
import me.shouheng.data.entity.TimeLine;
import me.shouheng.data.entity.Weather;

/**
 * Created by WngShhng (shouheng2015@gmail.com) on 2017/8/12.
 */
public enum ModelType {
    NONE(0, Model.class, R.string.model_name_none),
    NOTE(3, Note.class, R.string.model_name_note),
    NOTEBOOK(9, Notebook.class, R.string.model_name_notebook),
    ALARM(10, Alarm.class, R.string.model_name_alarm),
    ATTACHMENT(11, Attachment.class, R.string.model_name_attachment),
    LOCATION(13, Location.class, R.string.model_name_location),
    MIND_SNAGGING(14, QuickNote.class, R.string.model_name_quick_note),
    TIME_LINE(15, TimeLine.class, R.string.model_name_timeline),
    WEATHER(16, Weather.class, R.string.model_name_weather),
    CATEGORY(17, Category.class, R.string.model_name_category);

    public final int id;

    public final Class<?> cls;

    @StringRes
    public final int typeName;

    ModelType(int id, Class<?> cls, int typeName) {
        this.id = id;
        this.cls = cls;
        this.typeName = typeName;
    }

    public static ModelType getTypeById(int id){
        for (ModelType type : values()){
            if (type.id == id){
                return type;
            }
        }
        return NONE;
    }

    public static ModelType getTypeByName(Class<?> cls) {
        for (ModelType type : values()){
            if (type.cls.getName().equals(cls.getName())){
                return type;
            }
        }
        return NONE;
    }
}
