package me.shouheng.commons.widget.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wangshouheng on 2017/3/31.*/
public class EmptySupportRecyclerView extends RecyclerView {

    private View emptyView;

    public EmptySupportRecyclerView(Context context) {
        super(context);
    }

    public EmptySupportRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptySupportRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {

        @Override
        public void onChanged() {
            showEmptyView();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            showEmptyView();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            showEmptyView();
        }
    };

    public void showEmptyView() {
        RecyclerView.Adapter<?> adapter = getAdapter();
        if(adapter != null && emptyView != null) {
            if(adapter.getItemCount() == 0) {
                emptyView.setVisibility(View.VISIBLE);
                EmptySupportRecyclerView.this.setVisibility(View.GONE);
            } else{
                emptyView.setVisibility(View.GONE);
                EmptySupportRecyclerView.this.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        super.setAdapter(adapter);
        if(adapter != null) {
            adapter.registerAdapterDataObserver(observer);
            observer.onChanged();
        }
    }

    public void setEmptyView(View v) {
        emptyView = v;
    }
}
