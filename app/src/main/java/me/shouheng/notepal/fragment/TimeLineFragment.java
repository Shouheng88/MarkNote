package me.shouheng.notepal.fragment;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.TimeLinesAdapter;
import me.shouheng.notepal.databinding.FragmentTimeLineBinding;
import me.shouheng.notepal.model.TimeLine;
import me.shouheng.notepal.provider.TimelineStore;
import me.shouheng.notepal.provider.schema.TimelineSchema;
import me.shouheng.notepal.util.ToastUtils;

/**
 * Created by wangshouheng on 2017/8/19. */
public class TimeLineFragment extends CommonFragment<FragmentTimeLineBinding> {

    private TimeLinesAdapter adapter;

    private boolean isLoadingMore = false;

    private int modelsCount, pageNumber = 20, startIndex = 0;

    public static TimeLineFragment newInstance() {
        Bundle args = new Bundle();
        TimeLineFragment fragment = new TimeLineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_time_line;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        configToolbar();

        configTimeline();
    }

    private void configToolbar() {
        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setTitle(R.string.timeline);
    }

    private void configTimeline() {
        modelsCount = TimelineStore.getInstance(getContext()).getCount(null, null, false);
        List<TimeLine> timeLines;
        if (modelsCount <= pageNumber) {
            // per page count > total cont -> LOAD ALL
            timeLines = TimelineStore.getInstance(getContext()).get(null, TimelineSchema.ADDED_TIME + " DESC ");
        } else {
            // load first page
            timeLines = TimelineStore.getInstance(getContext()).getPageTimeLines(startIndex, pageNumber);
        }

        adapter = new TimeLinesAdapter(getContext(), timeLines);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        getBinding().rv.setEmptyView(getBinding().ivEmpty);
        getBinding().rv.setLayoutManager(layoutManager);
        getBinding().rv.setAdapter(adapter);
        getBinding().rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();
                if (lastVisibleItem + 1 == totalItemCount && dy > 0) {
                    if (!isLoadingMore) {
                        getBinding().mpb.setVisibility(View.VISIBLE);
                        recyclerView.post(() -> loadMoreData());
                    }
                }
            }
        });
    }

    private void loadMoreData() {
        isLoadingMore = true;
        // 初始位置移动20
        startIndex += pageNumber;
        List<TimeLine> timeLines;
        if (startIndex > modelsCount) {
            // 初始位置大于总数，说明没有更多数据了
            ToastUtils.makeToast(getContext(), R.string.no_more_data);
            isLoadingMore = false;
            getBinding().mpb.setVisibility(View.GONE);
            return;
        } else if (startIndex + pageNumber > modelsCount) { // 如果将要加载的总数超出了数目总数
            timeLines = TimelineStore.getInstance(getContext()).getPageTimeLines(startIndex, startIndex + pageNumber - modelsCount);
        } else {
            timeLines = TimelineStore.getInstance(getContext()).getPageTimeLines(startIndex, pageNumber);
        }
        adapter.addData(timeLines);
        adapter.notifyDataSetChanged();
        isLoadingMore = false;
        getBinding().mpb.setVisibility(View.GONE);
    }
}
