package me.shouheng.notepal;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.stetho.Stetho;
import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixManager;
import com.umeng.commonsdk.UMConfigure;

import me.shouheng.commons.BaseApplication;
import me.shouheng.commons.utils.LogUtils;

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

        // queryAndLoadNewPatch 不可放在 attachBaseContext 中，否则无网络权限，建议放在后面任意时刻，如 onCreate 中
        SophixManager.getInstance().queryAndLoadNewPatch();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);

        /* -------- debug info -------- */
        SophixManager.getInstance()
                .setContext(this)
                .setAppVersion("1.0.0")
                .setAesKey(null)
                // 如果这里设置了参数，那么就不会启用 manifest 中的值
                .setSecretMetaData(getHotfixAppId(), getHotfixAppSecret(), getHotfixRSA())
                .setEnableDebug(false)
                .setPatchLoadStatusStub((mode, code, info, handlePatchVersion) -> {
                    // 补丁加载回调通知
                    if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                        // 表明补丁加载成功
                        LogUtils.d("======= PATCH LOAD SUCCESS!");
                    } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                        /*
                         * 表明新补丁生效需要重启. 开发者可提示用户或者强制重启:
                         * 建议: 用户可以监听进入后台事件, 然后调用killProcessSafely自杀，以此加快应用补丁，详见1.3.2.3
                         */
//                        needRelaunch = true;
                    } else {
                        // 其它错误信息, 查看 PatchStatus 类说明
                        LogUtils.d(code);
                    }
                }).initialize();
    }

    @Override
    protected String getHotfixAppId() {
        return "25536505";
    }

    @Override
    protected String getHotfixAppSecret() {
        return "2ec69ccfe4ed3e7b519ef88733697099";
    }

    @Override
    protected String getHotfixRSA() {
        return "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCFcf/KAxh6MXuveVrtrR2xiVBjmFvYcJ" +
                "gBuW9FRR6gs2dPANuB0gQIxdt5K0m75I4PtZR8G8g7oLpk9HC2QD21NEXzMWzSLkGCKaX4De5fWm4VklE" +
                "Rab98Mb9pAKNPzLjhflAYG6Cn0uhmA9kZnf+2WiIYNb48nLsaW9bIWf+6jxePn/VD59G0IqtJYa1T3L36" +
                "Ld2TeXkfz9BnE+AwcpLppYTxSPHB6m6Ep3Jo+ISy2W7+Jw8Czh5OAra1qrTJLQ3CvXd5F5+QqvvRUEN/5" +
                "HPmtG74d6s+T2tNPezsjn7KRBi5PntArawSL95CuLF5+9SQpcqL+pC6ZwlJT1e0QHo3AgMBAAECggEAJg" +
                "DZP8ax6dq9xrNg2d87XZwlcLerMHCgWZ1duvR8THfLLAyqdsZAr97pKhDR5tioPIER0GZ5F8ImUynqD30" +
                "sFbbVVPT2cMULku4ZfhM5l55BS5Nn0vWyusPQJy8vU0KSRGWcmcEEd8bwuFzQLyN3946jtxFXJBvejRsq" +
                "h9RZp98+wUBqiqVLgtf3fB8mCUdXOTkQ8xe9Jdo6JNldcf0rTvC5dzJ5+Ggli059rtnURnNhHUZRDT4MK" +
                "t7zXJPlAzQ6Ap7BXU99CHyDdO8M+aC8S+rRZ2AhiGwjUNplBh7dq4kfbdHC1+SyQThDIcWQR/E4smRpIG" +
                "oIjw0aYZ83aC0DwQKBgQDJsQ98gFV8UoF+c73o9Gzu/CQWdRPcpl+v3NBcEOQqJP9OH777XZiMiuM9QGK" +
                "Wb94IplRz4/NJbz5UvWs0gwnSwZAkSFHIJZw+ZADQgtiFX1l40mvvsFn3eKlvg6YTEX1wQAaeY7yUzlV1" +
                "14Gv4aCfDr34mLTAycO9WvswMBKz0wKBgQCpYJsQQk/cyUbV31CP6oAmxMFThBtuM4xv/0wnPuc3xbD2T" +
                "GHlH+3WTAIxBndiBd3/hGF/T14osPeKhXsQCFe4OOjMfv+jE80RPac0+1KEPw4v8jtz5K1rF+A7kJOoQo" +
                "DwXHPgPfMJW6FJ4s+cyZq74IhOiY81UMsb4wEvNKx1jQKBgQDEQbl7AXmtdq8w97j05FrXlZwcCiKgk3g" +
                "KrUhGPd13MPcI8xUojWOyZjdGU89a6VHZgtgsyMPkUg4J8SNPPq8hWF5FH+YMZqSJhU/RlXDRHv319nM3" +
                "EZgJmWzt6OGCLoOr5XFLUGuhNMGt4Fz+YwOjonmyXA2OhwYjzFMeCbk5JwKBgF42CuonSE6xcgIiPahfM" +
                "jreM/5lO/C3IYVmRpCJz4hZIM5OvCD6+oq5KnrkcuEGDG5EzwpGNkSB8p6NEl9flJM2rF6awUyPBgBx/6" +
                "BbrpE6lYSbwu/6oW8xD5tyFn9/xkJr2lQ/gQCShAsZ6or2JGgeQxMUNqDcOK1hPdZpAQg9AoGBAKP4n5y" +
                "7UbLztk+2tOq8hM+UAj3HJy78vvrz5d+rgKbgzaAD7BdpMcyGp4ijyZKDQK0p04XSWNjM7D2CYy1z5BKI" +
                "6CIpSWQJY0A42+C+KRevliee0mPW3ZuBQICK9SF5ahPqScltVTQsnXILwZdc9lA975WprmxbBH6z7I6oG" +
                "f+x";
    }

    public static boolean passwordNotChecked() {
        return !passwordChecked;
    }

    public static void setPasswordChecked() {
        PalmApp.passwordChecked = true;
    }
}
