package me.shouheng.notepal.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatImageView;

import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.notepal.PalmApp;
import my.shouheng.palmmarkdown.tools.MarkdownFormat;

public class MDItemView extends AppCompatImageView {

    private MarkdownFormat markdownFormat;

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

    public void setMarkdownFormat(MarkdownFormat markdownFormat) {
        this.markdownFormat = markdownFormat;
        setImageDrawable(ColorUtils.tintDrawable(PalmApp.getDrawableCompact(markdownFormat.drawableResId), Color.WHITE));
    }

    public MarkdownFormat getMarkdownFormat() {
        return markdownFormat;
    }
}
