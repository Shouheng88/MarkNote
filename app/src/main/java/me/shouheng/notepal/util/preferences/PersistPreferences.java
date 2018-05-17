package me.shouheng.notepal.util.preferences;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.Calendar;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;

/**
 * Created by Wang Shouheng on 2017/12/5. */
public class PersistPreferences extends BasePreferences {

    private static PersistPreferences sInstance;

    public static PersistPreferences getInstance() {
        if (sInstance == null) {
            synchronized (PersistPreferences.class) {
                if (sInstance == null){
                    sInstance = new PersistPreferences(PalmApp.getContext());
                }
            }
        }
        return sInstance;
    }

    private PersistPreferences(Context context) {
        super(context);
    }

    public void setAttachmentUri(@NonNull Uri uri) {
        putString(R.string.key_attachment_uri, uri.toString());
    }

    public String getAttachmentUri() {
        return getString(R.string.key_attachment_uri, "");
    }

    public void setAttachmentFilePath(String filePath) {
        putString(R.string.key_attachment_file_path, filePath);
    }

    public String getAttachmentFilePath() {
        return getString(R.string.key_attachment_file_path, "");
    }

    public void setTourActivityShowed(boolean showed) {
        putBoolean(R.string.key_is_tour_activity_showed, showed);
    }

    public boolean isTourActivityShowed() {
        return getBoolean(R.string.key_is_tour_activity_showed, false);
    }
}
