package me.shouheng.notepal.async.onedrive;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.onedrive.sdk.core.ClientException;
import com.onedrive.sdk.extensions.Item;

import java.io.File;

import me.shouheng.notepal.manager.onedrive.OneDriveManager;
import me.shouheng.notepal.provider.PalmDB;
import me.shouheng.notepal.util.FileHelper;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.NetworkUtils;
import me.shouheng.notepal.util.PreferencesUtils;
import me.shouheng.notepal.util.SynchronizeUtils;

/**
 * Created by shouh on 2018/3/30.*/
public class OneDriveBackupService extends IntentService {

    private PreferencesUtils preferencesUtils;

    public static void start(Context context) {
        Intent service = new Intent(context, OneDriveBackupService.class);
        context.startService(service);
    }

    public OneDriveBackupService() {
        super("OneDriveBackupService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        preferencesUtils = PreferencesUtils.getInstance(getApplicationContext());

        boolean isNetworkAvailable = NetworkUtils.isNetworkAvailable(getApplicationContext());
        boolean isWifi = NetworkUtils.isWifi(getApplicationContext());
        boolean isOnlyWifi = PreferencesUtils.getInstance(getApplicationContext()).isBackupOnlyInWifi();

        if (isNetworkAvailable && (!isOnlyWifi || isWifi)) {
            uploadDatabaseAndPreferences();
//            updateAttachments();
        }
    }

    private void uploadDatabaseAndPreferences() {
        String itemId = preferencesUtils.getOneDriveBackupItemId();
        if (!TextUtils.isEmpty(itemId) && (
                SynchronizeUtils.shouldOneDriveDatabaseSync()
                        || SynchronizeUtils.shouldOneDrivePreferencesSync())) {
            new DelDBAndPrefTask(new DelDBAndPrefTask.OnFilesDeletedListener() {
                @Override
                public void onDeleted() {
                    uploadDatabase(itemId);
                    uploadPreferences(itemId);
                }

                @Override
                public void onFailed(ClientException ex) {
                    LogUtils.e(ex);
                }
            }).execute(itemId);
        }
    }

    private void updateAttachments() {
        String filesItemId = preferencesUtils.getOneDriveFilesBackupItemId();
        if (!TextUtils.isEmpty(filesItemId)) {
            new BatchUploadUtil(filesItemId, 5).begin();
        } else {
            LogUtils.e("Error! No files backup item id.");
        }
    }

    private void uploadDatabase(String itemId) {
        File database = getDatabasePath(PalmDB.DATABASE_NAME);
        new FileUploadTask(itemId, new OneDriveManager.UploadProgressCallback<Item>() {
            @Override
            public void success(Item item) {
                preferencesUtils.setOneDriveDatabaseItemId(item.id);
                preferencesUtils.setOneDriveDatabaseLastSyncTime(System.currentTimeMillis());
            }

            @Override
            public void failure(Exception e) {
                LogUtils.e(e);
            }
        }).execute(database);
    }

    private void uploadPreferences(String itemId) {
        File preferences = FileHelper.getPreferencesFile(this);
        new FileUploadTask(itemId, new OneDriveManager.UploadProgressCallback<Item>() {

            @Override
            public void success(Item item) {
                preferencesUtils.setOneDrivePreferencesItemId(item.id);
                preferencesUtils.setOneDrivePreferenceLastSyncTime(System.currentTimeMillis());
            }

            @Override
            public void failure(Exception e) {
                LogUtils.e(e);
            }
        }).execute(preferences);
    }
}
