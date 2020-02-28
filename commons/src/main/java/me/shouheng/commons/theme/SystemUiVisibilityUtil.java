package me.shouheng.commons.theme;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;

import me.shouheng.commons.utils.PalmUtils;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class SystemUiVisibilityUtil {

    private static final int FLAG_IMMERSIVE = View.SYSTEM_UI_FLAG_IMMERSIVE // 与SYSTEM_UI_FLAG_HIDE_NAVIGATION组合使用，没有设置的话在隐藏导航栏后将没有交互
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // 隐藏虚拟按键(导航栏)
            | View.SYSTEM_UI_FLAG_FULLSCREEN; // Activity全屏显示，且状态栏被隐藏覆盖掉

    @TargetApi(17)
    private final static int FLAG_VISIBILITY_17 = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
            | View.SYSTEM_UI_FLAG_FULLSCREEN;

    @TargetApi(19)
    private final static int FLAG_VISIBILITY_19 = FLAG_VISIBILITY_17 // hide status bar
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_IMMERSIVE;

    public static int getSystemVisibility() {
        if (PalmUtils.isLollipop()) {
            return FLAG_VISIBILITY_19;
        } else {
            return FLAG_VISIBILITY_17;
        }
    }

    public static void exit(Activity activity) {
        if (Build.VERSION.SDK_INT >= 19) {
            addFlags(activity.getWindow().getDecorView(), FLAG_IMMERSIVE);
        }
    }

    private static void addFlags(View decorView, int flags) {
        decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | flags);
    }

    public static void enter(Activity activity) {
        if (Build.VERSION.SDK_INT >= 19) {
            clearFlags(activity.getWindow().getDecorView(), FLAG_IMMERSIVE);
        }
    }

    private static void clearFlags(View view, int flags) {
        view.setSystemUiVisibility(view.getSystemUiVisibility() & ~flags);
        // & ~flags 清除view.getSystemUiVisibility()中的flags
    }
}

