package me.shouheng.notepal.fragment.setting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.onedrive.sdk.core.ClientException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import me.shouheng.commons.activity.CommonActivity;
import me.shouheng.commons.event.PageName;
import me.shouheng.commons.event.*;
import me.shouheng.commons.fragment.BPreferenceFragment;
import me.shouheng.commons.utils.LogUtils;
import me.shouheng.commons.utils.PalmUtils;
import me.shouheng.commons.utils.PermissionUtils;
import me.shouheng.commons.utils.ToastUtils;
import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.DirectoryActivity;
import me.shouheng.notepal.service.DataBackupService;
import me.shouheng.notepal.manager.FileManager;
import me.shouheng.notepal.onedrive.DefaultCallback;
import me.shouheng.notepal.onedrive.OneDriveManager;
import me.shouheng.notepal.common.enums.SyncTimeInterval;
import me.shouheng.notepal.common.preferences.SyncPreferences;

/**
 * Created by WngShhng on 2018/1/5.
 */
@PageName(name = UMEvent.PAGE_SETTING_BACKUP)
public class SettingsBackup extends BPreferenceFragment {

    private final int REQUEST_PICK_FOLDER = 0x000F;

    private Preference prefOneDrive;
    private Preference prefOneDriveSignOut;
    private Preference prefTimeInterval;

    private SyncPreferences syncPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configToolbar();

        syncPreferences = SyncPreferences.getInstance();

        addPreferencesFromResource(R.xml.preferences_data_backup);

