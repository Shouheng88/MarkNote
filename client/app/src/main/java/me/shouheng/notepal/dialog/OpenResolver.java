package me.shouheng.notepal.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import me.shouheng.notepal.Constants;
import me.shouheng.notepal.R;

/**
 * Created by WngShhng (shouheng2015@gmail.com) on 2018/1/3.
 * Refactored by WngShhng (shouheng2015@gmail.com) on 2018/12/01.
 */
public class OpenResolver extends DialogFragment {

    private OnResolverTypeClickListener onResolverTypeClickListener;

    public static OpenResolver newInstance(OnResolverTypeClickListener onResolverTypeClickListener) {
        OpenResolver fragment = new OpenResolver();
        fragment.setOnResolverTypeClickListener(onResolverTypeClickListener);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View root = LayoutInflater.from(getContext()).inflate(R.layout.dialog_open_resolver, null, false);

        root.findViewById(R.id.tv_text).setOnClickListener(view ->
                resolveClicked(Constants.MIME_TYPE_OF_PLAIN_TEXT));
        root.findViewById(R.id.tv_image).setOnClickListener(view ->
                resolveClicked(Constants.MIME_TYPE_OF_IMAGE));
        root.findViewById(R.id.tv_audio).setOnClickListener(view ->
                resolveClicked(Constants.MIME_TYPE_OF_AUDIO));
        root.findViewById(R.id.tv_video).setOnClickListener(view ->
                resolveClicked(Constants.MIME_TYPE_OF_VIDEO));
        root.findViewById(R.id.tv_other).setOnClickListener(view ->
                resolveClicked(Constants.MIME_TYPE_OTHERS));

        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.attachment_open_as)
                .setView(root)
                .create();
    }

    private void setOnResolverTypeClickListener(OnResolverTypeClickListener onResolverTypeClickListener) {
        this.onResolverTypeClickListener = onResolverTypeClickListener;
    }

    private void resolveClicked(String mimeType) {
        if (onResolverTypeClickListener != null) {
            onResolverTypeClickListener.onResolverClicked(mimeType);
        }
    }

    public interface OnResolverTypeClickListener {
        void onResolverClicked(String mimeType);
    }
}
