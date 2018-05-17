package me.shouheng.notepal.fragment.setting;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;

import com.afollestad.materialdialogs.MaterialDialog;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.base.CommonActivity;
import me.shouheng.notepal.activity.base.ThemedActivity;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.dialog.FeedbackDialog;
import me.shouheng.notepal.dialog.NoticeDialog;
import me.shouheng.notepal.intro.IntroActivity;
import me.shouheng.notepal.model.Feedback;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.util.IntentUtils;
import me.shouheng.notepal.widget.ColorPreference;

/**
 * Created by wang shouheng on 2017/12/21.*/
public class SettingsFragment extends BaseFragment {

    private CheckBoxPreference isDarkTheme, coloredNavigationBar;

    private ColorPreference primaryColor, accentColor;

    /**
     * Used to transfer click message to the activity. */
    private Preference.OnPreferenceClickListener listener = preference -> {
        if (getActivity() != null && getActivity() instanceof OnPreferenceClickListener) {
            ((OnPreferenceClickListener) getActivity()).onPreferenceClick(preference.getKey());
        }
        return true;
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        isDarkTheme = (CheckBoxPreference) findPreference(getString(R.string.key_is_dark_theme));
        primaryColor = (ColorPreference) findPreference(getString(R.string.key_primary_color));
        accentColor = (ColorPreference) findPreference(getString(R.string.key_accent_color));
        coloredNavigationBar = (CheckBoxPreference) findPreference(getString(R.string.key_is_colored_navigation_bar));
        primaryColor.setValue(ColorUtils.primaryColor(getActivity()));
        accentColor.setValue(ColorUtils.accentColor(getActivity()));

        setPreferenceClickListeners();
    }

    private void setPreferenceClickListeners() {
        isDarkTheme.setOnPreferenceClickListener(preference -> {
            updateThemeSettings();
            return true;
        });
        coloredNavigationBar.setOnPreferenceClickListener(preference -> {
            ((ThemedActivity) getActivity()).updateTheme();
            return true;
        });

        findPreference(R.string.key_feedback).setOnPreferenceClickListener(preference -> {
            showFeedbackEditor();
            return true;
        });
        findPreference(R.string.key_user_guide).setOnPreferenceClickListener(preference -> {
            IntentUtils.openWiki(getActivity());
            return true;
        });
        findPreference(R.string.key_user_intro).setOnPreferenceClickListener(preference -> {
            showIntroduction();
            return true;
        });
        findPreference(R.string.key_support_develop).setOnPreferenceClickListener(preference -> {
            NoticeDialog.newInstance().show(((CommonActivity) getActivity()).getSupportFragmentManager(), "Notice");
            return true;
        });

        primaryColor.setOnPreferenceClickListener(listener);
        accentColor.setOnPreferenceClickListener(listener);
        findPreference(R.string.key_preferences).setOnPreferenceClickListener(listener);
        findPreference(R.string.key_setup_dashboard).setOnPreferenceClickListener(listener);
        findPreference(R.string.key_data_backup).setOnPreferenceClickListener(listener);
        findPreference(R.string.key_data_security).setOnPreferenceClickListener(listener);
        findPreference(R.string.key_about).setOnPreferenceClickListener(listener);
        findPreference(R.string.key_key_note_settings).setOnPreferenceClickListener(listener);
    }

    private void showFeedbackEditor() {
        FeedbackDialog.newInstance(getActivity(), (dialog, feedback) -> sendFeedback(feedback))
                .show(((CommonActivity) getActivity()).getSupportFragmentManager(), "Feedback Editor");
    }

    private void sendFeedback(Feedback feedback) {
        String subject = String.format(Constants.DEVELOPER_EMAIL_PREFIX, feedback.getFeedbackType().name());
        String body = feedback.getQuestion() + Constants.DEVELOPER_EMAIL_EMAIL_PREFIX + feedback.getEmail();
        IntentUtils.sendEmail(getActivity(), subject, body);
    }

    private void showIntroduction() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.text_tips)
                .content(R.string.show_introduction_again)
                .positiveText(R.string.text_ok)
                .negativeText(R.string.text_cancel)
                .onPositive((materialDialog, dialogAction) -> IntroActivity.launch(getActivity()))
                .show();
    }

    private void updateThemeSettings() {
        ColorUtils.forceUpdateThemeStatus(getActivity());
        ((ThemedActivity) getActivity()).reUpdateTheme();
    }

    public void notifyAccentColorChanged(int accentColor) {
        this.accentColor.setValue(accentColor);
    }

    public void notifyPrimaryColorChanged(int primaryColor) {
        this.primaryColor.setValue(primaryColor);
    }

    public interface OnPreferenceClickListener {
        void onPreferenceClick(String key);
    }
}
