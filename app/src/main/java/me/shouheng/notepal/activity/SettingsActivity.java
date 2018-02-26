package me.shouheng.notepal.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.afollestad.materialdialogs.color.ColorChooserDialog;

import org.polaric.colorful.Colorful;

import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.ActivitySettingsBinding;
import me.shouheng.notepal.fragment.AppInfoFragment;
import me.shouheng.notepal.fragment.PrimaryPickerFragment;
import me.shouheng.notepal.fragment.SettingsBackup;
import me.shouheng.notepal.fragment.SettingsSecurity;
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

    private final static int REQUEST_CODE_PASSWORD = 0x0201;

    private static final String EXTRA_NAME_REQUEST_CODE = "extra.requestcode";

    public static void startActivityForResult(Activity mContext, int requestCode){
        Intent intent = new Intent(mContext, SearchActivity.class);
        intent.putExtra(EXTRA_NAME_REQUEST_CODE, requestCode);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        mContext.startActivityForResult(intent, requestCode);
    }

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
            case PreferencesUtils.ACCENT_COLOR:
                showAccentColorPicker();
                break;
            case SettingsFragment.KEY_DATA_BACKUP:
                FragmentHelper.replaceWithCallback(this, new SettingsBackup(), R.id.fragment_container);
                break;
            case SettingsFragment.KEY_DATA_SECURITY:
                if (preferencesUtils.isPasswordRequired() && !TextUtils.isEmpty(preferencesUtils.getPassword())) {
                    LockActivity.requirePassword(this, REQUEST_CODE_PASSWORD);
                } else {
                    FragmentHelper.replaceWithCallback(this, new SettingsSecurity(), R.id.fragment_container);
                }
                break;
            case SettingsFragment.KEY_ABOUT:
                FragmentHelper.replaceWithCallback(this, new AppInfoFragment(), R.id.fragment_container);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_PASSWORD:
                if (resultCode == Activity.RESULT_OK) {
                    FragmentHelper.replaceWithCallback(this, new SettingsSecurity(), R.id.fragment_container);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
