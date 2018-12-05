package me.shouheng.notepal.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.data.entity.Model;
import me.shouheng.data.utils.Selectable;
import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.picker.ModelsPickerStrategy;

/**
 * Created by wangshouheng on 2017/10/5.
 */
public class ModelsPickerAdapter<T extends Model & Selectable> extends BaseQuickAdapter<T, BaseViewHolder> {

    private int selectedColor = -1;

    @NonNull
    private ModelsPickerStrategy<T> modelsPickerStrategy;

    public ModelsPickerAdapter(@Nullable List<T> data, @NonNull ModelsPickerStrategy<T> modelsPickerStrategy) {
        super(R.layout.item_universal_icon_layout, data);
        this.modelsPickerStrategy = modelsPickerStrategy;
    }

    @Override
    protected void convert(BaseViewHolder helper, T item) {
        helper.setText(R.id.tv_title, modelsPickerStrategy.getTitle(item));
        helper.setText(R.id.tv_sub_title, modelsPickerStrategy.getSubTitle(item));
        helper.setVisible(R.id.iv_more, modelsPickerStrategy.shouldShowMore());
        helper.setImageDrawable(R.id.iv_icon, modelsPickerStrategy.getIconDrawable(item));
        if (modelsPickerStrategy.isMultiple() && item.isSelected()) {
            helper.itemView.setBackgroundColor(getSelectedColor());
        } else {
            helper.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private int getSelectedColor() {
        if (selectedColor == -1) {
            selectedColor = ColorUtils.accentColor() + 536_870_912;
        }
        return selectedColor;
    }

    public void setModelsPickerStrategy(@NonNull ModelsPickerStrategy<T> modelsPickerStrategy) {
        this.modelsPickerStrategy = modelsPickerStrategy;
    }
}
