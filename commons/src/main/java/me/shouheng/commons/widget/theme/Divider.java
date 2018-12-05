package me.shouheng.commons.widget.theme;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import me.shouheng.commons.R;
import me.shouheng.commons.utils.ColorUtils;

/**
 * Created by wangshouheng on 2017/12/5.*/
public class Divider extends View {

    public Divider(Context context) {
        super(context);
        initTheme(context);
    }

    public Divider(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initTheme(context);
    }

    public Divider(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTheme(context);
    }

    private void initTheme(Context context) {
        setBackgroundResource(ColorUtils.isDarkTheme() ? R.color.white_divider_color : R.color.black_divider_color);
    }

    public void setTheme(boolean isDarkTheme) {
        setBackgroundResource(isDarkTheme ? R.color.white_divider_color : R.color.black_divider_color);
    }
}
