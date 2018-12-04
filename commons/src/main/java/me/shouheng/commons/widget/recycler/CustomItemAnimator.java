package me.shouheng.commons.widget.recycler;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;

import java.util.List;

import me.shouheng.commons.R;
import me.shouheng.commons.utils.PalmUtils;

/**
 * Created by WngShhng on 2017/4/1.
 */
public class CustomItemAnimator extends DefaultItemAnimator {

    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    private int lastAddAnimatedItem = -2;

    private int lastPosition = -1;

    @Override
    public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder) {
        return true;
    }

    @NonNull
    @Override
    public ItemHolderInfo recordPreLayoutInformation(
            @NonNull RecyclerView.State state,
            @NonNull RecyclerView.ViewHolder viewHolder,
            int changeFlags,
            @NonNull List<Object> payloads) {
        if (changeFlags == FLAG_CHANGED) {
            for (Object payload : payloads) {
                if (payload instanceof String) {
                    return new CustomItemHolderInfo((String) payload);
                }
            }
        }
        return super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads);
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder.getLayoutPosition() > lastAddAnimatedItem) {
            lastAddAnimatedItem++;
            runEnterAnimation(viewHolder);
            return false;
        }
        dispatchAddFinished(viewHolder);
        return false;
    }

    private void runEnterAnimation(RecyclerView.ViewHolder holder) {
        if (PalmUtils.isLollipop()) {
            setAnimation(holder.itemView, holder.getAdapterPosition());
        } else {
            if (holder.getAdapterPosition() > 10) {
                setAnimation(holder.itemView, holder.getAdapterPosition());
            }
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.anim_slide_in_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder,
                                 @NonNull RecyclerView.ViewHolder newHolder,
                                 @NonNull ItemHolderInfo preInfo,
                                 @NonNull ItemHolderInfo postInfo) {
        if (preInfo instanceof CustomItemHolderInfo) {
            CustomItemHolderInfo customItemHolderInfo = (CustomItemHolderInfo) preInfo;

            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(newHolder.itemView, "scaleX", 0.8f, 1f);
            bounceAnimX.setDuration(300);
            bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

            ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(newHolder.itemView, "scaleY", 0.8f, 1f);
            bounceAnimY.setDuration(300);
            bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);

            animatorSet.play(bounceAnimX).with(bounceAnimY);
            animatorSet.start();
        }
        return false;
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {
        super.endAnimation(item);
    }

    @Override
    public void endAnimations() {
        super.endAnimations();
    }

    public static class CustomItemHolderInfo extends ItemHolderInfo {

        String updateAction;

        CustomItemHolderInfo(String updateAction) {
            this.updateAction = updateAction;
        }
    }
}
