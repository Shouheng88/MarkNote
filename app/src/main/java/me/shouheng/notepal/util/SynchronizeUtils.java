package me.shouheng.notepal.util;

import android.app.Activity;
import android.text.TextUtils;

import com.onedrive.sdk.core.ClientException;

import java.io.File;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.SettingsActivity;
import me.shouheng.notepal.async.onedrive.OneDriveBackupService;
import me.shouheng.notepal.manager.onedrive.DefaultCallback;
import me.shouheng.notepal.manager.onedrive.OneDriveManager;

/**
 * Created by shouh on 2018/4/5.*/
public class SynchronizeUtils {

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
     * @param force true to force to synchronize */
    public static void syncOneDrive(Activity activity, int req, boolean force) {
        // If forced to synchronize and the information is not set, go to the setting page.
        if (!SynchronizeUtils.checkOneDriveSettings() && force) {
            ToastUtils.makeToast(R.string.login_drive_message);
            SettingsActivity.start(activity, SettingsActivity.ACTION_NAV_TO_BACKUP_FRAGMENT, req);
            return;
        }

        // Only synchronize to OneDrive if forced to or arrived the time interval.
        if (force || SynchronizeUtils.shouldOneDriveSync()) {
            OneDriveManager.getInstance().connectOneDrive(activity, new DefaultCallback<Void>(activity) {
                @Override
                public void success(Void aVoid) {
                    ToastUtils.makeToast(R.string.text_syncing);
                    OneDriveBackupService.start(activity);
                }

                @Override
                public void failure(ClientException error) {
                    ToastUtils.makeToast(error.getMessage());
                }
            });
        }
    }

    /**
     * Check if the OneDrive synchronization information is set.
     *
     * @return true if set, otherwise false */
    private static boolean checkOneDriveSettings() {
        String itemId = PreferencesUtils.getInstance().getOneDriveBackupItemId();
        String filesItemId = PreferencesUtils.getInstance().getOneDriveFilesBackupItemId();
        return !TextUtils.isEmpty(itemId) && !TextUtils.isEmpty(filesItemId);
    }

    /**
     * Should synchronize to OneDrive according to time interval in settings.
     *
     * @return true if should synchronize. */
    private static boolean shouldOneDriveSync() {
        long lastSyncTime = PreferencesUtils.getInstance().getOneDriveLastSyncTime();
        long syncTimeInterval = PreferencesUtils.getInstance().getSyncTimeInterval().millis;
        return lastSyncTime + syncTimeInterval < System.currentTimeMillis();
    }

    /**
     * Should sync database to OneDrive
     *
     * @return true if should sync. */
    public static boolean shouldOneDriveDatabaseSync() {
        long lastSyncTime = PreferencesUtils.getInstance().getOneDriveDatabaseLastSyncTime();
        File database = FileHelper.getDatabaseFile(PalmApp.getContext());
        long lastModifiedTime = database.lastModified();
        return lastModifiedTime > lastSyncTime;
    }

    public static boolean shouldOneDrivePreferencesSync() {
        long lastSyncTime = PreferencesUtils.getInstance().getOneDrivePreferenceLastSyncTime();
        File preferences = FileHelper.getPreferencesFile(PalmApp.getContext());
        long lastModifiedTime = preferences.lastModified();
        return lastModifiedTime > lastSyncTime;
    }
}
