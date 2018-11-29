package me.shouheng.commons.widget.theme;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by shouh on 2018/3/21.*/
public class DroidSerifTextView extends SupportTextView {

    public DroidSerifTextView(Context context) {
        super(context);
        init();
    }

    public DroidSerifTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DroidSerifTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "DroidSerif-Regular.ttf");
        setTypeface(typeface);
    }
}
