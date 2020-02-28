package me.shouheng.commons;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.squareup.leakcanary.LeakCanary;

import io.fabric.sdk.android.Fabric;

/**
 * @author shouh
 * @version $Id: BaseApplication, v 0.1 2018/6/6 21:58 shouh Exp$
 */
public abstract class BaseApplication extends Application {

    private static BaseApplication instance;

    public static BaseApplication getContext() {
        return instance;
    }

    private boolean needRelaunch = false;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        LeakCanary.install(this);

        Fabric.with(this, new Crashlytics());
    }
}
