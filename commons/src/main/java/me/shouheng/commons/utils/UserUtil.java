package me.shouheng.commons.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by WngShhng on 2017/3/13.
 */
public final class UserUtil {

    private static UserUtil sInstance;

    private static SharedPreferences mPreferences;

    private static final String USER_ID_KEPT = "User_Id_Kept";

    private UserUtil(final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static UserUtil getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (UserUtil.class) {
                if (sInstance == null) {
                    sInstance = new UserUtil(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    public long getUserIdKept() {
        return mPreferences.getLong(USER_ID_KEPT, 0L);
    }

    public void setUserIdKept(long userId) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(USER_ID_KEPT, 0);
        editor.apply();
    }
}
