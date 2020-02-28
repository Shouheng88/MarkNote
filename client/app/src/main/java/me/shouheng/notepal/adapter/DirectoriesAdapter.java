package me.shouheng.notepal.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.LinkedList;

import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.utils.PalmUtils;
import me.shouheng.data.model.Directory;
import me.shouheng.notepal.R;

/**
 * Created by shouh on 2018/3/30.
 */
public class DirectoriesAdapter extends BaseQuickAdapter<Directory, BaseViewHolder> {

    private int primaryColor;
    private boolean isDarkTheme;

    private Drawable dirIcon;

    public DirectoriesAdapter(Context context) {
        super(R.layout.item_directory, new LinkedList<>());
        primaryColor = ColorUtils.primaryColor();
        isDarkTheme = ColorUtils.isDarkTheme();
    }

    @Override
    protected void convert(BaseViewHolder helper, Directory item) {
        if (isDarkTheme) helper.itemView.setBackgroundResource(R.color.dark_theme_background);
        helper.setText(R.id.tv_title, item.getName());
        helper.setImageDrawable(R.id.iv_icon, getDirIcon());
    }

    private Drawable getDirIcon() {
        if (dirIcon == null) {
            dirIcon = ColorUtils.tintDrawable(PalmUtils.getDrawableCompact(R.drawable.ic_folder_black_24dp), primaryColor);
        }
        return dirIcon;
    }
}
