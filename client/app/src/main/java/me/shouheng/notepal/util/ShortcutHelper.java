package me.shouheng.notepal.util;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import me.shouheng.data.entity.Note;
import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.MainActivity;

import static me.shouheng.notepal.Constants.SHORTCUT_ACTION_VIEW_NOTE;
import static me.shouheng.notepal.Constants.SHORTCUT_EXTRA_NOTE_CODE;

public class ShortcutHelper {

    /**
     * TODO handle for the next releases.
     * 1. Check the shortcut support for app versions, behaviors for pre API 25 and after API 25;
     * 2. Change the Note icon.
     *
     * @param context context
     * @param note the note model
     */
    public static void addShortcut(Context context, @NonNull Note note) {
        Context mContext = context.getApplicationContext();
        Intent shortcutIntent = new Intent(mContext, MainActivity.class);
        shortcutIntent.putExtra(SHORTCUT_EXTRA_NOTE_CODE, note.getCode());
        shortcutIntent.setAction(SHORTCUT_ACTION_VIEW_NOTE);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, note.getTitle());
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(mContext, R.drawable.note_shortcut));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

        context.sendBroadcast(addIntent);
    }
}
