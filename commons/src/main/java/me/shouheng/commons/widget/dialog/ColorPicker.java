package me.shouheng.commons.widget.dialog;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import me.shouheng.commons.R;
import me.shouheng.commons.utils.PalmUtils;
import me.shouheng.commons.utils.ViewUtils;
import me.shouheng.commons.widget.recycler.BaseViewHolder;
import me.shouheng.commons.widget.recycler.SingleChoiceAdapter;

public class ColorPicker extends PopupPicker {

    public static final String TAG = "ColorPicker";

    private int[] mColors;
    private String[] mColorsNames;

    private int mType;
    private View.OnClickListener mOnClickListener;
    private ColorPickerAdapter mAdapter;
    private Rect mWindowRect;

    public ColorPicker(Context context, int type) {
        super(context, R.style.ColorPickerAnimation);
        mColors = context.getResources().getIntArray(R.array.thing);
        mColorsNames = context.getResources().getStringArray(R.array.thing_colors_names);
        mType = type;
        ViewGroup.LayoutParams params = mRecyclerView.getLayoutParams();
        params.width = (int) (mScreenDensity * 128);
        if (mType == PickerType.COLOR_HAVE_ALL) {
            params.height = (int) (mScreenDensity * 304);
        } else if (mType == PickerType.COLOR_NO_ALL) {
            params.height = (int) (mScreenDensity * 264);
        }
        // For every 2 new colors you want to add, you should also add 48 dp to picker's height.
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this.context, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mType == PickerType.COLOR_HAVE_ALL) {
                    return position == 0 ? 2 : 1;
                } else if (mType == PickerType.COLOR_NO_ALL) {
                    return 1;
                }
                return 0;
            }
        });
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ColorPickerAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mWindowRect = new Rect();
    }

    @Override
    public void show(View parent) {
        int xOffset = 0;
        int[] location = new int[2];
        parent.getLocationOnScreen(location);
        // if we are in multi-window mode, we should detect which part we are in, left or right.
        boolean isRightWindow = location[0] != 0;

        parent.getWindowVisibleDisplayFrame(mWindowRect);
//        if (mType == PickerType.COLOR_NO_ALL) {
//            xOffset += (int) (mScreenDensity * 36);
//            if (DisplayUtil.isTablet(this.context)) {
//                xOffset += (int) (mScreenDensity * 12);
//            }
//        }
//        if (mWindowRect.right != ViewUtils.getWindowSize(this.context).x) {
//            if (!DeviceUtil.hasNougatApi() || isRightWindow) {
//                xOffset += (int) (mScreenDensity * 40);
//            }
//        }

        mPopupWindow.showAtLocation(parent, Gravity.TOP | Gravity.END,
                xOffset, ViewUtils.getStatusBarHeight(this.context));
    }

    public void setPickedListener(View.OnClickListener listener) {
        mOnClickListener = listener;
    }

    public int getPickedIndex() {
        return mAdapter.getPickedPosition();
    }

    public int getPickedColor() {
        int picked = mAdapter.getPickedPosition();
        if (mType == PickerType.COLOR_HAVE_ALL) {
            if (picked <= 0) {
                return -1979711488;
            } else {
                return mColors[picked - 1];
            }
        } else return mColors[picked];
    }

    @Override
    public void pickForUI(int index) {
        mAdapter.pick(index);
        if (mAnchor != null) {
            updateAnchor();
        }
    }

    @Override
    public void updateAnchor() {
        if (mType == PickerType.COLOR_HAVE_ALL) {
            ((Drawable) mAnchor).mutate().setColorFilter(getPickedColor(), PorterDuff.Mode.SRC_ATOP);
        }
    }

    private class ColorPickerAdapter extends SingleChoiceAdapter {

        static final int ALL_COLOR = 0;
        static final int NORMAL = 1;

        private LayoutInflater mInflater;

        ColorPickerAdapter() {
            mInflater = LayoutInflater.from(ColorPicker.this.context);
        }

        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ALL_COLOR) {
                return new AllColorViewHolder(
                        mInflater.inflate(R.layout.color_picker_bt, parent, false));
            } else {
                return new FabViewHolder(
                        mInflater.inflate(R.layout.color_picker_fab, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(BaseViewHolder viewHolder, int position) {
            if (mType == PickerType.COLOR_HAVE_ALL) {
                if (position == 0) {
                    AllColorViewHolder holder = (AllColorViewHolder) viewHolder;
                    if (mPickedPosition == 0) {
                        holder.bt.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_checkbox_checked, 0, 0, 0);
                        holder.bt.setContentDescription(
                                context.getString(R.string.cd_picked) + holder.bt.getText() + ",");
                    } else {
                        holder.bt.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_checkbox_unchecked, 0, 0, 0);
                        holder.bt.setContentDescription(
                                context.getString(R.string.cd_unpicked) + holder.bt.getText() + ",");
                    }
                    holder.bt.setClickable(mPickedPosition != 0);
                } else {
                    setFab(viewHolder, position);
                }
            } else if (mType == PickerType.COLOR_NO_ALL) {
                setFab(viewHolder, position);
            }
        }

        private void setFab(RecyclerView.ViewHolder viewHolder, int position) {
            FabViewHolder holder = (FabViewHolder) viewHolder;
            int index = mType == PickerType.COLOR_HAVE_ALL ? position - 1 : position;
            holder.fab.setBackgroundTintList(ColorStateList.valueOf(mColors[index]));
            setFabMargin(holder.fab, index);
            if (mPickedPosition == position) {
                holder.fab.setImageDrawable(ContextCompat.getDrawable(
                        context, R.drawable.ic_color_picked));
                holder.fab.setContentDescription(
                        context.getString(R.string.cd_picked) + mColorsNames[index] + ",");
            } else {
                holder.fab.setImageDrawable(null);
                holder.fab.setContentDescription(
                        context.getString(R.string.cd_unpicked) + mColorsNames[index] + ",");
            }
            holder.fab.setClickable(mPickedPosition != position);
        }

        private void setFabMargin(FloatingActionButton fab, int index) {
            int m8 = (int) (8 * mScreenDensity);
            int m4 = m8 >> 1;
            int m16 = m8 << 1;
            GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) fab.getLayoutParams();
            switch (index) {
                case 0:
                    if (mType == PickerType.COLOR_HAVE_ALL) {
                        params.setMargins(m16, m8, m8, m4);
                    } else {
                        params.setMargins(m16, m16, m8, m4);
                    }
                    break;
                case 1:
                    if (mType == PickerType.COLOR_HAVE_ALL) {
                        params.setMargins(m8, m8, m16, m4);
                    } else {
                        params.setMargins(m8, m16, m16, m4);
                    }
                    break;
                case 2:
                    params.setMargins(m16, m4, m8, m4);
                    break;
                case 3:
                    params.setMargins(m8, m4, m16, m4);
                    break;
                case 4:
                    params.setMargins(m16, m4, m8, m4);
                    break;
                case 5:
                    params.setMargins(m8, m4, m16, m4);
                    break;
                case 6:
                    params.setMargins(m16, m4, m8, m4);
                    break;
                case 7:
                    params.setMargins(m8, m4, m16, m4);
                    break;
                case 8:
                    params.setMargins(m16, m4, m8, m16);
                    break;
                case 9:
                    params.setMargins(m8, m4, m16, m16);
                    break;
                default:break;
            }
            if (PalmUtils.isJellyBeanMR1()) {
                params.setMarginStart(params.leftMargin);
                params.setMarginEnd(params.rightMargin);
            }
        }

        @Override
        public int getItemCount() {
            if (mType == PickerType.COLOR_HAVE_ALL) {
                return mColors.length + 1;
            } else if (mType == PickerType.COLOR_NO_ALL) {
                return mColors.length;
            }
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            if (mType == PickerType.COLOR_HAVE_ALL) {
                return position == 0 ? ALL_COLOR : NORMAL;
            } else if (mType == PickerType.COLOR_NO_ALL) {
                return NORMAL;
            }
            return super.getItemViewType(position);
        }

        class AllColorViewHolder extends BaseViewHolder {

            final Button bt;

            AllColorViewHolder(View itemView) {
                super(itemView);
                bt = f(R.id.bt_all_color);
                bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPopupWindow.dismiss();
                        pickForUI(0);
                        if (mOnClickListener != null) {
                            mOnClickListener.onClick(v);
                        }
                    }
                });
            }
        }

        class FabViewHolder extends BaseViewHolder {

            final FloatingActionButton fab;

            FabViewHolder(View itemView) {
                super(itemView);
                fab = f(R.id.fab_pick_color);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPopupWindow.dismiss();
                        pickForUI(getAdapterPosition());
                        if (mOnClickListener != null) {
                            mOnClickListener.onClick(v);
                        }
                    }
                });
            }
        }
    }

    public static final class PickerType {

        private PickerType() {}

        public static final int COLOR_HAVE_ALL             = 0;
        public static final int COLOR_NO_ALL               = 1;
        public static final int AFTER_TIME                 = 2;
        public static final int TIME_TYPE_NO_HOUR_MINUTE   = 3;
        public static final int TIME_TYPE_HAVE_HOUR_MINUTE = 4;
        public static final int DAY_OF_WEEK                = 5;
        public static final int DAY_OF_MONTH               = 6;
        public static final int MONTH_OF_YEAR              = 7;

    }
}
