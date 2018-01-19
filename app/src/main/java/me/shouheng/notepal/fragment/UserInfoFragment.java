package me.shouheng.notepal.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.LoginActivity;
import me.shouheng.notepal.config.TextLength;
import me.shouheng.notepal.databinding.FragmentUserInfoBinding;
import me.shouheng.notepal.dialog.SimpleEditDialog;
import me.shouheng.notepal.model.enums.ModelType;
import me.shouheng.notepal.provider.helper.StatisticsHelper;
import me.shouheng.notepal.util.LogUtils;
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

        getBinding().lcv.setLineChartData(getLineChartData());

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

    private LineChartData getLineChartData() {
        Calendar sevenDaysAgo = Calendar.getInstance();
        sevenDaysAgo.set(Calendar.HOUR_OF_DAY, 0);
        sevenDaysAgo.set(Calendar.MINUTE, 0);
        sevenDaysAgo.set(Calendar.SECOND, 0);
        sevenDaysAgo.set(Calendar.MILLISECOND, 0);
        sevenDaysAgo.add(Calendar.DAY_OF_YEAR, -6);
        List<String> days = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        for (int i=0; i<7; i++){
            days.add(sdf.format(sevenDaysAgo.getTime()));
            sevenDaysAgo.add(Calendar.DAY_OF_YEAR, 1);
        }

        LineChartData data = new LineChartData();
        data.setLines(Arrays.asList(getLine(ModelType.NOTE)));
        data.setAxisXBottom(null);
        data.setAxisYLeft(null);
        data.setBaseValue(0);
        data.setValueLabelBackgroundColor(Color.TRANSPARENT);
        Axis axis = Axis.generateAxisFromCollection(Arrays.asList(0.0f, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f), days);
        data.setAxisXBottom(axis);
        return data;
    }

    private Line getLine(ModelType modelType) {
        List<Integer> lineStatistics = StatisticsHelper.getAddedStatistics(getContext(), modelType);
        List<PointValue> values = new LinkedList<>();
        int length = lineStatistics.size();
        for (int j = 0; j < length; ++j) {
            values.add(new PointValue(j, lineStatistics.get(j)));
        }
        LogUtils.d("getLineChartData: " + lineStatistics);

        Line line = new Line(values);
        line.setColor(accentColor());
        line.setShape(ValueShape.CIRCLE);
        line.setCubic(false);
        line.setFilled(true);
        line.setHasLabels(true);
        line.setHasLines(true);
        line.setHasPoints(true);
        line.setPointRadius(3);

        return line;
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
