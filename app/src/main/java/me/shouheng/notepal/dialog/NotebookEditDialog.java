package me.shouheng.notepal.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.color.ColorChooserDialog;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.CommonActivity;
import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.widget.CircleImageView;
import me.shouheng.notepal.widget.WatcherTextView;

/**
 * Created by wangshouheng on 2017/7/23.*/
@SuppressLint("ValidFragment")
public class NotebookEditDialog extends DialogFragment {

    private EditText etNotebookName;
    private LinearLayout llNameEditBG;
    private CircleImageView civNotebookColor;

    private int notebookColor;
    private String notebookName = null;

    private OnConfirmNotebookEditListener onConfirmNotebookEditListener;

    public static NotebookEditDialog newInstance(Context context, Notebook notebook, OnConfirmNotebookEditListener onConfirmNotebookEditListener){
        return new NotebookEditDialog(context, notebook, onConfirmNotebookEditListener);
    }

    private NotebookEditDialog(Context context, Notebook notebook, OnConfirmNotebookEditListener onConfirmNotebookEditListener) {
        this.notebookName = notebook.getTitle();
        this.notebookColor = notebook.getColor();
        this.onConfirmNotebookEditListener = onConfirmNotebookEditListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_notebook_editor_layout, null);

        etNotebookName = rootView.findViewById(R.id.et_notebook_name);
        etNotebookName.setText(TextUtils.isEmpty(notebookName) ? "" : notebookName);
        LinearLayout llCategoryColor = rootView.findViewById(R.id.ll_notebook_color);
        llNameEditBG = rootView.findViewById(R.id.ll_title_background);
        civNotebookColor = rootView.findViewById(R.id.civ_notebook_color);
        WatcherTextView wtv = rootView.findViewById(R.id.watcher);
        wtv.bindEditText(etNotebookName);

        updateUIBySelectedColor(notebookColor);

        llCategoryColor.setOnClickListener(v -> showColorPickerDialog());

        return new AlertDialog.Builder(getContext())
                .setView(rootView)
                .setPositiveButton(R.string.text_confirm, (dialog, which) -> {
                    if (TextUtils.isEmpty(etNotebookName.getText())){
                        ToastUtils.makeToast(getContext(), R.string.title_required);
                        return;
                    }
                    notebookName = etNotebookName.getText().toString();
                    if (onConfirmNotebookEditListener != null){
                        onConfirmNotebookEditListener.onConfirmNotebook(notebookName, notebookColor);
                    }
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.text_cancel, null)
                .create();
    }

    private void showColorPickerDialog(){
        assert getActivity() != null;
        new ColorChooserDialog.Builder((CommonActivity) getActivity(), R.string.pick_notebook_color)
                .preselect(notebookColor)
                .accentMode(false)
                .titleSub(R.string.pick_notebook_color)
                .backButton(R.string.text_back)
                .doneButton(R.string.done_label)
                .cancelButton(R.string.text_cancel)
                .show();
    }

    public void updateUIBySelectedColor(int color){
        notebookColor = color;
        llNameEditBG.setBackgroundColor(color);
        civNotebookColor.setFillingCircleColor(color);
    }

    public void setOnConfirmNotebookEditListener(OnConfirmNotebookEditListener onConfirmNotebookEditListener) {
        this.onConfirmNotebookEditListener = onConfirmNotebookEditListener;
    }

    public interface OnConfirmNotebookEditListener {
        void onConfirmNotebook(String categoryName, int notebookColor);
    }
}
