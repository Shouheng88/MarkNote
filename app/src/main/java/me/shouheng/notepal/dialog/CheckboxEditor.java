package me.shouheng.notepal.dialog;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.DialogCheckboxEditorBinding;

/**
 * Created by shouh on 2018/3/25.*/
public class CheckboxEditor extends DialogFragment {

    private OnGetContentListener onGetContentListener;

    public static CheckboxEditor newInstance(OnGetContentListener onGetContentListener) {
        Bundle args = new Bundle();
        CheckboxEditor fragment = new CheckboxEditor();
        fragment.setArguments(args);
        fragment.setOnGetContentListener(onGetContentListener);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DialogCheckboxEditorBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_checkbox_editor, null, false);

        return new AlertDialog.Builder(getContext())
                .setView(binding.getRoot())
                .setTitle(R.string.text_checkbox)
                .setNegativeButton(R.string.text_cancel, null)
                .setPositiveButton(R.string.text_confirm, (dialogInterface, i) -> {
                    if (onGetContentListener != null) {
                        onGetContentListener.listContent(binding.tvName.getText().toString(), binding.cbChecked.isChecked());
                    }
                })
                .create();
    }

    public void setOnGetContentListener(OnGetContentListener onGetContentListener) {
        this.onGetContentListener = onGetContentListener;
    }

    public interface OnGetContentListener {
        void listContent(String content, boolean isChecked);
    }
}
