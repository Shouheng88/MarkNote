package me.shouheng.notepal.async.onedrive;

import android.os.AsyncTask;

import com.onedrive.sdk.extensions.Item;

import java.io.File;

import me.shouheng.notepal.manager.onedrive.OneDriveManager;
import me.shouheng.notepal.util.LogUtils;

/**
 * Created by shouh on 2018/3/31.*/
public class FileUploadTask extends AsyncTask<File, Integer, String> {

    private String itemId;

    FileUploadTask(String itemId) {
        this.itemId = itemId;
    }

    @Override
    protected String doInBackground(File... files) {
        LogUtils.d(files.length);
        for (File file : files) {
            OneDriveManager.getInstance().upload(itemId, file, new OneDriveManager.UploadProgressCallback<Item>() {
                @Override
                public void progress(long current, long max) {}

                @Override
                public void success(Item item) {
                    LogUtils.d(item);
                }

                @Override
                public void failure(Exception e) {
                    LogUtils.e(e.getMessage());
                }
            });
        }
        return "executed";
    }
}
