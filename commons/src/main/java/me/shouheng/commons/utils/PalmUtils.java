package me.shouheng.commons.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by WngShhng on 2017/12/23.
 */
public class PalmUtils {

    public static boolean isAlive(Fragment fragment) {
        return fragment != null
                && fragment.isAdded()
                && fragment.getActivity() != null
                && !fragment.getActivity().isFinishing();
    }

    public static void copy(Activity ctx, String content) {
        ClipboardManager clipboardManager = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
        assert clipboardManager != null;
        clipboardManager.setText(content);
    }

    public static int parseInteger(String intString, int defaultValue) {
        int number;
        try {
            number = TextUtils.isEmpty(intString) ? defaultValue : Integer.parseInt(intString);
        } catch (NumberFormatException e) {
            number = defaultValue;
        }
        return number;
    }

    public static int getStatusBarHeight(Resources r) {
        int resourceId = r.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            return r.getDimensionPixelSize(resourceId);
        return 0;
    }

    public static View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
    }
}
