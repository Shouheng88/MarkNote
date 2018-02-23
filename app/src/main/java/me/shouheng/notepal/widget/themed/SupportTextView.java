package me.shouheng.notepal.widget.themed;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import me.shouheng.notepal.R;
import me.shouheng.notepal.util.ColorUtils;

/**
 * Created by wangshouheng on 2017/12/5.*/
public class SupportTextView extends AppCompatTextView {

    private int lightThemeTextColor;

    private int darkThemeTextColor;

    public SupportTextView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public SupportTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public SupportTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.SupportTextView, 0, 0);
        lightThemeTextColor = attr.getColor(R.styleable.SupportTextView_light_theme_text_color,
                getContext().getResources().getColor(R.color.md_grey_850));
        darkThemeTextColor = attr.getColor(R.styleable.SupportTextView_dark_theme_text_color, Color.WHITE);
        attr.recycle();

        int textColor = ColorUtils.isDarkTheme(getContext()) ? darkThemeTextColor : lightThemeTextColor;
        setTextColor(textColor);
    }

    public int getLightThemeTextColor() {
        return lightThemeTextColor;
    }

    public void setLightThemeTextColor(int lightThemeTextColor) {
        this.lightThemeTextColor = lightThemeTextColor;
    }

    public int getDarkThemeTextColor() {
        return darkThemeTextColor;
    }

    public void setDarkThemeTextColor(int darkThemeTextColor) {
        this.darkThemeTextColor = darkThemeTextColor;
    }

    public void setTheme(boolean isDarkTheme) {
        int textColor = isDarkTheme ? darkThemeTextColor : lightThemeTextColor;
        setTextColor(textColor);
    }
}
