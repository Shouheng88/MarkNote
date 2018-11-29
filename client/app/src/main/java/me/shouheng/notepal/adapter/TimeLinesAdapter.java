package me.shouheng.notepal.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.notepal.R;
import me.shouheng.data.entity.TimeLine;
import me.shouheng.commons.utils.TimeUtils;
import me.shouheng.notepal.util.preferences.PrefUtils;
import me.shouheng.commons.widget.CircleImageView;
import me.shouheng.commons.widget.Timeline;

/**
 * Created by wangshouheng on 2017/8/19. */
public class TimeLinesAdapter extends BaseQuickAdapter<TimeLine, BaseViewHolder> {

    private Context context;

    private Drawable atomDrawable;

    public TimeLinesAdapter(Context context, @Nullable List<TimeLine> data) {
        super(R.layout.item_time_line, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, TimeLine timeLine) {
        helper.setText(R.id.tv, getOperation(timeLine));
        helper.setImageResource(R.id.iv_operation, getOperationRes(timeLine));
        ((CircleImageView) helper.getView(R.id.civ)).setFillingCircleColor(getOperationColor(timeLine));
        helper.setText(R.id.tv_date, TimeUtils.getShortDate(context, timeLine.getAddedTime()));
        helper.setText(R.id.tv_time, TimeUtils.getShortTime(context, timeLine.getAddedTime()));
        helper.setText(R.id.tv_sub, timeLine.getModelName());
        helper.setTextColor(R.id.tv_sub, ColorUtils.accentColor(context));
        ((Timeline) helper.getView(R.id.timeLine)).setAtomDrawable(atomDrawable());
    }

    private Drawable atomDrawable() {
        if (atomDrawable == null) {
            atomDrawable = ColorUtils.tintDrawable(R.drawable.solid_circle_green, ColorUtils.accentColor());
        }
        return atomDrawable;
    }

    private String getOperation(TimeLine timeLine) {
        return context.getString(timeLine.getOperation().operationName)
                + " " + context.getString(timeLine.getModelType().typeName) + " : ";
    }

    private @DrawableRes int getOperationRes(TimeLine timeLine) {
        switch (timeLine.getModelType()) {
            case NOTE: return R.drawable.ic_doc_text_alpha;
            case NOTEBOOK: return R.drawable.ic_folder_black_24dp;
            case ALARM: return R.drawable.ic_access_alarm_grey;
            case LOCATION: return R.drawable.ic_location1_grey_24dp;
            case MIND_SNAGGING: return R.drawable.ic_lightbulb_outline_black_24dp;
            case ATTACHMENT: return R.drawable.ic_attach_file_black;
        }
        return R.drawable.ic_insert_drive_file_grey_24dp;
    }

    private int getOperationColor(TimeLine timeLine) {
        return PrefUtils.getInstance().getTimeLineColor(timeLine.getOperation());
    }
}

