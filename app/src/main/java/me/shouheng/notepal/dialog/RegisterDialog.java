package me.shouheng.notepal.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.ThemedActivity;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.widget.UIRippleButton;


/**
 * Created by wangshouheng on 2017/3/11. */
public class RegisterDialog extends DialogFragment{

    public static RegisterDialog newInstance() {
        return new RegisterDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_register_layout, null);
        final Dialog regDlg =  new AlertDialog.Builder(getActivity())
                .setTitle(String.format(getString(R.string.register_palm_college), getString(R.string.app_name)))
                .setView(rootView)
                .create();
        regDlg.setCancelable(false);
        rootView.findViewById(R.id.cfm_register).setOnClickListener(v -> {
            String strPsd = ((EditText) rootView.findViewById(R.id.in_psd)).getText().toString();
            String strEmail = ((EditText) rootView.findViewById(R.id.in_account)).getText().toString();
            onConfirmRegister(strEmail, strPsd);
        });
        rootView.findViewById(R.id.cancel_register).setOnClickListener(v -> regDlg.dismiss());
        setTheme(rootView);
        return regDlg;
    }

    private void onConfirmRegister(String account, String psd){}

    private void setTheme(View rootView){
        if (! (getActivity() instanceof ThemedActivity)){
            throw new RuntimeException("The activity this fragment associated must extends the ThemedActivity");
        }
        UIRippleButton btnRegister = rootView.findViewById(R.id.cfm_register);
        btnRegister.setBackgroundColor(ColorUtils.primaryColor(getContext()));
        btnRegister.setUnpressedColor(ColorUtils.primaryColor(getContext()));
    }
}
