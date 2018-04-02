package me.shouheng.notepal.async.onedrive;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;

import me.shouheng.notepal.provider.PalmDB;
import me.shouheng.notepal.util.FileHelper;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.NetworkUtils;
import me.shouheng.notepal.util.PreferencesUtils;

/**
 * Created by shouh on 2018/3/30.*/
public class OneDriveBackupService extends IntentService {

    public static void start(Context context) {
        Intent service = new Intent(context, OneDriveBackupService.class);
        context.startService(service);
    }

    public OneDriveBackupService() {
        super("OneDriveBackupService");
    }

    private PreferencesUtils preferencesUtils;

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        preferencesUtils = PreferencesUtils.getInstance(getApplicationContext());

        boolean isNetworkAvailable = NetworkUtils.isNetworkAvailable(getApplicationContext());
        boolean isWifi = NetworkUtils.isWifi(getApplicationContext());
        boolean isOnlyWifi = PreferencesUtils.getInstance(getApplicationContext()).isBackupOnlyInWifi();

        if (isNetworkAvailable && (!isOnlyWifi || isWifi)) {
            upload();
        }
    }

    private void upload() {
        String itemId = preferencesUtils.getOneDriveBackupItemId();
        if (!TextUtils.isEmpty(itemId)) {
            // Delete before upload.
            new DelDBAndSettingTask(() -> {
                uploadDB(itemId);
                uploadSettings(itemId);
            }).execute(itemId);
        } else {
            LogUtils.e("Error! No backup item id.");
        }

        String filesItemId = preferencesUtils.getOneDriveFilesBackupItemId();
        if (!TextUtils.isEmpty(filesItemId)) {
            uploadAttachments(filesItemId);
        } else {
            LogUtils.e("Error! No files backup item id.");
        }
    }

    private void uploadDB(String itemId) {
        File database = getDatabasePath(PalmDB.DATABASE_NAME);
        new FileUploadTask(itemId).execute(database);
    }

    private void uploadSettings(String itemId) {
        File preferences = FileHelper.getSharedPreferencesFile(this);
        new FileUploadTask(itemId).execute(preferences);
    }

    private void uploadAttachments(String itemId) {
        new BatchUploadUtil(itemId, 10).begin();
    }
}
