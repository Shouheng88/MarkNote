package me.shouheng.notepal.fragment;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.StackingBehavior;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.CommonActivity;
import me.shouheng.notepal.activity.ThemedActivity;
import me.shouheng.notepal.dialog.DonateDialog;
import me.shouheng.notepal.dialog.DonateDialog.DonateChannel;
import me.shouheng.notepal.dialog.FeedbackDialog;
import me.shouheng.notepal.intro.IntroActivity;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.util.LogUtils;
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
    public final static String KEY_ABOUT = "about";
    public final static String KEY_DATA_BACKUP = "data_backup";
    public final static String KEY_DATA_SECURITY = "data_security";
    private final static String KEY_SUPPORT_DEVELOP = "support_develop";

    private CheckBoxPreference isDarkTheme, coloredNavigationBar;

    private ColorPreference primaryColor, accentColor, noteColor, notebookColor;

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
                ((OnPreferenceClickListener) getActivity()).onPreferenceClick(PreferencesUtils.ACCENT_COLOR);
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

        findPreference(KEY_FEEDBACK).setOnPreferenceClickListener(preference -> {
            FeedbackDialog.newInstance(getActivity(), (dialog, feedback) ->
                    LogUtils.d("onSend: " + feedback)
            ).show(((CommonActivity) getActivity()).getSupportFragmentManager(), "Feedback Editor");
            return true;
        });
        findPreference(KEY_USER_GUIDE).setOnPreferenceClickListener(preference -> {
            return true;
        });
        findPreference(KEY_USER_INTRO).setOnPreferenceClickListener(preference -> {
            showIntroduction();
            return true;
        });

        findPreference(KEY_DATA_BACKUP).setOnPreferenceClickListener(preference -> {
            if (getActivity() != null && getActivity() instanceof OnPreferenceClickListener) {
                ((OnPreferenceClickListener) getActivity()).onPreferenceClick(KEY_DATA_BACKUP);
            }
            return true;
        });
        findPreference(KEY_DATA_SECURITY).setOnPreferenceClickListener(preference -> {
            if (getActivity() != null && getActivity() instanceof OnPreferenceClickListener) {
                ((OnPreferenceClickListener) getActivity()).onPreferenceClick(KEY_DATA_SECURITY);
            }
            return true;
        });

        findPreference(KEY_SUPPORT_DEVELOP).setOnPreferenceClickListener(preference -> {
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
            return true;
        });
        findPreference(KEY_ABOUT).setOnPreferenceClickListener(preference -> {
            if (getActivity() != null && getActivity() instanceof OnPreferenceClickListener) {
                ((OnPreferenceClickListener) getActivity()).onPreferenceClick(KEY_ABOUT);
            }
            return true;
        });
    }

    private void showDonateDialog(DonateChannel donateChannel) {
        DonateDialog.newInstance(donateChannel).show(((CommonActivity) getActivity()).getSupportFragmentManager(), "Donate Dialog");
    }

    private void showIntroduction() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.text_tips)
                .content(R.string.show_introduction_again)
                .positiveText(R.string.text_confirm)
                .negativeText(R.string.text_cancel)
                .onPositive((materialDialog, dialogAction) -> {
                    IntroActivity.launch(getActivity());
                })
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

    public void notifyNoteColorChanged(int noteColor) {
        this.noteColor.setValue(noteColor);
    }

    public void notifyNotebookColorChanged(int notebookColor) {
        this.notebookColor.setValue(notebookColor);
    }

    public interface OnPreferenceClickListener {
        void onPreferenceClick(String key);
    }
}
