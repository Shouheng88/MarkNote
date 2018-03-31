package me.shouheng.notepal.fragment.setting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.onedrive.sdk.core.ClientException;

import org.polaric.colorful.PermissionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.DirectoryActivity;
import me.shouheng.notepal.activity.base.CommonActivity;
import me.shouheng.notepal.async.DataBackupIntentService;
import me.shouheng.notepal.listener.OnFragmentDestroyListener;
import me.shouheng.notepal.manager.one.drive.DefaultCallback;
import me.shouheng.notepal.manager.one.drive.OneDriveManager;
import me.shouheng.notepal.model.Directory;
import me.shouheng.notepal.util.FileHelper;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.PreferencesUtils;
import me.shouheng.notepal.util.StringUtils;
import me.shouheng.notepal.util.ToastUtils;

/**
 * Created by wang shouheng on 2018/1/5.*/
public class SettingsBackup extends PreferenceFragment {

    private final static String KEY_BACKUP_TO_EXTERNAL_STORAGE = "backup_to_external_storage";
    private final static String KEY_IMPORT_FROM_EXTERNAL_STORAGE = "import_from_external_storage";
    private final static String KEY_DELETE_EXTERNAL_STORAGE_BACKUP = "delete_external_storage_backup";
    private final static String KEY_ONE_DRIVE_BACKUP = "key_one_drive_backup";

    private final int REQUEST_PICK_FOLDER = 0x000F;
    private Preference prefOneDrive;

    private PreferencesUtils preferencesUtils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configToolbar();

        preferencesUtils = PreferencesUtils.getInstance(getActivity());

        addPreferencesFromResource(R.xml.preferences_data_backup);

