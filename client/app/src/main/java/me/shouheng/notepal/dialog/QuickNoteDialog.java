package me.shouheng.notepal.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.kennyc.bottomsheet.BottomSheet;
import com.kennyc.bottomsheet.BottomSheet.Builder;
import com.kennyc.bottomsheet.BottomSheetListener;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

import me.shouheng.commons.activity.PermissionActivity;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.utils.PermissionUtils;
import me.shouheng.commons.utils.PermissionUtils.Permission;
import me.shouheng.commons.utils.ToastUtils;
import me.shouheng.data.entity.Attachment;
import me.shouheng.data.entity.QuickNote;
import me.shouheng.data.model.enums.ModelType;
import me.shouheng.data.store.AttachmentsStore;
import me.shouheng.notepal.Constants;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.DialogQuickNoteBinding;
import me.shouheng.notepal.manager.FileManager;
import me.shouheng.notepal.util.AttachmentHelper;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by wangshouheng on 2017/8/19.
 */
public class QuickNoteDialog extends DialogFragment implements AttachmentHelper.OnAttachingFileListener {

    public final static String ARGS_KEY_QUICK_NOTE = "__args_key_quick_note";

    private QuickNote quickNote;
    private Attachment attachment;

    private MediaPlayer mPlayer;
    private DialogInteraction interaction;
    private DialogQuickNoteBinding binding;
    private Button btnPos, btnNeg, btnNeu;

    public static QuickNoteDialog newInstance(@NonNull QuickNote quickNote, DialogInteraction interaction) {
        Bundle args = new Bundle();
        args.putSerializable(ARGS_KEY_QUICK_NOTE, quickNote);
        QuickNoteDialog fragment = new QuickNoteDialog();
        fragment.setArguments(args);
        fragment.setInteraction(interaction);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        assert args != null;
        this.quickNote = (QuickNote) args.getSerializable(ARGS_KEY_QUICK_NOTE);
        // use the attachment from database as default
        this.attachment = AttachmentsStore.getInstance()
                .getAttachment(ModelType.MIND_SNAGGING, quickNote.getCode());

        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_quick_note, null, false);

        binding.wtv.bindEditText(binding.et);
        binding.et.setText(quickNote.getContent());
        binding.et.addTextChangedListener(new EtTextWatcher());

        setupAttachment(attachment);

        // region region : custom dialog
        AlertDialog dialog = new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                .setView(binding.getRoot())
                .setNeutralButton(R.string.text_attachment, null)
                .setPositiveButton(R.string.text_confirm, null)
                .setNegativeButton(R.string.text_cancel, null)
                .create();

        dialog.setOnShowListener(dlg -> {
            btnPos = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                if (interaction != null) {
                    quickNote.setContent(binding.et.getText().toString());
                    interaction.onConfirm(getDialog(), quickNote, attachment);
                }
            });

