package me.shouheng.notepal.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import me.shouheng.notepal.R;

/**
 * Created by wang shouheng on 2017/12/23. */
public class ColorPreference extends Preference {

    private int value;

    private boolean primary;

    public ColorPreference(Context context) {
        super(context);
        initAttrs(null, 0);
    }

    public ColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs, 0);
    }

    public ColorPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(attrs, defStyle);
    }

    private void initAttrs(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ColorPreference, defStyle, defStyle);
        primary = a.getBoolean(R.styleable.ColorPreference_primary, true);
        a.recycle();
        setWidgetLayoutResource(R.layout.widget_pref_color_layout);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        ImageView previewView = view.findViewById(R.id.color_view);
//        ColorUtils.setColorViewValue(previewView, value, false, colorShape);
    }

    public void setValue(int value) {
        if (callChangeListener(value)) {
            this.value = value;
            persistInt(value);
            notifyChanged();
        }
    }

    @Override
    protected void onClick() {
        super.onClick();
        if (primary) {

        }
    }

    @Override
    protected void onAttachedToActivity() {
        super.onAttachedToActivity();
        if (primary) {
//            ColorUtils.attach(getContext(), this, getFragmentTag());
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedInt(0) : (Integer) defaultValue);
    }

    public String getFragmentTag() {
        return "color_" + getKey();
    }

    public int getValue() {
        return value;
    }
}
