package me.shouheng.notepal.activity;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import me.shouheng.notepal.R;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.databinding.ActivityWidgetConfigurationBinding;
import me.shouheng.notepal.dialog.picker.NotebookPickerDialog;
import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.viewmodel.NotebookViewModel;
import me.shouheng.notepal.widget.desktop.ListRemoteViewsFactory;

public class ConfigActivity extends AppCompatActivity {

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private Notebook selectedNotebook;

    private ActivityWidgetConfigurationBinding binding;

    private NotebookViewModel notebookViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setResult(RESULT_CANCELED);
        super.onCreate(savedInstanceState);

        notebookViewModel = ViewModelProviders.of(this).get(NotebookViewModel.class);

        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_widget_configuration, null, false);
        setContentView(binding.getRoot());

        handleArguments();

        doCreateView();
    }

    private void handleArguments() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        SharedPreferences sharedPreferences = getApplication().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS);
        long nbCode = sharedPreferences.getLong(Constants.PREF_WIDGET_NOTEBOOK_CODE_PREFIX + String.valueOf(mAppWidgetId), 0);

        if (nbCode != 0) fetchNotebook(nbCode);
    }

    private void fetchNotebook(long nbCode) {
        notebookViewModel.get(nbCode).observe(this, notebookResource -> {
            if (notebookResource == null) {
                ToastUtils.makeToast(R.string.text_failed_to_load_data);
                return;
            }
            switch (notebookResource.status) {
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed_to_load_data);
                    break;
                case SUCCESS:
                    if (notebookResource.data != null) {
                        selectedNotebook = notebookResource.data;
                        updateWhenSelectNotebook();
                    }
                    break;
            }
        });
    }

    private void doCreateView() {
        binding.llFolder.setOnClickListener(view -> showNotebookPicker());
        binding.btnPositive.setOnClickListener(view -> onConfirm());
    }

    private void onConfirm() {
        ListRemoteViewsFactory.updateConfiguration(getApplicationContext(), mAppWidgetId, selectedNotebook);
        finishWithOK();
    }

    private void showNotebookPicker() {
        NotebookPickerDialog.newInstance()
                .setOnItemSelectedListener((dialog, notebook, position) -> {
                    selectedNotebook = notebook;
                    updateWhenSelectNotebook();
                    dialog.dismiss();
                })
                .show(getSupportFragmentManager(), "NOTEBOOK_PICKER");
    }

    private void updateWhenSelectNotebook() {
        if (selectedNotebook != null) {
            binding.tvFolder.setText(selectedNotebook.getTitle());
            binding.tvFolder.setTextColor(selectedNotebook.getColor());
        }
    }

    private void finishWithOK() {
        Intent intent = new Intent();
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, intent);
        finish();
    }
}
