package me.shouheng.notepal.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import me.shouheng.data.model.enums.ModelType;
import me.shouheng.data.model.enums.Operation;
import me.shouheng.notepal.model.TimeLine;
import me.shouheng.notepal.provider.schema.TimelineSchema;


/**
 * 时间线的数据库方法
 *
 * Created by wangshouheng on 2017/8/13. */
public class TimelineStore extends BaseStore<TimeLine> {

    private static TimelineStore sInstance = null;

    public static TimelineStore getInstance(Context context){
        if (sInstance == null){
            synchronized (TimelineStore.class) {
                if (sInstance == null) {
                    sInstance = new TimelineStore(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    private TimelineStore(Context context) {
        super(context);
    }

    @Override
    protected void afterDBCreated(SQLiteDatabase db) {}

    @Override
    protected void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    @Override
    public void fillModel(TimeLine model, Cursor cursor) {
        model.setOperation(Operation.getTypeById(cursor.getInt(cursor.getColumnIndex(TimelineSchema.OPERATION))));
        model.setModelCode(cursor.getLong(cursor.getColumnIndex(TimelineSchema.MODEL_CODE)));
        model.setModelName(cursor.getString(cursor.getColumnIndex(TimelineSchema.MODEL_NAME)));
        model.setModelType(ModelType.getTypeById(cursor.getInt(cursor.getColumnIndex(TimelineSchema.MODEL_TYPE))));
    }

    @Override
    protected void fillContentValues(ContentValues values, TimeLine model) {
        values.put(TimelineSchema.OPERATION, model.getOperation().id);
        values.put(TimelineSchema.MODEL_CODE, model.getModelCode());
        values.put(TimelineSchema.MODEL_NAME, model.getModelName());
        values.put(TimelineSchema.MODEL_TYPE, model.getModelType().id);
    }
}
