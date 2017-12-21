package me.shouheng.notepal.widget;

import android.content.Context;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wang shouheng on 2017/12/21.*/
public class ThemedPreferenceCategory extends PreferenceCategory {

    private Context context;

    public ThemedPreferenceCategory(Context context) {
        super(context);
        this.context = context;
    }

    public ThemedPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public ThemedPreferenceCategory(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
//        TextView titleView = view.findViewById(android.R.id.title);
//        titleView.setTextColor(Config.accentColor(context, Helpers.getATEKey(context)));
    }
}

