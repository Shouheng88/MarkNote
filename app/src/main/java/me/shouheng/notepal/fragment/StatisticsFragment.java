package me.shouheng.notepal.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.FragmentSnaggingsBinding;
import me.shouheng.notepal.model.Stats;
import me.shouheng.notepal.provider.helper.StatisticsHelper;
import me.shouheng.notepal.util.LogUtils;

/**
 * Created by wang shouheng on 2018/1/19. */
public class StatisticsFragment extends BaseFragment<FragmentSnaggingsBinding> {

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
