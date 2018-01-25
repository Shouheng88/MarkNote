package me.shouheng.notepal.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.List;

import me.shouheng.notepal.R;
import me.shouheng.notepal.config.Constants;

/**
 * Created by wangshouheng on 2017/3/13. */
public class IntentUtils {

    public static void sendEmail(Activity context, String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + Constants.DEVELOPER_EMAIL));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
//        intent.putExtra(Intent.EXTRA_EMAIL, Constants.DEVELOPER_EMAIL);

        if (IntentUtils.isAvailable(context, intent, null)) {
            context.startActivity(intent);
        } else {
            ModelHelper.copyToClipboard(context, "mailto:" + Constants.DEVELOPER_EMAIL + "\n" + subject + ":\n" + body);
            ToastUtils.makeToasts(R.string.failed_to_resolve_intent);
            ToastUtils.makeToasts(R.string.content_was_copied_to_clipboard);
        }
    }

    public static boolean isAvailable(Context ctx, Intent intent, String[] features) {
        final PackageManager mgr = ctx.getPackageManager();
        List<ResolveInfo> list = mgr.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        boolean res = list.size() > 0;
        if (features != null) {
            for (String feature : features) {
                res = res && mgr.hasSystemFeature(feature);
            }
        }
        return res;
    }

    /**
     * Checks Intent's action
     *
     * @param i Intent to ckeck
     * @param action Action to compare with
     * @return */
    public static boolean checkAction(Intent i, String action) {
        return action.equals(i.getAction());
    }

    /**
     * Checks Intent's actions
     *
     * @param i Intent to ckeck
     * @param actions Multiple actions to compare with
     * @return*/
    public static boolean checkAction(Intent i, String... actions) {
        for (String action : actions) {
            if (checkAction(i, action)) return true;
        }
        return false;
    }
}