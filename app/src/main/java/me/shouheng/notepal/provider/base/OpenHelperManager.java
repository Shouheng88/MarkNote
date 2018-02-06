package me.shouheng.notepal.provider.base;

import android.annotation.SuppressLint;
import android.content.Context;

import me.shouheng.notepal.provider.PalmDB;
import me.shouheng.notepal.util.LogUtils;

/**
 * Created by wang shouheng on 2018/2/6.*/
public class OpenHelperManager {
    @SuppressLint("StaticFieldLeak")
    private static volatile PalmDB helper = null;
    private static boolean wasClosed = false;
    private static int instanceCount = 0;

    public static synchronized void releaseHelper() {
        instanceCount--;
        LogUtils.e(String.format("releasing helper %s, instance count = %s", helper, instanceCount));
        if (instanceCount <= 0) {
            if (helper != null) {
                LogUtils.e(String.format("zero instances, closing helper %s", helper));
                helper.close();
                helper = null;
                wasClosed = true;
            }
            if (instanceCount < 0) {
                LogUtils.e(String.format("too many calls to release helper, instance count = %s", instanceCount));
            }
        }
    }

    public static synchronized PalmDB getHelper(Context context) {
        if (helper == null) {
            helper = PalmDB.getInstance(context);
        }
        return helper;
    }
}
