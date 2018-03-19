package me.shouheng.notepal.provider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wangshouheng on 2017/3/13. */
public class PalmDB extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "NotePal.db";
    private static final int VERSION = 3;

    private Context mContext;
    @SuppressLint("StaticFieldLeak")
    private static PalmDB sInstance = null;

    private volatile boolean isOpen = true;

    public static PalmDB getInstance(final Context context){
        if (sInstance == null){
            synchronized (PalmDB.class) {
                if (sInstance == null) {
                    sInstance = new PalmDB(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    private PalmDB(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LocationsStore.getInstance(mContext).onCreate(db);
        AttachmentsStore.getInstance(mContext).onCreate(db);
        AlarmsStore.getInstance().onCreate(db);
        NotesStore.getInstance(mContext).onCreate(db);
        TimelineStore.getInstance(mContext).onCreate(db);
        MindSnaggingStore.getInstance(mContext).onCreate(db);
        NotebookStore.getInstance(mContext).onCreate(db);
        CategoryStore.getInstance(mContext).onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LocationsStore.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);
        AttachmentsStore.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);
        AlarmsStore.getInstance().onUpgrade(db, oldVersion, newVersion);
        NotesStore.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);
        TimelineStore.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);
        MindSnaggingStore.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);
        MindSnaggingStore.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);
        NotebookStore.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);
        CategoryStore.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public synchronized void close() {
        super.close();
        isOpen = false;
        sInstance = null;
    }

    public boolean isOpen() {
        return isOpen;
    }
}
