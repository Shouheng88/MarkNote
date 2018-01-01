package me.shouheng.notepal.fragment;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.ThemedActivity;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.util.PreferencesUtils;
import me.shouheng.notepal.widget.ColorPreference;

/**
 * Created by wang shouheng on 2017/12/21.*/
public class SettingsFragment extends PreferenceFragment {

    private CheckBoxPreference isDarkTheme, coloredNavigationBar;

    private ColorPreference primaryColor, accentColor, noteColor, notebookColor;

    private PreferencesUtils preferencesUtils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferencesUtils = PreferencesUtils.getInstance(getActivity());

        addPreferencesFromResource(R.xml.preferences);

        isDarkTheme = (CheckBoxPreference) findPreference(PreferencesUtils.IS_DARK_THEME);
        primaryColor = (ColorPreference) findPreference(PreferencesUtils.PRIMARY_COLOR);
        accentColor = (ColorPreference) findPreference(PreferencesUtils.ACCENT_COLOR);
        coloredNavigationBar = (CheckBoxPreference) findPreference(PreferencesUtils.COLORED_NAVIGATION_BAR);

        noteColor = (ColorPreference) findPreference(PreferencesUtils.DEFAULT_NOTE_COLOR);
        notebookColor = (ColorPreference) findPreference(PreferencesUtils.DEFAULT_NOTEBOOK_COLOR);

        setPreferenceClickListeners();
    }

    private void setPreferenceClickListeners() {
        isDarkTheme.setOnPreferenceClickListener(preference -> {
            updateThemeSettings();
            return true;
        });
        primaryColor.setOnPreferenceClickListener(preference -> {
            if (getActivity() != null && getActivity() instanceof OnPreferenceClickListener) {
                ((OnPreferenceClickListener) getActivity()).onPreferenceClick(PreferencesUtils.PRIMARY_COLOR);
            }
            return true;
        });
        accentColor.setOnPreferenceClickListener(preference -> {
            if (getActivity() != null && getActivity() instanceof OnPreferenceClickListener) {
                ((OnPreferenceClickListener) getActivity()).onPreferenceClick(PreferencesUtils.DEFAULT_NOTEBOOK_COLOR);
            }
            return true;
        });
        coloredNavigationBar.setOnPreferenceClickListener(preference -> {
            ((ThemedActivity) getActivity()).updateTheme();
            return true;
        });

        notebookColor.setOnPreferenceClickListener(preference -> {
            if (getActivity() != null && getActivity() instanceof OnPreferenceClickListener) {
                ((OnPreferenceClickListener) getActivity()).onPreferenceClick(PreferencesUtils.DEFAULT_NOTEBOOK_COLOR);
            }
            return true;
        });
        noteColor.setOnPreferenceClickListener(preference -> {
            if (getActivity() != null && getActivity() instanceof OnPreferenceClickListener) {
                ((OnPreferenceClickListener) getActivity()).onPreferenceClick(PreferencesUtils.DEFAULT_NOTE_COLOR);
            }
            return true;
        });
    }

    private void updateThemeSettings() {
        ColorUtils.forceUpdateThemeStatus(getActivity());
        ((ThemedActivity) getActivity()).reUpdateTheme();
    }

    public interface OnPreferenceClickListener {
        void onPreferenceClick(String key);
    }
}
