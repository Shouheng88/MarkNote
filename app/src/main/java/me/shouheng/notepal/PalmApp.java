package me.shouheng.notepal;

import android.app.Application;

import com.facebook.stetho.Stetho;

import org.polaric.colorful.Colorful;


/**
 * Created by wangshouheng on 2017/2/26. */
public class PalmApp extends Application{

    private static PalmApp mInstance;

    public static synchronized PalmApp getContext() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        Colorful.init(this);

        Stetho.initializeWithDefaults(this);
    }
}
