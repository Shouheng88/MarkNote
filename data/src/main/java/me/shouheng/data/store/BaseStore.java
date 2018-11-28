package me.shouheng.data.store;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.lang.reflect.ParameterizedType;
import java.util.LinkedList;
import java.util.List;

import me.shouheng.commons.utils.LogUtils;
import me.shouheng.commons.utils.UserUtil;
import me.shouheng.data.PalmDB;
import me.shouheng.data.utils.annotation.Table;
import me.shouheng.data.helper.StoreHelper;
import me.shouheng.data.helper.TimelineHelper;
import me.shouheng.data.entity.Model;
import me.shouheng.data.model.enums.Operation;
import me.shouheng.data.model.enums.Status;
import me.shouheng.data.schema.BaseSchema;
import me.shouheng.data.utils.OpenUtils;

/**
 * Created by wangshouheng on 2017/8/18. */
public abstract class BaseStore<T extends Model> {

    private PalmDB mPalmDatabase;

    protected Class<T> entityClass;

    protected String tableName;

    protected long userId;

    @SuppressWarnings("unchecked")
    public BaseStore(Context context) {
        this.mPalmDatabase = PalmDB.getInstance(context);
        LogUtils.d(mPalmDatabase); // the instance should be singleton
        userId = UserUtil.getInstance(context).getUserIdKept();
        entityClass = (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        if (!entityClass.isAnnotationPresent(Table.class))
            throw new IllegalArgumentException("Entity class should have Table.class annotation");
        tableName = entityClass.getAnnotation(Table.class).name();
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(StoreHelper.getTableCreateSQL(tableName, entityClass));
        afterDBCreated(db);
    }

    protected abstract void afterDBCreated(SQLiteDatabase db);

    public abstract void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion);

    protected abstract void fillModel(T model, Cursor cursor);

    protected abstract void fillContentValues(ContentValues values, T model);

    protected SQLiteDatabase getWritableDatabase() {
        OpenUtils.requireConnection();
        return mPalmDatabase.getWritableDatabase();
    }

    protected void closeDatabase(SQLiteDatabase database) {
        OpenUtils.releaseHelper(mPalmDatabase);
    }

    protected synchronized void closeCursor(Cursor cursor) {
        if (cursor == null || cursor.isClosed()) return;
        try {
            cursor.close();
        } catch (Exception e){
            LogUtils.d("Couldn't close cursor correctly");
        }
    }


    public synchronized T get(long code) {
        return get(code, Status.NORMAL, false);
    }

    public synchronized T get(long code, Status status, boolean exclude) {
        Cursor cursor = null;
        T model = null;
        SQLiteDatabase database = getWritableDatabase();
        try {
            cursor = database.rawQuery(" SELECT * FROM " + tableName
                            + " WHERE " + BaseSchema.USER_ID + " = " + userId
                            + " AND " + BaseSchema.CODE + " = " + code
                            + (status == null ? "" : " AND " + BaseSchema.STATUS + (exclude ? " != " : " = ") + status.id),
                    new String[]{});
            model = get(cursor);
        } finally {
            closeCursor(cursor);
            closeDatabase(database);
        }
        return model;
    }

    public synchronized List<T> get(String whereSQL, String orderSQL) {
        return get(whereSQL, orderSQL, Status.NORMAL, false);
    }

    /**
     * Get list of models of given status. This method has many conditions as below:
     * 1).IF STATUS == NULL, WE RETURN MODELS OF ALL STATUS;
     * 2).IF STATUS != NULL && EXCLUDE == TRUE, WE RETURN THE MODELS OF GIVEN STATUS;
     * 3).IF STATUS != NULL && EXCLUDE == TRUE, WE RETURN THE MODELS EXCLUDE GIVEN STATUS.
     *
     * @param whereSQL where SQL
     * @param orderSQL order SQL
     * @param status status
     * @param exclude whether exclude or include
     * @return the models list
     */
    public synchronized List<T> get(String whereSQL, String orderSQL, Status status, boolean exclude) {
        Cursor cursor = null;
        List<T> models = null;
        SQLiteDatabase database = getWritableDatabase();
        try {
            cursor = database.rawQuery(" SELECT * FROM " + tableName
                            + " WHERE " + BaseSchema.USER_ID + " = " + userId
                            + (TextUtils.isEmpty(whereSQL) ? "" : " AND " + whereSQL)
                            + (status == null ? "" : " AND " + BaseSchema.STATUS + (exclude ? " != " : " = ") + status.id)
                            + (TextUtils.isEmpty(orderSQL) ? "" : " ORDER BY " + orderSQL),
                    new String[]{});
            models = getList(cursor);
        } finally {
            closeCursor(cursor);
            closeDatabase(database);
        }
        return models;
    }

