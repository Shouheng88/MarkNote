package me.shouheng.notepal.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * Created by wangshouheng on 2017/3/13. */
public class IntentChecker {

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