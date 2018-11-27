package me.shouheng.notepal.dialog.picker;

import android.support.v7.app.AlertDialog;

import java.util.LinkedList;
import java.util.List;

import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.ModelsPickerAdapter;
import me.shouheng.notepal.adapter.picker.CategoryPickerStrategy;
import me.shouheng.notepal.model.Category;
import me.shouheng.notepal.widget.EmptyView;

/**
 * Created by shouh on 2018/3/20.*/
public class CategoryPickerDialog extends BasePickerDialog<Category> {

    private List<Category> categories = new LinkedList<>();

    private OnConfirmClickListener onConfirmClickListener;

    private OnAddClickListener onAddClickListener;

    public static CategoryPickerDialog newInstance(List<Category> categories) {
        CategoryPickerDialog dialog = new CategoryPickerDialog();
        dialog.setCategories(categories);
        return dialog;
    }

    @Override
    protected ModelsPickerAdapter<Category> prepareAdapter() {
        return new ModelsPickerAdapter<>(categories, new CategoryPickerStrategy());
    }

    private void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    @Override
    protected void onCreateDialog(AlertDialog.Builder builder, EmptyView emptyView) {
        builder.setTitle(getString(R.string.pick_category));
        builder.setNegativeButton(R.string.text_cancel, null);
        builder.setPositiveButton(R.string.text_ok, (dialogInterface, i) -> {
            if (onConfirmClickListener != null) {
                onConfirmClickListener.onConfirm(getSelected());
            }
        });
        builder.setNeutralButton(R.string.text_add_tags, (dialogInterface, i) -> {
            if (onAddClickListener != null) {
                onAddClickListener.onAdd();
            }
        });
        emptyView.setTitle(getString(R.string.no_category_available));
        emptyView.setIcon(ColorUtils.tintDrawable(
                PalmApp.getDrawableCompact(R.drawable.ic_labels_grey_24dp), getImageTintColor()));
    }

    private int getImageTintColor() {
        return PalmApp.getColorCompact(ColorUtils.isDarkTheme(getContext())
                ? R.color.dark_theme_empty_icon_tint_color : R.color.light_theme_empty_icon_tint_color);
    }

    public void setOnConfirmClickListener(OnConfirmClickListener onConfirmClickListener) {
        this.onConfirmClickListener = onConfirmClickListener;
    }

    public interface OnConfirmClickListener {
        void onConfirm(List<Category> selected);
    }

    public void setOnAddClickListener(OnAddClickListener onAddClickListener) {
        this.onAddClickListener = onAddClickListener;
    }

    public interface OnAddClickListener {
        void onAdd();
    }
}
