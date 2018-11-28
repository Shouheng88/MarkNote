package me.shouheng.notepal.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatImageView;

import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.easymark.editor.Format;
import me.shouheng.notepal.PalmApp;

public class MDItemView extends AppCompatImageView {

    private Format format;

    public MDItemView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        int childWidthSize = getMeasuredWidth();
        int childHeightSize = getMeasuredHeight();
        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childHeightSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setFormat(Format format) {
        this.format = format;
        setImageDrawable(ColorUtils.tintDrawable(PalmApp.getDrawableCompact(format.drawableResId), Color.WHITE));
    }

    public Format getFormat() {
        return format;
    }
}
