package me.shouheng.notepal.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutInfo.Builder;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.annotation.NonNull;

import me.shouheng.data.entity.Note;
import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.MainActivity;

import static me.shouheng.notepal.Constants.SHORTCUT_ACTION_VIEW_NOTE;
import static me.shouheng.notepal.Constants.SHORTCUT_EXTRA_NOTE_CODE;

public class ShortcutHelper {

    /**
     *  Create shortcut for note.
     *
     *  @param context context
     * @param note the note model
     */
    public static void createShortcut(Context context, @NonNull Note note) {
        Context mContext = context.getApplicationContext();
        Intent shortcutIntent = new Intent(mContext, MainActivity.class);
        shortcutIntent.putExtra(SHORTCUT_EXTRA_NOTE_CODE, note.getCode());
        shortcutIntent.setAction(SHORTCUT_ACTION_VIEW_NOTE);

        if (VERSION.SDK_INT >= VERSION_CODES.N_MR1) {
            ShortcutManager mShortcutManager = context.getSystemService(ShortcutManager.class);
            if (mShortcutManager != null && VERSION.SDK_INT >= VERSION_CODES.O) {
                if (mShortcutManager.isRequestPinShortcutSupported()) {
                    ShortcutInfo pinShortcutInfo = new Builder(context, String.valueOf(note.getCode()))
                            .setShortLabel(note.getTitle())
                            .setLongLabel(note.getTitle())
                            .setIntent(shortcutIntent)
                            .setIcon(Icon.createWithResource(context, R.drawable.ic_launcher_round))
                            .build();

                    Intent pinnedShortcutCallbackIntent = mShortcutManager.createShortcutResultIntent(pinShortcutInfo);

                    PendingIntent successCallback = PendingIntent.getBroadcast(context, /* request code */ 0,
                            pinnedShortcutCallbackIntent, /* flags */ 0);

                    mShortcutManager.requestPinShortcut(pinShortcutInfo, successCallback.getIntentSender());
                }
            } else {
                createShortcutOld(context, shortcutIntent, note);
            }
        } else {
            createShortcutOld(context, shortcutIntent, note);
        }
    }

    /**
     * Use the old style way to create shortcut
     *
     * @param context the context
     * @param shortcutIntent the shortcut intent
     * @param note the note for shortcut
     */
    private static void createShortcutOld(Context context, Intent shortcutIntent, Note note) {
        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, note.getTitle());
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(context, R.drawable.ic_launcher_round));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        context.sendBroadcast(addIntent);
    }
}
