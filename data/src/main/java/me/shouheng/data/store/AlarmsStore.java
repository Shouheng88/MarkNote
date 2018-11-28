package me.shouheng.data.store;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.shouheng.commons.BaseApplication;
import me.shouheng.data.entity.Alarm;
import me.shouheng.data.model.DaysOfMonth;
import me.shouheng.data.model.DaysOfWeek;
import me.shouheng.data.entity.Model;
import me.shouheng.data.model.enums.AlarmType;
import me.shouheng.data.model.enums.ModelType;
import me.shouheng.data.model.enums.Status;
import me.shouheng.data.schema.AlarmSchema;

/**
 * Created by wangshouheng on 2017/4/18.*/
public class AlarmsStore extends BaseStore<Alarm> {

    private static volatile AlarmsStore sInstance = null;

    public static AlarmsStore getInstance() {
        if (sInstance == null) {
            synchronized (AlarmsStore.class) {
                if (sInstance == null) {
                    sInstance = new AlarmsStore(BaseApplication.getContext());
                }
            }
        }
        return sInstance;
    }

    private AlarmsStore(Context context){
        super(context);
    }

    @Override
    protected void afterDBCreated(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    @Override
    public void fillModel(Alarm model, Cursor cursor) {
        model.setModelCode(cursor.getLong(cursor.getColumnIndex(AlarmSchema.MODEL_CODE)));
        model.setModelType(ModelType.getTypeById(cursor.getInt(cursor.getColumnIndex(AlarmSchema.MODEL_TYPE))));
        model.setAlarmType(AlarmType.getTypeById(cursor.getInt(cursor.getColumnIndex(AlarmSchema.ALARM_TYPE))));
        model.setEnabled(cursor.getInt(cursor.getColumnIndex(AlarmSchema.ALARM_TYPE)) == 1);
        model.setHour(cursor.getInt(cursor.getColumnIndex(AlarmSchema.HOUR)));
        model.setMinute(cursor.getInt(cursor.getColumnIndex(AlarmSchema.MINUTE)));
        model.setDaysOfWeek(DaysOfWeek.getInstance(cursor.getInt(cursor.getColumnIndex(AlarmSchema.DAYS_OF_WEEK))));
        model.setDaysOfMonth(DaysOfMonth.getInstance(cursor.getInt(cursor.getColumnIndex(AlarmSchema.DAYS_OF_MONTH))));
        model.setStartDate(new Date(cursor.getLong(cursor.getColumnIndex(AlarmSchema.START_DATE))));
        model.setEndDate(new Date(cursor.getLong(cursor.getColumnIndex(AlarmSchema.END_DATE))));
        model.setEnabled(cursor.getInt(cursor.getColumnIndex(AlarmSchema.ENABLED)) == 1);

        Calendar nextTime = Calendar.getInstance();
        nextTime.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(AlarmSchema.NEXT_TIME)));
        model.setNextTime(nextTime);
    }

    @Override
    protected void fillContentValues(ContentValues values, Alarm model) {
        values.put(AlarmSchema.MODEL_CODE, model.getModelCode());
        values.put(AlarmSchema.MODEL_TYPE, model.getModelType().id);
        values.put(AlarmSchema.ENABLED, model.isEnabled() ? 1 : 0);
        values.put(AlarmSchema.ALARM_TYPE, model.getAlarmType() == null ? AlarmType.SPECIFIED_DATE.id : model.getAlarmType().id);
        values.put(AlarmSchema.HOUR, model.getHour());
        values.put(AlarmSchema.MINUTE, model.getMinute());
        values.put(AlarmSchema.DAYS_OF_WEEK, model.getDaysOfWeek() == null ? 0 :model.getDaysOfWeek().getCoded());
        values.put(AlarmSchema.DAYS_OF_MONTH, model.getDaysOfMonth() == null ? 0 :model.getDaysOfMonth().getCoded());
        values.put(AlarmSchema.START_DATE, model.getStartDate() == null ? 0 : model.getStartDate().getTime());
        values.put(AlarmSchema.END_DATE, model.getEndDate() == null ? Long.MAX_VALUE : model.getEndDate().getTime());
        values.put(AlarmSchema.NEXT_TIME, model.getNextTime().getTimeInMillis());
    }

    public synchronized <T extends Model> Alarm getAlarm(T model, String orderSQL){
        List<Alarm> alarms = get(AlarmSchema.MODEL_TYPE + " = ? "
                        + " AND " + AlarmSchema.MODEL_CODE + " = " + model.getCode()
                        + " AND " + AlarmSchema.STATUS + " = " + Status.NORMAL.id,
                new String[]{String.valueOf(ModelType.getTypeByName(model.getClass()).id)},
                orderSQL);
        return alarms.size() > 0 ? alarms.get(0) : null;
    }
}
