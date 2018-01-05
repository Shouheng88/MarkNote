package me.shouheng.notepal.async;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by wang shouheng on 2018/1/5.*/
public class DataBackupIntentService extends IntentService {

    public final static String INTENT_BACKUP_NAME = "backup_name";
    public final static String ACTION_DATA_EXPORT = "action_data_export";
    public final static String INTENT_BACKUP_INCLUDE_SETTINGS = "backup_include_settings";

    public final static String ACTION_DATA_IMPORT = "action_data_import";
    public final static String ACTION_DATA_IMPORT_SPRINGPAD = "action_data_import_springpad";

    public final static String ACTION_DATA_DELETE = "action_data_delete";

    public final static String EXTRA_SPRINGPAD_BACKUP = "extra_springpad_backup";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DataBackupIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
