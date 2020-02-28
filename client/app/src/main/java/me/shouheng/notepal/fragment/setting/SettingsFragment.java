package me.shouheng.notepal.fragment.setting;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import me.shouheng.commons.activity.ContainerActivity;
import me.shouheng.commons.activity.ThemedActivity;
import me.shouheng.commons.event.PageName;
import me.shouheng.commons.event.*;
import me.shouheng.commons.fragment.BPreferenceFragment;
import me.shouheng.commons.fragment.WebviewFragment;
import me.shouheng.commons.helper.ActivityHelper;
import me.shouheng.commons.helper.DialogHelper;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.utils.PalmUtils;
import me.shouheng.commons.utils.PersistData;
import me.shouheng.notepal.Constants;
import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.AboutActivity;
import me.shouheng.notepal.activity.FabSortActivity;
import me.shouheng.notepal.activity.SettingsActivity;
import me.shouheng.notepal.dialog.ThemePickDialog;

/**
 * Created by WngShhng (shouheng2015@gmail.com) on 2017/12/21.
 */
@PageName(name = UMEvent.PAGE_SETTING)
public class SettingsFragment extends BPreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        if (getActivity() != null) {
            ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (ab != null) ab.setTitle(R.string.setting_title);
        }

        Preference theme = findPreference(R.string.key_setting_theme);
        theme.setOnPreferenceClickListener(preference -> {
            Activity activity = getActivity();
            if (activity instanceof AppCompatActivity) {
                DialogHelper.open(ThemePickDialog.class).show((AppCompatActivity) activity,"THEME_PICKER");
            }
            return true;
        });
        theme.setIcon(PalmUtils.getDrawableCompact(getThemeStyle().isDarkTheme ?
                R.drawable.ic_color_lens_white_24dp : R.drawable.ic_color_lens_black_24dp));

        findPreference(R.string.key_setting_custom_fab).setOnPreferenceClickListener(preference -> {
            ActivityHelper.start(getActivity(), FabSortActivity.class);
            return true;
        });

        findPreference(R.string.key_setting_nav_bar_result).setOnPreferenceChangeListener((preference, newValue) -> {
            PersistData.putBoolean(R.string.key_setting_nav_bar_result, (Boolean) newValue);
            Activity activity = getActivity();
            if (activity instanceof ThemedActivity) {
                ((ThemedActivity) activity).updateNavigationBar();
            }
            return true;
        });


        findPreference(R.string.key_setting_note).setOnPreferenceClickListener(preference -> {
            SettingsActivity.open(SettingsNote.class).launch(getActivity());
            return true;
        });

        findPreference(R.string.key_setting_security).setOnPreferenceClickListener(preference -> {
            SettingsActivity.open(SettingsSecurity.class).launch(getActivity());
            return true;
        });

        Preference backup = findPreference(R.string.key_setting_backup);
        backup.setOnPreferenceClickListener(preference -> {
            SettingsActivity.open(SettingsBackup.class).launch(getActivity());
            return true;
        });
        backup.setIcon(ColorUtils.tintDrawable(R.drawable.ic_wb_cloudy_black_24dp,
                getThemeStyle().isDarkTheme ? Color.WHITE : Color.BLACK));

        findPreference(R.string.key_setting_guide).setOnPreferenceClickListener(preference -> {
            ContainerActivity.open(WebviewFragment.class)
                    .put(WebviewFragment.ARGUMENT_KEY_TITLE, PalmUtils.getStringCompact(R.string.setting_category_help_user_guide))
                    .put(WebviewFragment.ARGUMENT_KEY_URL, Constants.PAGE_GUIDE)
                    .launch(getActivity());
            return true;
        });

        findPreference(R.string.key_setting_feedback).setOnPreferenceClickListener(preference -> {
            boolean isEn = "en".equals(PalmUtils.getStringCompact(R.string.language_code));
            ContainerActivity.open(WebviewFragment.class)
                    .put(WebviewFragment.ARGUMENT_KEY_TITLE, PalmUtils.getStringCompact(R.string.setting_category_help_feedback))
                    .put(WebviewFragment.ARGUMENT_KEY_URL, isEn ? Constants.PAGE_FEEDBACK_ENGLISH : Constants.PAGE_FEEDBACK_CHINESE)
                    .launch(getActivity());
            return true;
        });

        findPreference(R.string.key_setting_translate).setOnPreferenceClickListener(preference -> {
            ContainerActivity.open(WebviewFragment.class)
                    .put(WebviewFragment.ARGUMENT_KEY_TITLE, PalmUtils.getStringCompact(R.string.setting_category_help_translate))
                    .put(WebviewFragment.ARGUMENT_KEY_URL, Constants.PAGE_TRANSLATE)
                    .launch(getActivity());
            return true;
        });


        findPreference(R.string.key_setting_about).setOnPreferenceClickListener(preference -> {
            ActivityHelper.open(AboutActivity.class)
                    .put(AboutActivity.APP_ABOUT_ARG_OPEN_SOURCE_ONLY, false)
                    .launch(getActivity());
            return true;
        });

        findPreference(R.string.key_setting_privacy).setOnPreferenceClickListener(preference -> {
            ContainerActivity.open(WebviewFragment.class)
                    .put(WebviewFragment.ARGUMENT_KEY_TITLE, PalmUtils.getStringCompact(R.string.setting_category_others_about_privacy))
                    .put(WebviewFragment.ARGUMENT_KEY_URL, Constants.PAGE_PRIVACY)
                    .launch(getActivity());
            return true;
        });

        findPreference(R.string.key_setting_open_source).setOnPreferenceClickListener(preference -> {
            ActivityHelper.open(AboutActivity.class)
                    .put(AboutActivity.APP_ABOUT_ARG_OPEN_SOURCE_ONLY, true)
                    .launch(getActivity());
            return true;
        });
    }
}
