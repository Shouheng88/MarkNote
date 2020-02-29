package me.shouheng.commons.widget.dialog;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import me.shouheng.commons.R;

public abstract class PopupPicker {

    public static String TAG = "PopupPicker";

    protected Context context;
    protected float mScreenDensity;

    protected PopupWindow mPopupWindow;
    protected Object mAnchor;
    protected View mContentView;
    protected RecyclerView mRecyclerView;

    public PopupPicker(Context context, int popupAnimStyle) {
        this.context = context;
        mScreenDensity = context.getResources().getDisplayMetrics().density;

        mContentView = LayoutInflater.from(context).inflate(R.layout.rv_popup_picker, null);
        mRecyclerView = mContentView.findViewById(R.id.rv_popup_picker);
        mPopupWindow = new PopupWindow(mContentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_picker));
        mContentView.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK
                    && event.getRepeatCount() == 1) {
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                    return true;
                }
            }
            return false;
        });
        mPopupWindow.setAnimationStyle(popupAnimStyle);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
    }

    public void setAnchor(Object anchor) {
        mAnchor = anchor;
    }

    public abstract void updateAnchor();

    public abstract void show(View parent);

    public abstract void pickForUI(int index);

    public abstract int getPickedIndex();

    public void dismiss() {
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

}
