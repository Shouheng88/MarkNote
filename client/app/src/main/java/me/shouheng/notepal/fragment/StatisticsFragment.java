package me.shouheng.notepal.fragment;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import me.shouheng.data.model.Stats;
import me.shouheng.mvvm.base.anno.FragmentConfiguration;
import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.FragmentStatisticsBinding;
import me.shouheng.notepal.vm.StatisticViewModel;
import me.shouheng.utils.ui.ToastUtils;

/**
 * The statistics fragment used to show the user statistics.
 *
 * Created by WngShhng (shouheng2015@gmail.com) on 2018/1/19.
 */
@FragmentConfiguration(layoutResId = R.layout.fragment_statistics)
public class StatisticsFragment extends BaseFragment<FragmentStatisticsBinding, StatisticViewModel> {

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        if (getActivity() != null) {
            ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (ab != null) ab.setTitle(R.string.drawer_menu_statistics);
        }

        getBinding().lcvNote.setValueSelectionEnabled(false);
        getBinding().lcvNote.setLineChartData(getVM().getDefaultNoteData(accentColor()));
        getBinding().ccvModels.setColumnChartData(getVM().getDefaultModelsData());
        getBinding().ccvAttachment.setColumnChartData(getVM().getDefaultAttachmentData());

        addSubscription();

        getVM().getStats();
    }

    private void addSubscription() {
        getVM().getStatsLiveData().observe(this, resources -> {
            assert resources != null;
            switch (resources.status) {
                case SUCCESS:
                    assert resources.data != null;
                    outputStats(resources.data);
                    break;
                case FAILED:
                    ToastUtils.showShort(R.string.text_failed);
                    break;
            }
        });
    }

    private void outputStats(Stats stats) {
        outputNotesStats(stats.getNotesStats());

        outputModelsStats(Arrays.asList(
                stats.getTotalNotes(),
                stats.getTotalNotebooks(),
                stats.getTotalCategories(),
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
}