    /**
     * In this method, we did`t specify the given status param. IN THIS METHOD WE ONLY SPECIFY THE
     * USER_ID IS CURRENT LOGIN USER_ID. So, you can add as many where conditions as you want.
     *
     * @param whereSQL where SQL
     * @param whereArgs where args, may be empty, but not NULL.
     * @param orderSQL order SQL
     * @return the models list returned
     */
    public synchronized List<T> get(String whereSQL, String[] whereArgs, String orderSQL) {
        Cursor cursor = null;
        List<T> models = null;
        SQLiteDatabase database = getWritableDatabase();
        try {
            cursor = database.rawQuery(" SELECT * FROM " + tableName +
                    " WHERE " + BaseSchema.USER_ID + " = " + userId
                    + (TextUtils.isEmpty(whereSQL) ? "" : " AND " + whereSQL)
                    + (TextUtils.isEmpty(orderSQL) ? "" : " ORDER BY " + orderSQL), whereArgs);
            models = getList(cursor);
        } finally {
            closeCursor(cursor);
            closeDatabase(database);
        }
        return models;
    }

    public synchronized List<T> getArchived(String whereSQL, String orderSQL) {
        return get(whereSQL, orderSQL, Status.ARCHIVED, false);
    }

    public synchronized List<T> getTrashed(String whereSQL, String orderSQL) {
        return get(whereSQL, orderSQL, Status.TRASHED, false);
    }

