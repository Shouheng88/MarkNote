package me.shouheng.commons.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.text.ClipboardManager;

import me.shouheng.commons.BaseApplication;

/**
 * Created by WngShhng on 2017/12/23.
 */
public class PalmUtils {

    /**
     * API 17
     *
     * @return true->above API 17
     */
    public static boolean isJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    /**
     * API 18
     *
     * @return true->above API 18
     */
    public static boolean isJellyBeanMR2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    public static boolean isKitKat(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * API 21
     *
     * @return true->above API 21
     */
    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * API 23
     *
     * @return true->above API 23
     */
    public static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean isAlive(Fragment fragment) {
        return fragment != null
                && fragment.isAdded()
                && fragment.getActivity() != null
                && !fragment.getActivity().isFinishing();
    }

    public static boolean isAlive(android.app.Fragment fragment) {
        return fragment != null
                && fragment.isAdded()
                && fragment.getActivity() != null
                && !fragment.getActivity().isFinishing();
    }

    public static boolean isAlive(Activity activity) {
        return activity != null
                && !activity.isFinishing()
                && !activity.isDestroyed();
    }

    public static int getColorCompact(@ColorRes int colorRes) {
        return BaseApplication.getContext().getResources().getColor(colorRes);
    }

    public static String getStringCompact(@StringRes int stringRes) {
        return BaseApplication.getContext().getResources().getString(stringRes);
    }

    public static Drawable getDrawableCompact(@DrawableRes int drawableRes) {
        return BaseApplication.getContext().getResources().getDrawable(drawableRes);
    }

    public static int getIntegerCompact(@IntegerRes int integerRes) {
        return BaseApplication.getContext().getResources().getInteger(integerRes);
    }

    public static String getPackageName() {
        return BaseApplication.getContext().getApplicationContext().getPackageName();
    }

    public static void copy(Activity ctx, String content) {
        ClipboardManager clipboardManager = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
        assert clipboardManager != null;
        clipboardManager.setText(content);
    }
}
