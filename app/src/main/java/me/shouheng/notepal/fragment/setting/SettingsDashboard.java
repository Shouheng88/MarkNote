package me.shouheng.notepal.fragment.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import org.polaric.colorful.BaseActivity;
import org.polaric.colorful.PermissionUtils;

import me.shouheng.notepal.R;
import me.shouheng.notepal.config.TextLength;
import me.shouheng.notepal.databinding.DialogDrawerBgOptionsBinding;
import me.shouheng.notepal.dialog.SimpleEditDialog;
import me.shouheng.notepal.listener.OnAttachingFileListener;
import me.shouheng.notepal.listener.OnSettingsChangedListener;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.util.AttachmentHelper;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.util.PreferencesUtils;
import me.shouheng.notepal.util.ToastUtils;

/**
 * Created by shouh on 2018/3/18. */
public class SettingsDashboard extends PreferenceFragment implements
        OnAttachingFileListener {
    private final static String KEY_USER_INFO_BG_VISIBLE = "key_user_info_bg_visible";
    private final static String KEY_USER_INFO_BG = "key_user_info_bg";
    private final static String KEY_USER_INFO_BG_MOTTO = "key_user_info_motto";

    private PreferencesUtils preferencesUtils;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferencesUtils = PreferencesUtils.getInstance(getActivity());

        configToolbar();

        addPreferencesFromResource(R.xml.preferences_dashboard_personalize);

        setPreferenceClickListeners();
    }

    private void configToolbar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) actionBar.setTitle(R.string.setting_personalize_dashboard);
    }

    private void setPreferenceClickListeners() {
        findPreference(KEY_USER_INFO_BG_VISIBLE).setOnPreferenceClickListener(preference -> {
            notifyDashboardChanged();
            return true;
        });
        findPreference(KEY_USER_INFO_BG).setOnPreferenceClickListener(preference -> {
            showBgOptions();
            return true;
        });
        findPreference(KEY_USER_INFO_BG_MOTTO).setOnPreferenceClickListener(preference -> {
            showMottoEditor();
            return true;
        });
    }

    private void showMottoEditor() {
        SimpleEditDialog.newInstance(preferencesUtils.getUserMotto(), content -> {
            notifyDashboardChanged();
            preferencesUtils.setUserMotto(content);
        }).setMaxLength(TextLength.MOTTO_TEXT_LENGTH.length)
                .show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "MOTTO_EDITOR");
    }

    private void showBgOptions() {
        DialogDrawerBgOptionsBinding bgOptionsBinding = DataBindingUtil.inflate(getActivity().getLayoutInflater(),
                R.layout.dialog_drawer_bg_options, null, false);

        Dialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.setting_dashboard_user_info_bg)
                .setView(bgOptionsBinding.getRoot())
                .create();
        dialog.show();

        bgOptionsBinding.civBg1.setFillingCircleColor(
                preferencesUtils.getUserInfoBG() == null ? ColorUtils.accentColor(getActivity())
                        : Color.parseColor("#cccccc"));

        bgOptionsBinding.rlBg1.setOnClickListener(view -> {
            preferencesUtils.setUserInfoBG(null);
            notifyDashboardChanged();
            dialog.dismiss();
        });
        bgOptionsBinding.tvPick.setOnClickListener(view -> {
            PermissionUtils.checkStoragePermission((BaseActivity) getActivity(),
                    () -> AttachmentHelper.pickFromAlbum(SettingsDashboard.this));
            dialog.dismiss();
        });
        bgOptionsBinding.tvPick.setTextColor(ColorUtils.accentColor(getActivity()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            AttachmentHelper.resolveResult(SettingsDashboard.this, requestCode, data, this::onGetBackgroundImage);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onGetBackgroundImage(@NonNull Attachment attachment) {
        preferencesUtils.setUserInfoBG(attachment.getUri());
        notifyDashboardChanged();
    }

    private void notifyDashboardChanged() {
        if (getActivity() != null && getActivity() instanceof OnSettingsChangedListener) {
            ((OnSettingsChangedListener) getActivity()).onDashboardSettingChanged(OnSettingsChangedListener.ChangedType.DRAWER_CONTENT);
        }
    }

    @Override
    public void onAttachingFileErrorOccurred(Attachment attachment) {
        ToastUtils.makeToast(R.string.failed_to_save_attachment);
    }

    @Override
    public void onAttachingFileFinished(Attachment attachment) {
        onGetBackgroundImage(attachment);
    }
}
