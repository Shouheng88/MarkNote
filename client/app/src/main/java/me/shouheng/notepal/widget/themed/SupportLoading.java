package me.shouheng.notepal.widget.themed;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.WidgetSupportLoadingBinding;
import me.shouheng.notepal.util.ColorUtils;

/**
 * Created by Employee on 2018/3/13. */
public class SupportLoading extends RelativeLayout {

    public SupportLoading(Context context) {
        super(context);
        init(context, null, 0);
    }

    public SupportLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public SupportLoading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        WidgetSupportLoadingBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.widget_support_loading, this, true);

        boolean isDarkTheme = ColorUtils.isDarkTheme(context);
        binding.rlBg.setBackgroundResource(isDarkTheme ? R.color.dark_theme_background : R.color.light_theme_background);
    }
}
