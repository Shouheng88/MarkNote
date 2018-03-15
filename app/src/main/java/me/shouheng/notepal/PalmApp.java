package me.shouheng.notepal;

import android.app.Activity;
import android.app.Application;
import android.support.multidex.MultiDex;

import com.facebook.stetho.Stetho;

import org.polaric.colorful.Colorful;

import me.shouheng.notepal.model.Model;

/**
 * TODO All the todo items in later version:
 *
 * 1. Add ringtone to {@link me.shouheng.notepal.async.DataBackupIntentService} when included the notification logic;
 * 2. Enable copy link logic when the server is ready. {@link me.shouheng.notepal.util.ModelHelper#copyLink(Activity, Model)};
 * 3. Add Google Drive logic, check if the file has backup time in google drive;
 * 4. Make ripple.xml uniform in every place;
 * 5. Use ViewModel to load data async;
 * 6. Modify import from external logic, since current logic did nothing according to the db version and change,
 *    You may also research the performance when the db version is different.
 * 7. Refine NoteViewFragment performance;
 * 8. Add sortable selections in list fragment.
 * 9. Location logic of foreign country;
 * 10. Weather logic, only add weather data in db;
 * 11. Statistic;
 * 12. Calendar + Timeline;
 * 13. Google map location info;
 * 14. Multiple platform statistics and user trace;
 * 15, Bug in LockActivity when screen orientation changed. (Multiple input and headers);
 * 16. Enable pick category with icons;
 * 17. Solve all the bugs in fabric;
 * 18. Add picture edit logic.
 *
 * Created by wangshouheng on 2017/2/26. */
public class PalmApp extends Application {

    private static PalmApp mInstance;

    private static boolean passwordChecked;

    public static synchronized PalmApp getContext() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        MultiDex.install(this);

        Colorful.init(this);

        /*
         * Enable stetho only in debug mode. */
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
    }

    public static boolean isPasswordChecked() {
        return passwordChecked;
    }

    public static void setPasswordChecked() {
        PalmApp.passwordChecked = true;
    }
}
