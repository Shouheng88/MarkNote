package me.shouheng.notepal.async.onedrive;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.onedrive.sdk.extensions.Item;

import java.io.File;

import me.shouheng.notepal.manager.onedrive.OneDriveManager;
import me.shouheng.notepal.provider.PalmDB;
import me.shouheng.notepal.util.FileHelper;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.NetworkUtils;
import me.shouheng.notepal.util.SynchronizeUtils;
import me.shouheng.notepal.util.preferences.SyncPreferences;

/**
 * Created by shouh on 2018/3/30.*/
public class OneDriveBackupService extends IntentService {

    private SyncPreferences syncPreferences;

    public static void start(Context context) {
        Intent service = new Intent(context, OneDriveBackupService.class);
        context.startService(service);
    }

    public OneDriveBackupService() {
        super("OneDriveBackupService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        syncPreferences = SyncPreferences.getInstance();

        boolean isNetworkAvailable = NetworkUtils.isNetworkAvailable(getApplicationContext());
        boolean isWifi = NetworkUtils.isWifi(getApplicationContext());
        boolean isOnlyWifi = syncPreferences.isBackupOnlyInWifi();

        if (isNetworkAvailable && (!isOnlyWifi || isWifi)) {
            uploadDatabaseAndPreferences();
            updateAttachments();
        }
    }

    private void uploadDatabaseAndPreferences() {
        String itemId = syncPreferences.getOneDriveBackupItemId();
        if (!TextUtils.isEmpty(itemId)) {
            if (SynchronizeUtils.shouldOneDriveDatabaseSync()) {
                uploadDatabase(itemId);
            }
            if (SynchronizeUtils.shouldOneDrivePreferencesSync()) {
                uploadPreferences(itemId);
            }
        }
    }

    private void updateAttachments() {
        String filesItemId = syncPreferences.getOneDriveFilesBackupItemId();
        if (!TextUtils.isEmpty(filesItemId)) {
            BatchUploadPool batchUploadPool = BatchUploadPool.getInstance(filesItemId);
            if (batchUploadPool.isTerminated()) {
                batchUploadPool.begin();
            }
        } else {
            LogUtils.e("Error! No files backup item id.");
        }
    }

    private void uploadDatabase(String itemId) {
        File database = getDatabasePath(PalmDB.DATABASE_NAME);
        new FileUploadTask(itemId, ConflictBehavior.REPLACE, new OneDriveManager.UploadProgressCallback<Item>() {
            @Override
            public void success(Item item) {
                syncPreferences.setOneDriveDatabaseItemId(item.id);
                syncPreferences.setOneDriveDatabaseLastSyncTime(System.currentTimeMillis());
                syncPreferences.setOneDriveLastSyncTime(System.currentTimeMillis());
            }

            @Override
            public void failure(Exception e) {
                LogUtils.e(e);
            }
        }).execute(database);
    }

    private void uploadPreferences(String itemId) {
        File preferences = FileHelper.getPreferencesFile(this);
        new FileUploadTask(itemId, ConflictBehavior.REPLACE, new OneDriveManager.UploadProgressCallback<Item>() {

            @Override
            public void success(Item item) {
                syncPreferences.setOneDrivePreferencesItemId(item.id);
                syncPreferences.setOneDrivePreferenceLastSyncTime(System.currentTimeMillis());
                syncPreferences.setOneDriveLastSyncTime(System.currentTimeMillis());
            }

            @Override
            public void failure(Exception e) {
                LogUtils.e(e);
            }
        }).execute(preferences);
    }
}
