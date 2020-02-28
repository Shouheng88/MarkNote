package me.shouheng.notepal.adapter.picker;

import android.graphics.drawable.Drawable;

import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.utils.PalmUtils;
import me.shouheng.data.entity.Category;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;

/**
 * Created by shouh on 2018/3/20. */
public class CategoryPickerStrategy implements ModelsPickerStrategy<Category> {

    @Override
    public String getTitle(Category model) {
        return model.getName();
    }

    @Override
    public String getSubTitle(Category model) {
        return PalmApp.getContext().getResources().getQuantityString(
                R.plurals.text_notes_number, model.getCount(), model.getCount());
    }

    @Override
    public Drawable getIconDrawable(Category model) {
        Drawable drawable =  PalmUtils.getDrawableCompact(model.getPortrait().iconRes);
        return ColorUtils.tintDrawable(drawable, model.getColor());
    }

    @Override
    public boolean shouldShowMore() {
        return false;
    }

    @Override
    public boolean isMultiple() {
        return true;
    }
}
