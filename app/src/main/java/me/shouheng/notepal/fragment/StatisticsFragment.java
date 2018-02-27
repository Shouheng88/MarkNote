package me.shouheng.notepal.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

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
        if (getActivity() != null) {
            ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (ab != null) ab.setTitle(R.string.statistic);
        }
    }

    private void outputStats(Stats stats) {
        LogUtils.d(stats);

        getBinding().ccvModels.setColumnChartData(StatisticsHelper.getModelsData(getContext(), stats));

        getBinding().lcvNote.setValueSelectionEnabled(false);
        getBinding().lcvNote.setLineChartData(StatisticsHelper.getLineChartData(getContext(), stats));

        getBinding().ccvAttachment.setColumnChartData(StatisticsHelper.getAttachmentsData(getContext(), stats));
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