        setPreferenceClickListeners();
    }

    private void configToolbar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) actionBar.setTitle(R.string.setting_category_universal_backup);
    }

    private void setPreferenceClickListeners() {
        findPreference(R.string.key_backup_external).setOnPreferenceClickListener(preference -> {
            PermissionUtils.checkStoragePermission((CommonActivity) getActivity(), this::showBackupNameEditor);
            return true;
        });
        findPreference(R.string.key_backup_import).setOnPreferenceClickListener(preference -> {
            PermissionUtils.checkStoragePermission((CommonActivity) getActivity(), this::showExternalBackupImport);
            return true;
        });
        findPreference(R.string.key_backup_delete).setOnPreferenceClickListener(preference -> {
            PermissionUtils.checkStoragePermission((CommonActivity) getActivity(), this::showExternalBackupDelete);
            return true;
        });

        prefTimeInterval = findPreference(R.string.key_backup_time_interval);
        prefTimeInterval.setOnPreferenceClickListener(preference -> {
            timeIntervalPicker();
            return true;
        });
        refreshTimeInterval();

        prefOneDrive = findPreference(R.string.key_backup_onedrive_singin);
        prefOneDrive.setOnPreferenceClickListener(preference -> {
            PermissionUtils.checkStoragePermission((CommonActivity) getActivity(), this::connectOneDrive);
            return true;
        });

        prefOneDriveSignOut = findPreference(R.string.key_backup_onedrvie_signout);
        prefOneDriveSignOut.setOnPreferenceClickListener(preference -> {
            OneDriveManager oneDriveManager = OneDriveManager.getInstance();
            oneDriveManager.signOut();
            syncPreferences.setOneDriveBackupItemId(null);
            syncPreferences.setOneDriveFilesBackupItemId(null);
            refreshOneDriveMessage();
            return true;
        });

        refreshOneDriveMessage();
    }

    // region OneDrive synchronization
    private void connectOneDrive() {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setTitle(R.string.text_please_wait);
        pd.setCancelable(false);
        pd.show();

        OneDriveManager.getInstance().connectOneDrive(getActivity(), new DefaultCallback<Void>(getActivity()) {
            @Override
            public void success(Void aVoid) {
                pd.dismiss();
                onLoginSuccess();
            }

            @Override
            public void failure(ClientException error) {
                pd.dismiss();
                super.failure(error);
            }
        });
    }

    private void onLoginSuccess() {
        String itemId = syncPreferences.getOneDriveBackupItemId();
        boolean isDirSpecified = !TextUtils.isEmpty(itemId);
        if (!isDirSpecified) {
            DirectoryActivity.startExplore(this, REQUEST_PICK_FOLDER);
        }
    }

    private void refreshOneDriveMessage() {
        String backItemId = syncPreferences.getOneDriveBackupItemId();
        if (!TextUtils.isEmpty(backItemId)) {
            prefOneDrive.setSummary(String.format(getString(R.string.setting_backup_onedrive_backup_folder), backItemId));
            prefOneDriveSignOut.setEnabled(true);
        } else {
            prefOneDrive.setSummary(R.string.setting_backup_onedrive_sign_sub);
            prefOneDriveSignOut.setEnabled(false);
        }
    }
    // endregion

    // region External storage backup
    private void showBackupNameEditor() {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_backup_layout, null);

        String defName = String.valueOf(System.currentTimeMillis());
        EditText tvFileName = v.findViewById(R.id.export_file_name);
        tvFileName.setText(defName);

        AppCompatCheckBox cb = v.findViewById(R.id.backup_include_settings);

        new MaterialDialog.Builder(getActivity())
                .title(R.string.setting_backup_external_name)
                .customView(v, false)
                .positiveText(R.string.text_confirm)
                .onPositive((dialog, which) -> {
                    String backupName;
                    if (TextUtils.isEmpty(backupName = tvFileName.getText().toString())) {
                        backupName = defName;
                    }
                    Intent service = new Intent(getActivity(), DataBackupService.class);
                    service.setAction(DataBackupService.ACTION_DATA_EXPORT);
                    service.putExtra(DataBackupService.INTENT_BACKUP_INCLUDE_SETTINGS, cb.isChecked());
                    service.putExtra(DataBackupService.INTENT_BACKUP_NAME, backupName);
                    getActivity().startService(service);
                }).build().show();
    }

    private void showExternalBackupImport() {
        final String[] backups = getExternalBackups();
        if (backups.length == 0) {
            ToastUtils.makeToast(R.string.setting_backup_external_empty);
            return;
        }

        new MaterialDialog.Builder(getActivity())
                .title(R.string.setting_backup_external_import)
                .items(backups)
                .itemsCallbackSingleChoice(-1, (dialog, itemView, which, text) -> {
                    if (TextUtils.isEmpty(text)) {
                        ToastUtils.makeToast(R.string.text_failed);
                        return true;
                    }
                    showExternalBackupImportConfirm(text.toString());
                    return true;
                })
                .positiveText(R.string.text_confirm)
                .onPositive((dialog, which) -> {})
                .build().show();
    }

    private void showExternalBackupImportConfirm(String backup) {
        File backupDir = FileManager.getExternalBackupDir(backup);
        long size = FileManager.getSize(backupDir) / 1024;
        String sizeString = size > 1024 ? size / 1024 + "Mb" : size + "Kb";

        String prefName = FileManager.getPreferencesFile(getActivity()).getName();
        boolean hasPreferences = (new File(backupDir, prefName)).exists();

        String message = getString(R.string.setting_backup_external_import_warn) + "\n\n"
                + backup + " (" + sizeString + (hasPreferences ? " " + getString(R.string.setting_backup_text_setting_included) : "") + ")";

        new MaterialDialog.Builder(getActivity())
                .title(R.string.setting_backup_external_import_tips)
                .content(message)
                .positiveText(R.string.text_confirm)
                .onPositive((dialog, which) -> {
                    Intent service = new Intent(getActivity(), DataBackupService.class);
                    service.setAction(DataBackupService.ACTION_DATA_IMPORT);
                    service.putExtra(DataBackupService.INTENT_BACKUP_NAME, backup);
                    getActivity().startService(service);
                }).build().show();
    }

    private String[] getExternalBackups() {
        String[] backups = FileManager.getExternalBackupRootDir().list();
        Arrays.sort(backups);
        return backups;
    }

    private void showExternalBackupDelete() {
        final String[] backups = getExternalBackups();
        if (backups.length == 0) {
            ToastUtils.makeToast(R.string.setting_backup_external_empty);
            return;
        }

        ArrayList<String> selected = new ArrayList<>();
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.text_delete)
                .setMultiChoiceItems(backups, new boolean[backups.length], (dialog, which, isChecked) -> {
                    if (isChecked) {
                        selected.add(backups[which]);
                    } else {
                        selected.remove(backups[which]);
                    }
                })
                .setNegativeButton(R.string.text_cancel, null)
                .setPositiveButton(R.string.text_confirm, (dialog, which) -> {
                    LogUtils.d(selected);
                    if (selected.isEmpty()) {
                        ToastUtils.makeToast(R.string.text_failed);
                    } else {
                        showExternalBackupDeleteConfirm(selected);
                    }
                }).show();
    }

    private void showExternalBackupDeleteConfirm(ArrayList<String> selected) {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.text_warning)
                .content(R.string.setting_backup_external_delete_confirm)
                .positiveText(R.string.text_confirm)
                .onPositive((dialog, which) -> {
                    Intent service = new Intent(getActivity(), DataBackupService.class);
                    service.setAction(DataBackupService.ACTION_DATA_DELETE);
                    service.putStringArrayListExtra(DataBackupService.INTENT_BACKUP_NAME, selected);
                    getActivity().startService(service);
                }).build().show();
    }
    // endregion

    // region Synchronization time interval
    private void timeIntervalPicker() {
        SyncTimeInterval[] timeIntervals = SyncTimeInterval.values();
        String[] items = new String[timeIntervals.length];
        int length = timeIntervals.length;
        for (int i=0; i<length; i++) {
            items[i] = PalmUtils.getStringCompact(timeIntervals[i].resName);
        }

        new MaterialDialog.Builder(getActivity())
                .items(items)
                .itemsCallback((dialog, itemView, position, text) -> {
                    syncPreferences.setSyncTimeInterval(SyncTimeInterval.getTypeById(position));
                    refreshTimeInterval();
                }).show();
    }

    private void refreshTimeInterval() {
        prefTimeInterval.setSummary(syncPreferences.getSyncTimeInterval().resName);
    }
    // endregion

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PICK_FOLDER:
                    refreshOneDriveMessage();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
