package me.shouheng.notepal.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.notepal.R;


/**
 * Created by wangshouheng on 2017/3/15.
 */
@SuppressLint("ValidFragment")
public class SimpleEditDialog extends DialogFragment {

    private OnAcceptListener onAcceptListener;

    private String content = "", previousContent = "";

    private EditText etContent;
    private TextView tv;

    private boolean isNumeric;

    private Integer maxLength;

    public static SimpleEditDialog newInstance(String content, OnAcceptListener onAcceptListener){
        return new SimpleEditDialog(content, onAcceptListener);
    }

    public SimpleEditDialog(String content, OnAcceptListener onAcceptListener) {
        if (content != null) {
            this.content = content;
            this.previousContent = content;
        }
        this.onAcceptListener = onAcceptListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View dlgRootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_simple_edit_layout, null);

        tv = dlgRootView.findViewById(R.id.tv);
        tv.setTextColor(ColorUtils.accentColor());
        int len = (content == null ? 0 : content.length());
        if (maxLength != null) {
            String s = len + "/" + maxLength;
            tv.setText(s);
        } else {
            tv.setText(String.valueOf(len));
        }

        etContent = dlgRootView.findViewById(R.id.et_content);
        etContent.setText(content);
        if (isNumeric){
            etContent.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
            etContent.setSingleLine(true);
        }

        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (previousContent.equals(s.toString())){
                    SimpleEditDialog.this.setCancelable(true);
                } else {
                    SimpleEditDialog.this.setCancelable(false);
                }
                if (maxLength != null) {
                    String sb = s.length() + "/" + maxLength;
                    tv.setText(sb);
                } else {
                    tv.setText(String.valueOf(s.length()));
                }
            }
        });

        if (maxLength != null) {
            etContent.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxLength)});
        }

        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.text_edit)
                .setView(dlgRootView)
                .setPositiveButton(R.string.text_confirm, (dialog, which) -> {
                    if (onAcceptListener != null){
                        onAcceptListener.onAccept(etContent.getText().toString());
                    }
                })
                .setNegativeButton(R.string.text_give_up, (dialog, which) -> SimpleEditDialog.this.dismiss())
                .create();
    }

    public SimpleEditDialog setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
        if (etContent != null) {
            etContent.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxLength)});
        }
        return this;
    }

    public SimpleEditDialog setInputTypeNumeric(boolean numeric){
        this.isNumeric = numeric;
        return this;
    }

    public interface OnAcceptListener {
        void onAccept(String content);
    }
}
