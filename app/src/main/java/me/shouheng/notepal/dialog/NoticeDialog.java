package me.shouheng.notepal.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import me.shouheng.notepal.R;

/**
 * Created by wang shouheng on 2018/1/25. */
public class NoticeDialog extends DialogFragment {

    public static NoticeDialog newInstance() {
        Bundle args = new Bundle();
        NoticeDialog fragment = new NoticeDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.text_tips)
                .setPositiveButton(R.string.text_confirm, null)
                .create();
    }
}
