package me.shouheng.notepal.util;

import android.os.Build;

import me.shouheng.notepal.PalmApp;

/**
 * Created by wang shouheng on 2017/12/23.*/
public class PalmUtils {

    /**
     * API 16
     *
     * @return true->above API 16 */
    public static boolean isJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * API 17
     *
     * @return true->above API 17 */
    public static boolean isJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    /**
     * API 18
     *
     * @return true->above API 18 */
    public static boolean isJellyBeanMR2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    public static boolean isKitKat(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * API 21
     *
     * @return true->above API 21 */
    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * API 23
     *
     * @return true->above API 23 */
    public static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static String getPackageName(){
        return PalmApp.getContext().getApplicationContext().getPackageName();
    }
}
