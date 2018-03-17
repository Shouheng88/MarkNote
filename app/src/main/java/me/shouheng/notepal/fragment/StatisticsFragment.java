package me.shouheng.notepal.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.FragmentStatisticsBinding;
import me.shouheng.notepal.fragment.base.BaseFragment;
import me.shouheng.notepal.model.Stats;
import me.shouheng.notepal.model.data.Status;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.viewmodel.StatisticViewModel;

/**
 * Created by wang shouheng on 2018/1/19. */
public class StatisticsFragment extends BaseFragment<FragmentStatisticsBinding> {

    private StatisticViewModel statisticViewModel;

    private int primaryColor;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_statistics;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        configValues();

        configToolbar();

        showDefaultValues();

        outputStats();
    }

    private void configValues() {
        statisticViewModel = ViewModelProviders.of(this).get(StatisticViewModel.class);
        primaryColor = primaryColor();
    }

    private void configToolbar() {
        if (getActivity() != null) {
            ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (ab != null) ab.setTitle(R.string.statistic);
        }
    }

    private void showDefaultValues() {
        getBinding().lcvNote.setValueSelectionEnabled(false);
        getBinding().lcvNote.setLineChartData(statisticViewModel.getDefaultNoteData(primaryColor));

        getBinding().ccvModels.setColumnChartData(statisticViewModel.getDefaultModelsData());

        getBinding().ccvAttachment.setColumnChartData(statisticViewModel.getDefaultAttachmentData());
    }

    private void outputStats() {
        if (getActivity() instanceof OnStatisticInteractListener) {
            ((OnStatisticInteractListener) getActivity()).onStatisticLoadStateChanged(Status.LOADING);
        }
        statisticViewModel.getStats().observe(this, statsResource -> {
            LogUtils.d(statsResource);
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
        outputNotesStats(stats.getNotesStats());

        outputModelsStats(Arrays.asList(
                stats.getTotalNotes(),
                stats.getTotalNotebooks(),
                stats.getTotalMinds(),
                stats.getTotalAttachments(),
                stats.getTotalLocations()));

        outputAttachmentStats(Arrays.asList(
                stats.getFiles(),
                stats.getImages(),
                stats.getSketches(),
                stats.getVideos(),
                stats.getAudioRecordings()));
    }

    private void outputNotesStats(List<Integer> notes) {
        for (Line line : getBinding().lcvNote.getLineChartData().getLines()) {
            int length = line.getValues().size();
            PointValue pointValue;
            for (int i=0; i<length; i++) {
                pointValue = line.getValues().get(i);
                pointValue.setTarget(pointValue.getX(), notes.get(i));
            }
        }
        getBinding().lcvNote.startDataAnimation();
    }

    private void outputModelsStats(List<Integer> addedModels) {
        int i = 0;
        for (Column column : getBinding().ccvModels.getChartData().getColumns()) {
            for (SubcolumnValue subcolumnValue : column.getValues()) {
                subcolumnValue.setTarget(addedModels.get(i));
            }
            i++;
        }
        getBinding().ccvModels.startDataAnimation();
    }

    private void outputAttachmentStats(List<Integer> attachments) {
        int i = 0;
        for (Column column : getBinding().ccvAttachment.getChartData().getColumns()) {
            for (SubcolumnValue subcolumnValue : column.getValues()) {
                subcolumnValue.setTarget(attachments.get(i));
            }
            i++;
        }
        getBinding().ccvAttachment.startDataAnimation();
    }

    public interface OnStatisticInteractListener {
        void onStatisticLoadStateChanged(Status status);
    }
}
