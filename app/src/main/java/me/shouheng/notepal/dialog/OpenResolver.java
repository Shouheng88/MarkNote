package me.shouheng.notepal.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import java.io.Serializable;

import me.shouheng.notepal.R;

/**
 * Created by wang shouheng on 2018/1/3.*/
public class OpenResolver extends DialogFragment {

    private final static String KEY_EXTRA_RESOLVER_LISTENER = "key_resolver_listener";

    private OnResolverTypeClickListener onResolverTypeClickListener;

    public static OpenResolver newInstance(OnResolverTypeClickListener onResolverTypeClickListener) {
        Bundle args = new Bundle();
        OpenResolver fragment = new OpenResolver();
        args.putSerializable(KEY_EXTRA_RESOLVER_LISTENER, onResolverTypeClickListener);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        handleArgument();

        View root = LayoutInflater.from(getContext()).inflate(R.layout.dialog_open_resolver, null, false);

        root.findViewById(R.id.tv_text).setOnClickListener(view -> resolveClicked(MimeType.Text));
        root.findViewById(R.id.tv_image).setOnClickListener(view -> resolveClicked(MimeType.Image));
        root.findViewById(R.id.tv_audio).setOnClickListener(view -> resolveClicked(MimeType.Audio));
        root.findViewById(R.id.tv_video).setOnClickListener(view -> resolveClicked(MimeType.Video));
        root.findViewById(R.id.tv_other).setOnClickListener(view -> resolveClicked(MimeType.Other));

        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.openas)
                .setView(root)
                .create();
    }

    private void handleArgument() {
        Bundle bundle;
        if ((bundle = getArguments()) != null && bundle.containsKey(KEY_EXTRA_RESOLVER_LISTENER)) {
            onResolverTypeClickListener = (OnResolverTypeClickListener) bundle.get(KEY_EXTRA_RESOLVER_LISTENER);
        }
    }

    private void resolveClicked(MimeType mimeType) {
        if (onResolverTypeClickListener != null) {
            onResolverTypeClickListener.onResolverClicked(mimeType);
        }
    }

    public enum MimeType {
        Text("text/plain"),
        Image("image/*"),
        Audio("audio/*"),
        Video("video/*"),
        Other("*/*");

        public final String mimeType;

        MimeType(String mimeType) {
            this.mimeType = mimeType;
        }
    }

    public interface OnResolverTypeClickListener extends Serializable {
        void onResolverClicked(MimeType mimeType);
    }
}
