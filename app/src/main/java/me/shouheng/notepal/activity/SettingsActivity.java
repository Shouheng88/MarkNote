package me.shouheng.notepal.activity;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.afollestad.materialdialogs.color.ColorChooserDialog;

import org.polaric.colorful.Colorful;

import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.ActivitySettingsBinding;
import me.shouheng.notepal.fragment.PrimaryPickerFragment;
import me.shouheng.notepal.fragment.SettingsFragment;
import me.shouheng.notepal.listener.OnFragmentDestroyListener;
import me.shouheng.notepal.listener.OnThemeSelectedListener;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.util.FragmentHelper;
import me.shouheng.notepal.util.PreferencesUtils;
import me.shouheng.notepal.util.ToastUtils;

public class SettingsActivity extends CommonActivity<ActivitySettingsBinding> implements
        SettingsFragment.OnPreferenceClickListener, OnThemeSelectedListener, OnFragmentDestroyListener {

    private String keyForColor;

    private PreferencesUtils preferencesUtils;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        preferencesUtils = PreferencesUtils.getInstance(this);

        configToolbar();

        configFragment();
    }

    private void configToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.text_settings);
        if (!isDarkTheme()) toolbar.setPopupTheme(R.style.AppTheme_PopupOverlay);
    }

    private void configFragment() {
        FragmentHelper.replace(this, new SettingsFragment(), R.id.fragment_container);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, int selectedColor) {
        switch (keyForColor) {
            case PreferencesUtils.ACCENT_COLOR:
                setupTheme(dialog, selectedColor);
                if (isSettingsFragment()) ((SettingsFragment) getCurrentFragment())
                        .notifyAccentColorChanged(selectedColor);
                break;
            case PreferencesUtils.DEFAULT_NOTE_COLOR:
                preferencesUtils.setDefaultNoteColor(selectedColor);
                if (isSettingsFragment()) ((SettingsFragment) getCurrentFragment())
                        .notifyNoteColorChanged(selectedColor);
                break;
            case PreferencesUtils.DEFAULT_NOTEBOOK_COLOR:
                preferencesUtils.setDefaultNotebookColor(selectedColor);
                if (isSettingsFragment()) ((SettingsFragment) getCurrentFragment())
                        .notifyNotebookColorChanged(selectedColor);
                break;
        }
    }

    private void setupTheme(@NonNull ColorChooserDialog colorChooserDialog, @ColorInt int i) {
        String colorName = ColorUtils.getColorName(i);
        PreferencesUtils.getInstance(this).setAccentColor(Colorful.AccentColor.getByColorName(colorName));
        ColorUtils.forceUpdateThemeStatus(this);
        updateTheme();
        ToastUtils.makeToast(this, R.string.set_successfully);
    }

    private boolean isSettingsFragment() {
        return getCurrentFragment() instanceof SettingsFragment;
    }

    private android.app.Fragment getCurrentFragment() {
        return getFragmentManager().findFragmentById(R.id.fragment_container);
    }

    @Override
    public void onPreferenceClick(String key) {
        keyForColor = key;
        switch (key) {
            case PreferencesUtils.PRIMARY_COLOR:
                FragmentHelper.replaceWithCallback(this,
                        PrimaryPickerFragment.newInstance(), R.id.fragment_container);
                break;
            case PreferencesUtils.DEFAULT_NOTE_COLOR:
                showPrimaryColorPicker(R.string.setting_set_default_note_color, preferencesUtils.getDefaultNoteColor());
                break;
            case PreferencesUtils.DEFAULT_NOTEBOOK_COLOR:
                showPrimaryColorPicker(R.string.setting_set_default_notebook_color, preferencesUtils.getDefaultNotebookColor());
                break;
            case PreferencesUtils.ACCENT_COLOR:
                showAccentColorPicker();
                break;
        }
    }

    @Override
    public void onThemeSelected(Colorful.ThemeColor themeColor) {
        getBinding().bar.findViewById(R.id.toolbar).setBackgroundColor(primaryColor());
        if (isSettingsFragment()) ((SettingsFragment) getCurrentFragment())
                .notifyPrimaryColorChanged(getResources().getColor(themeColor.getColorRes()));
    }

    private void showPrimaryColorPicker(@StringRes int titleRes, int defaultValue) {
        new ColorChooserDialog.Builder(this, titleRes)
                .preselect(defaultValue)
                .accentMode(false)
                .titleSub(titleRes)
                .backButton(R.string.text_back)
                .doneButton(R.string.done_label)
                .cancelButton(R.string.text_cancel)
                .show();
    }

    private void showAccentColorPicker() {
        new ColorChooserDialog.Builder(this, R.string.select_accent_color)
                .allowUserColorInput(false)
                .preselect(ColorUtils.accentColor(this))
                .allowUserColorInputAlpha(false)
                .titleSub(R.string.select_accent_color)
                .accentMode(true)
                .backButton(R.string.text_back)
                .doneButton(R.string.done_label)
                .cancelButton(R.string.text_cancel)
                .show();
    }

    @Override
    public void onFragmentDestroy() {
        getSupportActionBar().setTitle(R.string.text_settings);
    }
}
