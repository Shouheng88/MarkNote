package org.polaric.colorful;

import android.app.ActivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

public abstract class ColorfulActivity extends AppCompatActivity {

    /* 主题字符串，也是用于存储到SharedPreferences中的字符串 */
    private String themeString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* 从Colorful中获取配置字符串，不过这应该要求先调用Colorful的init方法 */
        themeString = Colorful.getThemeString();

        /* 将主题信息应用到程序中，不过这似乎并没有调用Colorful中的applyTheme方法 */
        setTheme(Colorful.getThemeDelegate().getStyleResBase());
        getTheme().applyStyle(Colorful.getThemeDelegate().getStyleResPrimary(), true);
        getTheme().applyStyle(Colorful.getThemeDelegate().getStyleResAccent(), true);

        /* 根据API版本决定是否应该使用沉浸式 */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Colorful.getThemeDelegate().isTranslucent()) getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ActivityManager.TaskDescription tDesc = new ActivityManager.TaskDescription(null, null, getResources().getColor(Colorful.getThemeDelegate().getPrimaryColor().getColorRes()));
            setTaskDescription(tDesc);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /* 当主题配置发生变化的时候，重建Activity以将新的配置信息应用到程序中 */
        if (!Colorful.getThemeString().equals(themeString)) {
            Log.d(Util.LOG_TAG, "Theme change detected, restarting activity");
            recreate();
        }
    }
}
