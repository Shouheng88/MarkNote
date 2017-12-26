package me.shouheng.notepal.widget.themed;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import me.shouheng.notepal.R;
import me.shouheng.notepal.util.ColorUtils;


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
        setBackgroundResource(ColorUtils.isDarkTheme(context) ? R.color.white_divider_color : R.color.black_divider_color);
    }

    public void setTheme(boolean isDarkTheme) {
        setBackgroundResource(isDarkTheme ? R.color.white_divider_color : R.color.black_divider_color);
    }
}
