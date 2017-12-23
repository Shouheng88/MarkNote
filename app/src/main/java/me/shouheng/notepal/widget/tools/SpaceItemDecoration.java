package me.shouheng.notepal.widget.tools;

import android.graphics.Rect;
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
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(leftDp, upDp, rightDp, downDp);
    }
}