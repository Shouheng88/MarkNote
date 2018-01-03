package my.shouheng.palmmarkdown.tools;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class GenieUtils {

    public static void startActivityFailSafe(Context context, Intent intent) {
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException ignore) {}
    }

    public static void startActivityFailSafe(Context context, Intent intent, Bundle bundle) {
        try {
            context.startActivity(intent, bundle);
        } catch (ActivityNotFoundException ignore) {}
    }

    public static void startActivityForResultFailSafe(Activity activity, Intent intent, int reqCode) {
        try {
            activity.startActivityForResult(intent, reqCode);
        } catch (ActivityNotFoundException ignore) {}
    }
}
