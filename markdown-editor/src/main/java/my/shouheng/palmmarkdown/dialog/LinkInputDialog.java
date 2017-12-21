package my.shouheng.palmmarkdown.dialog;

import android.app.Dialog;
import android.graphics.Color;
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

import my.shouheng.palmmarkdown.R;

/**
 * Created by wangshouheng on 2017/6/30. */
public class LinkInputDialog extends DialogFragment{

    private int buttonColor = Color.BLACK;

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
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.view_common_input_link_view, null, false);

        final TextInputLayout titleHint = (TextInputLayout) rootView.findViewById(R.id.inputNameHint);
        final TextInputLayout linkHint = (TextInputLayout) rootView.findViewById(R.id.inputHint);

        final EditText etTitle = (EditText) rootView.findViewById(R.id.et_md_title);
        final EditText etLink = (EditText) rootView.findViewById(R.id.et_md_link);

        TextView tvConfirm = (TextView) rootView.findViewById(R.id.tv_md_confirm);
        TextView tvCancel = (TextView) rootView.findViewById(R.id.tv_md_cancel);

        tvCancel.setTextColor(buttonColor);
        tvConfirm.setTextColor(buttonColor);

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titleStr = etTitle.getText().toString().trim();
                String linkStr = etLink.getText().toString().trim();

                if (TextUtils.isEmpty(titleStr)) titleHint.setError(getString(R.string.md_link_title_cannot_empty));
                if (TextUtils.isEmpty(linkStr)) titleHint.setError(getString(R.string.md_link_cannot_empty));

                if (titleHint.isErrorEnabled()) titleHint.setErrorEnabled(false);
                if (linkHint.isErrorEnabled()) linkHint.setErrorEnabled(false);

                if (onConfirmClickListener != null) onConfirmClickListener.onConfirmClick(titleStr, linkStr);

                LinkInputDialog.this.dismiss();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinkInputDialog.this.dismiss();
            }
        });

        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.md_insert_link)
                .setView(rootView)
                .create();
    }

    public LinkInputDialog setButtonColor(int buttonColor){
        this.buttonColor = buttonColor;
        return this;
    }

    public void setOnConfirmClickListener(OnConfirmClickListener onConfirmClickListener) {
        this.onConfirmClickListener = onConfirmClickListener;
    }

    public interface OnConfirmClickListener {
        void onConfirmClick(String title, String link);
    }
}
