package me.shouheng.notepal.fragment.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import me.shouheng.commons.helper.ActivityHelper;
import me.shouheng.commons.activity.ContainerActivity;
import me.shouheng.commons.activity.ThemedActivity;
import me.shouheng.commons.fragment.BPreferenceFragment;
import me.shouheng.commons.helper.DialogHelper;
import me.shouheng.commons.fragment.WebviewFragment;
import me.shouheng.commons.utils.PersistData;
import me.shouheng.commons.utils.IntentUtils;
import me.shouheng.commons.utils.PalmUtils;
import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.AboutActivity;
import me.shouheng.notepal.activity.FabSortActivity;
import me.shouheng.notepal.activity.LockActivity;
import me.shouheng.notepal.activity.MenuSortActivity;
import me.shouheng.notepal.activity.SettingsActivity;
import me.shouheng.notepal.Constants;
import me.shouheng.notepal.dialog.ThemePickDialog;
import me.shouheng.notepal.util.preferences.LockPreferences;

/**
 * Created by wang shouheng on 2017/12/21.*/
public class SettingsFragment extends BPreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        // config toolbar
        if (getActivity() != null) {
            ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (ab != null) ab.setTitle(R.string.setting_title);
        }

        // region preferences : theme
        Preference theme = findPreference(R.string.key_setting_theme);
        theme.setOnPreferenceClickListener(preference -> {
            Activity activity = getActivity();
            if (activity instanceof AppCompatActivity) {
                DialogHelper.open(ThemePickDialog.class).show((AppCompatActivity) activity,"THEME_PICKER");
            }
            return true;
        });

        findPreference(R.string.key_custom_fab).setOnPreferenceClickListener(preference -> {
            ActivityHelper.start(getActivity(), FabSortActivity.class);
            return true;
        });

        findPreference(R.string.key_note_editor_menu_sort).setOnPreferenceClickListener(preference -> {
            ActivityHelper.start(getActivity(), MenuSortActivity.class);
            return true;
        });

        findPreference(R.string.key_setting_theme_color_nav_bar).setOnPreferenceChangeListener((preference, newValue) -> {
            PersistData.putBoolean(R.string.key_setting_theme_color_nav_bar, (Boolean) newValue);
            Activity activity = getActivity();
            if (activity instanceof ThemedActivity) {
                ((ThemedActivity) activity).updateNavigationBar();
            }
            return true;
        });
        // endregion theme preferences

        // region preferences : universal
        findPreference(R.string.key_key_note_settings).setOnPreferenceClickListener(preference -> {
            SettingsActivity.open(SettingsNote.class).launch(getActivity());
            return true;
        });

        findPreference(R.string.key_data_security).setOnPreferenceClickListener(preference -> {
            if (LockPreferences.getInstance().isPasswordRequired()
                    && !TextUtils.isEmpty(LockPreferences.getInstance().getPassword())) {
                LockActivity.requirePassword(SettingsFragment.this, 1);
            } else {
                SettingsActivity.open(SettingsSecurity.class).launch(getActivity());
            }
            return true;
        });

        findPreference(R.string.key_data_backup).setOnPreferenceClickListener(preference -> {
            SettingsActivity.open(SettingsBackup.class).launch(getActivity());
            return true;
        });
        // endregion universal

        // region preferences : help & feedback
        findPreference(R.string.key_user_guide).setOnPreferenceClickListener(preference -> {
            ContainerActivity.open(WebviewFragment.class)
                    .put(WebviewFragment.ARGUMENT_KEY_TITLE, PalmUtils.getStringCompact(R.string.setting_help_guide))
                    .put(WebviewFragment.ARGUMENT_KEY_URL, Constants.GUIDE_PAGE)
                    .launch(getActivity());
            return true;
        });

        findPreference(R.string.key_feedback).setOnPreferenceClickListener(preference -> {
            boolean isEn = "en".equals(PalmUtils.getStringCompact(R.string.language_code));
            ContainerActivity.open(WebviewFragment.class)
                    .put(WebviewFragment.ARGUMENT_KEY_TITLE, PalmUtils.getStringCompact(R.string.feedback))
                    .put(WebviewFragment.ARGUMENT_KEY_URL, isEn ? Constants.FEEDBACK_ENGLISH : Constants.FEEDBACK_CHINESE)
                    .launch(getActivity());
            return true;
        });

        findPreference(R.string.key_setting_item_help_translate).setOnPreferenceClickListener(preference -> {
            IntentUtils.sendEmail(getActivity(), Constants.DEVELOPER_EMAIL, "TRANSLATE", "");
            return true;
        });
        // endregion

        // region preferences : about
        findPreference(R.string.key_about).setOnPreferenceClickListener(preference -> {
            ActivityHelper.open(AboutActivity.class)
                    .put(AboutActivity.APP_ABOUT_ARG_OPEN_SOURCE_ONLY, false)
                    .launch(getActivity());
            return true;
        });

        findPreference(R.string.key_setting_item_about_privacy).setOnPreferenceClickListener(preference -> {
            ContainerActivity.open(WebviewFragment.class)
                    .put(WebviewFragment.ARGUMENT_KEY_TITLE, PalmUtils.getStringCompact(R.string.setting_about_privacy))
                    .put(WebviewFragment.ARGUMENT_KEY_URL, Constants.PRIVACY_PAGE)
                    .launch(getActivity());
            return true;
        });

        findPreference(R.string.key_setting_item_about_open_source).setOnPreferenceClickListener(preference -> {
            ActivityHelper.open(AboutActivity.class)
                    .put(AboutActivity.APP_ABOUT_ARG_OPEN_SOURCE_ONLY, true)
                    .launch(getActivity());
            return true;
        });
        // endregion
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    SettingsActivity.open(SettingsSecurity.class).launch(getActivity());
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected String umengPageName() {
        return "Settings preferences";
    }
}
