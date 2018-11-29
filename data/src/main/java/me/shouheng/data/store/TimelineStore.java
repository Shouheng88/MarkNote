package me.shouheng.data.store;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import me.shouheng.commons.BaseApplication;
import me.shouheng.data.entity.TimeLine;
import me.shouheng.data.model.enums.ModelType;
import me.shouheng.data.model.enums.Operation;
import me.shouheng.data.schema.TimelineSchema;

/**
 * Created by wangshouheng on 2017/8/13. */
public class TimelineStore extends BaseStore<TimeLine> {

    private static volatile TimelineStore sInstance = null;

    public static TimelineStore getInstance() {
        if (sInstance == null) {
            synchronized (TimelineStore.class) {
                if (sInstance == null) {
                    sInstance = new TimelineStore(BaseApplication.getContext());
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
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

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
