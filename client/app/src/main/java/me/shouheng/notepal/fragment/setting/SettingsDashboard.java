package me.shouheng.notepal.fragment.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.polaric.colorful.BaseActivity;
import org.polaric.colorful.PermissionUtils;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.config.TextLength;
import me.shouheng.notepal.databinding.DialogDrawerBgOptionsBinding;
import me.shouheng.notepal.dialog.SimpleEditDialog;
import me.shouheng.notepal.listener.OnAttachingFileListener;
import me.shouheng.notepal.listener.OnFragmentDestroyListener;
import me.shouheng.notepal.listener.SettingChangeType;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.util.AttachmentHelper;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.util.preferences.DashboardPreferences;

/**
 * Created by shouh on 2018/3/18. */
public class SettingsDashboard extends BaseFragment implements OnAttachingFileListener {

    private DashboardPreferences dashboardPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dashboardPreferences = DashboardPreferences.getInstance();

        configToolbar();

        addPreferencesFromResource(R.xml.preferences_dashboard_personalize);

        setPreferenceClickListeners();
    }

    private void configToolbar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) actionBar.setTitle(R.string.setting_personalize_dashboard);
    }

    private void setPreferenceClickListeners() {
        findPreference(R.string.key_user_info_bg_visible).setOnPreferenceClickListener(preference -> {
            notifyDashboardChanged(SettingChangeType.DRAWER);
            return true;
        });
        findPreference(R.string.key_user_info_bg).setOnPreferenceClickListener(preference -> {
            showBgOptions();
            return true;
        });
        findPreference(R.string.key_user_info_motto).setOnPreferenceClickListener(preference -> {
            showMottoEditor();
            return true;
        });
    }

    private void showMottoEditor() {
        SimpleEditDialog.newInstance(dashboardPreferences.getUserMotto(), content -> {
            notifyDashboardChanged(SettingChangeType.DRAWER);
            dashboardPreferences.setUserMotto(content);
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
                Constants.DEFAULT_USER_INFO_BG.equals(dashboardPreferences.getUserInfoBG().toString()) ?
                        ColorUtils.accentColor(getActivity()) : Color.parseColor("#cccccc"));

        Glide.with(PalmApp.getContext())
                .load(Constants.DEFAULT_USER_INFO_BG)
                .centerCrop()
                .crossFade()
                .into(bgOptionsBinding.civDefault);

        bgOptionsBinding.rlBg1.setOnClickListener(view -> {
            dashboardPreferences.setUserInfoBG(null);
            notifyDashboardChanged(SettingChangeType.DRAWER);
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
            AttachmentHelper.resolveResult(SettingsDashboard.this, requestCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onGetBackgroundImage(@NonNull Attachment attachment) {
        dashboardPreferences.setUserInfoBG(attachment.getUri());
        notifyDashboardChanged(SettingChangeType.DRAWER);
    }

    @Override
    public void onAttachingFileErrorOccurred(Attachment attachment) {
        ToastUtils.makeToast(R.string.failed_to_save_attachment);
    }

    @Override
    public void onAttachingFileFinished(Attachment attachment) {
        onGetBackgroundImage(attachment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() instanceof OnFragmentDestroyListener) {
            ((OnFragmentDestroyListener) getActivity()).onFragmentDestroy();
        }
    }
}
