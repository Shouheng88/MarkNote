package my.shouheng.palmmarkdown.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import my.shouheng.palmmarkdown.R;

/**
 * Created by wang shouheng on 2018/1/3.*/
@SuppressLint("ValidFragment")
public class OpenResolver extends DialogFragment {

    private OnResolverTypeClickListener onResolverTypeClickListener;

    public static OpenResolver newInstance(OnResolverTypeClickListener onResolverTypeClickListener) {
        Bundle args = new Bundle();
        OpenResolver fragment = new OpenResolver(onResolverTypeClickListener);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("ValidFragment")
    public OpenResolver(OnResolverTypeClickListener onResolverTypeClickListener) {
        this.onResolverTypeClickListener = onResolverTypeClickListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View root = LayoutInflater.from(getContext()).inflate(R.layout.view_common_open_resolver, null, false);
        root.findViewById(R.id.tv_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onResolverTypeClickListener != null) {
                    onResolverTypeClickListener.onResolverClicked(MimeType.Text);
                }
            }
        });
        root.findViewById(R.id.tv_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onResolverTypeClickListener != null) {
                    onResolverTypeClickListener.onResolverClicked(MimeType.Image);
                }
            }
        });
        root.findViewById(R.id.tv_audio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onResolverTypeClickListener != null) {
                    onResolverTypeClickListener.onResolverClicked(MimeType.Audio);
                }
            }
        });
        root.findViewById(R.id.tv_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onResolverTypeClickListener != null) {
                    onResolverTypeClickListener.onResolverClicked(MimeType.Video);
                }
            }
        });
        root.findViewById(R.id.tv_other).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onResolverTypeClickListener != null) {
                    onResolverTypeClickListener.onResolverClicked(MimeType.Other);
                }
            }
        });
        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.openas)
                .setView(root)
                .create();
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

    public interface OnResolverTypeClickListener {
        void onResolverClicked(MimeType mimeType);
    }
}
