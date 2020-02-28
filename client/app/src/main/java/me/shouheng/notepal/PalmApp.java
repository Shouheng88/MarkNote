package me.shouheng.notepal;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.stetho.Stetho;
import com.umeng.commonsdk.UMConfigure;

import me.shouheng.commons.BaseApplication;

/**
 * 重点：
 * 1.自动刷新到新的笔记历史栈里面，防止数据丢失；
 * 2.笔记编辑界面底部的按钮可以自定义，现在的按钮位置需要调整；
 * 3.打开笔记的时候先从OneDrive上面检查备份信息；
 * 4.备份的文件的名称需要改；
 *
 * Created by WngShhng with passion and love on 2017/2/26..
 * Contact me : shouheng2015@gmail.com.
 */
public class PalmApp extends BaseApplication {

    private static PalmApp mInstance;

    private static boolean passwordChecked;

    public static synchronized PalmApp getContext() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        /* Enable stetho only in debug mode. */
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
            UMConfigure.setLogEnabled(true);
        }

        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);
    }

    public static boolean passwordNotChecked() {
        return !passwordChecked;
    }

    public static void setPasswordChecked() {
        PalmApp.passwordChecked = true;
    }
}
