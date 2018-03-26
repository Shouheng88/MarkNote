package me.shouheng.notepal.dialog;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;

import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.DialogMathJaxEditorBinding;
import me.shouheng.notepal.util.ToastUtils;

/**
 * Created by shouh on 2018/3/25.*/
public class MathJaxEditor extends DialogFragment {

    private OnGetMathJaxListener onGetMathJaxListener;

    public static MathJaxEditor newInstance(OnGetMathJaxListener onGetMathJaxListener) {
        Bundle args = new Bundle();
        MathJaxEditor fragment = new MathJaxEditor();
        fragment.setArguments(args);
        fragment.setOnGetMathJaxListener(onGetMathJaxListener);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DialogMathJaxEditorBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_math_jax_editor, null, false);

        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.text_math_jax)
                .setView(binding.getRoot())
                .setPositiveButton(R.string.text_confirm, (dialogInterface, i) -> {
                    String exp = binding.tvExp.getText().toString();
                    if (TextUtils.isEmpty(exp))  {
                        ToastUtils.makeToast(R.string.invalid_input_tips);
                        return;
                    }

                    if (onGetMathJaxListener != null) {
                        onGetMathJaxListener.onGetMathJax(exp, binding.cbChecked.isChecked());
                    }
                })
                .setNegativeButton(R.string.text_cancel, null)
                .create();
    }

    public void setOnGetMathJaxListener(OnGetMathJaxListener onGetMathJaxListener) {
        this.onGetMathJaxListener = onGetMathJaxListener;
    }

    public interface OnGetMathJaxListener {
        void onGetMathJax(String exp, boolean isSingleLine);
    }
}
