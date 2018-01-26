package me.shouheng.notepal.widget.desktop;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.ActivityWidgetConfigurationBinding;
import me.shouheng.notepal.dialog.NotebookPickerDialog;
import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.provider.schema.NoteSchema;
import me.shouheng.notepal.util.LogUtils;

public class WidgetConfigurationActivity extends AppCompatActivity {

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private String sqlCondition;
    private Notebook selectedNotebook;

    private ActivityWidgetConfigurationBinding activityWidgetConfigurationBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setResult(RESULT_CANCELED);
        super.onCreate(savedInstanceState);

        activityWidgetConfigurationBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_widget_configuration, null, false);
        setContentView(activityWidgetConfigurationBinding.getRoot());

        doCreateView(savedInstanceState);
    }

    public ActivityWidgetConfigurationBinding getBinding() {
        return activityWidgetConfigurationBinding;
    }

    protected void doCreateView(Bundle savedInstanceState) {
        getBinding().widgetConfigRadiogroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.widget_config_notes:
                    getBinding().llFolder.setEnabled(true);
                    break;
                case R.id.widget_config_minds:
                    getBinding().llFolder.setEnabled(false);
                    break;
                default:
                    LogUtils.e("Wrong element choosen: " + checkedId);
            }
        });

        getBinding().llFolder.setOnClickListener(view -> showNotebookPicker());

        getBinding().btnPositive.setOnClickListener(view -> {
            if (getBinding().widgetConfigRadiogroup.getCheckedRadioButtonId() == R.id.widget_config_notes) {
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

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) finish();
    }

    private void showNotebookPicker() {
        NotebookPickerDialog.newInstance()
                .setOnItemSelectedListener((dialog, notebook, position) -> {
                    selectedNotebook = notebook;
                    getBinding().tvFolder.setText(selectedNotebook.getTitle());
                    dialog.dismiss();
                })
                .show(getSupportFragmentManager(), "NOTEBOOK_PICKER");
    }
}
