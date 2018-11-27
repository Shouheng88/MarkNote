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

import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.notepal.R;

/**
 * Created by wangshouheng on 2017/6/30. */
public class LinkInputDialog extends DialogFragment {

    private OnConfirmClickListener onConfirmClickListener;

    public static LinkInputDialog getInstance(){
        return new LinkInputDialog();
    }

    public static LinkInputDialog getInstance(OnConfirmClickListener onConfirmClickListener){
        LinkInputDialog linkInputDialog = new LinkInputDialog();
        linkInputDialog.setOnConfirmClickListener(onConfirmClickListener);
        return linkInputDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_link_input_layout, null, false);

        final TextInputLayout titleHint = rootView.findViewById(R.id.til_name);
        final TextInputLayout linkHint = rootView.findViewById(R.id.til_hint);

        final EditText etTitle = rootView.findViewById(R.id.et_md_title);
        final EditText etLink = rootView.findViewById(R.id.et_md_link);

        TextView tvConfirm = rootView.findViewById(R.id.tv_md_confirm);
        TextView tvCancel = rootView.findViewById(R.id.tv_md_cancel);

        int accentColor = ColorUtils.accentColor(getContext());
        tvCancel.setTextColor(accentColor);
        tvConfirm.setTextColor(accentColor);

        tvConfirm.setOnClickListener(v -> {
            String titleStr = etTitle.getText().toString().trim();
            String linkStr = etLink.getText().toString().trim();

            if (TextUtils.isEmpty(titleStr)) titleHint.setError(getString(R.string.md_link_title_cannot_empty));
            if (TextUtils.isEmpty(linkStr)) titleHint.setError(getString(R.string.md_link_cannot_empty));

            if (titleHint.isErrorEnabled()) titleHint.setErrorEnabled(false);
            if (linkHint.isErrorEnabled()) linkHint.setErrorEnabled(false);

            if (onConfirmClickListener != null) onConfirmClickListener.onConfirmClick(titleStr, linkStr);

            LinkInputDialog.this.dismiss();
        });

        tvCancel.setOnClickListener(v -> LinkInputDialog.this.dismiss());

        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.md_insert_link)
                .setView(rootView)
                .create();
    }

    public void setOnConfirmClickListener(OnConfirmClickListener onConfirmClickListener) {
        this.onConfirmClickListener = onConfirmClickListener;
    }

    public interface OnConfirmClickListener {
        void onConfirmClick(String title, String link);
    }
}
