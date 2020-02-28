package me.shouheng.notepal.common.preferences;

import me.shouheng.commons.utils.PersistData;
import me.shouheng.notepal.R;
import me.shouheng.notepal.common.enums.SyncTimeInterval;

public class SyncPreferences {

    private static SyncPreferences sInstance = new SyncPreferences();

    public static SyncPreferences getInstance() {
        return sInstance;
    }

    private SyncPreferences() { }

    public void setBackupOnlyInWifi(boolean isOnlyWifi) {
        PersistData.putBoolean(R.string.key_backup_only_wifi, isOnlyWifi);
    }

    public boolean isBackupOnlyInWifi() {
        return PersistData.getBoolean(R.string.key_backup_only_wifi, true);
    }

    public SyncTimeInterval getSyncTimeInterval() {
        return SyncTimeInterval.getTypeById(PersistData.getInt(R.string.key_backup_time_interval, SyncTimeInterval.EVERY_30_MINUTES.id));
    }

    public void setSyncTimeInterval(SyncTimeInterval syncTimeInterval) {
        PersistData.putInt(R.string.key_backup_time_interval, syncTimeInterval.id);
    }

    public long getOneDriveLastSyncTime() {
        return PersistData.getLong(R.string.key_onedrive_last_sync_time, 0);
    }

    public void setOneDriveLastSyncTime(long lastSyncTime) {
        PersistData.putLong(R.string.key_onedrive_last_sync_time, lastSyncTime);
    }

    public String getOneDriveBackupItemId() {
        return PersistData.getString(R.string.key_onedrive_backup_dir_item_id, null);
    }

    public void setOneDriveBackupItemId(String itemId) {
        PersistData.putString(R.string.key_onedrive_backup_dir_item_id, itemId);
    }

    public String getOneDriveLastBackupItemId() {
        return PersistData.getString(R.string.key_onedrive_last_backup_dir_item_id, null);
    }

    public void setOneDriveLastBackupItemId(String itemId) {
        PersistData.putString(R.string.key_onedrive_last_backup_dir_item_id, itemId);
    }

    public void setOneDriveFilesBackupItemId(String itemId) {
        PersistData.putString(R.string.key_onedrive_files_backup_dir_item_id, itemId);
    }

    public String getOneDriveFilesBackupItemId() {
        return PersistData.getString(R.string.key_onedrive_files_backup_dir_item_id, null);
    }

    public long getOneDriveDatabaseLastSyncTime() {
        return PersistData.getLong(R.string.key_onedrive_database_last_sync_time, 0);
    }

    public void setOneDriveDatabaseLastSyncTime(long lastSyncTime) {
        PersistData.putLong(R.string.key_onedrive_database_last_sync_time, lastSyncTime);
    }

    public long getOneDrivePreferenceLastSyncTime() {
        return PersistData.getLong(R.string.key_onedrive_preferences_last_sync_time, 0);
    }

    public void setOneDrivePreferenceLastSyncTime(long lastSyncTime) {
        PersistData.putLong(R.string.key_onedrive_preferences_last_sync_time, lastSyncTime);
    }

    public String getOneDriveDatabaseItemId() {
        return PersistData.getString(R.string.key_onedrive_database_item_id, null);
    }

    public void setOneDriveDatabaseItemId(String itemId) {
        PersistData.putString(R.string.key_onedrive_database_item_id, itemId);
    }

    public String getOneDrivePreferencesItemId() {
        return PersistData.getString(R.string.key_onedrive_preferences_item_id, null);
    }

    public void setOneDrivePreferencesItemId(String itemId) {
        PersistData.putString(R.string.key_onedrive_preferences_item_id, itemId);
    }
}
