package me.shouheng.commons;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

import me.shouheng.mvvm.MVVMs;

/**
 * @author shouh
 * @version $Id: BaseApplication, v 0.1 2018/6/6 21:58 shouh Exp$
 */
public abstract class BaseApplication extends Application {

    private static BaseApplication instance;

    public static BaseApplication getContext() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MVVMs.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        MVVMs.onCreate(this);
        LeakCanary.install(this);
    }
}
