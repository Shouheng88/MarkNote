package me.shouheng.notepal.async.onedrive;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.onedrive.sdk.concurrency.ICallback;
import com.onedrive.sdk.core.ClientException;

import me.shouheng.notepal.manager.onedrive.OneDriveManager;
import me.shouheng.notepal.util.PreferencesUtils;
import me.shouheng.notepal.util.SynchronizeUtils;

/**
 * Created by shouh on 2018/4/1.*/
public class DelDBAndPrefTask extends AsyncTask<String, Integer, String> {

    private OnFilesDeletedListener onFilesDeletedListener;

    private PreferencesUtils preferencesUtils;

    private int delCount, toDelCount = 0;

    DelDBAndPrefTask(OnFilesDeletedListener onFilesDeletedListener) {
        this.onFilesDeletedListener = onFilesDeletedListener;
        this.preferencesUtils = PreferencesUtils.getInstance();
    }

    @Override
    protected String doInBackground(String... params) {
        String oneDriveDatabaseItemId = preferencesUtils.getOneDriveDatabaseItemId();
        String oneDrivePreferencesItemId = preferencesUtils.getOneDrivePreferencesItemId();
        if (!TextUtils.isEmpty(oneDriveDatabaseItemId) && SynchronizeUtils.shouldOneDriveDatabaseSync()) {
            toDelCount++;
            deleteItem(oneDriveDatabaseItemId);
        }
        if (!TextUtils.isEmpty(oneDrivePreferencesItemId) && SynchronizeUtils.shouldOneDrivePreferencesSync()) {
            toDelCount++;
            deleteItem(oneDrivePreferencesItemId);
        }
        if (toDelCount == 0 && onFilesDeletedListener != null) {
            onFilesDeletedListener.onDeleted();
        }
        return "Executed";
    }

    private void deleteItem(String itemId) {
        OneDriveManager.getInstance().delete(itemId, new ICallback<Void>() {
            @Override
            public void success(Void aVoid) {
                if (++delCount == toDelCount && onFilesDeletedListener != null) {
                    onFilesDeletedListener.onDeleted();
                }
            }

            @Override
            public void failure(ClientException ex) {
                if (onFilesDeletedListener != null) {
                    onFilesDeletedListener.onFailed(ex);
                }
            }
        });
    }

    public interface OnFilesDeletedListener {
        void onDeleted();
        void onFailed(ClientException ex);
    }
}
