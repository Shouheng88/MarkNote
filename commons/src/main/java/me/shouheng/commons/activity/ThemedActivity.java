package me.shouheng.commons.activity;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;

import me.shouheng.commons.R;
import me.shouheng.commons.theme.ThemeUtils;
import me.shouheng.commons.utils.PersistData;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.utils.PalmUtils;
import me.shouheng.commons.theme.ThemeStyle;

/**
 * Created by wang shouheng on 2017/12/21.*/
public abstract class ThemedActivity extends UMengActivity {

    private ThemeStyle themeStyle;

    /**
     * Whether use the theme of preference
     *
     * @return whether use the theme
     */
    protected boolean useColorfulTheme() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        themeStyle = ThemeUtils.getInstance().getThemeStyle();
        if (useColorfulTheme()) {
            setTheme(themeStyle.style);
            ThemeUtils.customStatusBar(this);
        }
        updateNavigationBar();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (useColorfulTheme() && ThemeUtils.getInstance().getThemeStyle() != themeStyle) {
            recreate();
        }
    }

    /**
     * Set the status bar color.
     *
     * @param color the status bar color
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void setStatusBarColor(@ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }

    public ThemeStyle getThemeStyle() {
        return themeStyle;
    }

    protected boolean isDarkTheme(){
        return themeStyle.isDarkTheme;
    }

    /**
     * Update the navigation bar color
     */
    public void updateNavigationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (PersistData.getBoolean(R.string.key_setting_nav_bar_result, false)) {
                getWindow().setNavigationBarColor(ColorUtils.primaryColor());
            } else {
                getWindow().setNavigationBarColor(Color.BLACK);
            }
        }
    }

    @ColorInt
    protected int primaryColor(){
        return PalmUtils.getColorCompact(themeStyle.primaryColor);
    }

    @ColorInt
    protected int accentColor(){
        return PalmUtils.getColorCompact(themeStyle.accentColor);
    }
}
