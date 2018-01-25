package me.shouheng.notepal.util;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

import me.shouheng.notepal.PalmApp;

/**
 * Created by wangshouheng on 2017/2/25. */
public class ToastUtils {

    private static Toast toast;

    public static void makeToast(String msg) {
        Toast.makeText(PalmApp.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void makeToasts(@StringRes int msgRes) {
        Toast.makeText(PalmApp.getContext(), msgRes, Toast.LENGTH_SHORT).show();
    }

    public static void makeToast(@StringRes int msgRes){
        if (toast == null){
            toast = Toast.makeText(PalmApp.getContext(), msgRes, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msgRes);
        }
        toast.show();
    }

    public static void makeToast(Context context, @StringRes int msgRes){
        if (toast == null){
            toast = Toast.makeText(context.getApplicationContext(), msgRes, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msgRes);
        }
        toast.show();
    }
}
