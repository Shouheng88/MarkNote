package me.shouheng.notepal.dialog;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.DialogNoticeBinding;

/**
 * Created by wang shouheng on 2018/1/25. */
public class NoticeDialog extends DialogFragment {

    private DialogNoticeBinding binding;

    public static NoticeDialog newInstance() {
        Bundle args = new Bundle();
        NoticeDialog fragment = new NoticeDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_notice, null, false);
        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.text_to_user)
                .setView(binding.getRoot())
                .setPositiveButton(R.string.text_confirm, null)
                .create();
    }
}
