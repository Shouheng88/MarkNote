package me.shouheng.notepal.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import me.shouheng.notepal.model.MindSnagging;
import me.shouheng.notepal.provider.schema.MindSnaggingSchema;

/**
 * Created by wangshouheng on 2017/8/18. */
public class MindSnaggingStore extends BaseStore<MindSnagging> {

    private static MindSnaggingStore sInstance = null;

    public static MindSnaggingStore getInstance(Context context){
        if (sInstance == null){
            synchronized (MindSnaggingStore.class) {
                if (sInstance == null) {
                    sInstance = new MindSnaggingStore(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    private MindSnaggingStore(Context context) {
        super(context);
    }

    @Override
    protected void afterDBCreated(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    @Override
    public void fillModel(MindSnagging model, Cursor cursor) {
        model.setContent(cursor.getString(cursor.getColumnIndex(MindSnaggingSchema.CONTENT)));
        String uri = cursor.getString(cursor.getColumnIndex(MindSnaggingSchema.PICTURE));
        if (uri != null) model.setPicture(Uri.parse(uri));
    }

    @Override
    public void fillContentValues(ContentValues values, MindSnagging model) {
        values.put(MindSnaggingSchema.CONTENT, model.getContent());
        values.put(MindSnaggingSchema.PICTURE, model.getPicture() == null ? null : model.getPicture().toString());
    }
}
