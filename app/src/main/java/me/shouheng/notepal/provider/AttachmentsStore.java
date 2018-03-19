package me.shouheng.notepal.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import java.util.List;

import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.model.enums.ModelType;
import me.shouheng.notepal.model.enums.Status;
import me.shouheng.notepal.provider.schema.AttachmentSchema;

/**
 * Created by wangshouheng on 2017/4/9.*/
public class AttachmentsStore extends BaseStore<Attachment> {

    private static AttachmentsStore sInstance = null;

    public static AttachmentsStore getInstance(Context context){
        if (sInstance == null){
            synchronized (AttachmentsStore.class) {
                if (sInstance == null) {
                    sInstance = new AttachmentsStore(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    private AttachmentsStore(final Context context){
        super(context);
    }

    @Override
    protected void afterDBCreated(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion){}

    @Override
    public void fillModel(Attachment model, Cursor cursor) {
        model.setModelCode(cursor.getLong(cursor.getColumnIndex(AttachmentSchema.MODEL_CODE)));
        model.setModelType(ModelType.getTypeById(cursor.getInt(cursor.getColumnIndex(AttachmentSchema.MODEL_TYPE))));
        String uriStr = cursor.getString(cursor.getColumnIndex(AttachmentSchema.URI));
        model.setUri(TextUtils.isEmpty(uriStr) ? null : Uri.parse(uriStr));
        model.setPath(cursor.getString(cursor.getColumnIndex(AttachmentSchema.PATH)));
        model.setName(cursor.getString(cursor.getColumnIndex(AttachmentSchema.NAME)));
        model.setSize(cursor.getLong(cursor.getColumnIndex(AttachmentSchema.SIZE)));
        model.setLength(cursor.getLong(cursor.getColumnIndex(AttachmentSchema.LENGTH)));
        model.setMineType(cursor.getString(cursor.getColumnIndex(AttachmentSchema.MINE_TYPE)));
    }

    @Override
    protected void fillContentValues(ContentValues values, Attachment model) {
        values.put(AttachmentSchema.MODEL_CODE, model.getModelCode());
        values.put(AttachmentSchema.MODEL_TYPE, model.getModelType().id);
        values.put(AttachmentSchema.URI, model.getUri() != null ? model.getUri().toString() : null);
        values.put(AttachmentSchema.PATH, model.getPath());
        values.put(AttachmentSchema.NAME, model.getName());
        values.put(AttachmentSchema.SIZE, model.getSize());
        values.put(AttachmentSchema.LENGTH, model.getLength());
        values.put(AttachmentSchema.MINE_TYPE, model.getMineType());
    }

    public synchronized Attachment getAttachment(ModelType modelType, long modelCode) {
        List<Attachment> list = getAttachments(modelType, modelCode, AttachmentSchema.ADDED_TIME + " DESC ");
        return list.size() > 0 ? list.get(0) : null;
    }

    public synchronized List<Attachment> getAttachments(ModelType modelType, long modelCode, String orderSQL) {
        Cursor cursor = null;
        List<Attachment> models = null;
        SQLiteDatabase database = getWritableDatabase();
        try {
            cursor = database.rawQuery(" SELECT * FROM " + tableName +
                    " WHERE " + AttachmentSchema.USER_ID + " = " + userId
                    + " AND " + AttachmentSchema.MODEL_TYPE + " = " + modelType.id
                    + " AND " + AttachmentSchema.MODEL_CODE + " = " + modelCode
                    + " AND " + AttachmentSchema.STATUS + " = " + Status.NORMAL.id
                    + (TextUtils.isEmpty(orderSQL) ? "" : " ORDER BY " + orderSQL), new String[]{});
            models = getList(cursor);
        } finally {
            closeCursor(cursor);
            closeDatabase(database);
        }
        return models;
    }
}
