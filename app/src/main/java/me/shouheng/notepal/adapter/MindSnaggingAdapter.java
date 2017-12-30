package me.shouheng.notepal.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.model.MindSnagging;
import me.shouheng.notepal.util.FileHelper;
import me.shouheng.notepal.util.TimeUtils;

/**
 * Created by wangshouheng on 2017/8/20.*/
public class MindSnaggingAdapter extends BaseQuickAdapter<MindSnagging, BaseViewHolder> {

    private Context context;

    public MindSnaggingAdapter(Context context, @Nullable List<MindSnagging> data) {
        super(R.layout.item_universal_layout, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, MindSnagging mindSnagging) {
        if (mindSnagging.getPicture() != null) {
            helper.setVisible(R.id.image_view_cover, true);
            Uri thumbnailUri = FileHelper.getThumbnailUri(context, mindSnagging.getPicture());
            Glide.with(PalmApp.getContext())
                    .load(thumbnailUri)
                    .centerCrop()
                    .crossFade()
                    .into((ImageView) helper.getView(R.id.image_view_cover));
        } else {
            helper.setVisible(R.id.image_view_cover, false);
        }
        helper.setText(R.id.text_view_title, mindSnagging.getContent());
        helper.setText(R.id.tv_added_time, TimeUtils.getPrettyTime(mindSnagging.getAddedTime()));
    }
}
