package org.polaric.colorful;

import android.app.ActivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;

public abstract class ColorfulActivity extends BaseActivity {

    private String themeString;

    protected boolean recreateForThemeChange;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        themeString = Colorful.getThemeString();

        setTheme(Colorful.getThemeDelegate().getStyleResBase());
        getTheme().applyStyle(Colorful.getThemeDelegate().getStyleResPrimary(), true);
        getTheme().applyStyle(Colorful.getThemeDelegate().getStyleResAccent(), true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Colorful.getThemeDelegate().isTranslucent()) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            ActivityManager.TaskDescription tDesc = new ActivityManager.TaskDescription(null, null,
                    getResources().getColor(Colorful.getThemeDelegate().getPrimaryColor().getColorRes()));
            setTaskDescription(tDesc);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Colorful.getThemeString().equals(themeString)) {
            Log.d(Util.LOG_TAG, "Theme change detected, restarting activity");
            recreate();
            recreateForThemeChange = true;
        }
    }
}
