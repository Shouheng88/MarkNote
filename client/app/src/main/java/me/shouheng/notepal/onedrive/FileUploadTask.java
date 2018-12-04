package me.shouheng.notepal.onedrive;

import android.os.AsyncTask;

import com.onedrive.sdk.extensions.Item;

import java.io.File;

import me.shouheng.commons.utils.LogUtils;

/**
 * Created by shouh on 2018/3/31.*/
public class FileUploadTask extends AsyncTask<File, Integer, String> {

    private String itemId;

    private String conflictBehavior;

    private OneDriveManager.UploadProgressCallback<Item> uploadProgressCallback;

    FileUploadTask(String itemId,
                   String conflictBehavior,
                   OneDriveManager.UploadProgressCallback<Item> uploadProgressCallback) {
        this.itemId = itemId;
        this.conflictBehavior = conflictBehavior;
        this.uploadProgressCallback = uploadProgressCallback;
    }

    @Override
    protected String doInBackground(File... files) {
        LogUtils.d(files.length);
        for (File file : files) {
            OneDriveManager.getInstance().upload(itemId, file, conflictBehavior, uploadProgressCallback);
        }
        return "executed";
    }
}
