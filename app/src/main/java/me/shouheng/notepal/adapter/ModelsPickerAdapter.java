package me.shouheng.notepal.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.picker.ModelsPickerStrategy;
import me.shouheng.notepal.model.Model;

/**
 * Created by wangshouheng on 2017/10/5.*/
public class ModelsPickerAdapter<T extends Model> extends BaseQuickAdapter<T, BaseViewHolder> {

    @NonNull
    private ModelsPickerStrategy<T> modelsPickerStrategy;

    public ModelsPickerAdapter(@Nullable List<T> data) {
        super(R.layout.item_universal_icon_layout, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, T item) {
        helper.setText(R.id.tv_title, modelsPickerStrategy.getTitle(item));
        helper.setText(R.id.tv_sub_title, modelsPickerStrategy.getSubTitle(item));
        helper.setVisible(R.id.iv_more, modelsPickerStrategy.shouldShowMore());
        helper.setImageDrawable(R.id.iv_icon, modelsPickerStrategy.getIconDrawable(item));
    }

    public void setModelsPickerStrategy(@NonNull ModelsPickerStrategy<T> modelsPickerStrategy) {
        this.modelsPickerStrategy = modelsPickerStrategy;
    }
}
