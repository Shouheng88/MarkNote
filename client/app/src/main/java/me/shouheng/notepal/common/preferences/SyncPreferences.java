package me.shouheng.notepal.common.preferences;

import me.shouheng.notepal.R;
import me.shouheng.notepal.common.enums.SyncTimeInterval;
import me.shouheng.utils.app.ResUtils;
import me.shouheng.utils.store.SPUtils;

public class SyncPreferences {

    private static SyncPreferences sInstance = new SyncPreferences();

    public static SyncPreferences getInstance() {
        return sInstance;
    }

    private SyncPreferences() { }

    public void setBackupOnlyInWifi(boolean isOnlyWifi) {
        SPUtils.getInstance().put(ResUtils.getString(R.string.key_backup_only_wifi), isOnlyWifi);
    }

    public boolean isBackupOnlyInWifi() {
        return SPUtils.getInstance().getBoolean(ResUtils.getString(R.string.key_backup_only_wifi), true);
    }

    public SyncTimeInterval getSyncTimeInterval() {
        return SyncTimeInterval.getTypeById(SPUtils.getInstance().getInt(ResUtils.getString(R.string.key_backup_time_interval), SyncTimeInterval.EVERY_30_MINUTES.id));
    }

    public void setSyncTimeInterval(SyncTimeInterval syncTimeInterval) {
        SPUtils.getInstance().put(ResUtils.getString(R.string.key_backup_time_interval), syncTimeInterval.id);
    }

    public long getOneDriveLastSyncTime() {
        return SPUtils.getInstance().getLong(ResUtils.getString(R.string.key_onedrive_last_sync_time), 0);
    }

    public void setOneDriveLastSyncTime(long lastSyncTime) {
        SPUtils.getInstance().put(ResUtils.getString(R.string.key_onedrive_last_sync_time), lastSyncTime);
    }

    public String getOneDriveBackupItemId() {
        return SPUtils.getInstance().getString(ResUtils.getString(R.string.key_onedrive_backup_dir_item_id), null);
    }

    public void setOneDriveBackupItemId(String itemId) {
        SPUtils.getInstance().put(ResUtils.getString(R.string.key_onedrive_backup_dir_item_id), itemId);
    }

    public String getOneDriveLastBackupItemId() {
        return SPUtils.getInstance().getString(ResUtils.getString(R.string.key_onedrive_last_backup_dir_item_id), null);
    }

    public void setOneDriveLastBackupItemId(String itemId) {
        SPUtils.getInstance().put(ResUtils.getString(R.string.key_onedrive_last_backup_dir_item_id), itemId);
    }

    public void setOneDriveFilesBackupItemId(String itemId) {
        SPUtils.getInstance().put(ResUtils.getString(R.string.key_onedrive_files_backup_dir_item_id), itemId);
    }

    public String getOneDriveFilesBackupItemId() {
        return SPUtils.getInstance().getString(ResUtils.getString(R.string.key_onedrive_files_backup_dir_item_id), null);
    }

    public long getOneDriveDatabaseLastSyncTime() {
        return SPUtils.getInstance().getLong(ResUtils.getString(R.string.key_onedrive_database_last_sync_time), 0);
    }

    public void setOneDriveDatabaseLastSyncTime(long lastSyncTime) {
        SPUtils.getInstance().put(ResUtils.getString(R.string.key_onedrive_database_last_sync_time), lastSyncTime);
    }

    public long getOneDrivePreferenceLastSyncTime() {
        return SPUtils.getInstance().getLong(ResUtils.getString(R.string.key_onedrive_preferences_last_sync_time), 0);
    }

    public void setOneDrivePreferenceLastSyncTime(long lastSyncTime) {
        SPUtils.getInstance().put(ResUtils.getString(R.string.key_onedrive_preferences_last_sync_time), lastSyncTime);
    }

    public String getOneDriveDatabaseItemId() {
        return SPUtils.getInstance().getString(ResUtils.getString(R.string.key_onedrive_database_item_id), null);
    }

    public void setOneDriveDatabaseItemId(String itemId) {
        SPUtils.getInstance().put(ResUtils.getString(R.string.key_onedrive_database_item_id), itemId);
    }

    public String getOneDrivePreferencesItemId() {
        return SPUtils.getInstance().getString(ResUtils.getString(R.string.key_onedrive_preferences_item_id), null);
    }

    public void setOneDrivePreferencesItemId(String itemId) {
        SPUtils.getInstance().put(ResUtils.getString(R.string.key_onedrive_preferences_item_id), itemId);
    }
}