            btnNeg = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> {
                if (interaction != null) {
                    interaction.onCancel(getDialog());
                }
            });

            btnNeu = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> showAttachmentPicker());

            btnNeg.setTextColor(ColorUtils.accentColor());
            btnPos.setTextColor(Color.GRAY);
            btnPos.setEnabled(false);
            if (attachment == null) {
                btnNeu.setTextColor(ColorUtils.accentColor());
            } else {
                btnNeu.setEnabled(false);
                btnNeu.setTextColor(Color.GRAY);
            }
        });
        // endregion custom dialog

        return dialog;
    }

    @Override
    public void onAttachingFileErrorOccurred(Attachment attachment) {
        ToastUtils.makeToast(R.string.text_failed_to_save_attachment);
    }

    @Override
    public void onAttachingFileFinished(Attachment attachment) {
        if (AttachmentHelper.checkAttachment(attachment)) {
            setupAttachment(attachment);
        } else {
            ToastUtils.makeToast(R.string.text_failed_to_save_attachment);
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
                btnPos.setEnabled(false);
                btnPos.setTextColor(Color.GRAY);
            } else {
                if (!btnPos.isEnabled()) {
                    btnPos.setEnabled(true);
                    btnPos.setTextColor(ColorUtils.accentColor());
                }
            }
        }
    }

    private void showAttachmentPicker() {
        new Builder(Objects.requireNonNull(getContext()))
                .setTitle(R.string.text_pick)
                .setStyle(ColorUtils.isDarkTheme() ? R.style.BottomSheet_Dark : R.style.BottomSheet)
                .setMenu(ColorUtils.getThemedBottomSheetMenu(getContext(), R.menu.attachment_picker))
                .setListener(new BottomSheetListener() {
                    @Override
                    public void onSheetShown(@NonNull BottomSheet bottomSheet, @Nullable Object o) {}

                    @Override
                    public void onSheetItemSelected(@NonNull BottomSheet bottomSheet, MenuItem menuItem, @Nullable Object o) {
                        switch (menuItem.getItemId()) {
                            case R.id.item_pick_from_album: {
                                Activity activity = getActivity();
                                if (activity != null) {
                                    PermissionUtils.checkStoragePermission((PermissionActivity) activity,
                                            () -> AttachmentHelper.pickOneFromCustomAlbum(QuickNoteDialog.this));
                                }
                                break;
                            }
                            case R.id.item_pick_take_a_photo: {
                                Activity activity = getActivity();
                                if (activity != null) {
                                    PermissionUtils.checkPermissions((PermissionActivity) activity,
                                            () -> AttachmentHelper.takeAPhoto(QuickNoteDialog.this),
                                            Permission.STORAGE, Permission.CAMERA);
                                }
                                break;
                            }
                            case R.id.item_pick_create_sketch: {
                                Activity activity = getActivity();
                                if (activity != null) {
                                    PermissionUtils.checkStoragePermission((PermissionActivity) activity,
                                            () -> AttachmentHelper.createSketch(QuickNoteDialog.this));
                                }
                                break;
                            }
                        }
                    }

                    @Override
                    public void onSheetDismissed(@NonNull BottomSheet bottomSheet, @Nullable Object o, int i) {}
                })
                .show();
    }

    /**
     * Set the attachment to display in the dialog
     *
     * @param attachment the attachment
     */
    private void setupAttachment(final Attachment attachment) {
        // already have an attachment
        if (attachment == null) return;

        // this means the user added a new attachment
        if (this.attachment == null) setCancelable(false);
        this.attachment = attachment;
        quickNote.setPicture(attachment.getUri());

        btnNeu.setEnabled(false);
        btnNeu.setTextColor(Color.GRAY);
        btnPos.setEnabled(true);
        btnPos.setTextColor(ColorUtils.accentColor());

        // display attachment in dialog
        binding.siv.setVisibility(View.VISIBLE);
        if (Constants.MIME_TYPE_AUDIO.equals(attachment.getMineType())){
            binding.siv.setImageResource(attachment.isAudioPlaying() ? R.drawable.stop : R.drawable.play);
        } else {
            Uri thumbnailUri = FileManager.getThumbnailUri(getContext(), attachment.getUri());
            Glide.with(PalmApp.getContext())
                    .load(thumbnailUri)
                    .transition(withCrossFade())
                    .into(binding.siv);
        }

        // setup attachment click event
        binding.siv.setOnClickListener(v -> {
            if (Constants.MIME_TYPE_AUDIO.equals(attachment.getMineType())) {
                if (isPlaying()) {
                    stopPlaying();
                } else {
                    startPlaying(attachment);
                }
            } else {
                AttachmentHelper.resolveClickEvent(getContext(),
                        attachment, Collections.singletonList(attachment), attachment.getName());
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
            ToastUtils.makeToast(R.string.attachment_play_record_failed);
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

    private void setInteraction(DialogInteraction interaction) {
        this.interaction = interaction;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (isPlaying()) stopPlaying();
        if (interaction != null) interaction.onCancel();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (isPlaying()) stopPlaying();
        if (interaction != null) interaction.onDismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AttachmentHelper.onActivityResult(this, requestCode, resultCode, data);
    }

    public interface DialogInteraction {

        default void onCancel() { }

        default void onDismiss() { }

        void onCancel(Dialog dialog);

        void onConfirm(Dialog dialog, QuickNote quickNote, Attachment attachment);
    }
}
