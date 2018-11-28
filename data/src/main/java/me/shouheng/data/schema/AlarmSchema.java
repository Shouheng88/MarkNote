package me.shouheng.data.schema;

/**
 * Created by WngShhng on 2017/12/10.*/
public interface AlarmSchema extends BaseSchema {
    String TABLE_NAME = "gt_alarm";
    String MODEL_CODE = "model_code";
    String MODEL_TYPE = "model_type";
    String ALARM_TYPE = "alarm_type";
    String HOUR = "hour";
    String MINUTE = "minute";
    String DAYS_OF_WEEK = "days_of_week";
    String DAYS_OF_MONTH = "days_of_month";
    String START_DATE = "start_date";
    String END_DATE = "end_date";
    String NEXT_TIME = "next_time";
    String ENABLED = "enabled";
}
