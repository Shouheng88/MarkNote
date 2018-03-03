package me.shouheng.notepal.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import me.shouheng.notepal.R;
import me.shouheng.notepal.util.ColorUtils;

/**
 * Created by wangshouheng on 2017/7/1. */
public class TableInputDialog extends DialogFragment{

    private OnConfirmClickListener onConfirmClickListener;

    public static TableInputDialog getInstance(){
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
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_link_input_layout, null, false);

        TextView tvConfirm = rootView.findViewById(R.id.tv_md_confirm);
        TextView tvCancel = rootView.findViewById(R.id.tv_md_cancel);

        int buttonColor = ColorUtils.accentColor(getContext());
        tvCancel.setTextColor(buttonColor);
        tvConfirm.setTextColor(buttonColor);

        final TextInputLayout rowNumberHint = rootView.findViewById(R.id.rowNumberHint);
        final TextInputLayout columnNumberHint = rootView.findViewById(R.id.columnNumberHint);

        final EditText etRows = rootView.findViewById(R.id.et_md_rows_number);
        final EditText etCols = rootView.findViewById(R.id.et_md_cols_number);

        tvConfirm.setOnClickListener(v -> {
            String rowNumberStr = etRows.getText().toString().trim();
            String columnNumberStr = etCols.getText().toString().trim();

            if (TextUtils.isEmpty(rowNumberStr)) rowNumberHint.setError(getString(R.string.md_rows_cannot_empty));
            if (TextUtils.isEmpty(columnNumberStr)) columnNumberHint.setError(getString(R.string.md_cols_cannot_empty));

            if (rowNumberHint.isErrorEnabled()) rowNumberHint.setErrorEnabled(false);
            if (columnNumberHint.isErrorEnabled()) columnNumberHint.setErrorEnabled(false);

            if (onConfirmClickListener != null) onConfirmClickListener.onConfirmClick(rowNumberStr, columnNumberStr);

            TableInputDialog.this.dismiss();
        });

        tvCancel.setOnClickListener(v -> TableInputDialog.this.dismiss());

        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.md_insert_table)
                .setView(rootView)
                .create();
    }

    public void setOnConfirmClickListener(OnConfirmClickListener onConfirmClickListener) {
        this.onConfirmClickListener = onConfirmClickListener;
    }

    public interface OnConfirmClickListener {
        void onConfirmClick(String rows, String cols);
    }
}
