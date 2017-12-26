package me.shouheng.notepal.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.ClipboardManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.model.Model;

/**
 * Created by wangshouheng on 2017/11/4.*/
public class ModelHelper {

    public static <T extends Model> String getTimeInfo(T model) {
        return PalmApp.getContext().getString(R.string.text_created_time) + " : "
                + TimeUtils.getPrettyTime(model.getAddedTime()) + "\n"
                + PalmApp.getContext().getString(R.string.text_last_modified_time) + " : "
                + TimeUtils.getPrettyTime(model.getLastModifiedTime()) + "\n"
                + PalmApp.getContext().getString(R.string.text_last_sync_time) + " : "
                + (model.getLastSyncTime().getTime() == 0 ? "--" : TimeUtils.getPrettyTime(model.getLastModifiedTime()));
    }

    public static void copyToClipboard(Activity ctx, String content) {
        ClipboardManager clipboardManager = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(content);
        ToastUtils.makeToast(ctx, R.string.content_was_copied_to_clipboard);
    }

    public static void share(Context context, String title, String content, List<Attachment> attachments) {
        Intent shareIntent = new Intent();
        if (attachments.size() == 0) {
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
        } else if (attachments.size() == 1) {
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType(attachments.get(0).getMineType());
            shareIntent.putExtra(Intent.EXTRA_STREAM, attachments.get(0).getUri());
        } else {
            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            ArrayList<Uri> uris = new ArrayList<>();
            Map<String, Boolean> mimeTypes = new HashMap<>();
            for (Attachment attachment : attachments) {
                uris.add(attachment.getUri());
                mimeTypes.put(attachment.getMineType(), true);
            }
            shareIntent.setType(mimeTypes.size() > 1 ? "*/*" : (String) mimeTypes.keySet().toArray()[0]);
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        }
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);

        context.startActivity(Intent.createChooser(shareIntent,
                context.getResources().getString(R.string.share_message_chooser)));
    }

    public static <T extends Model> void copyLink(Activity ctx, T model) {
        if (model.getLastSyncTime().getTime() == 0) {
            ToastUtils.makeToast(ctx, R.string.cannot_get_link_of_not_synced_item);
            return;
        }

        // todo 获取指定数据类型的链接的逻辑实现

        ClipboardManager clipboardManager = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(null);
    }
}