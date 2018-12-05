package me.shouheng.commons.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.List;

import me.shouheng.commons.R;

/**
 * Created by wangshouheng on 2017/3/13. */
public class IntentUtils {

    /**
     * Send file to some platforms. See constants in {@link Intent} for more details.
     *
     * @param context the context to send file
     * @param file the file
     * @param mimeType the mime type of file
     * @param authority the authority see {@link android.support.v4.content.FileProvider#getUriForFile} for details.
     * @param title the title of the popped chooser
     */
    public static void sendFile(Context context, File file, String mimeType, String authority, String title) {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.setType(mimeType);
        i.putExtra(Intent.EXTRA_STREAM, getUriFromFile(context, file, authority));
        context.startActivity(Intent.createChooser(i, title));
    }

    /**
     * Get uri from the file.
     *
     * @param context the context
     * @param file the file
     * @param authority the authority. see {@link android.support.v4.content.FileProvider#getUriForFile}
     * @return the uri of a file
     */
    private static Uri getUriFromFile(Context context, File file, String authority) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            return FileProvider.getUriForFile(context, authority, file);
        } else {
            return Uri.fromFile(file);
        }
    }

    /**
     * Send email to given email address.
     *
     * @param context the context
     * @param address the email address
     * @param subject the subject of email
     * @param body the email body
     */
    public static void sendEmail(Activity context, String address, String subject, String body) {
        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + address));
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, body);

        if (IntentUtils.isAvailable(context, i, null)) {
            context.startActivity(i);
        } else {
            ToastUtils.makeToast(R.string.text_failed_to_resolve_intent);
        }
    }

    /**
     * Open app in market
     *
     * @param context the context
     * @param applicationId the application id
     */
    public static void openInMarket(Context context, String applicationId) {
        String GOOGLE_PLAY_WEB_PAGE = "https://play.google.com/store/apps/details?id=" + applicationId;
        String MARKET_PAGE = "market://details?id=" + applicationId;

        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_PAGE));
        if (IntentUtils.isAvailable(context, i, null)){
            try {
                context.startActivity(i);
            } catch (ActivityNotFoundException ex) {
                ToastUtils.makeToast(R.string.text_failed_to_resolve_intent);
            }
        } else {
            Intent i2 = new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_WEB_PAGE));
            if (IntentUtils.isAvailable(context, i2, null)) {
                launchUrl(context, GOOGLE_PLAY_WEB_PAGE);
            } else {
                ToastUtils.makeToast(R.string.text_failed_to_resolve_intent);
            }
        }
    }

    /**
     * Open the url in browser
     *
     * @param context the context
     * @param url the url
     */
    public static void openWebPage(Context context, String url) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (IntentUtils.isAvailable(context, i, null)) {
            launchUrl(context, url);
        } else {
            ToastUtils.makeToast(R.string.text_failed_to_resolve_intent);
        }
    }

    /**
     * Start activity of given uri and mime type.
     *
     * @param context the context
     * @param uri the uri for intent
     * @param mimeType the mime type for intent
     */
    public static void startActivity(Context context, Uri uri, String mimeType) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, mimeType);
        if (IntentUtils.isAvailable(context, intent, null)) {
            context.startActivity(intent);
        } else {
            ToastUtils.makeToast(R.string.text_failed_to_resolve_intent);
        }
    }

    private static void launchUrl(Context context, String url) {
        int primaryColor = ColorUtils.primaryColor();

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder
                .setToolbarColor(primaryColor)
                .setSecondaryToolbarColor(ColorUtils.calStatusBarColor(primaryColor))
                .build();
        customTabsIntent.launchUrl(context, Uri.parse(url));
    }

    /**
     * Check is the intent is available
     *
     * @param ctx the context
     * @param intent the intent to check
     * @param features the features
     * @return is the intent available
     */
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

    private static boolean checkAction(Intent i, String action) {
        return action.equals(i.getAction());
    }

    public static boolean checkAction(Intent i, String ...actions) {
        for (String action : actions) {
            if (checkAction(i, action)) return true;
        }
        return false;
    }
}