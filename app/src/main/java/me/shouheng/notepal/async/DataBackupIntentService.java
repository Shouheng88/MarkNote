package me.shouheng.notepal.async;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.MainActivity;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.provider.AttachmentsStore;
import me.shouheng.notepal.provider.PalmDB;
import me.shouheng.notepal.util.FileHelper;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.NotificationsHelper;

/**
 * Created by wang shouheng on 2018/1/5.*/
public class DataBackupIntentService extends IntentService {

    public final static String INTENT_BACKUP_NAME = "backup_name";
    public final static String ACTION_DATA_EXPORT = "action_data_export";
    public final static String INTENT_BACKUP_INCLUDE_SETTINGS = "backup_include_settings";

    public final static String ACTION_DATA_IMPORT = "action_data_import";
    public final static String ACTION_DATA_IMPORT_SPRINGPAD = "action_data_import_springpad";

    public final static String ACTION_DATA_DELETE = "action_data_delete";

    private NotificationsHelper mNotificationsHelper;

    public DataBackupIntentService() {
        super("DataBackupIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        mNotificationsHelper = new NotificationsHelper(this)
                .createNotification(R.drawable.ic_save_white, getString(R.string.working), null)
                .setIndeterminate()
                .setOngoing()
                .show();

        if (ACTION_DATA_EXPORT.equals(intent.getAction())) {
            exportData(intent);
        } else if (ACTION_DATA_IMPORT.equals(intent.getAction())) {
            importData(intent);
        } else if (ACTION_DATA_DELETE.equals(intent.getAction())) {
            deleteData(intent);
        }
    }

    private void createNotification(Intent intent, Context mContext, String title, String message, File backupDir) {

        Intent intentLaunch;
        if (DataBackupIntentService.ACTION_DATA_IMPORT.equals(intent.getAction())
                || DataBackupIntentService.ACTION_DATA_IMPORT_SPRINGPAD.equals(intent.getAction())) {
            intentLaunch = new Intent(mContext, MainActivity.class);
            intentLaunch.setAction(Constants.ACTION_RESTART_APP);
        } else {
            intentLaunch = new Intent();
        }
        // Add this bundle to the intent
        intentLaunch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentLaunch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Creates the PendingIntent
        PendingIntent notifyIntent = PendingIntent.getActivity(mContext, 0, intentLaunch, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationsHelper mNotificationsHelper = new NotificationsHelper(mContext);
        mNotificationsHelper.createNotification(R.drawable.ic_save_white, title, notifyIntent)
                .setMessage(message)
//                .setRingtone(prefs.getString("settings_notification_ringtone", null))
                .setLedActive();
//        if (prefs.getBoolean("settings_notification_vibration", true)) {
//            mNotificationsHelper.setVibration();
//        }
        mNotificationsHelper.show();
    }

    // region export data
    private synchronized void exportData(Intent intent) {
        boolean includeSettings = intent.getBooleanExtra(INTENT_BACKUP_INCLUDE_SETTINGS, false);
        String backupName = intent.getStringExtra(INTENT_BACKUP_NAME);

        File backupDir = FileHelper.getBackupDir(backupName);
        // delete previous backup if exist
        FileHelper.delete(this, backupDir.getAbsolutePath());

        backupDir = FileHelper.getBackupDir(backupName);

        exportDB(backupDir);

        exportAttachments(backupDir);

        if (includeSettings) exportSettings(backupDir);

        String title = getString(R.string.backup_data_export_completed);
        String text = backupDir.getPath();
        createNotification(intent, this, title, text, backupDir);
    }

    private boolean exportDB(File backupDir) {
        File database = getDatabasePath(PalmDB.DATABASE_NAME);
        return (FileHelper.copyFile(database, new File(backupDir, PalmDB.DATABASE_NAME)));
    }

    private boolean exportAttachments(File backupDir) {
        File attachmentsDir = FileHelper.getAttachmentDir(this);
        File destDir = new File(backupDir, attachmentsDir.getName());

        AttachmentsStore store = AttachmentsStore.getInstance(this);
        List<Attachment> list = store.get(null, null);

        int exported = 0;
        for (Attachment attachment : list) {
            FileHelper.copyToBackupDir(destDir, new File(attachment.getPath()));
            mNotificationsHelper.setMessage(getString(R.string.text_attachment) + " " + exported++ + "/" + list.size()).show();
        }
        return true;
    }

    private boolean exportSettings(File backupDir) {
        File preferences = FileHelper.getSharedPreferencesFile(this);
        return (FileHelper.copyFile(preferences, new File(backupDir, preferences.getName())));
    }
    // endregion

    // region import data
    synchronized private void importData(Intent intent) {
        String backupName = intent.getStringExtra(INTENT_BACKUP_NAME);
        File backupDir = FileHelper.getBackupDir(backupName);

        importDB(backupDir);

        importAttachments(backupDir);

        importSettings(backupDir);

        String title = getString(R.string.backup_data_import_completed);
        String text = getString(R.string.backup_click_to_refresh_application);
        createNotification(intent, this, title, text, backupDir);
    }

    private boolean importDB(File backupDir) {
        File database = getDatabasePath(PalmDB.DATABASE_NAME);
        if (database.exists()) {
            database.delete();
        }
        return (FileHelper.copyFile(new File(backupDir, PalmDB.DATABASE_NAME), database));
    }

    private void importAttachments(File backupDir) {
        File attachmentsDir = FileHelper.getAttachmentDir(this);
        File backupAttachmentsDir = new File(backupDir, attachmentsDir.getName());
        if (!backupAttachmentsDir.exists()) {
            return;
        }
        Collection list = FileUtils.listFiles(backupAttachmentsDir, FileFilterUtils.trueFileFilter(), TrueFileFilter.INSTANCE);
        Iterator i = list.iterator();
        int imported = 0;
        File file = null;
        while (i.hasNext()) {
            try {
                file = (File) i.next();
                FileUtils.copyFileToDirectory(file, attachmentsDir, true);
                mNotificationsHelper.setMessage(getString(R.string.text_attachment) + " " + imported++ + "/" + list.size()).show();
            } catch (IOException e) {
                LogUtils.e("Error importing the attachment " + file.getName());
            }
        }
    }

    private boolean importSettings(File backupDir) {
        File preferences = FileHelper.getSharedPreferencesFile(this);
        File preferenceBackup = new File(backupDir, preferences.getName());
        return (FileHelper.copyFile(preferenceBackup, preferences));
    }
    // endregion

    // region delete data
    synchronized private void deleteData(Intent intent) {
        List<String> backups = intent.getStringArrayListExtra(INTENT_BACKUP_NAME);

        StringBuilder names = new StringBuilder();
        for (String backup : backups) {
            File backupDir = FileHelper.getBackupDir(backup);
            FileHelper.delete(this, backupDir.getAbsolutePath());
            names.append(backup);
            names.append(",");
        }

        String title = getString(R.string.backup_data_deletion_completed);
        names.append(getString(R.string.text_delete));
        createNotification(intent, this, title, names.toString(), null);
    }
    // endregion
}
