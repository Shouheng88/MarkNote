package me.shouheng.notepal.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
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
import me.shouheng.notepal.util.enums.MindSnaggingListType;
import me.shouheng.notepal.widget.tools.BubbleTextGetter;

/**
 * Created by wangshouheng on 2017/8/20.*/
public class MindSnaggingAdapter extends BaseQuickAdapter<MindSnagging, BaseViewHolder> implements BubbleTextGetter {

    private Context context;

    private MindSnaggingListType listType;

    public MindSnaggingAdapter(Context context,
                               MindSnaggingListType listType,
                               @Nullable List<MindSnagging> data) {
        super(listType == MindSnaggingListType.ONE_COL ?
                R.layout.item_universal_layout : R.layout.item_universal_layout_two_cols, data);
        this.context = context;
        this.listType = listType;
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
            helper.getView(R.id.image_view_cover).setVisibility(View.GONE);
        }
        helper.setText(R.id.text_view_title, mindSnagging.getContent());
        helper.setText(R.id.tv_added_time, TimeUtils.getPrettyTime(mindSnagging.getAddedTime()));
        helper.addOnClickListener(R.id.iv_more);
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        MindSnagging snagging = getItem(pos);
        String content;
        if (snagging == null || TextUtils.isEmpty(content = snagging.getContent())) {
            return "";
        }
        try {
            return String.valueOf(content.charAt(0));
        } catch (Exception e) {
            return "";
        }
    }
}
