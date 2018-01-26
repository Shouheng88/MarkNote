package org.polaric.colorful;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

class Util {
    static final String LOG_TAG = "colorful";

    static final String PREFERENCE_KEY = "colorful_pref_key";

    static final String SPLIT = ":";

    public static String getPackageName(Context ctx){
        PackageInfo info;
        try {
            info = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            return info.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
