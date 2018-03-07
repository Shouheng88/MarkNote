package me.shouheng.notepal.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.Glide;

import java.io.IOException;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.databinding.DialogMindSnaggingLayoutBinding;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.model.MindSnagging;
import me.shouheng.notepal.model.enums.ModelType;
import me.shouheng.notepal.provider.AttachmentsStore;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.util.FileHelper;
import me.shouheng.notepal.util.ToastUtils;

/**
 * Created by wangshouheng on 2017/8/19. */
public class MindSnaggingDialog extends DialogFragment {

    private final static String KEY_EXTRA_BUILDER = "key_extra_builder";

    private MindSnagging mindSnagging;
    private Attachment attachment;

    private MediaPlayer mPlayer;

    private OnConfirmListener onConfirmListener;
    private OnAddAttachmentListener onAddAttachmentListener;
    private OnAttachmentClickListener onAttachmentClickListener;
    private OnLifeMethodCalledListener onLifeMethodCalledListener;

    private DialogMindSnaggingLayoutBinding binding;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        handleArguments();

        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_mind_snagging_layout, null, false);

        binding.wtv.bindEditText(binding.et);
        binding.et.setText(mindSnagging.getContent());
        binding.et.addTextChangedListener(new EtTextWatcher());
        binding.iv.setOnClickListener(v -> {
            if (onAddAttachmentListener != null) {
                onAddAttachmentListener.onAddAttachment(mindSnagging);
            }
        });

        setAttachment(attachment);

        initButtons();

        binding.bottom.btnNegative.setOnClickListener(v -> dismiss());
        binding.bottom.btnPositive.setOnClickListener(v -> {
            if (onConfirmListener != null){
                mindSnagging.setContent(binding.et.getText().toString());
                onConfirmListener.onConfirm(mindSnagging, attachment);
            }
            dismiss();
        });
        binding.bottom.btnNeutral.setOnClickListener(v -> {
            if (onAddAttachmentListener != null) {
                onAddAttachmentListener.onAddAttachment(mindSnagging);
            }
        });

        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.edit_mind_snagging)
                .setView(binding.getRoot())
                .create();
    }

    private void handleArguments() {
        Bundle bundle;
        Builder builder;
        if ((bundle = getArguments()) != null
                && bundle.containsKey(KEY_EXTRA_BUILDER)
                && (builder = (Builder) bundle.get(KEY_EXTRA_BUILDER)) != null) {
            this.mindSnagging = builder.mindSnagging;
            this.attachment = builder.attachment;
            this.onConfirmListener = builder.onConfirmListener;
            this.onAddAttachmentListener = builder.onAddAttachmentListener;
            this.onAttachmentClickListener = builder.onAttachmentClickListener;
            this.onLifeMethodCalledListener = builder.onLifeMethodCalledListener;
            this.attachment = AttachmentsStore.getInstance(PalmApp.getContext())
                    .getAttachment(ModelType.MIND_SNAGGING, mindSnagging.getCode());
        }
    }

    private void initButtons() {
        binding.bottom.btnNegative.setTextColor(ColorUtils.accentColor(getContext()));

        binding.bottom.btnPositive.setTextColor(Color.GRAY);
        binding.bottom.btnPositive.setEnabled(false);

        if (attachment == null) {
            binding.bottom.btnNeutral.setTextColor(ColorUtils.accentColor(getContext()));
        } else {
            binding.bottom.btnNeutral.setEnabled(false);
            binding.bottom.btnNeutral.setTextColor(Color.GRAY);
        }
    }

    private class EtTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            setCancelable(false);
            if (s.length() == 0 && attachment == null) {
                binding.bottom.btnPositive.setEnabled(false);
                binding.bottom.btnPositive.setTextColor(Color.GRAY);
            } else {
                if (!binding.bottom.btnPositive.isEnabled()) {
                    binding.bottom.btnPositive.setEnabled(true);
                    binding.bottom.btnPositive.setTextColor(ColorUtils.accentColor(getContext()));
                }
            }
        }
    }

    public void setAttachment(final Attachment attachment) {
        if (attachment == null) return;

        // This means the user added a new attachment
        if (this.attachment == null) setCancelable(false);
        this.attachment = attachment;
        mindSnagging.setPicture(attachment.getUri());

        binding.bottom.btnNeutral.setEnabled(false);
        binding.bottom.btnNeutral.setTextColor(Color.GRAY);
        binding.bottom.btnPositive.setEnabled(true);
        binding.bottom.btnPositive.setTextColor(ColorUtils.accentColor(getContext()));

        binding.iv.setVisibility(View.GONE);
        binding.siv.setVisibility(View.VISIBLE);

        if (Constants.MIME_TYPE_AUDIO.equals(attachment.getMineType())){
            binding.siv.setImageResource(attachment.isAudioPlaying() ? R.drawable.stop : R.drawable.play);
        } else {
            Uri thumbnailUri = FileHelper.getThumbnailUri(getContext(), attachment.getUri());
            Glide.with(PalmApp.getContext())
                    .load(thumbnailUri)
                    .centerCrop()
                    .crossFade()
                    .into(binding.siv);
        }

        binding.siv.setOnClickListener(v -> {
            if (Constants.MIME_TYPE_AUDIO.equals(attachment.getMineType())) {
                if (isPlaying()) {
                    stopPlaying();
                } else {
                    startPlaying(attachment);
                }
            }
            if (onAttachmentClickListener != null) {
                onAttachmentClickListener.onClick(attachment);
            }
        });
    }

    private boolean isPlaying() {
        return mPlayer != null && mPlayer.isPlaying();
    }

    private void startPlaying(Attachment attachment) {
        if (mPlayer == null) mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(getContext(), attachment.getUri());
            mPlayer.prepare();
            mPlayer.start();
            notifyPlayingStateChanged(true);
            mPlayer.setOnCompletionListener(mp -> {
                mPlayer = null;
                notifyPlayingStateChanged(false);
            });
        } catch (IOException e) {
            ToastUtils.makeToast(R.string.failed_when_play_audio);
        }
    }

    private void stopPlaying() {
        if (mPlayer != null) {
            notifyPlayingStateChanged(false);
            mPlayer.release();
            mPlayer = null;
        }
    }

    private void notifyPlayingStateChanged(boolean playing) {
        if (attachment != null){
            attachment.setAudioPlaying(playing);
            binding.siv.setImageResource(attachment.isAudioPlaying() ? R.drawable.stop : R.drawable.play);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (isPlaying()) stopPlaying();
        if (onLifeMethodCalledListener != null) onLifeMethodCalledListener.onCancel();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (isPlaying()) stopPlaying();
        if (onLifeMethodCalledListener != null) onLifeMethodCalledListener.onDismiss();
    }

    public static class Builder implements Parcelable {
        private MindSnagging mindSnagging;
        private Attachment attachment;

        private OnConfirmListener onConfirmListener;
        private OnAddAttachmentListener onAddAttachmentListener;
        private OnAttachmentClickListener onAttachmentClickListener;
        private OnLifeMethodCalledListener onLifeMethodCalledListener;

        public Builder() {}

        protected Builder(Parcel in) {
            mindSnagging = in.readParcelable(MindSnagging.class.getClassLoader());
            attachment = in.readParcelable(Attachment.class.getClassLoader());
        }

        public static final Creator<Builder> CREATOR = new Creator<Builder>() {
            @Override
            public Builder createFromParcel(Parcel in) {
                return new Builder(in);
            }

            @Override
            public Builder[] newArray(int size) {
                return new Builder[size];
            }
        };

        public Builder setMindSnagging(MindSnagging mindSnagging) {
            this.mindSnagging = mindSnagging;
            return this;
        }

        public Builder setAttachment(Attachment attachment) {
            this.attachment = attachment;
            return this;
        }

        public Builder setOnAddAttachmentListener(OnAddAttachmentListener onAddAttachmentListener) {
            this.onAddAttachmentListener = onAddAttachmentListener;
            return this;
        }

        public Builder setOnConfirmListener(OnConfirmListener onConfirmListener) {
            this.onConfirmListener = onConfirmListener;
            return this;
        }

        public Builder setOnAttachmentClickListener(OnAttachmentClickListener onAttachmentClickListener) {
            this.onAttachmentClickListener = onAttachmentClickListener;
            return this;
        }

        public Builder setOnLifeMethodCalledListener(OnLifeMethodCalledListener onLifeMethodCalledListener) {
            this.onLifeMethodCalledListener = onLifeMethodCalledListener;
            return this;
        }

        public MindSnaggingDialog build() {
            MindSnaggingDialog dialog = new MindSnaggingDialog();
            Bundle bundle = new Bundle();
            bundle.putParcelable(KEY_EXTRA_BUILDER, this);
            dialog.setArguments(bundle);
            return dialog;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(mindSnagging, flags);
            dest.writeParcelable(attachment, flags);
        }
    }

    public interface OnConfirmListener {
        void onConfirm(MindSnagging mindSnagging, Attachment attachment);
    }

    public interface OnAddAttachmentListener {
        void onAddAttachment(MindSnagging mindSnagging);
    }

    public interface OnAttachmentClickListener {
        void onClick(Attachment attachment);
    }

    public interface OnLifeMethodCalledListener {
        void onCancel();
        void onDismiss();
    }
}
