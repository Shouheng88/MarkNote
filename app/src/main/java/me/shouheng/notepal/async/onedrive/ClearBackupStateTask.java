package me.shouheng.notepal.async.onedrive;

import android.os.AsyncTask;

import me.shouheng.notepal.provider.AttachmentsStore;
import me.shouheng.notepal.util.preferences.PreferencesUtils;

/**
 * Created by shouh on 2018/4/7.*/
public class ClearBackupStateTask extends AsyncTask<Void, Void, Void>{

    @Override
    protected Void doInBackground(Void... voids) {
        PreferencesUtils preferencesUtils = PreferencesUtils.getInstance();
        preferencesUtils.setOneDriveLastSyncTime(0);
        preferencesUtils.setOneDriveDatabaseLastSyncTime(0);
        preferencesUtils.setOneDrivePreferenceLastSyncTime(0);
        AttachmentsStore.getInstance().clearOneDriveBackupState();
        return null;
    }
}
