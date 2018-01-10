package me.shouheng.notepal.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.support.customtabs.CustomTabsIntent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;

/**
 * Created by Wang Shouheng on 2017/12/5. */
public class ViewUtils {

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return resId > 0 ? context.getResources().getDimensionPixelOffset(resId) : result;
    }

    public static int dp2Px(Context context, float dpValues){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValues * scale + 0.5f);
    }

    public static int sp2Px(Context context, float spValues){
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int)(spValues * fontScale + 0.5f);
    }

    public static int getWindowWidth(Context context){
        WindowManager window = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        window.getDefaultDisplay().getRealSize(point);
        return point.x;
    }

    public static int getWindowHeight(Context context){
        WindowManager window = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        window.getDefaultDisplay().getRealSize(point);
        return point.y;
    }

    public static Point getWindowSize(Context context){
        WindowManager window = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        window.getDefaultDisplay().getRealSize(point);
        return point;
    }

    public static int getStatusBarHeight(Resources r) {
        int resourceId = r.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            return r.getDimensionPixelSize(resourceId);
        return 0;
    }

    public static View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
    }

    public static void setAlpha(View v, float alpha) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            final AlphaAnimation animation = new AlphaAnimation(1F, alpha);
            animation.setFillAfter(true);
            v.startAnimation(animation);
        } else {
            v.setAlpha(alpha);
        }
    }

    public static void launchUrl(Context context, String url) {
        int primaryColor = ColorUtils.primaryColor(context);

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder
                .setToolbarColor(primaryColor)
                .setSecondaryToolbarColor(ColorUtils.calStatusBarColor(primaryColor))
                .build();
        customTabsIntent.launchUrl(context, Uri.parse(url));
    }
}
