package me.shouheng.notepal.activity;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.afollestad.materialdialogs.color.ColorChooserDialog;

import org.polaric.colorful.Colorful;

import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.ActivitySettingsBinding;
import me.shouheng.notepal.fragment.PrimaryPickerFragment;
import me.shouheng.notepal.fragment.SettingsFragment;
import me.shouheng.notepal.listener.OnThemeSelectedListener;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.util.FragmentHelper;
import me.shouheng.notepal.util.PreferencesUtils;
import me.shouheng.notepal.util.ToastUtils;

public class SettingsActivity extends CommonActivity<ActivitySettingsBinding> implements
        SettingsFragment.OnPreferenceClickListener, OnThemeSelectedListener {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
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
        setupTheme(dialog, selectedColor);
    }

    private void setupTheme(@NonNull ColorChooserDialog colorChooserDialog, @ColorInt int i) {
        String colorName = ColorUtils.getColorName(i);
        PreferencesUtils.getInstance(this).setAccentColor(Colorful.AccentColor.getByColorName(colorName));
        ColorUtils.forceUpdateThemeStatus(this);
        updateTheme();
        ToastUtils.makeToast(this, R.string.set_successfully);
    }

    @Override
    public void onPreferenceClick(String key) {
        switch (key) {
            case PreferencesUtils.PRIMARY_COLOR:
                FragmentHelper.replaceWithCallback(this,
                        PrimaryPickerFragment.newInstance(), R.id.fragment_container);
                break;
        }
    }

    @Override
    public void onThemeSelected(Colorful.ThemeColor themeColor) {
        getBinding().bar.findViewById(R.id.toolbar).setBackgroundColor(primaryColor());
    }
}
