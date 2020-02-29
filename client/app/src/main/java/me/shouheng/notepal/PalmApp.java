package me.shouheng.notepal;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.stetho.Stetho;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import java.io.File;

import me.shouheng.commons.BaseApplication;
import me.shouheng.notepal.activity.MainActivity;
import me.shouheng.uix.UIX;
import me.shouheng.uix.page.CrashActivity;
import me.shouheng.utils.UtilsApp;
import me.shouheng.utils.app.ResUtils;
import me.shouheng.utils.permission.Permission;
import me.shouheng.utils.permission.PermissionUtils;
import me.shouheng.utils.stability.CrashHelper;
import me.shouheng.utils.stability.L;
import me.shouheng.utils.store.PathUtils;

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
        MobclickAgent.openActivityDurationTrack(false);
//        MVVMs.onCreate(this);
        UIX.INSTANCE.init(this);
        UtilsApp.init(this);
        if (BuildConfig.DEBUG) {
            UMConfigure.setLogEnabled(true);
        }
        L.getConfig().setLogSwitch(BuildConfig.DEBUG);
        // 配置崩溃工具，文件存储在：data/data/package_name/files/crash 下面
        if (PermissionUtils.hasPermissions(Permission.STORAGE)) {
            CrashHelper.init(this,
                    new File(PathUtils.getExternalAppFilesPath(), "crash"),
                    (crashInfo, e) -> new CrashActivity.Companion.Builder(getApplicationContext())
                            .setRestartActivity(MainActivity.class)
                            .setCrashInfo(crashInfo)
                            .setCrashImage(R.drawable.uix_crash_error_image)
                            .setTips(ResUtils.getString(R.string.text_crash_tips))
                            .launch());
        }
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
