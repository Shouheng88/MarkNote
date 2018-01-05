package me.shouheng.notepal.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.CommonActivity;
import me.shouheng.notepal.async.DataBackupIntentService;
import me.shouheng.notepal.listener.OnFragmentDestroyListener;
import me.shouheng.notepal.util.PermissionUtils;
import me.shouheng.notepal.util.PreferencesUtils;
import me.shouheng.notepal.util.StringUtils;
import me.shouheng.notepal.util.ToastUtils;

/**
 * Created by wang shouheng on 2018/1/5.*/
public class SettingsBackup extends PreferenceFragment {

    private final static String KEY_EXTERNAL_STORAGE = "backup_to_external_storage";

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
        findPreference(KEY_EXTERNAL_STORAGE).setOnPreferenceClickListener(preference -> {
            PermissionUtils.checkStoragePermission((CommonActivity) getActivity(), this::showBackupNameEditor);
            return true;
        });
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
                        ToastUtils.makeToast(getActivity(), R.string.backup_data_export_name_empty);
                        backupName = defName;
                    }
                    Intent service = new Intent(getActivity(), DataBackupIntentService.class);
                    service.setAction(DataBackupIntentService.ACTION_DATA_EXPORT);
                    service.putExtra(DataBackupIntentService.INTENT_BACKUP_INCLUDE_SETTINGS, cb.isChecked());
                    service.putExtra(DataBackupIntentService.INTENT_BACKUP_NAME, backupName);
                    getActivity().startService(service);
                }).build().show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() instanceof OnFragmentDestroyListener) {
            ((OnFragmentDestroyListener) getActivity()).onFragmentDestroy();
        }
    }
}
