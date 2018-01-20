package me.shouheng.notepal.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.LoginActivity;
import me.shouheng.notepal.config.TextLength;
import me.shouheng.notepal.databinding.FragmentUserInfoBinding;
import me.shouheng.notepal.dialog.SimpleEditDialog;
import me.shouheng.notepal.model.enums.ModelType;
import me.shouheng.notepal.provider.helper.StatisticsHelper;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.util.UserUtil;

/**
 * Created by wangshouheng on 2017/8/11. */
public class UserInfoFragment extends CommonFragment<FragmentUserInfoBinding> {

    private final static int REQUEST_FOR_LOGIN = 50000;

    private boolean logined = false;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_user_info;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        configAccountViews();

        getBinding().llTimeline.setOnClickListener(v -> toTimeLine());

        getBinding().lcv.setValueSelectionEnabled(false);
        getBinding().lcv.setLineChartData(StatisticsHelper.getLineChartData(getContext(), ModelType.NOTE, primaryColor()));

        getBinding().llLogout.setOnClickListener(v -> logout());

        getBinding().rlHeader.setOnClickListener(v -> toStatistics());
    }

    private void configAccountViews() {
        logined = UserUtil.getInstance(getContext()).getUserIdKept() != 0;
        if (!logined) {
            getBinding().tvUserAccount.setText("");
            getBinding().tvUserAccount.setVisibility(View.GONE);
            getBinding().tvUserName.setText(R.string.not_login_click_to_login);
            getBinding().tvUserName.setTextColor(accentColor());
        }

        getBinding().llUser.setOnClickListener(v -> login());
        getBinding().llSchool.setOnClickListener(v -> showSchoolEditor());
        getBinding().llMajor.setOnClickListener(v -> showMajorEditor());
    }

    private void toTimeLine() {
        if (getActivity() != null && getActivity() instanceof OnItemSelectedListener) {
            ((OnItemSelectedListener) getActivity()).onTimelineSelected();
        }
    }

    private void toStatistics() {
        if (getActivity() != null && getActivity() instanceof OnItemSelectedListener) {
            ((OnItemSelectedListener) getActivity()).onChatHeaderSelected();
        }
    }

    private void login() {
        if (!logined) {
            LoginActivity.startForResult(this, REQUEST_FOR_LOGIN);
        }
    }

    private void logout() {}

    private void showSchoolEditor() {
        if (!logined) {
            ToastUtils.makeToast(getContext(), R.string.not_login_try_after_login);
            return;
        }
        String school = null;
        SimpleEditDialog.newInstance(school, content -> {})
                .setMaxLength(TextLength.SCHOOL_TEXT_LENGTH.length)
                .show(getFragmentManager(), "SCHOOL_EDITOR");
    }

    private void showMajorEditor() {
        if (!logined) {
            ToastUtils.makeToast(getContext(), R.string.not_login_try_after_login);
            return;
        }
        String major = null;
        SimpleEditDialog.newInstance(major, content -> {})
                .setMaxLength(TextLength.MAJOR_TEXT_LENGTH.length)
                .show(getFragmentManager(), "SCHOOL_EDITOR");
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.user_info);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_FOR_LOGIN:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public interface OnItemSelectedListener {
        void onTimelineSelected();
        void onChatHeaderSelected();
    }
}
