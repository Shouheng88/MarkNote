package me.shouheng.notepal.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import me.shouheng.commons.event.PageName;
import me.shouheng.commons.event.*;
import me.shouheng.commons.fragment.CommonFragment;
import me.shouheng.commons.utils.LogUtils;
import me.shouheng.commons.utils.ToastUtils;
import me.shouheng.data.entity.TimeLine;
import me.shouheng.data.model.enums.Status;
import me.shouheng.data.schema.TimelineSchema;
import me.shouheng.data.store.TimelineStore;
import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.TimeLinesAdapter;
import me.shouheng.notepal.databinding.FragmentTimeLineBinding;

/**
 * Created by WngShhng (shouheng2015@gmail.com) on 2017/8/19.
 * */
@PageName(name = UMEvent.PAGE_TIMELINE)
public class TimeLineFragment extends CommonFragment<FragmentTimeLineBinding> {

    private TimeLinesAdapter adapter;

    private boolean isLoadingMore = false;

    private int modelsCount, pageNumber = 20, startIndex = 0;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_time_line;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        /* Config toolbar. */
        if (getActivity() != null) {
            ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (ab != null) ab.setTitle(R.string.drawer_menu_time_line);
        }

        /* Config views. */
        modelsCount = TimelineStore.getInstance().getCount(null, null, false);
        List<TimeLine> timeLines = TimelineStore.getInstance().getPage(
                startIndex, pageNumber, TimelineSchema.ADDED_TIME + " DESC ", Status.NORMAL, false);
        adapter = new TimeLinesAdapter(getContext(), timeLines);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        getBinding().rv.setEmptyView(getBinding().ivEmpty);
        getBinding().rv.setLayoutManager(layoutManager);
        getBinding().rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
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
        getBinding().rv.setAdapter(adapter);
    }

    private void loadMoreData() {
        LogUtils.d("startIndex:" + startIndex);
        isLoadingMore = true;
        startIndex += pageNumber;
        if (startIndex > modelsCount) {
            startIndex -= pageNumber;
            ToastUtils.makeToast(R.string.timeline_no_more_data);
        } else {
            List<TimeLine> list = TimelineStore.getInstance().getPage(startIndex,
                    pageNumber,
                    TimelineSchema.ADDED_TIME + " DESC ",
                    Status.NORMAL,
                    false);
            adapter.addData(list);
            adapter.notifyDataSetChanged();
        }
        getBinding().mpb.setVisibility(View.GONE);
        isLoadingMore = false;
    }
}
