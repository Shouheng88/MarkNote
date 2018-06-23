package me.shouheng.notepal.util.preferences;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.config.Constants;

public class DashboardPreferences extends BasePreferences {

    private static DashboardPreferences preferences;

    public static DashboardPreferences getInstance() {
        if (preferences == null) {
            synchronized (DashboardPreferences.class) {
                if (preferences == null) {
                    preferences = new DashboardPreferences(PalmApp.getContext());
                }
            }
        }
        return preferences;
    }

    private DashboardPreferences(Context context) {
        super(context);
    }

    public void setUserInfoBG(@Nullable Uri uri) {
        putString(R.string.key_user_info_bg, uri == null ? "" : uri.toString());
    }

    public Uri getUserInfoBG() {
        String bgUri = getString(R.string.key_user_info_bg, null);
        if (!TextUtils.isEmpty(bgUri)) {
            return Uri.parse(bgUri);
        }
        return Uri.parse(Constants.DEFAULT_USER_INFO_BG);
    }

    public void setUserInfoBGEnable(boolean enable) {
        putBoolean(R.string.key_user_info_bg_visible, enable);
    }

    public boolean isUserInfoBgEnable() {
        return getBoolean(R.string.key_user_info_bg_visible, true);
    }

    public void setUserMotto(String motto) {
        putString(R.string.key_user_info_motto, motto);
    }

    public String getUserMotto() {
        return getString(R.string.key_user_info_motto, PalmApp.getStringCompact(R.string.setting_dashboard_user_motto_default));
    }
}
