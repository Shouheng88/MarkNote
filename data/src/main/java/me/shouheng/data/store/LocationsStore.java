package me.shouheng.data.store;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.List;

import me.shouheng.commons.BaseApplication;
import me.shouheng.data.helper.StoreHelper;
import me.shouheng.data.entity.Location;
import me.shouheng.data.entity.Note;
import me.shouheng.data.model.enums.ModelType;
import me.shouheng.data.model.enums.Status;
import me.shouheng.data.schema.BaseSchema;
import me.shouheng.data.schema.LocationSchema;

/**
 * Created by wangshouheng on 2017/4/6.*/
public class LocationsStore extends BaseStore<Location> {

    private static volatile LocationsStore sInstance = null;

    public static LocationsStore getInstance() {
        if (sInstance == null) {
            synchronized (LocationsStore.class) {
                if (sInstance == null) {
                    sInstance = new LocationsStore(BaseApplication.getContext());
                }
            }
        }
        return sInstance;
    }

    private LocationsStore(final Context context){
        super(context);
    }

    @Override
    protected void afterDBCreated(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion){}

    @Override
    public void fillModel(Location model, Cursor cursor) {
        model.setLongitude(cursor.getDouble(cursor.getColumnIndex(LocationSchema.LONGITUDE)));
        model.setLatitude(cursor.getDouble(cursor.getColumnIndex(LocationSchema.LATITUDE)));
        model.setCountry(cursor.getString(cursor.getColumnIndex(LocationSchema.COUNTRY)));
        model.setProvince(cursor.getString(cursor.getColumnIndex(LocationSchema.PROVINCE)));
        model.setCity(cursor.getString(cursor.getColumnIndex(LocationSchema.CITY)));
        model.setDistrict(cursor.getString(cursor.getColumnIndex(LocationSchema.DISTRICT)));
        model.setModelCode(cursor.getLong(cursor.getColumnIndex(LocationSchema.MODEL_CODE)));
        model.setModelType(ModelType.getTypeById(cursor.getInt(cursor.getColumnIndex(LocationSchema.MODEL_TYPE))));
    }

    @Override
    protected void fillContentValues(ContentValues values, Location model) {
        values.put(LocationSchema.LONGITUDE, model.getLongitude());
        values.put(LocationSchema.LATITUDE, model.getLatitude());
        values.put(LocationSchema.COUNTRY, model.getCountry());
        values.put(LocationSchema.PROVINCE, model.getProvince());
        values.put(LocationSchema.CITY, model.getCity());
        values.put(LocationSchema.DISTRICT, model.getDistrict());
        values.put(LocationSchema.MODEL_CODE, model.getModelCode());
        values.put(LocationSchema.MODEL_TYPE, model.getModelType().id);
    }

    private synchronized Location getLocation(String whereSQL, String orderSQL){
        Cursor cursor = null;
        Location location = null;
        SQLiteDatabase database = getWritableDatabase();
        try {
            cursor = database.rawQuery(" SELECT * FROM " + tableName +
                            " WHERE " + LocationSchema.USER_ID + " = ? "
                            + (TextUtils.isEmpty(whereSQL) ? "" : " AND " + whereSQL)
                            + " AND " + LocationSchema.STATUS + " = " + Status.NORMAL.id
                            + (TextUtils.isEmpty(orderSQL) ? "" : " ORDER BY " + orderSQL),
                    new String[]{String.valueOf(userId)});
            location = get(cursor);
        } finally {
            closeCursor(cursor);
            closeDatabase(database);
        }
        return location;
    }

    public synchronized Location getLocation(Note note) {
        return getLocation(LocationSchema.MODEL_CODE + " = " + note.getCode()
                + " AND " + LocationSchema.MODEL_TYPE + " = " + ModelType.NOTE.id,
                LocationSchema.ADDED_TIME + " DESC ");
    }

    public synchronized List<Location> getDistinct(String whereSQL, String orderSQL) {
        Cursor cursor = null;
        List<Location> models = null;
        SQLiteDatabase database = getWritableDatabase();
        try {
            cursor = database.rawQuery(" SELECT DISTINCT "
                    + LocationSchema.COUNTRY + "," + LocationSchema.PROVINCE + "," + LocationSchema.CITY + "," + LocationSchema.DISTRICT
                    + " FROM " + tableName
                    + " WHERE " + BaseSchema.USER_ID + " = " + userId
                    + (TextUtils.isEmpty(whereSQL) ? "" : " AND " + whereSQL)
                    + (TextUtils.isEmpty(orderSQL) ? "" : " ORDER BY " + orderSQL), new String[]{});
            models = StoreHelper.getDistinctLocations(cursor);
        } finally {
            closeCursor(cursor);
            closeDatabase(database);
        }
        return models;
    }
}
