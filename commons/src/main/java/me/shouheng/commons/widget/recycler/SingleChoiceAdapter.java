package me.shouheng.commons.widget.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public abstract class SingleChoiceAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    public static final String TAG = "SingleChoiceAdapter";

    protected int mPickedPosition = -1;

    @Override
    public abstract BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(BaseViewHolder viewHolder, int position);

    @Override
    public abstract int getItemCount();

    public void pick(int position) {
        mPickedPosition = position;
        notifyDataSetChanged();
    }

    public int getPickedPosition() {
        return mPickedPosition;
    }
}
