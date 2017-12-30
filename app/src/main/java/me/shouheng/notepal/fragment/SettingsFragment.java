package me.shouheng.notepal.fragment;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;

import com.afollestad.materialdialogs.color.ColorChooserDialog;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.SettingsActivity;
import me.shouheng.notepal.activity.ThemedActivity;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.util.PreferencesUtils;
import me.shouheng.notepal.widget.ColorPreference;

/**
 * Created by wang shouheng on 2017/12/21.*/
public class SettingsFragment extends PreferenceFragment {

    private final static String IS_DARK_THEME = "is_dark_theme";
    public final static String PRIMARY_COLOR = "primary_color";
    private final static String ACCENT_COLOR = "accent_color";

    private CheckBoxPreference isDarkTheme;

    private ColorPreference primaryColor, accentColor;

    private PreferencesUtils preferencesUtils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferencesUtils = PreferencesUtils.getInstance(getActivity());

        addPreferencesFromResource(R.xml.preferences);

        isDarkTheme = (CheckBoxPreference) findPreference(IS_DARK_THEME);
        primaryColor = (ColorPreference) findPreference(PRIMARY_COLOR);
        accentColor = (ColorPreference) findPreference(ACCENT_COLOR);

        setPreferenceClickListeners();
    }

    private void setPreferenceClickListeners() {
        isDarkTheme.setOnPreferenceClickListener(preference -> {
            updateThemeSettings();
            return true;
        });
        primaryColor.setOnPreferenceClickListener(preference -> {
            if (getActivity() != null && getActivity() instanceof OnPreferenceClickListener) {
                ((OnPreferenceClickListener) getActivity()).onPreferenceClick(PRIMARY_COLOR);
            }
            return true;
        });
        accentColor.setOnPreferenceClickListener(preference -> {
            showAccentColorPicker();
            return true;
        });
    }

    private void showAccentColorPicker() {
        new ColorChooserDialog.Builder((SettingsActivity) getActivity(), R.string.select_accent_color)
                .allowUserColorInput(false)
                .preselect(ColorUtils.accentColor(getActivity()))
                .allowUserColorInputAlpha(false)
                .titleSub(R.string.select_accent_color)
                .accentMode(true)
                .backButton(R.string.text_back)
                .doneButton(R.string.done_label)
                .cancelButton(R.string.text_cancel)
                .show();
    }

    private void updateThemeSettings() {
        ColorUtils.forceUpdateThemeStatus(getActivity());
        ((ThemedActivity) getActivity()).reUpdateTheme();
    }

    public interface OnPreferenceClickListener {
        void onPreferenceClick(String key);
    }
}
