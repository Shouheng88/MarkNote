package me.shouheng.notepal.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import org.polaric.colorful.BaseActivity;
import org.polaric.colorful.PermissionUtils;

import java.io.File;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.SketchActivity;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.databinding.DialogAttachmentPickerLayoutBinding;
import me.shouheng.notepal.util.AttachmentHelper;
import me.shouheng.notepal.util.FileHelper;
import me.shouheng.notepal.util.PalmUtils;
import me.shouheng.notepal.util.PreferencesUtils;
import me.shouheng.notepal.util.ToastUtils;

/**
 * Created by wangshouheng on 2017/4/7.*/
@SuppressLint("ValidFragment")
public class AttachmentPickerDialog extends DialogFragment {

    private boolean isRecordVisible;
    private boolean isVideoVisible;
    private boolean isAlbumVisible;
    private boolean isFilesVisible;
    private boolean isAddLinkVisible;

    private Fragment mFragment;

    private OnPickAudioSelectedListener onItemSelectedListener;

    private OnAddNetUriSelectedListener onAddNetUriSelectedListener;

    @SuppressLint("ValidFragment")
    public AttachmentPickerDialog(Builder builder) {
        this.mFragment = builder.fragment;
        this.isRecordVisible = builder.isRecordVisible;
        this.isVideoVisible = builder.isVideoVisible;
        this.isFilesVisible = builder.isFilesVisible;
        this.isAlbumVisible = builder.isAlbumVisible;
        this.isAddLinkVisible = builder.isAddLinkVisible;
        this.onItemSelectedListener = builder.onItemSelectedListener;
        this.onAddNetUriSelectedListener = builder.onAddNetUriSelectedListener;
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

        binding.tvLink.setOnClickListener(v -> {
            if (onAddNetUriSelectedListener != null) onAddNetUriSelectedListener.onAddUriSelected();
            dismiss();
        });
        binding.tvLink.setVisibility(isAddLinkVisible ? View.VISIBLE : View.GONE);

        binding.tvTakePhoto.setOnClickListener(v -> resolveCaptureEvent());

        binding.tvRecordSound.setOnClickListener(v -> resolveRecordEvent());
        binding.tvRecordSound.setVisibility(isRecordVisible ? View.VISIBLE : View.GONE);

        binding.tvTakeVideo.setOnClickListener(v -> resolveShotEvent());
        binding.tvTakeVideo.setVisibility(isVideoVisible ? View.VISIBLE : View.GONE);

        binding.tvSketch.setOnClickListener(v -> resolveSketchEvent());

        return new AlertDialog.Builder(getContext())
                .setView(binding.getRoot())
                .create();
    }

