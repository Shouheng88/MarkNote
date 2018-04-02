package me.shouheng.notepal.util;

import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;

import com.onedrive.sdk.core.ClientException;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.SettingsActivity;
import me.shouheng.notepal.async.onedrive.OneDriveBackupService;
import me.shouheng.notepal.manager.onedrive.DefaultCallback;
import me.shouheng.notepal.manager.onedrive.OneDriveManager;

/**
 * Created by wang shouheng on 2017/12/23.*/
public class PalmUtils {

    /**
     * API 16
     *
     * @return true->above API 16 */
    public static boolean isJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * API 17
     *
     * @return true->above API 17 */
    public static boolean isJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    /**
     * API 18
     *
     * @return true->above API 18 */
    public static boolean isJellyBeanMR2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    public static boolean isKitKat(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * API 21
     *
     * @return true->above API 21 */
    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * API 23
     *
     * @return true->above API 23 */
    public static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static String getPackageName(){
        return PalmApp.getContext().getApplicationContext().getPackageName();
    }

    /**
     * Sync to One Drive.
     *
     * @param activity current activity */
    public static void syncOneDrive(Activity activity) {
        syncOneDrive(activity, 0, false);
    }

    /**
     * Sync to One Drive
     *
     * @param activity current activity
     * @param req the request code for opening setting activity
     * @param force true to goto setting, else return */
    public static void syncOneDrive(Activity activity, int req, boolean force) {
        String itemId = PreferencesUtils.getInstance().getOneDriveBackupItemId();
        String filesItemId = PreferencesUtils.getInstance().getOneDriveFilesBackupItemId();
        if ((TextUtils.isEmpty(itemId) || TextUtils.isEmpty(filesItemId)) && force) {
            ToastUtils.makeToast(R.string.login_drive_message);
            SettingsActivity.start(activity, SettingsActivity.ACTION_NAV_TO_BACKUP_FRAGMENT, req);
            return;
        }

        OneDriveManager.getInstance().connectOneDrive(activity, new DefaultCallback<Void>(activity) {
            @Override
            public void success(Void aVoid) {
                OneDriveBackupService.start(activity);
                ToastUtils.makeToast(R.string.text_syncing);
            }

            @Override
            public void failure(ClientException error) {
                super.failure(error);
            }
        });
    }
}
