package me.shouheng.notepal.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.widget.CircleImageView;
import me.shouheng.data.entity.Category;
import me.shouheng.notepal.R;

/**
 * Created by WngShhng (shouheng2015@gmail.com) on 2018/2/14.
 */
public class CategoriesAdapter extends BaseQuickAdapter<Category, BaseViewHolder> {

    private Context context;
    private boolean isDarkTheme;

    public CategoriesAdapter(Context context, @Nullable List<Category> data) {
        super(R.layout.item_category, data);
        this.context = context;
        this.isDarkTheme = ColorUtils.isDarkTheme(context);
    }

    @Override
    protected void convert(BaseViewHolder helper, Category category) {
        final int categoryColor = category.getColor();
        if (isDarkTheme) {
            helper.itemView.setBackgroundResource(R.color.dark_theme_background);
        }
        helper.setText(R.id.tv_title, category.getName());
        helper.setText(R.id.tv_sub_title, context.getResources().getQuantityString(
                R.plurals.text_notes_number, category.getCount(), category.getCount()));
        ((CircleImageView) helper.getView(R.id.iv_folder_background)).setFillingCircleColor(categoryColor);
        helper.addOnClickListener(R.id.iv_more);
        helper.setImageResource(R.id.iv_folder_portrait, category.getPortrait().iconRes);
    }
}
