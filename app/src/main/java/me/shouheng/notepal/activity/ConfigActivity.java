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
import me.shouheng.notepal.dialog.picker.NotebookPickerDialog;
import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.provider.NotebookStore;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.widget.desktop.ListRemoteViewsFactory;
import me.shouheng.notepal.widget.desktop.ListWidgetType;

public class ConfigActivity extends AppCompatActivity {

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Notebook selectedNotebook;

    private ListWidgetType listWidgetType;

    private ActivityWidgetConfigurationBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setResult(RESULT_CANCELED);
        super.onCreate(savedInstanceState);

        handleArguments();

        binding = DataBindingUtil.inflate(getLayoutInflater(),
                R.layout.activity_widget_configuration, null, false);
        setContentView(binding.getRoot());

        doCreateView();
    }

    private void handleArguments() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        SharedPreferences sharedPreferences = getApplication().getSharedPreferences(
                Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS);
        int widgetTypeId = sharedPreferences.getInt(
                Constants.PREF_WIDGET_TYPE_PREFIX + String.valueOf(mAppWidgetId),
                ListWidgetType.NOTES_LIST.id);
        long nbCode = sharedPreferences.getLong(
                Constants.PREF_WIDGET_NOTEBOOK_CODE_PREFIX + String.valueOf(mAppWidgetId),
                0);
        if (nbCode != 0) {
            selectedNotebook = NotebookStore.getInstance(getApplicationContext()).get(nbCode);
            updateWhenSelectNotebook();
        }
        listWidgetType = ListWidgetType.getListWidgetType(widgetTypeId);
        LogUtils.d(listWidgetType);
    }

    protected void doCreateView() {
        binding.rgType.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_notes:
                    binding.llFolder.setEnabled(true);
                    break;
                case R.id.rb_minds:
                    binding.llFolder.setEnabled(false);
                    break;
                default:
                    LogUtils.e("Wrong element choosen: " + checkedId);
            }
        });
        if (listWidgetType == ListWidgetType.MINDS_LIST) {
            binding.rgType.check(binding.rbMinds.getId());
        }

        /*
         * set whether enable the function of switching list type.
         * We don't let the user switch the list type. */
        Intent i = getIntent();
        if (i != null
                && i.hasExtra(Constants.EXTRA_CONFIG_SWITCH_ENABLE)
                && !i.getBooleanExtra(Constants.EXTRA_CONFIG_SWITCH_ENABLE, false)) {
            binding.rgType.setEnabled(false);
            binding.rbMinds.setEnabled(false);
            binding.rbNotes.setEnabled(false);
        }

        binding.llFolder.setOnClickListener(view -> showNotebookPicker());
        binding.llFolder.setEnabled(listWidgetType == ListWidgetType.NOTES_LIST);

        binding.btnPositive.setOnClickListener(view -> onConfirm());
    }

    private void onConfirm() {
        boolean isNotes = binding.rgType.getCheckedRadioButtonId() == R.id.rb_notes;
        ListRemoteViewsFactory.updateConfiguration(getApplicationContext(),
                mAppWidgetId,
                selectedNotebook,
                isNotes ? ListWidgetType.NOTES_LIST : ListWidgetType.MINDS_LIST);
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
