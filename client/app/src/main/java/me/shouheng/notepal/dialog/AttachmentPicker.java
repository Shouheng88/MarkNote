package me.shouheng.notepal.dialog;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import java.io.Serializable;

import me.shouheng.commons.activity.PermissionActivity;
import me.shouheng.commons.utils.PermissionUtils;
import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.DialogAttachmentPickerLayoutBinding;
import me.shouheng.notepal.util.AttachmentHelper;

/**
 * The attachment picker dialog
 *
 * Created by WngShhng (shouheng2015@gmail.com) on 2017/4/7.
 * Refactored by WngShhng (shouheng2015@gmail.com) on 2018/12/01. */
public class AttachmentPicker extends DialogFragment {

    private boolean isRecordVisible;
    private boolean isVideoVisible;
    private boolean isAlbumVisible;
    private boolean isFilesVisible;

    private Fragment mFragment;

    private OnPickAudioSelectedListener onItemSelectedListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DialogAttachmentPickerLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_attachment_picker_layout, null, false);

        binding.tvAlbum.setOnClickListener(v -> resolveAlbumClickEvent());
        binding.tvAlbum.setVisibility(isAlbumVisible ? View.VISIBLE : View.GONE);

        binding.tvFile.setOnClickListener(v -> resolveFileClickEvent());
        binding.tvFile.setVisibility(isFilesVisible ? View.VISIBLE : View.GONE);

        binding.tvTakePhoto.setOnClickListener(v -> resolveCaptureEvent());

        binding.tvRecordSound.setOnClickListener(v -> resolveRecordEvent());
        binding.tvRecordSound.setVisibility(isRecordVisible ? View.VISIBLE : View.GONE);

        binding.tvTakeVideo.setVisibility(isVideoVisible ? View.VISIBLE : View.GONE);

        binding.tvSketch.setOnClickListener(v -> resolveSketchEvent());

        return new AlertDialog.Builder(getContext())
                .setView(binding.getRoot())
                .create();
    }

    private void setBuilder(Builder builder) {
        this.mFragment = builder.fragment;
        this.isRecordVisible = builder.isRecordVisible;
        this.isVideoVisible = builder.isVideoVisible;
        this.isFilesVisible = builder.isFilesVisible;
        this.isAlbumVisible = builder.isAlbumVisible;
        this.onItemSelectedListener = builder.onItemSelectedListener;
    }

    private void resolveAlbumClickEvent() {
        assert getActivity() != null;
        PermissionUtils.checkStoragePermission((PermissionActivity) getActivity(), () -> {
            if (mFragment != null){
                AttachmentHelper.pickFromAlbum(mFragment);
            } else {
                AttachmentHelper.pickFromAlbum(getActivity());
            }
            dismiss();
        });
    }

    private void resolveFileClickEvent() {
        assert getActivity() != null;
        PermissionUtils.checkStoragePermission((PermissionActivity) getActivity(), () -> {
            if (mFragment != null) {
                AttachmentHelper.pickFiles(mFragment);
            } else {
                AttachmentHelper.pickFiles(getActivity());
            }
            dismiss();
        });
    }

    private void resolveCaptureEvent() {
        assert getActivity() != null;
        PermissionUtils.checkStoragePermission((PermissionActivity) getActivity(), () -> {
            if (mFragment != null) {
                AttachmentHelper.capture(mFragment);
            } else {
                AttachmentHelper.capture(getActivity());
            }
            dismiss();
        });
    }

    private void resolveRecordEvent() {
        assert getActivity() != null;
        PermissionUtils.checkRecordPermission((PermissionActivity) getActivity(), () -> {
            if (onItemSelectedListener != null){
                onItemSelectedListener.onSelectedAudioRecord();
            }
            dismiss();
        });
    }

    private void resolveSketchEvent() {
        assert getActivity() != null;
        PermissionUtils.checkStoragePermission((PermissionActivity) getActivity(), () -> {
            if (mFragment != null){
                AttachmentHelper.sketch(mFragment);
            } else {
                AttachmentHelper.sketch(getActivity());
            }
            dismiss();
        });
    }

    public static class Builder implements Serializable {
        private Fragment fragment;

        private boolean isRecordVisible = true;
        private boolean isVideoVisible = true;
        private boolean isAlbumVisible = true;
        private boolean isFilesVisible = true;

        private OnPickAudioSelectedListener onItemSelectedListener;

        public Builder() {}

        public Builder(Fragment fragment) {
            this.fragment = fragment;
        }

        public Builder setRecordVisible(boolean recordVisible) {
            isRecordVisible = recordVisible;
            return this;
        }

        public Builder setVideoVisible(boolean videoVisible) {
            isVideoVisible = videoVisible;
            return this;
        }

        public Builder setAlbumVisible(boolean albumVisible) {
            isAlbumVisible = albumVisible;
            return this;
        }

        public Builder setFilesVisible(boolean filesVisible) {
            isFilesVisible = filesVisible;
            return this;
        }

        public Builder setOnItemSelectedListener(OnPickAudioSelectedListener onItemSelectedListener) {
            this.onItemSelectedListener = onItemSelectedListener;
            return this;
        }

        public AttachmentPicker build() {
            AttachmentPicker dialog = new AttachmentPicker();
            dialog.setBuilder(this);
            return dialog;
        }
    }

    public interface OnPickAudioSelectedListener {
        void onSelectedAudioRecord();
    }
}
