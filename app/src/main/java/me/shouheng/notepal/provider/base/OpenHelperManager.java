package me.shouheng.notepal.provider.base;

import android.annotation.SuppressLint;

import me.shouheng.notepal.provider.PalmDB;
import me.shouheng.notepal.util.LogUtils;

/**
 * Created by wang shouheng on 2018/2/6.*/
public class OpenHelperManager {
    @SuppressLint("StaticFieldLeak")
    private static boolean isClosed = false;
    private static int instanceCount = 0;

    public static synchronized void releaseHelper(PalmDB helper) {
        instanceCount--;
        LogUtils.d(String.format("releasing helper %s, instance count = %s", helper, instanceCount));
        if (instanceCount <= 0) {
            if (helper != null) {
                LogUtils.d(String.format("zero instances, closing helper %s", helper));
                helper.close();
                isClosed = true;
            }
            if (instanceCount < 0) {
                LogUtils.d(String.format("too many calls to release helper, instance count = %s", instanceCount));
            }
        }
    }

    public static synchronized void requireConnection() {
        isClosed = false;
        instanceCount++;
    }

    public static boolean isClosed() {
        return isClosed;
    }
}
