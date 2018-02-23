package me.shouheng.notepal.widget.themed;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import me.shouheng.notepal.R;
import me.shouheng.notepal.util.ColorUtils;

/**
 * Created by wangshouheng on 2017/12/5.*/
public class SupportImageView extends android.support.v7.widget.AppCompatImageView {

    private int lightThemeTintColor;

    private int darkThemeTintColor;

    public SupportImageView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public SupportImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public SupportImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.SupportImageView, 0, 0);
        lightThemeTintColor = attr.getColor(R.styleable.SupportImageView_light_theme_tint,
                getContext().getResources().getColor(R.color.light_theme_image_tint_color));
        darkThemeTintColor = attr.getColor(R.styleable.SupportImageView_dark_theme_tint,
                getContext().getResources().getColor(R.color.dark_theme_image_tint_color));
        attr.recycle();

        int tintColor = ColorUtils.isDarkTheme(getContext()) ? darkThemeTintColor : lightThemeTintColor;
        if (getDrawable() != null) setImageDrawable(ColorUtils.tintDrawable(getDrawable(), tintColor));
    }

    public int getLightThemeTintColor() {
        return lightThemeTintColor;
    }

    public void setLightThemeTintColor(int lightThemeTintColor) {
        this.lightThemeTintColor = lightThemeTintColor;
    }

    public int getDarkThemeTintColor() {
        return darkThemeTintColor;
    }

    public void setDarkThemeTintColor(int darkThemeTintColor) {
        this.darkThemeTintColor = darkThemeTintColor;
    }

    public void setTheme(boolean isDarkTheme) {
        int tintColor = isDarkTheme ? darkThemeTintColor : lightThemeTintColor;
        if (getDrawable() != null) setImageDrawable(ColorUtils.tintDrawable(getDrawable(), tintColor));
    }
}
