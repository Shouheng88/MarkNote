package me.shouheng.notepal.adapter.picker;

import android.content.Context;
import android.graphics.drawable.Drawable;

import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.notepal.R;
import me.shouheng.data.entity.Notebook;

/**
 * Created by wangshouheng on 2017/10/5.*/
public class NotebookPickerStrategy implements ModelsPickerStrategy<Notebook> {

    private Context context;

    private Drawable notebookDrawable;

    public NotebookPickerStrategy(Context context) {
        this.context = context;
    }

    @Override
    public String getTitle(Notebook model) {
        return model.getTitle();
    }

    @Override
    public String getSubTitle(Notebook notebook) {
        return context.getResources().getQuantityString(
                R.plurals.text_notes_number, notebook.getCount(), notebook.getCount());
    }

    @Override
    public Drawable getIconDrawable(Notebook model) {
        return notebookDrawable(model);
    }

    private Drawable notebookDrawable(Notebook notebook) {
        if (notebookDrawable == null) {
            notebookDrawable = ColorUtils.tintDrawable(
                    context.getResources().getDrawable(R.drawable.ic_folder_black_24dp), notebook.getColor());
        }
        return notebookDrawable;
    }

    @Override
    public boolean shouldShowMore() {
        return false;
    }

    @Override
    public boolean isMultiple() {
        return false;
    }
}
