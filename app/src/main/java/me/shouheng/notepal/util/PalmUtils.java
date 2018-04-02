package me.shouheng.notepal.util;

import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;

import com.onedrive.sdk.core.ClientException;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.SettingsActivity;
import me.shouheng.notepal.async.OneDriveBackupService;
import me.shouheng.notepal.manager.one.drive.DefaultCallback;
import me.shouheng.notepal.manager.one.drive.OneDriveManager;

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
     * Sync to one drive.
     *
     * @param activity current activity
     * @param req the request code for opening setting activity */
    public static void syncOneDrive(Activity activity, int req) {
        String itemId = PreferencesUtils.getInstance().getOneDriveBackupItemId();
        String filesItemId = PreferencesUtils.getInstance().getOneDriveFilesBackupItemId();
        if (TextUtils.isEmpty(itemId) || TextUtils.isEmpty(filesItemId)) {
            ToastUtils.makeToast(R.string.login_drive_message);
            SettingsActivity.start(activity, SettingsActivity.ACTION_NAV_TO_BACKUP_FRAGMENT, req);
            return;
        }

        OneDriveManager oneDriveManager = OneDriveManager.getInstance();
        try {
            oneDriveManager.getOneDriveClient();
            OneDriveBackupService.start(activity);
            ToastUtils.makeToast(R.string.text_syncing);
        } catch (final UnsupportedOperationException ignored) {
            oneDriveManager.createOneDriveClient(activity, new DefaultCallback<Void>(activity) {
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
}
