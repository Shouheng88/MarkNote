package me.shouheng.notepal.util.preferences;

import android.content.Context;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;

public class NotePreferences extends BasePreferences {

    private static NotePreferences sInstance;

    public static NotePreferences getInstance() {
        if (sInstance == null) {
            synchronized (NotePreferences.class) {
                if (sInstance == null){
                    sInstance = new NotePreferences(PalmApp.getContext());
                }
            }
        }
        return sInstance;
    }

    private NotePreferences(Context context) {
        super(context);
    }

    public void setShowNoteExpanded(boolean isExpanded) {
        putBoolean(R.string.key_key_show_note_expanded, isExpanded);
    }

    public boolean isNoteExpanded() {
        return getBoolean(R.string.key_key_show_note_expanded, true);
    }

    public void setNoteFileExtension(String extension) {
        putString(R.string.key_note_file_extension, extension);
    }

    public String getNoteFileExtension() {
        return "." + getString(R.string.key_note_file_extension, "md");
    }

    public boolean isImageAutoCompress() {
        return getBoolean(R.string.key_auto_compress_image, true);
    }
}
