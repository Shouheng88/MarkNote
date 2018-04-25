package me.shouheng.notepal.util.preferences;

import android.content.Context;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.util.enums.SyncTimeInterval;

public class SyncPreferences extends BasePreferences {

    private static SyncPreferences sInstance;

    public static SyncPreferences getInstance() {
        if (sInstance == null) {
            synchronized (SyncPreferences.class) {
                if (sInstance == null){
                    sInstance = new SyncPreferences(PalmApp.getContext());
                }
            }
        }
        return sInstance;
    }

    private SyncPreferences(Context context) {
        super(context);
    }

    public void setBackupOnlyInWifi(boolean isOnlyWifi) {
        putBoolean(R.string.key_backup_only_wifi, isOnlyWifi);
    }

    public boolean isBackupOnlyInWifi() {
        return getBoolean(R.string.key_backup_only_wifi, true);
    }

    public SyncTimeInterval getSyncTimeInterval() {
        return SyncTimeInterval.getTypeById(getInt(R.string.key_sync_time_interval, SyncTimeInterval.EVERY_30_MINUTES.id));
    }

    public void setSyncTimeInterval(SyncTimeInterval syncTimeInterval) {
        putInt(R.string.key_sync_time_interval, syncTimeInterval.id);
    }

    public long getOneDriveLastSyncTime() {
        return getLong(R.string.key_one_drive_last_sync_time, 0);
    }

    public void setOneDriveLastSyncTime(long lastSyncTime) {
        putLong(R.string.key_one_drive_last_sync_time, lastSyncTime);
    }

    public String getOneDriveBackupItemId() {
        return getString(R.string.key_one_drive_backup_dir_item_id, null);
    }

    public void setOneDriveBackupItemId(String itemId) {
        putString(R.string.key_one_drive_backup_dir_item_id, itemId);
    }

    public String getOneDriveLastBackupItemId() {
        return getString(R.string.key_one_drive_last_backup_dir_item_id, null);
    }

    public void setOneDriveLastBackupItemId(String itemId) {
        putString(R.string.key_one_drive_last_backup_dir_item_id, itemId);
    }

    public void setOneDriveFilesBackupItemId(String itemId) {
        putString(R.string.key_one_drive_files_backup_dir_item_id, itemId);
    }

    public String getOneDriveFilesBackupItemId() {
        return getString(R.string.key_one_drive_files_backup_dir_item_id, null);
    }

    public long getOneDriveDatabaseLastSyncTime() {
        return getLong(R.string.key_one_drive_database_last_sync_time, 0);
    }

    public void setOneDriveDatabaseLastSyncTime(long lastSyncTime) {
        putLong(R.string.key_one_drive_database_last_sync_time, lastSyncTime);
    }

    public long getOneDrivePreferenceLastSyncTime() {
        return getLong(R.string.key_one_drive_preferences_last_sync_time, 0);
    }

    public void setOneDrivePreferenceLastSyncTime(long lastSyncTime) {
        putLong(R.string.key_one_drive_preferences_last_sync_time, lastSyncTime);
    }

    public String getOneDriveDatabaseItemId() {
        return getString(R.string.key_one_drive_database_item_id, null);
    }

    public void setOneDriveDatabaseItemId(String itemId) {
        putString(R.string.key_one_drive_database_item_id, itemId);
    }

    public String getOneDrivePreferencesItemId() {
        return getString(R.string.key_one_drive_preferences_item_id, null);
    }

    public void setOneDrivePreferencesItemId(String itemId) {
        putString(R.string.key_one_drive_preferences_item_id, itemId);
    }
}
