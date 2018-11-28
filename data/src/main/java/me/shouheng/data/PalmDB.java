package me.shouheng.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import me.shouheng.data.store.AlarmsStore;
import me.shouheng.data.store.AttachmentsStore;
import me.shouheng.data.store.CategoryStore;
import me.shouheng.data.store.LocationsStore;
import me.shouheng.data.store.NotebookStore;
import me.shouheng.data.store.NotesStore;
import me.shouheng.data.store.TimelineStore;
import me.shouheng.data.store.WeatherStore;

import static me.shouheng.data.DBConfig.DATABASE_NAME;
import static me.shouheng.data.DBConfig.VERSION;

/**
 * Resources need to modify when add new model:
 *
 * Created by wangshouheng on 2017/3/13. */
public class PalmDB extends SQLiteOpenHelper {

    private static volatile PalmDB sInstance = null;

    private volatile boolean isOpen = true;

    public static PalmDB getInstance(final Context context) {
        if (sInstance == null) {
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
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LocationsStore.getInstance().onCreate(db);
        AttachmentsStore.getInstance().onCreate(db);
        AlarmsStore.getInstance().onCreate(db);
        NotesStore.getInstance().onCreate(db);
        TimelineStore.getInstance().onCreate(db);
        NotebookStore.getInstance().onCreate(db);
        CategoryStore.getInstance().onCreate(db);
        WeatherStore.getInstance().onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LocationsStore.getInstance().onUpgrade(db, oldVersion, newVersion);
        AttachmentsStore.getInstance().onUpgrade(db, oldVersion, newVersion);
        AlarmsStore.getInstance().onUpgrade(db, oldVersion, newVersion);
        NotesStore.getInstance().onUpgrade(db, oldVersion, newVersion);
        TimelineStore.getInstance().onUpgrade(db, oldVersion, newVersion);
        NotebookStore.getInstance().onUpgrade(db, oldVersion, newVersion);
        CategoryStore.getInstance().onUpgrade(db, oldVersion, newVersion);
        WeatherStore.getInstance().onUpgrade(db, oldVersion, newVersion);
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
