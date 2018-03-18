package me.shouheng.notepal.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.polaric.colorful.Colorful;

import java.util.List;

import me.shouheng.notepal.R;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.widget.CircleImageView;

/**
 * Created by wangshouheng on 2017/8/6. */
public class ThemesAdapter extends BaseQuickAdapter<Colorful.ThemeColor, BaseViewHolder> {

    private Context context;

    private Colorful.ThemeColor selectedTheme;

    private boolean isDarkTheme;

    ThemesAdapter(Context context, @Nullable List<Colorful.ThemeColor> data, Colorful.ThemeColor selectedTheme) {
        super(R.layout.item_theme_color, data);
        this.context = context;
        this.selectedTheme = selectedTheme;
        this.isDarkTheme = ColorUtils.isDarkTheme(context);
    }

    @Override
    protected void convert(BaseViewHolder helper, Colorful.ThemeColor themeColor) {
        int primaryColor = context.getResources().getColor(themeColor.getColorRes());

        helper.setImageResource(R.id.civ_color, themeColor.getColorRes());
        helper.setText(R.id.tv_theme_name, themeColor.getDisplayName());
        helper.setTextColor(R.id.tv_theme_name, primaryColor);

        if (selectedTheme != null && themeColor.getIdentifyName().equals(selectedTheme.getIdentifyName())) {
            helper.setVisible(R.id.civ_checked_bg, true);
            helper.setImageResource(R.id.civ_checked_bg, isDarkTheme ? R.color.dark_theme_background : R.color.light_theme_background);
            helper.setVisible(R.id.iv_checked, true);
            helper.setImageDrawable(R.id.iv_checked, ColorUtils.tintDrawable(
                    context.getResources().getDrawable(R.drawable.ic_check_circle_black_24dp), primaryColor));
            ((CircleImageView) helper.getView(R.id.civ_selected)).setFillingCircleColor(primaryColor);
        } else {
            helper.setVisible(R.id.civ_checked_bg, false);
            helper.setVisible(R.id.iv_checked, false);
            ((CircleImageView) helper.getView(R.id.civ_selected)).setFillingCircleColor(Color.LTGRAY);
        }
    }

    void setSelectedTheme(Colorful.ThemeColor selectedTheme) {
        this.selectedTheme = selectedTheme;
    }
}
