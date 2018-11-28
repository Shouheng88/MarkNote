package me.shouheng.data.model.enums;

/**
 * Created by wangshouheng on 2017/4/30. */
public enum AlarmType {
    BAD_BOY(0),
    SPECIFIED_DATE(1),
    WEEK_REPEAT(2),
    MONTH_REPEAT(3);

    public final int id;

    AlarmType(int id){
        this.id = id;
    }

    public static AlarmType getTypeById(int id){
        for (AlarmType type : values()){
            if (type.id == id){
                return type;
            }
        }
        return SPECIFIED_DATE;
    }
}
