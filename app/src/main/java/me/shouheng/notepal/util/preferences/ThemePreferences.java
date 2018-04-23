package me.shouheng.notepal.util.preferences;

import android.content.Context;

import me.shouheng.notepal.PalmApp;

public class ThemePreferences extends BasePreferences {

    private static ThemePreferences sInstance;

    public static ThemePreferences getInstance() {
        if (sInstance == null) {
            synchronized (ThemePreferences.class) {
                if (sInstance == null){
                    sInstance = new ThemePreferences(PalmApp.getContext());
                }
            }
        }
        return sInstance;
    }

    private ThemePreferences(Context context) {
        super(context);
    }
}
