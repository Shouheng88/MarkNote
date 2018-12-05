package me.shouheng.notepal.dialog;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;

import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.DialogInputTableLayoutBinding;

/**
 * Created by wangshouheng on 2017/7/1.
 */
public class TableInputDialog extends DialogFragment {

    private OnConfirmClickListener onConfirmClickListener;

    private DialogInputTableLayoutBinding binding;

    public static TableInputDialog getInstance() {
        return new TableInputDialog();
    }

    public static TableInputDialog getInstance(OnConfirmClickListener onConfirmClickListener){
        TableInputDialog tableInputDialog = new TableInputDialog();
        tableInputDialog.setOnConfirmClickListener(onConfirmClickListener);
        return tableInputDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_input_table_layout,
                null,
                false);

        int buttonColor = ColorUtils.accentColor();
        binding.tvMdCancel.setTextColor(buttonColor);
        binding.tvMdConfirm.setTextColor(buttonColor);

        binding.tvMdConfirm.setOnClickListener(v -> onConfirm());

        binding.tvMdCancel.setOnClickListener(v -> dismiss());

        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.note_table_insert)
                .setView(binding.getRoot())
                .create();
    }

    private void onConfirm() {
        String rowNumberStr = binding.etMdRowsNumber.getText().toString().trim();
        String columnNumberStr = binding.etMdColsNumber.getText().toString().trim();

        if (TextUtils.isEmpty(rowNumberStr)) binding.rowNumberHint.setError(getString(R.string.note_table_rows_required));
        if (TextUtils.isEmpty(columnNumberStr)) binding.columnNumberHint.setError(getString(R.string.note_table_cols_required));

        if (binding.rowNumberHint.isErrorEnabled()) binding.rowNumberHint.setErrorEnabled(false);
        if (binding.columnNumberHint.isErrorEnabled()) binding.columnNumberHint.setErrorEnabled(false);

        if (onConfirmClickListener != null) onConfirmClickListener.onConfirmClick(rowNumberStr, columnNumberStr);

        dismiss();
    }

    public void setOnConfirmClickListener(OnConfirmClickListener onConfirmClickListener) {
        this.onConfirmClickListener = onConfirmClickListener;
    }

    public interface OnConfirmClickListener {
        void onConfirmClick(String rows, String cols);
    }
}
