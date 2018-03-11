package me.shouheng.notepal.dialog.picker;

import android.support.v7.app.AlertDialog;

import java.util.List;

import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.ModelsPickerAdapter;
import me.shouheng.notepal.adapter.picker.NotebookPickerStrategy;
import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.provider.NotebookStore;
import me.shouheng.notepal.provider.schema.NotebookSchema;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.widget.EmptyView;

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
        return NotebookStore.getInstance(getContext()).getNotebooks(null, NotebookSchema.ADDED_TIME + " DESC ");
    }

    @Override
    protected void onCreateDialog(AlertDialog.Builder builder, EmptyView emptyView) {
        builder.setTitle(getString(R.string.pick_notebook));
        builder.setPositiveButton(R.string.text_cancel, null);
        emptyView.setTitle(getString(R.string.no_notebook_available));
        emptyView.setIcon(ColorUtils.tintDrawable(
                getContext().getResources().getDrawable(R.drawable.ic_folder_black_24dp), getImageTintColor()));
    }

    private int getImageTintColor() {
        return getContext().getResources().getColor(ColorUtils.isDarkTheme(getContext())
                ? R.color.dark_theme_empty_icon_tint_color : R.color.light_theme_empty_icon_tint_color);
    }
}
