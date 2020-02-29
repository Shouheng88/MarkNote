package me.shouheng.commons.widget.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class BaseViewHolder extends RecyclerView.ViewHolder {

    protected View mContentView;

    public BaseViewHolder(View itemView) {
        super(itemView);
        mContentView = itemView;
    }

    @SuppressWarnings("unchecked")
    protected final <T extends View> T f(int id) {
        return (T) mContentView.findViewById(id);
    }
}
