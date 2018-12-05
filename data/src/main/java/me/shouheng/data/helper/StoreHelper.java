package me.shouheng.data.helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import me.shouheng.data.utils.annotation.Column;
import me.shouheng.data.entity.Location;
import me.shouheng.data.entity.Model;
import me.shouheng.data.entity.TimeLine;
import me.shouheng.data.model.enums.Operation;
import me.shouheng.data.model.enums.Status;
import me.shouheng.data.schema.BaseSchema;
import me.shouheng.data.schema.LocationSchema;
import me.shouheng.data.schema.TimelineSchema;

/**
 * Created by WngShhng on 2017/12/10.
 */
public class StoreHelper {

    /**
     * sql fragment used to create entity db
     */
    private final static String CREATE_MODEL_FIELDS_SQL = "("
            + BaseSchema.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + BaseSchema.CODE + " INTEGER NOT NULL, "
            + BaseSchema.USER_ID + " TEXT NOT NULL, "
            + BaseSchema.ADDED_TIME + " INTEGER, "
            + BaseSchema.LAST_MODIFIED_TIME + " INTEGER, "
            + BaseSchema.STATUS + " INTEGER, "
            + BaseSchema.LAST_SYNC_TIME + " INTEGER,";

    public static <T extends Model> String getTableCreateSQL(String tableName, Class<T> entityClass) {
        StringBuilder sb = new StringBuilder(" CREATE TABLE IF NOT EXISTS ");
        sb.append(tableName);
        sb.append(CREATE_MODEL_FIELDS_SQL);
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                sb.append(" ");
                sb.append(field.getAnnotation(Column.class).name());
                Class type = field.getType();
                if (String.class == type || Uri.class == type) {
                    sb.append(" TEXT,");
                } else {
                    sb.append(" INTEGER,");
                }
            }
        }
        return sb.toString().substring(0, sb.length() - 1) + ")";
    }

    /**
     * get {@link ContentValues} from the entity
     *
     * @param model entity
     * @param <T> generic type
     * @return the {@link ContentValues}
     */
    public static <T extends Model> ContentValues getBaseContentValues(T model){
        ContentValues values = new ContentValues();
        values.put(BaseSchema.CODE, model.getCode());
        values.put(BaseSchema.USER_ID, model.getUserId());
        values.put(BaseSchema.ADDED_TIME, model.getAddedTime() == null ? 0 : model.getAddedTime().getTime());
        values.put(BaseSchema.LAST_MODIFIED_TIME, model.getLastModifiedTime() == null ? 0 : model.getLastModifiedTime().getTime());
        values.put(BaseSchema.STATUS, model.getStatus() == null ? Status.NORMAL.id : model.getStatus().id);
        values.put(BaseSchema.LAST_SYNC_TIME, model.getLastSyncTime() == null ? 0 : model.getLastSyncTime().getTime());
        return values;
    }

    /**
     * try to get the {@link Operation} name from given {@link Status} type
     *
     * @param toStatus status that we want to transfer to
     * @return the operation type
     */
    public static Operation getStatusOperation(Status toStatus) {
        switch (toStatus) {
            case ARCHIVED:
                return Operation.ARCHIVE;
            case TRASHED:
                return Operation.TRASH;
            case NORMAL:
                return Operation.RECOVER;
            case DELETED:
                return Operation.DELETE;
        }
        return Operation.NONE;
    }

    /**
     * get the entity from cursor that filled with the fields of class {@link Model}
     *
     * @param cursor cursor get from database
     * @param type the type of the entity
     * @param <T> generic type
     * @return the entity
     */
    public static <T extends Model> T getBaseModel(Cursor cursor, Class<T> type) {
        try {
            T model;
            fillBaseFields(cursor, model = type.newInstance());
            return model;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static <T extends Model> void fillBaseFields(Cursor cursor, T model) {
        model.setCode(cursor.getLong(cursor.getColumnIndex(BaseSchema.CODE)));
        model.setUserId(cursor.getLong(cursor.getColumnIndex(BaseSchema.USER_ID)));
        model.setAddedTime(new Date(cursor.getLong(cursor.getColumnIndex(BaseSchema.ADDED_TIME))));
        model.setLastModifiedTime(new Date(cursor.getLong(cursor.getColumnIndex(BaseSchema.LAST_MODIFIED_TIME))));
        model.setStatus(Status.getStatusById(cursor.getInt(cursor.getColumnIndex(BaseSchema.STATUS))));
        model.setLastSyncTime(new Date(cursor.getLong(cursor.getColumnIndex(BaseSchema.LAST_SYNC_TIME))));
    }

    /**
     * fill the {@link Model#lastModifiedTime} field of entity
     *
     * @param model the entity
     * @param <T> generic type of entity
     */
    public static <T extends Model> void setLastModifiedInfo(T model) {
        model.setLastModifiedTime(new Date());
    }

    public static ContentValues getContentValues(TimeLine timeLine) {
        ContentValues values = getBaseContentValues(timeLine);
        values.put(TimelineSchema.OPERATION, timeLine.getOperation().id);
        values.put(TimelineSchema.MODEL_CODE, timeLine.getModelCode());
        values.put(TimelineSchema.MODEL_TYPE, timeLine.getModelType().id);
        values.put(TimelineSchema.MODEL_NAME, timeLine.getModelName());
        return values;
    }

    public static List<Location> getDistinctLocations(Cursor cursor) {
        List<Location> locations = new LinkedList<>();
        if (cursor.moveToFirst()) {
            do {
                Location location = new Location();
                location.setCountry(cursor.getString(cursor.getColumnIndex(LocationSchema.COUNTRY)));
                location.setProvince(cursor.getString(cursor.getColumnIndex(LocationSchema.PROVINCE)));
                location.setCity(cursor.getString(cursor.getColumnIndex(LocationSchema.CITY)));
                location.setDistrict(cursor.getString(cursor.getColumnIndex(LocationSchema.DISTRICT)));
                locations.add(location);
            } while (cursor.moveToNext());
        }
        return locations;
    }
}
