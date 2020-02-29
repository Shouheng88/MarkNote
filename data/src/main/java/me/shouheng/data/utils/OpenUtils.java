package me.shouheng.data.utils;

import me.shouheng.data.PalmDB;
import me.shouheng.utils.stability.L;

/**
 * Created by wang shouheng on 2018/2/6.*/
public class OpenUtils {

    private static boolean isClosed = false;

    private static int instanceCount = 0;

    public static synchronized void releaseHelper(PalmDB helper) {
        instanceCount--;
        L.d(String.format("releasing helper %s, instance count = %s", helper, instanceCount));
        if (instanceCount <= 0) {
            if (helper != null) {
                L.d(String.format("zero instances, closing helper %s", helper));
                helper.close();
                isClosed = true;
            }
            if (instanceCount < 0) {
                L.d(String.format("too many calls to release helper, instance count = %s", instanceCount));
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
