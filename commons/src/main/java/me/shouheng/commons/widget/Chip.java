package me.shouheng.commons.widget;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import me.shouheng.commons.R;
import me.shouheng.commons.databinding.WidgetChipBinding;
import me.shouheng.commons.utils.ColorUtils;

/**
 * @author shouh
 * @version $Id: Chip, v 0.1 2018/11/18 18:38 shouh Exp$
 */
public class Chip extends LinearLayout {

    private WidgetChipBinding binding;

    public Chip(Context context) {
        super(context);
        init(context, null);
    }

    public Chip(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Chip(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.widget_chip, this, true);
    }

    public void setText(String txt) {
        binding.tv.setText(txt);
    }

    public void setIcon(@DrawableRes int res) {
        binding.iv.setImageDrawable(ColorUtils.tintDrawable(res, Color.WHITE));
    }

    public void setBackgroundColor(@ColorInt int color) {
        setBackground(ColorUtils.tintDrawable(R.drawable.ic_oval_background, color));
    }
}
