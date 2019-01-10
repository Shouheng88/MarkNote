package me.shouheng.commons;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.squareup.leakcanary.LeakCanary;
import com.taobao.sophix.PatchStatus;
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

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        LeakCanary.install(this);

        Fabric.with(this, new Crashlytics());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);

        // initialize 必须放在 attachBaseContext 最前面，初始化代码直接写在 Application 类里面，切勿封装到其他类。
        SophixManager.getInstance()
                .setContext(this)
                .setAppVersion(BuildConfig.VERSION_NAME)
                .setAesKey(null)
                // 如果这里设置了参数，那么就不会启用 manifest 中的值
                .setSecretMetaData(BaseConstants.HOTFIX_APP_ID_PART1 + BaseConstants.HOTFIX_APP_ID_PART2,
                        BaseConstants.HOTFIX_APP_SECRET_PART1 + BaseConstants.HOTFIX_APP_SECRET_PART2 + BaseConstants.HOTFIX_APP_SECRET_PART3,
                        BaseConstants.HOTFIX_RSA_PART1+ BaseConstants.HOTFIX_RSA_PART2 + BaseConstants.HOTFIX_RSA_PART3)
                .setEnableDebug(BuildConfig.DEBUG)
                .setPatchLoadStatusStub((mode, code, info, handlePatchVersion) -> {
                    // 补丁加载回调通知
                    if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                        // 表明补丁加载成功
                    } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                        // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                        // 建议: 用户可以监听进入后台事件, 然后调用killProcessSafely自杀，以此加快应用补丁，详见1.3.2.3
                    } else {
                        // 其它错误信息, 查看PatchStatus类说明
                    }
                }).initialize();

        // queryAndLoadNewPatch 不可放在 attachBaseContext 中，否则无网络权限，建议放在后面任意时刻，如 onCreate 中
        SophixManager.getInstance().queryAndLoadNewPatch();
    }
}
