package me.shouheng.commons;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.squareup.leakcanary.LeakCanary;
import com.taobao.sophix.SophixManager;

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

        registerLifecycleCallback();
    }

    /**
     * Must be called in {@link #attachBaseContext(Context)}
     *
     * https://help.aliyun.com/document_detail/69874.html?spm=a2c4g.11186623.6.553.71903991bQu9A6
     */
    private void configHotfix() {

    }

    /**
     * Register lifecycle callback to listener app in background.
     * So if the app loaded a patch and need to relaunch will be handle in the lifecycle callbacks.
     * But the lifecycle callback are only available on devices above API 14 (4.0), so if your min
     * support version are below 14, you need to use other ways to listener.
     */
    private void registerLifecycleCallback() {
        /* Will be called when any activity lifecycle changed. */
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            private int activeCount = 0;

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                // do nothing
            }

            @Override
            public void onActivityStarted(Activity activity) {
                // do nothing
                activeCount++;
            }

            @Override
            public void onActivityResumed(Activity activity) {
                // do nothing
            }

            @Override
            public void onActivityPaused(Activity activity) {
                // do nothing
            }

            @Override
            public void onActivityStopped(Activity activity) {
                // do nothing
                activeCount--;
                if (activeCount == 0 && needRelaunch) {
                    SophixManager.getInstance().killProcessSafely();
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                // do nothing
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                // do nothing
            }
        });
    }

    protected abstract String getHotfixAppId();

    protected abstract String getHotfixAppSecret();

    protected abstract String getHotfixRSA();
}
