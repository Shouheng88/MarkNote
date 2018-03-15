package me.shouheng.notepal.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.FragmentStatisticsBinding;
import me.shouheng.notepal.fragment.base.BaseFragment;
import me.shouheng.notepal.model.Stats;
import me.shouheng.notepal.model.data.Status;
import me.shouheng.notepal.provider.helper.StatisticsHelper;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.viewmodel.StatisticViewModel;

/**
 * Created by wang shouheng on 2018/1/19. */
public class StatisticsFragment extends BaseFragment<FragmentStatisticsBinding> {

    private StatisticViewModel statisticViewModel;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_statistics;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        statisticViewModel = ViewModelProviders.of(this).get(StatisticViewModel.class);

        configToolbar();

        showDefaultValues();

        outputStats();
    }

    private void configToolbar() {
        if (getActivity() != null) {
            ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (ab != null) ab.setTitle(R.string.statistic);
        }
    }

    private void showDefaultValues() {}

    private void outputStats() {
        if (getActivity() instanceof OnStatisticInteractListener) {
            ((OnStatisticInteractListener) getActivity()).onStatisticLoadStateChanged(Status.LOADING);
        }
        statisticViewModel.getStats().observe(this, statsResource -> {
            if (statsResource == null) {
                ToastUtils.makeToast(R.string.text_failed_to_load_data);
                return;
            }
            if (getActivity() instanceof OnStatisticInteractListener) {
                ((OnStatisticInteractListener) getActivity()).onStatisticLoadStateChanged(statsResource.status);
            }
            switch (statsResource.status) {
                case SUCCESS:
                    outputStats(statsResource.data);
                    break;
                case LOADING:
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed_to_load_data);
                    break;
            }
        });
    }

    private void outputStats(Stats stats) {
        getBinding().ccvModels.setColumnChartData(StatisticsHelper.getModelsData(getContext(), stats));

        getBinding().lcvNote.setValueSelectionEnabled(false);
        getBinding().lcvNote.setLineChartData(StatisticsHelper.getLineChartData(getContext(), stats));

        getBinding().ccvAttachment.setColumnChartData(StatisticsHelper.getAttachmentsData(getContext(), stats));
    }

    public interface OnStatisticInteractListener {
        void onStatisticLoadStateChanged(Status status);
    }
}
