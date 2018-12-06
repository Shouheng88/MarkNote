package me.shouheng.notepal.dialog.picker;

import android.support.v7.app.AlertDialog;

import java.util.List;

import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.ModelsPickerAdapter;
import me.shouheng.notepal.adapter.picker.NotebookPickerStrategy;
import me.shouheng.data.entity.Notebook;
import me.shouheng.data.store.NotebookStore;
import me.shouheng.data.schema.NotebookSchema;
import me.shouheng.commons.widget.recycler.EmptyView;

/**
 * Created by wangshouheng on 2017/10/5.*/
public class NotebookPickerDialog extends BasePickerDialog<Notebook> {

    public static NotebookPickerDialog newInstance() {
        return new NotebookPickerDialog();
    }

    @Override
    protected ModelsPickerAdapter<Notebook> prepareAdapter() {
        return new ModelsPickerAdapter<>(getNotebooks(), new NotebookPickerStrategy(getContext()));
    }

    private List<Notebook> getNotebooks() {
        return NotebookStore.getInstance().getNotebooks(null, NotebookSchema.ADDED_TIME + " DESC ");
    }

    @Override
    protected void onCreateDialog(AlertDialog.Builder builder, EmptyView emptyView) {
        builder.setTitle(getString(R.string.notebook_picker_title));
        builder.setPositiveButton(R.string.text_cancel, null);
        emptyView.setTitle(getString(R.string.notebook_picker_empty_message));
        emptyView.setIcon(ColorUtils.tintDrawable(R.drawable.ic_book, getImageTintColor()));
    }

    private int getImageTintColor() {
        return getContext().getResources().getColor(ColorUtils.isDarkTheme()
                ? R.color.dark_theme_empty_icon_tint_color : R.color.light_theme_empty_icon_tint_color);
    }
}
