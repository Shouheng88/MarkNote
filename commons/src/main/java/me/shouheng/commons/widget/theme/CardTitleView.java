package me.shouheng.commons.widget.theme;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import me.shouheng.commons.R;
import me.shouheng.commons.databinding.WidgetCardTitleViewBinding;
import me.shouheng.commons.utils.ColorUtils;

/**
 * Created by wang shouheng on 2018/2/23.*/
public class CardTitleView extends LinearLayout {

    private WidgetCardTitleViewBinding binding;

    private OnCardTitleClickListener onCardTitleClickListener;

    public CardTitleView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public CardTitleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CardTitleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.widget_card_title_view, this, true);

        ColorUtils.addRipple(binding.ll);

        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.CardTitleView, 0, 0);
        int mIcon = attr.getResourceId(R.styleable.CardTitleView_card_icon, -1);
        String mTitle = attr.getString(R.styleable.CardTitleView_card_title);
        String mSubTitle = attr.getString(R.styleable.CardTitleView_card_sub_title);
        attr.recycle();

        binding.tvTitle.setText(mTitle);
        binding.tvSubTitle.setText(mSubTitle);
        if (mIcon != -1) binding.ivIcon.setImageResource(mIcon);

        binding.ll.setOnClickListener(v -> {
            if (onCardTitleClickListener != null) {
                onCardTitleClickListener.onCardTitleClick();
            }
        });
    }

    public void setTitle(String title) {
        binding.tvTitle.setText(title);
    }

    public void setSubTitle(String subTitle) {
        binding.tvSubTitle.setText(subTitle);
    }

    public void setIcon(@DrawableRes int mIcon) {
        binding.ivIcon.setImageResource(mIcon);
    }

    public void setOnCardTitleClickListener(OnCardTitleClickListener onCardTitleClickListener) {
        this.onCardTitleClickListener = onCardTitleClickListener;
    }

    public interface OnCardTitleClickListener {
        void onCardTitleClick();
    }
}
