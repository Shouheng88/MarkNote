package me.shouheng.notepal.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.WidgetEmptyViewBinding;
import me.shouheng.notepal.util.ViewUtils;

/**
 * Created by wangshouheng on 2017/8/9. */
public class EmptyView extends LinearLayout {

    private WidgetEmptyViewBinding binding;

    public EmptyView(Context context) {
        this(context, null);
    }

    public EmptyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EmptyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.widget_empty_view, this, true);

        TypedArray attr = context.obtainStyledAttributes(attributeSet, R.styleable.EmptyView, 0, 0);
        int bottomTitleSize = attr.getDimensionPixelSize(R.styleable.EmptyView_title_size,
                ViewUtils.sp2Px(context, 16));
        int bottomSubTitleSize = attr.getDimensionPixelSize(R.styleable.EmptyView_sub_title_size,
                ViewUtils.sp2Px(context, 14));
        int mIcon = attr.getResourceId(R.styleable.EmptyView_empty_image, R.drawable.empty_sunny);
        String bottomTitle = attr.getString(R.styleable.EmptyView_title);
        String bottomSubTitle = attr.getString(R.styleable.EmptyView_sub_title);
        attr.recycle();

        binding.tvBottomTitle.setText(bottomTitle);
        binding.tvBottomSubTitle.setText(bottomSubTitle);

        binding.tvBottomTitle.setTextSize(bottomTitleSize);
        binding.tvBottomSubTitle.setTextSize(bottomSubTitleSize);

        binding.tvBottomTitle.setVisibility(TextUtils.isEmpty(bottomTitle) ? VISIBLE : GONE);
        binding.tvBottomSubTitle.setVisibility(TextUtils.isEmpty(bottomSubTitle) ? VISIBLE : GONE);

        binding.ivImage.setImageResource(mIcon);
    }

    public void setTitle(String title) {
        binding.tvBottomTitle.setText(title);
        binding.tvBottomTitle.setVisibility(TextUtils.isEmpty(title) ? VISIBLE : GONE);
    }

    public void setSubTitle(String subTitle) {
        binding.tvBottomSubTitle.setText(subTitle);
        binding.tvBottomSubTitle.setVisibility(TextUtils.isEmpty(subTitle) ? VISIBLE : GONE);
    }

    public void setIcon(@DrawableRes int mIcon) {
        binding.ivImage.setImageResource(mIcon);
    }
}
