package me.shouheng.notepal.fragment.setting;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.StackingBehavior;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.base.CommonActivity;
import me.shouheng.notepal.activity.base.ThemedActivity;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.dialog.DonateDialog;
import me.shouheng.notepal.dialog.DonateDialog.DonateChannel;
import me.shouheng.notepal.dialog.FeedbackDialog;
import me.shouheng.notepal.intro.IntroActivity;
import me.shouheng.notepal.model.Feedback;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.util.IntentUtils;
import me.shouheng.notepal.util.PreferencesUtils;
import me.shouheng.notepal.widget.ColorPreference;

/**
 * bug report -> shouheng2015@gmail.com
 * requirement, latest news -> Twitter, Google+, Weibo
 * donate -> WeiChat, AliPay
 *
 * Created by wang shouheng on 2017/12/21.*/
public class SettingsFragment extends PreferenceFragment {

    private final static String KEY_FEEDBACK = "feedback";
    private final static String KEY_USER_GUIDE = "user_guide";
    private final static String KEY_USER_INTRO = "user_intro";
    private final static String KEY_SUPPORT_DEVELOP = "support_develop";

    public final static String KEY_NOTE_SETTINGS = "key_note_settings";
    public final static String KEY_SETUP_DASHBOARD = "setup_dashboard";
    public final static String KEY_ABOUT = "about";
    public final static String KEY_DATA_BACKUP = "data_backup";
    public final static String KEY_DATA_SECURITY = "data_security";

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

        isDarkTheme = (CheckBoxPreference) findPreference(PreferencesUtils.IS_DARK_THEME);
        primaryColor = (ColorPreference) findPreference(PreferencesUtils.PRIMARY_COLOR);
        accentColor = (ColorPreference) findPreference(PreferencesUtils.ACCENT_COLOR);
        coloredNavigationBar = (CheckBoxPreference) findPreference(PreferencesUtils.COLORED_NAVIGATION_BAR);
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

        findPreference(KEY_FEEDBACK).setOnPreferenceClickListener(preference -> {
            showFeedbackEditor();
            return true;
        });
        findPreference(KEY_USER_GUIDE).setOnPreferenceClickListener(preference -> {
            return true;
        });
        findPreference(KEY_USER_INTRO).setOnPreferenceClickListener(preference -> {
            showIntroduction();
            return true;
        });
        findPreference(KEY_SUPPORT_DEVELOP).setOnPreferenceClickListener(preference -> {
            showSupport();
            return true;
        });

        primaryColor.setOnPreferenceClickListener(listener);
        accentColor.setOnPreferenceClickListener(listener);
        findPreference(KEY_SETUP_DASHBOARD).setOnPreferenceClickListener(listener);
        findPreference(KEY_DATA_BACKUP).setOnPreferenceClickListener(listener);
        findPreference(KEY_DATA_SECURITY).setOnPreferenceClickListener(listener);
        findPreference(KEY_ABOUT).setOnPreferenceClickListener(listener);
        findPreference(KEY_NOTE_SETTINGS).setOnPreferenceClickListener(listener);
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

    private void showDonateDialog(DonateChannel donateChannel) {
        DonateDialog.newInstance(donateChannel)
                .show(((CommonActivity) getActivity()).getSupportFragmentManager(), "Donate Dialog");
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

    private void showSupport() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.setting_support_development)
                .content(R.string.support_development_content)
                .positiveText(R.string.alipay)
                .onPositive((dialog, which) -> showDonateDialog(DonateChannel.AliPay))
                .negativeText(R.string.wechat)
                .onNegative((dialog, which) -> showDonateDialog(DonateChannel.WeChat))
                .neutralText(R.string.next_time)
                .stackingBehavior(StackingBehavior.ADAPTIVE)
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
