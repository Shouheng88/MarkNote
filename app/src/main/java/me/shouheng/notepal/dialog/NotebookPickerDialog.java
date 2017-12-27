package me.shouheng.notepal.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import me.shouheng.notepal.R;
import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.provider.NotebookStore;
import me.shouheng.notepal.provider.schema.NotebookSchema;
import me.shouheng.notepal.widget.EmptyView;


/**
 * Created by wangshouheng on 2017/10/5.*/
public class NotebookPickerDialog extends BasePickerDialog<Notebook>{

    private Context context;

    public static NotebookPickerDialog newInstance(Context context) {
        return new NotebookPickerDialog(context);
    }

    private NotebookPickerDialog(Context context) {
        this.context = context;
    }

    @Override
    protected ModelsPickerAdapter<Notebook> prepareAdapter() {
        return new ModelsPickerAdapter<>(context,
                NotebookStore.getInstance(context).getNotebooks(null, NotebookSchema.ADDED_TIME + " DESC "),
                new NotebookPickerStrategy(context));
    }

    @Override
    protected void onCreateDialog(AlertDialog.Builder builder, EmptyView emptyView) {
        builder.setTitle(getString(R.string.pick_notebook));
        builder.setPositiveButton(R.string.cancel, null);
        emptyView.setBottomTitle(getString(R.string.no_notebook_available));
    }
}
