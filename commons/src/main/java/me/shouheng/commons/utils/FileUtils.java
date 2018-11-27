package me.shouheng.commons.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * @author shouh
 * @version $Id: FileUtils, v 0.1 2018/11/21 22:30 shouh Exp$
 */
public class FileUtils {

    /**
     * Get uri from the file.
     *
     * @param context the context
     * @param file the file
     * @param authority the authority. see {@link android.support.v4.content.FileProvider#getUriForFile}
     * @return the uri of a file
     */
    public static Uri getUriFromFile(Context context, File file, String authority) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            return FileProvider.getUriForFile(context, authority, file);
        } else {
            return Uri.fromFile(file);
        }
    }
}
