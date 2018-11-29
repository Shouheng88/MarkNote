package me.shouheng.notepal.async.onedrive;

import android.os.AsyncTask;

import me.shouheng.data.store.AttachmentsStore;
import me.shouheng.notepal.util.preferences.SyncPreferences;

/**
 * Created by shouh on 2018/4/7.*/
public class ClearBackupStateTask extends AsyncTask<Void, Void, Void>{

    @Override
    protected Void doInBackground(Void... voids) {
        SyncPreferences syncPreferences = SyncPreferences.getInstance();
        syncPreferences.setOneDriveLastSyncTime(0);
        syncPreferences.setOneDriveDatabaseLastSyncTime(0);
        syncPreferences.setOneDrivePreferenceLastSyncTime(0);
        AttachmentsStore.getInstance().clearOneDriveBackupState();
        return null;
    }
}