    private void resolveAlbumClickEvent() {
        assert getActivity() != null;
        PermissionUtils.checkStoragePermission((BaseActivity) getActivity(), () -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            if (mFragment != null){
                mFragment.startActivityForResult(intent, AttachmentHelper.REQUEST_SELECT_IMAGE);
            } else {
                getActivity().startActivityForResult(intent, AttachmentHelper.REQUEST_SELECT_IMAGE);
            }
            dismiss();
        });
    }

    private void resolveFileClickEvent() {
        assert getActivity() != null;
        PermissionUtils.checkStoragePermission((BaseActivity) getActivity(), () -> {
            Intent intent;
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            if (PalmUtils.isJellyBeanMR2()) intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            intent.setType("*/*");
            if (mFragment != null) {
                mFragment.startActivityForResult(intent, AttachmentHelper.REQUEST_FILES);
            } else {
                getActivity().startActivityForResult(intent, AttachmentHelper.REQUEST_FILES);
            }
            dismiss();
        });
    }

    private void resolveCaptureEvent() {
        assert getActivity() != null;
        PermissionUtils.checkStoragePermission((BaseActivity) getActivity(), () -> {
            File file = FileHelper.createNewAttachmentFile(getActivity(), Constants.MIME_TYPE_IMAGE_EXTENSION);
            if (file == null){
                ToastUtils.makeToast(getActivity(), R.string.failed_to_create_file);
                dismiss();
                return;
            }

            Uri attachmentUri = FileHelper.getUriFromFile(getContext(), file);
            AttachmentHelper.setAttachmentUri(attachmentUri);
            AttachmentHelper.setFilePath(file.getPath());

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, attachmentUri);
            if (mFragment != null){
                mFragment.startActivityForResult(intent, AttachmentHelper.REQUEST_TAKE_PHOTO);
            } else {
                getActivity().startActivityForResult(intent, AttachmentHelper.REQUEST_TAKE_PHOTO);
            }
            dismiss();
        });
    }

    private void resolveRecordEvent() {
        assert getActivity() != null;
        PermissionUtils.checkRecordPermission((BaseActivity) getActivity(), () -> {
            if (onItemSelectedListener != null){
                onItemSelectedListener.onSelectedAudioRecord();
            }
            dismiss();
        });
    }

    private void resolveShotEvent() {
        assert getActivity() != null;
        PermissionUtils.checkStoragePermission((BaseActivity) getActivity(), () -> {
            File file = FileHelper.createNewAttachmentFile(getActivity(), Constants.MIME_TYPE_VIDEO_EXTENSION);
            if (file == null){
                ToastUtils.makeToast(getActivity(), R.string.failed_to_create_file);
                dismiss();
                return;
            }

            Uri attachmentUri = FileHelper.getUriFromFile(getContext(), file);
            AttachmentHelper.setAttachmentUri(attachmentUri);
            AttachmentHelper.setFilePath(file.getPath());

            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, attachmentUri);
            intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, PreferencesUtils.getInstance(getContext()).getVideoSizeLimit() * 1024 * 1024);
            if (mFragment != null){
                mFragment.startActivityForResult(intent, AttachmentHelper.REQUEST_TAKE_VIDEO);
            } else {
                getActivity().startActivityForResult(intent, AttachmentHelper.REQUEST_TAKE_VIDEO);
            }
            dismiss();
        });
    }

    private void resolveSketchEvent() {
        File file = FileHelper.createNewAttachmentFile(getActivity(), Constants.MIME_TYPE_SKETCH_EXTENSION);
        if (file == null) {
            ToastUtils.makeToast(getActivity(), R.string.failed_to_create_file);
            dismiss();
            return;
        }

        Uri attachmentUri = FileHelper.getUriFromFile(getContext(), file);
        String filePath = file.getPath();
        AttachmentHelper.setAttachmentUri(attachmentUri);
        AttachmentHelper.setFilePath(filePath);

        Intent intent = new Intent(getContext(), SketchActivity.class);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, filePath);
        if (mFragment != null){
            mFragment.startActivityForResult(intent, AttachmentHelper.REQUEST_SKETCH);
        } else {
            getActivity().startActivityForResult(intent, AttachmentHelper.REQUEST_SKETCH);
        }
        dismiss();
    }

    public interface OnPickAudioSelectedListener {
        void onSelectedAudioRecord();
    }

    public interface OnAddNetUriSelectedListener {
        void onAddUriSelected();
    }

    public static class Builder {
        private Fragment fragment;

        private boolean isRecordVisible = true;

        private boolean isVideoVisible = true;

        private boolean isAlbumVisible = true;

        private boolean isFilesVisible = true;

        private boolean isAddLinkVisible = true;

        private OnPickAudioSelectedListener onItemSelectedListener;

        private OnAddNetUriSelectedListener onAddNetUriSelectedListener;

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

        public Builder setAddLinkVisible(boolean addLinkVisible) {
            isAddLinkVisible = addLinkVisible;
            return this;
        }

        public Builder setOnItemSelectedListener(OnPickAudioSelectedListener onItemSelectedListener) {
            this.onItemSelectedListener = onItemSelectedListener;
            return this;
        }

        public Builder setOnAddNetUriSelectedListener(OnAddNetUriSelectedListener onAddNetUriSelectedListener) {
            this.onAddNetUriSelectedListener = onAddNetUriSelectedListener;
            return this;
        }

        public AttachmentPickerDialog build() {
            return new AttachmentPickerDialog(this);
        }
    }
}
