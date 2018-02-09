package me.shouheng.notepal.activity;

import android.appwidget.AppWidgetManager;
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
import me.shouheng.notepal.dialog.NotebookPickerDialog;
import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.provider.schema.NoteSchema;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.widget.desktop.ListRemoteViewsFactory;
import me.shouheng.notepal.widget.desktop.ListWidgetType;

public class ConfigActivity extends AppCompatActivity {

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private String sqlCondition;
    private Notebook selectedNotebook;

    private ListWidgetType listWidgetType;

    private ActivityWidgetConfigurationBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setResult(RESULT_CANCELED);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) finish();

        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_widget_configuration, null, false);
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getApplication().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS);
        listWidgetType = ListWidgetType.getListWidgetType(sharedPreferences.getInt(
                Constants.PREF_WIDGET_TYPE_PREFIX + String.valueOf(mAppWidgetId), ListWidgetType.NOTES_LIST.id));
        LogUtils.d(listWidgetType);

        doCreateView(savedInstanceState);
    }

    protected void doCreateView(Bundle savedInstanceState) {
        binding.widgetConfigRadiogroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.widget_config_notes:
                    binding.llFolder.setEnabled(true);
                    break;
                case R.id.widget_config_minds:
                    binding.llFolder.setEnabled(false);
                    break;
                default:
                    LogUtils.e("Wrong element choosen: " + checkedId);
            }
        });

        if (listWidgetType == ListWidgetType.MINDS_LIST) {
            binding.widgetConfigRadiogroup.check(binding.widgetConfigMinds.getId());
        }

        /**
         * set whether enable the function of switching list type. We don't let the user switch the list type. */
        if (getIntent().hasExtra(Constants.ACTION_CONFIG_SWITCH_ENABLE)
                && !getIntent().getBooleanExtra(Constants.ACTION_CONFIG_SWITCH_ENABLE, false)) {
            binding.widgetConfigRadiogroup.setEnabled(false);
            binding.widgetConfigMinds.setEnabled(false);
            binding.widgetConfigNotes.setEnabled(false);
        }

        binding.llFolder.setOnClickListener(view -> showNotebookPicker());
        binding.llFolder.setEnabled(listWidgetType == ListWidgetType.NOTES_LIST);

        binding.btnPositive.setOnClickListener(view -> {
            if (binding.widgetConfigRadiogroup.getCheckedRadioButtonId() == R.id.widget_config_notes) {
                if (selectedNotebook != null) {
                    sqlCondition = NoteSchema.TREE_PATH + " LIKE '" + selectedNotebook.getTreePath() + "'||'%'";
                } else {
                    sqlCondition = null;
                }
                ListRemoteViewsFactory.updateConfiguration(getApplicationContext(), mAppWidgetId, sqlCondition, ListWidgetType.NOTES_LIST);
            } else {
                sqlCondition = null;
                ListRemoteViewsFactory.updateConfiguration(getApplicationContext(), mAppWidgetId, sqlCondition, ListWidgetType.MINDS_LIST);
            }

            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        });
    }

    private void showNotebookPicker() {
        NotebookPickerDialog.newInstance()
                .setOnItemSelectedListener((dialog, notebook, position) -> {
                    selectedNotebook = notebook;
                    binding.tvFolder.setText(selectedNotebook.getTitle());
                    dialog.dismiss();
                })
                .show(getSupportFragmentManager(), "NOTEBOOK_PICKER");
    }
}