    public synchronized int getCount(String whereSQL, Status status, boolean exclude) {
        Cursor cursor = null;
        int count = 0;
        SQLiteDatabase database = getWritableDatabase();
        try {
            cursor = database.rawQuery(" SELECT COUNT(*) AS count FROM " + tableName +
                            " WHERE " + BaseSchema.USER_ID + " = " + userId
                            + (TextUtils.isEmpty(whereSQL) ? "" : " AND " + whereSQL)
                            + (status == null ? "" : " AND " + BaseSchema.STATUS + (exclude ? " != " : " = ") + status.id),
                    new String[]{});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    count = cursor.getInt(cursor.getColumnIndex("count"));
                } while (cursor.moveToNext());
            }
        } finally {
            closeCursor(cursor);
            closeDatabase(database);
        }
        return count;
    }

    public synchronized boolean isNewModel(Long code) {
        Cursor cursor = null;
        int count = 0;
        SQLiteDatabase database = getWritableDatabase();
        try {
            cursor = database.rawQuery(" SELECT COUNT(*) AS count FROM " + tableName +
                            " WHERE " + BaseSchema.USER_ID + " = " + userId
                            + " AND " + BaseSchema.CODE + " = " + code,
                    new String[]{});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    count = cursor.getInt(cursor.getColumnIndex("count"));
                } while (cursor.moveToNext());
            }
        } finally {
            closeCursor(cursor);
            closeDatabase(database);
        }
        return count == 0;
    }

    public synchronized List<T> getPage(int index, int pageCount, String orderSQL, Status status, boolean exclude) {
        Cursor cursor = null;
        List<T> models;
        final SQLiteDatabase database = getWritableDatabase();
        try {
            cursor = database.rawQuery(" SELECT * FROM " + tableName
                            + " WHERE " + BaseSchema.USER_ID + " = ? "
                            + (status == null ? "" : " AND " + BaseSchema.STATUS + (exclude ? " != " : " = ") + status.id)
                            + (TextUtils.isEmpty(orderSQL) ? "" : " ORDER BY " + orderSQL)
                            + " LIMIT ?, ? ",
                    new String[]{String.valueOf(userId), String.valueOf(index), String.valueOf(pageCount)});
            models = getList(cursor);
        } finally {
            closeCursor(cursor);
            closeDatabase(database);
        }
        return models;
    }


    public synchronized void saveModel(T model) {
        if (model == null) return;
        TimelineHelper.addTimeLine(model, Operation.ADD);
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {
            database.insert(tableName, null, getContentValues(model));
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
            closeDatabase(database);
        }
    }

    public synchronized void update(T model) {
        if (model == null) return;
        TimelineHelper.addTimeLine(model, Operation.UPDATE);
        StoreHelper.setLastModifiedInfo(model);
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {
            database.update(tableName, getContentValues(model),
                    BaseSchema.CODE + " = ? " + " AND " + BaseSchema.USER_ID + " = ? ",
                    new String[]{String.valueOf(model.getCode()), String.valueOf(userId)});
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
            closeDatabase(database);
        }
    }

    public synchronized void saveOrUpdate(T model) {
        if (model == null) return;

        if (isNewModel(model.getCode())) {
            saveModel(model);
        } else {
            update(model);
        }
    }

    public synchronized void update(T model, Status toStatus) {
        if (model == null || toStatus == null) return;
        TimelineHelper.addTimeLine(model, StoreHelper.getStatusOperation(toStatus));
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {
            database.execSQL(" UPDATE " + tableName
                            + " SET " + BaseSchema.STATUS + " = " + toStatus.id + " , " + BaseSchema.LAST_MODIFIED_TIME + " = ? "
                            + " WHERE " + BaseSchema.CODE + " = " + model.getCode()
                            + " AND " + BaseSchema.USER_ID + " = " + userId,
                    new String[]{String.valueOf(System.currentTimeMillis())});
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
            closeDatabase(database);
        }
    }

    public synchronized void batchUpdate(List<T> models, Status toStatus) {
        if (models == null || models.isEmpty() || toStatus == null) return;

        StringBuilder sb = new StringBuilder();
        int len = models.size();
        for (int i = 0; i < len; i++) {
            sb.append(String.valueOf(models.get(i).getCode()));
            if (i == len - 1) break;
            sb.append(" , ");
        }

        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {
            database.execSQL(" UPDATE " + tableName
                            + " SET " + BaseSchema.STATUS + " = ?, " + BaseSchema.LAST_MODIFIED_TIME + " = ? "
                            + " WHERE " + BaseSchema.CODE + " IN ( " + sb.toString() + " ) "
                            + " AND " + BaseSchema.USER_ID + " = ? ",
                    new String[]{String.valueOf(toStatus.id), String.valueOf(System.currentTimeMillis()), String.valueOf(userId)});
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
            closeDatabase(database);
        }
    }


    protected synchronized ContentValues getContentValues(T model){
        ContentValues values = StoreHelper.getBaseContentValues(model);
        fillContentValues(values, model);
        return values;
    }

    protected synchronized T get(Cursor cursor) {
        T model = null;
        if (cursor != null && !cursor.isClosed() && cursor.moveToFirst()){
            do {
                model = getModel(cursor);
            } while (cursor.moveToNext());
        } else if (cursor != null && cursor.isClosed()) {
            LogUtils.e("cursor is closed : " + cursor);
        }
        return model;
    }

    protected synchronized List<T> getList(Cursor cursor){
        LogUtils.d("Current Object: " + this + ", " + Thread.currentThread());
        List<T> models = new LinkedList<>();
        if (cursor != null && !cursor.isClosed() && cursor.moveToFirst()){ // exception here
            do {
                models.add(getModel(cursor));
            } while (cursor.moveToNext());
        } else if (cursor != null && cursor.isClosed()) {
            LogUtils.e("cursor is closed : " + cursor);
        }
        return models;
    }

    private T getModel(Cursor cursor) {
        T model = StoreHelper.getBaseModel(cursor, entityClass);
        fillModel(model, cursor);
        return model;
    }
}
