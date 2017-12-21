package org.polaric.colorful;

import android.content.Context;
import android.support.annotation.StyleRes;
import android.util.Log;

public class ThemeDelegate {

    private Colorful.ThemeColor primaryColor;

    private Colorful.AccentColor accentColor;

    private boolean translucent;

    private boolean dark;

    @StyleRes private int styleResPrimary;

    @StyleRes private int styleResAccent;

    @StyleRes private int styleResBase;

    ThemeDelegate(Context context, Colorful.ThemeColor primary, Colorful.AccentColor accent, boolean translucent, boolean dark) {
        this.primaryColor = primary;
        this.accentColor = accent;
        this.translucent = translucent;
        this.dark = dark;

        long curTime = System.currentTimeMillis();

        styleResPrimary = context.getResources().getIdentifier(primaryColor.getPrimaryName(), "style", context.getPackageName());
        styleResAccent = context.getResources().getIdentifier(accentColor.getAccentName(), "style", context.getPackageName());
        styleResBase = dark ? R.style.Colorful_Dark : R.style.Colorful_Light;

        Log.d(Util.LOG_TAG, "ThemeDelegate fetched theme in " + (System.currentTimeMillis() - curTime) + " ms");
    }

    public @StyleRes int getStyleResPrimary() {
        return styleResPrimary;
    }

    public @StyleRes int getStyleResAccent() {
        return styleResAccent;
    }

    public @StyleRes int getStyleResBase() {
        return styleResBase;
    }

    public Colorful.ThemeColor getPrimaryColor() {
        return primaryColor;
    }

    public Colorful.AccentColor getAccentColor() {
        return accentColor;
    }

    public boolean isTranslucent() {
        return translucent;
    }

    public boolean isDark() {
        return dark;
    }
}
