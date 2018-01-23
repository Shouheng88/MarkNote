package me.shouheng.notepal.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.Glide;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.DialogMindSnaggingLayoutBinding;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.model.MindSnagging;
import me.shouheng.notepal.model.enums.ModelType;
import me.shouheng.notepal.provider.AttachmentsStore;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.util.FileHelper;

/**
 * Created by wangshouheng on 2017/8/19. */
@SuppressLint("ValidFragment")
public class MindSnaggingDialog extends DialogFragment {

    private MindSnagging mindSnagging;
    private Attachment attachment;

    private OnConfirmListener onConfirmListener;
    private OnAddAttachmentListener onAddAttachmentListener;
    private OnAttachmentClickListener onAttachmentClickListener;

    private DialogMindSnaggingLayoutBinding binding;

    public MindSnaggingDialog(Builder builder) {
        this.mindSnagging = builder.mindSnagging;
        this.attachment = builder.attachment;
        this.onConfirmListener = builder.onConfirmListener;
        this.onAddAttachmentListener = builder.onAddAttachmentListener;
        this.onAttachmentClickListener = builder.onAttachmentClickListener;

        this.attachment = AttachmentsStore.getInstance(PalmApp.getContext()).getAttachment(ModelType.MIND_SNAGGING, mindSnagging.getCode());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
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

        binding.bottom.btnNeutral.setEnabled(false);
        binding.bottom.btnNeutral.setTextColor(Color.GRAY);

        binding.bottom.btnPositive.setEnabled(true);
        binding.bottom.btnPositive.setTextColor(ColorUtils.accentColor(getContext()));

        mindSnagging.setPicture(attachment.getUri());
        binding.iv.setVisibility(View.GONE);
        binding.siv.setVisibility(View.VISIBLE);
        Uri thumbnailUri = FileHelper.getThumbnailUri(getContext(), attachment);
        Glide.with(PalmApp.getContext())
                .load(thumbnailUri)
                .centerCrop()
                .crossFade()
                .into(binding.siv);

        binding.siv.setOnClickListener(v -> {
            if (onAttachmentClickListener != null) {
                onAttachmentClickListener.onClick(attachment);
            }
        });
    }

    public static class Builder {
        private MindSnagging mindSnagging;

        private Attachment attachment;

        private OnConfirmListener onConfirmListener;

        private OnAddAttachmentListener onAddAttachmentListener;

        private OnAttachmentClickListener onAttachmentClickListener;

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

        public MindSnaggingDialog build() {
            return new MindSnaggingDialog(this);
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
}
