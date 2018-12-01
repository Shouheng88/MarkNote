package me.shouheng.notepal.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.ClipboardManager;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.shouheng.commons.utils.PalmUtils;
import me.shouheng.commons.utils.TimeUtils;
import me.shouheng.data.entity.Attachment;
import me.shouheng.data.entity.Model;
import me.shouheng.notepal.Constants;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.config.TextLength;

/**
 * Created by wangshouheng on 2017/11/4.*/
public class ModelHelper {

    @Nullable
    private static Pattern titlePattern;

    @Nullable
    private static Pattern imagePattern;

    public static <T extends Model> String getTimeInfo(T model) {
        return PalmUtils.getStringCompact(R.string.text_created)
                + " : " + TimeUtils.getPrettyTime(model.getAddedTime()) + "\n"
                + PalmUtils.getStringCompact(R.string.text_updated)
                + " : " + TimeUtils.getPrettyTime(model.getLastModifiedTime());
    }

    public static void copy(Activity activity, String content) {
        ClipboardManager clipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(content);
    }

    public static void send(Context context, String title, String content, List<Attachment> attachments) {
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
                context.getResources().getString(R.string.text_send_to)));
    }

    public static void shareFile(Context context, File file, String mimeType) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType(mimeType);
        shareIntent.putExtra(Intent.EXTRA_STREAM, FileHelper.getUriFromFile(context, file));
        context.startActivity(Intent.createChooser(shareIntent, context.getResources().getString(R.string.text_send_to)));
    }

    public static String getNoteTitle(String inputTitle, String noteContent) {
        if (!TextUtils.isEmpty(inputTitle)) {
            if (inputTitle.length() >= TextLength.TITLE_TEXT_LENGTH.length) {
                return inputTitle.substring(0, TextLength.TITLE_TEXT_LENGTH.length);
            }
            return inputTitle;
        }

        // Use default note title
        if (TextUtils.isEmpty(noteContent)) {
            return PalmApp.getStringCompact(R.string.text_default_note_name);
        }

        // Get title from note content
        if (titlePattern == null) {
            titlePattern = Pattern.compile(Constants.TITLE_REGEX);
        }
        Matcher matcher = titlePattern.matcher(noteContent);
        if (matcher.find()) {
            String mdTitle = matcher.group();
            char[] chars = mdTitle.toCharArray();
            int i = 0;
            for (char c : chars) {
                if (c != '#' && c != ' ') {
                    break;
                }
                i++;
            }
            if (i < chars.length) {
                String title = mdTitle.substring(i);
                // The length of the matched title must be
                if (title.length() >= TextLength.TITLE_TEXT_LENGTH.length) {
                    title = title.substring(0, TextLength.TITLE_TEXT_LENGTH.length);
                }
                return title;
            }
        }

        // Use default note title
        return PalmApp.getStringCompact(R.string.text_default_note_name);
    }

    public static String getNotePreview(String noteContent) {
        if (TextUtils.isEmpty(noteContent)) {
            return "";
        }

        if (noteContent.length() > TextLength.NOTE_CONTENT_PREVIEW_LENGTH.length) {
            return noteContent.substring(0, TextLength.NOTE_CONTENT_PREVIEW_LENGTH.length).trim().replace('\n', ' ');
        }

        return noteContent.trim().replace('\n', ' ');
    }

    public static Uri getNotePreviewImage(String noteContent) {
        if (TextUtils.isEmpty(noteContent)) {
            return null;
        }

        if (imagePattern == null) {
            imagePattern = Pattern.compile(Constants.IMAGE_REGEX);
        }
        Matcher matcher = imagePattern.matcher(noteContent);
        if (matcher.find()) {
            String str = matcher.group();
            if (!TextUtils.isEmpty(str)) {
                int len = str.length();
                str = str.substring(str.lastIndexOf('(') + 1, len - 1);
                return Uri.parse(str);
            }
        }
        return null;
    }
}