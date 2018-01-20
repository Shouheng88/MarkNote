package me.shouheng.notepal.fragment;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;
import java.util.Arrays;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.FragmentStatisticsBinding;
import me.shouheng.notepal.model.Stats;
import me.shouheng.notepal.provider.helper.StatisticsHelper;
import me.shouheng.notepal.util.LogUtils;

/**
 * Created by wang shouheng on 2018/1/19. */
public class StatisticsFragment extends BaseFragment<FragmentStatisticsBinding> {

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_statistics;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        configToolbar();

        new StatsTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void configToolbar() {
        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setTitle(R.string.statistic);
    }

    private void outputStats(Stats stats) {
        LogUtils.d(stats);

        getBinding().lcvNote.setValueSelectionEnabled(false);
        getBinding().lcvNote.setLineChartData(StatisticsHelper.getLineChartData(stats.getNotesStats(), primaryColor()));

        getBinding().lcvMinds.setValueSelectionEnabled(false);
        getBinding().lcvMinds.setLineChartData(StatisticsHelper.getLineChartData(stats.getMindsStats(), primaryColor()));

        generateDefaultData(stats);
    }

    private void generateDefaultData(Stats stats) {
        ColumnChartData data = new ColumnChartData(Arrays.asList(
                getColumn(stats.getFiles(), Color.RED),
                getColumn(stats.getImages(), Color.GREEN),
                getColumn(stats.getSketches(), Color.BLUE),
                getColumn(stats.getVideos(), Color.YELLOW),
                getColumn(stats.getAudioRecordings(), Color.MAGENTA)));

        Axis axisX = Axis.generateAxisFromCollection(Arrays.asList(0.0f, 1.0f, 2.0f, 3.0f, 4.0f),
                Arrays.asList("A", "B", "C", "D", "E"));
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(null);

        getBinding().ccvAttachment.setColumnChartData(data);
    }

    private Column getColumn(float value, int color) {
        Column column = new Column(Arrays.asList(new SubcolumnValue(value, color)));
        column.setHasLabels(true);
        return column;
    }

    private static class StatsTask extends AsyncTask<Void, Void, Stats> {

        private WeakReference<StatisticsFragment> weakReference;

        StatsTask(StatisticsFragment statisticsFragment) {
            this.weakReference = new WeakReference<>(statisticsFragment);
        }

        @Override
        protected Stats doInBackground(Void... params) {
            if (weakReference.get() == null) return null;
            return StatisticsHelper.getStats(weakReference.get().getContext());
        }

        @Override
        protected void onPostExecute(Stats result) {
            if (result != null && weakReference.get() != null) {
                weakReference.get().outputStats(result);
            }
        }
    }
}
