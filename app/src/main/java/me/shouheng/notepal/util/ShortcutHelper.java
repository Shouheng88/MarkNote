package me.shouheng.notepal.util;

import android.content.Context;
import android.content.Intent;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.ContentActivity;
import me.shouheng.notepal.activity.MainActivity;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.model.Model;
import me.shouheng.notepal.model.Note;

public class ShortcutHelper {

    public static <T extends Model> void addShortcut(Context context, T model) {
        Context mContext = context.getApplicationContext();
        Intent shortcutIntent = new Intent(mContext, MainActivity.class);
        shortcutIntent.putExtra(Constants.EXTRA_CODE, model.getCode());
        shortcutIntent.putExtra(Constants.EXTRA_FRAGMENT, getFragmentToDispatch(model));
        shortcutIntent.setAction(Constants.ACTION_SHORTCUT);
        shortcutIntent.putExtra(ContentActivity.EXTRA_HAS_TOOLBAR, true);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getShortcutName(model));
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(mContext, R.drawable.notepal_note));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

        context.sendBroadcast(addIntent);
    }

    private static <T extends Model> String getShortcutName(T model) {
         if (model instanceof Note) {
            return ((Note) model).getTitle();
        }
        return "PalmCollege";
    }

    private static <T extends Model> String getFragmentToDispatch(T model) {
        if (model instanceof Note) {
            return Constants.VALUE_FRAGMENT_NOTE;
        }
        return "PalmCollege";
    }
}
