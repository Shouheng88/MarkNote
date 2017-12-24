package me.shouheng.notepal.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * Created by wangshouheng on 2017/2/25. */
public class ToastUtils {

    private static ProgressDialog progressDialog;

    private static Toast toast;

    public static void makeToast(Context context, String msg){
        if (toast == null){
            toast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
        }
        toast.setText(msg);
        toast.show();
    }

    public static void makeToast(Context context, @StringRes int msgRes){
        if (toast == null){
            toast = Toast.makeText(context.getApplicationContext(), msgRes, Toast.LENGTH_SHORT);
        }
        toast.setText(msgRes);
        toast.show();
    }

    public static void showProgress(Context context, String msg) {
        ProgressDialog progressDialog = new ProgressDialog(context.getApplicationContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(msg);
        progressDialog.show();
        ToastUtils.progressDialog = progressDialog;
    }

    public static void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