        setPreferenceClickListeners();
    }

    private void configToolbar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) actionBar.setTitle(R.string.setting_backup);
    }

    private void setPreferenceClickListeners() {
        findPreference(KEY_BACKUP_TO_EXTERNAL_STORAGE).setOnPreferenceClickListener(preference -> {
            PermissionUtils.checkStoragePermission((CommonActivity) getActivity(), this::showBackupNameEditor);
            return true;
        });
        findPreference(KEY_IMPORT_FROM_EXTERNAL_STORAGE).setOnPreferenceClickListener(preference -> {
            PermissionUtils.checkStoragePermission((CommonActivity) getActivity(), this::showExternalBackupImport);
            return true;
        });
        findPreference(KEY_DELETE_EXTERNAL_STORAGE_BACKUP).setOnPreferenceClickListener(preference -> {
            PermissionUtils.checkStoragePermission((CommonActivity) getActivity(), this::showExternalBackupDelete);
            return true;
        });

        prefOneDrive = findPreference(KEY_ONE_DRIVE_BACKUP);
        refreshOneDriveMessage();
        prefOneDrive.setOnPreferenceClickListener(preference -> {
            PermissionUtils.checkStoragePermission((CommonActivity) getActivity(), this::showOneDriveBackupDialog);
            return true;
        });
    }

    // region One Drive backup
    private void showOneDriveBackupDialog() {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setTitle(R.string.text_please_wait);
        pd.setCancelable(false);
        pd.show();

        OneDriveManager oneDriveManager = OneDriveManager.getInstance();
        try {
            oneDriveManager.getOneDriveClient();
            pd.dismiss();
            onLoginSuccess();
        } catch (final UnsupportedOperationException ignored) {
            LogUtils.d(ignored);
            new MaterialDialog.Builder(getActivity())
                    .title(R.string.text_tips)
                    .content(R.string.one_drive_sing_in_message)
                    .positiveText(R.string.text_confirm)
                    .onPositive((dialog, which) -> {
                        final ProgressDialog newPb = new ProgressDialog(getActivity());
                        newPb.setTitle(R.string.text_please_wait);
                        newPb.setCancelable(false);
                        newPb.show();
                        oneDriveManager.createOneDriveClient(getActivity(),
                                new DefaultCallback<Void>(getActivity()) {
                                    @Override
                                    public void success(Void aVoid) {
                                        newPb.dismiss();
                                        onLoginSuccess();
                                    }

                                    @Override
                                    public void failure(ClientException error) {
                                        newPb.dismiss();
                                        super.failure(error);
                                    }
                                });
                    })
                    .dismissListener(dialogInterface -> pd.dismiss())
                    .build().show();
        }
    }

    private void onLoginSuccess() {
        String itemId = preferencesUtils.getOneDriveBackupItemId();
        boolean isDirSpecified = !TextUtils.isEmpty(itemId);
        if (!isDirSpecified) {
            pickBackupDir();
        } else {
            new MaterialDialog.Builder(getActivity())
                    .title(R.string.text_tips)
                    .content(String.format(getString(R.string.one_drive_backup_account),
                            preferencesUtils.getOneDriveBackupName()))
                    .positiveText(R.string.text_sing_out)
                    .onPositive((dialog, which) -> {
                        OneDriveManager oneDriveManager = OneDriveManager.getInstance();
                        oneDriveManager.signOut();
                        preferencesUtils.setOneDriveBackupItemId(null);
                        refreshOneDriveMessage();
                    })
                    .build().show();
        }
    }

    private void pickBackupDir() {
        DirectoryActivity.startExplore(this, REQUEST_PICK_FOLDER);
    }

    private void refreshOneDriveMessage() {
        String backItemId = preferencesUtils.getOneDriveBackupItemId();
        if (!TextUtils.isEmpty(backItemId)) {
            prefOneDrive.setSummary(String.format(getString(R.string.one_drive_backup_account),
                    preferencesUtils.getOneDriveBackupName()));
        } else {
            prefOneDrive.setSummary(R.string.one_drive_backup_sub_title);
        }
    }

    private void showBackupNameEditor() {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_backup_layout, null);

        String defName = StringUtils.getTimeFileName();
        EditText tvFileName = v.findViewById(R.id.export_file_name);
        tvFileName.setText(defName);

        AppCompatCheckBox cb = v.findViewById(R.id.backup_include_settings);

        new MaterialDialog.Builder(getActivity())
                .title(R.string.backup_data_export_message)
                .customView(v, false)
                .positiveText(R.string.confirm)
                .onPositive((dialog, which) -> {
                    String backupName;
                    if (TextUtils.isEmpty(backupName = tvFileName.getText().toString())) {
                        ToastUtils.makeToast(R.string.backup_data_export_name_empty);
                        backupName = defName;
                    }
                    Intent service = new Intent(getActivity(), DataBackupIntentService.class);
                    service.setAction(DataBackupIntentService.ACTION_DATA_EXPORT);
                    service.putExtra(DataBackupIntentService.INTENT_BACKUP_INCLUDE_SETTINGS, cb.isChecked());
                    service.putExtra(DataBackupIntentService.INTENT_BACKUP_NAME, backupName);
                    getActivity().startService(service);
                }).build().show();
    }

    // endregion

    // region External storage backup
    private void showExternalBackupImport() {
        final String[] backups = getExternalBackups();
        if (backups.length == 0) {
            ToastUtils.makeToast(R.string.backup_no_backups_available);
            return;
        }

        new MaterialDialog.Builder(getActivity())
                .title(R.string.backup_data_import_message)
                .items(backups)
                .itemsCallbackSingleChoice(-1, (dialog, itemView, which, text) -> {
                    if (TextUtils.isEmpty(text)) {
                        ToastUtils.makeToast(R.string.backup_no_backup_data_selected);
                        return true;
                    }
                    showExternalBackupImportConfirm(text.toString());
                    return true;
                })
                .positiveText(R.string.confirm)
                .onPositive((dialog, which) -> {})
                .build().show();
    }

    private void showExternalBackupImportConfirm(String backup) {
        File backupDir = FileHelper.getBackupDir(backup);
        long size = FileHelper.getSize(backupDir) / 1024;
        String sizeString = size > 1024 ? size / 1024 + "Mb" : size + "Kb";

        String prefName = FileHelper.getSharedPreferencesFile(getActivity()).getName();
        boolean hasPreferences = (new File(backupDir, prefName)).exists();

        String message = getString(R.string.backup_data_import_message_warning) + "\n\n"
                + backup + " (" + sizeString + (hasPreferences ? " " + getString(R.string.backup_settings_included) : "") + ")";

        new MaterialDialog.Builder(getActivity())
                .title(R.string.backup_confirm_restoring_backup)
                .content(message)
                .positiveText(R.string.confirm)
                .onPositive((dialog, which) -> {
                    Intent service = new Intent(getActivity(), DataBackupIntentService.class);
                    service.setAction(DataBackupIntentService.ACTION_DATA_IMPORT);
                    service.putExtra(DataBackupIntentService.INTENT_BACKUP_NAME, backup);
                    getActivity().startService(service);
                }).build().show();
    }

    private String[] getExternalBackups() {
        String[] backups = FileHelper.getExternalStoragePublicDir().list();
        Arrays.sort(backups);
        return backups;
    }

    private void showExternalBackupDelete() {
        final String[] backups = getExternalBackups();
        if (backups.length == 0) {
            ToastUtils.makeToast(R.string.backup_no_backups_to_delete);
            return;
        }

        ArrayList<String> selected = new ArrayList<>();
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.backup_data_delete_message)
                .setMultiChoiceItems(backups, new boolean[backups.length], (dialog, which, isChecked) -> {
                    if (isChecked) {
                        selected.add(backups[which]);
                    } else {
                        selected.remove(backups[which]);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    LogUtils.d(selected);
                    if (selected.isEmpty()) {
                        ToastUtils.makeToast(R.string.backup_no_backup_data_selected);
                    } else {
                        showExternalBackupDeleteConfirm(selected);
                    }
                }).show();
    }

    private void showExternalBackupDeleteConfirm(ArrayList<String> selected) {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.text_warning)
                .content(R.string.backup_confirm_removing_backup)
                .positiveText(R.string.confirm)
                .onPositive((dialog, which) -> {
                    Intent service = new Intent(getActivity(), DataBackupIntentService.class);
                    service.setAction(DataBackupIntentService.ACTION_DATA_DELETE);
                    service.putStringArrayListExtra(DataBackupIntentService.INTENT_BACKUP_NAME, selected);
                    getActivity().startService(service);
                }).build().show();
    }
    // endregion

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PICK_FOLDER:
                    Directory directory = (Directory) data.getSerializableExtra(DirectoryActivity.KEY_EXTRA_DATA);
                    preferencesUtils.setOneDriveBackupItemId(directory.getId());
                    preferencesUtils.setOneDriveBackupName(directory.getName());
                    refreshOneDriveMessage();
                    ToastUtils.makeToast(String.format(getString(R.string.one_drive_backup_account), directory.getName()));
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() instanceof OnFragmentDestroyListener) {
            ((OnFragmentDestroyListener) getActivity()).onFragmentDestroy();
        }
    }
}
