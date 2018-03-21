package me.shouheng.notepal;

import android.app.Activity;
import android.app.Application;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
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
 * 16. Enable pick category with icons;
 * 17. Solve all the bugs in fabric;
 * 18. Replace the snagging logic with note (the dialog is still avilable, but the snagging list is removed);
 * 21. Share html and associated resources, note content and resources.
 * 22. Remove mind snagging.
 *
 * 不要让用户做太多的选择！
 * 只要一个主线功能就行！
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
//        if (BuildConfig.DEBUG) {
        // todo disable when release
        Stetho.initializeWithDefaults(this);
//        }
    }

    public static boolean isPasswordChecked() {
        return passwordChecked;
    }

    public static void setPasswordChecked() {
        PalmApp.passwordChecked = true;
    }

    public static String getStringCompact(@StringRes int resId) {
        return PalmApp.getContext().getString(resId);
    }

    public static @ColorInt int getColorCompact(@ColorRes int colorRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PalmApp.getContext().getColor(colorRes);
        } else {
            return PalmApp.getContext().getResources().getColor(colorRes);
        }
    }

    public static Drawable getDrawableCompact(@DrawableRes int resId) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            return getContext().getDrawable(resId);
        } else {
            return getContext().getResources().getDrawable(resId);
        }
    }
}
