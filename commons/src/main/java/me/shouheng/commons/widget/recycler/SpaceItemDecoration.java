package me.shouheng.commons.widget.recycler;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int leftDp, upDp, rightDp, downDp;

    public SpaceItemDecoration(int leftDp, int upDp, int rightDp, int downDp) {
        this.leftDp = leftDp;
        this.upDp = upDp;
        this.rightDp = rightDp;
        this.downDp = downDp;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect,
                               @NonNull View view,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(leftDp, upDp, rightDp, downDp);
    }
}