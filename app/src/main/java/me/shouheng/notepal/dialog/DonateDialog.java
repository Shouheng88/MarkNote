package me.shouheng.notepal.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import com.bumptech.glide.Glide;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.DialogDonateBinding;

/**
 * Created by wang shouheng on 2018/1/25.*/
public class DonateDialog extends DialogFragment {

    private final static String KEY_CHANNEL = "key_channel";

    private DonateChannel donateChannel;

    private DialogDonateBinding binding;

    public static DonateDialog newInstance(DonateChannel channel) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_CHANNEL, channel);
        DonateDialog fragment = new DonateDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_donate, null, false);

        donateChannel = (DonateChannel) getArguments().get(KEY_CHANNEL);

        configViews();

        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.setting_support_development)
                .setView(binding.getRoot())
                .setPositiveButton(R.string.cancel, null)
                .setNegativeButton(R.string.download_donate_picture, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // todo copy to picture album
                    }
                })
                .create();
    }

    private void configViews() {
        Glide.with(PalmApp.getContext())
                .load(donateChannel == DonateChannel.WeChat ? R.drawable.donate_wechat : R.drawable.donate_ali_pay)
                .centerCrop()
                .crossFade()
                .into(binding.ivDonate);
    }

    public enum DonateChannel {
        AliPay, WeChat
    }
}
