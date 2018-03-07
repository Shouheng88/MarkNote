package me.shouheng.notepal;

import android.app.Activity;
import android.app.Application;
import android.support.multidex.MultiDex;

import com.facebook.stetho.Stetho;

import org.polaric.colorful.Colorful;

import me.shouheng.notepal.model.Model;


/**
 * TODO All the todo items in later version:
 * 1. Add ringtone to {@link me.shouheng.notepal.async.DataBackupIntentService} when included the notification logic;
 * 2. Enable copy link logic when the server is ready. {@link me.shouheng.notepal.util.ModelHelper#copyLink(Activity, Model)}
 *
 * Created by wangshouheng on 2017/2/26. */
public class PalmApp extends Application{

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

        /**
         * Enable stetho only in debug mode. */
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
    }

    public static boolean isPasswordChecked() {
        return passwordChecked;
    }

    public static void setPasswordChecked(boolean passwordChecked) {
        PalmApp.passwordChecked = passwordChecked;
    }
}
