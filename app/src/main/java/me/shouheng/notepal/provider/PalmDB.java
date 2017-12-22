package me.shouheng.notepal.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wangshouheng on 2017/3/13. */
class PalmDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PalmCollege.db";

    private static final int VERSION = 1;

    private static PalmDB sInstance = null;

    private Context mContext;

    public static PalmDB getInstance(final Context context){
        if (sInstance == null){
            synchronized (SQLiteOpenHelper.class) {
                if (sInstance == null) {
                    sInstance = new PalmDB(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    private PalmDB(Context context){
        super(context, DATABASE_NAME, null, VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LocationsStore.getInstance(mContext).onCreate(db);
        AttachmentsStore.getInstance(mContext).onCreate(db);
        AlarmsStore.getInstance(mContext).onCreate(db);
        NotesStore.getInstance(mContext).onCreate(db);
        TimelineStore.getInstance(mContext).onCreate(db);
        MindSnaggingStore.getInstance(mContext).onCreate(db);
        NotebookStore.getInstance(mContext).onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LocationsStore.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);
        AttachmentsStore.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);
        AlarmsStore.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);
        NotesStore.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);
        TimelineStore.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);
        MindSnaggingStore.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);
        MindSnaggingStore.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);
        NotebookStore.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);
    }
}
