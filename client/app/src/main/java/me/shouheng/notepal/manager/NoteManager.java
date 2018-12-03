package me.shouheng.notepal.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.print.PrintManager;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.webkit.WebView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.shouheng.commons.utils.LogUtils;
import me.shouheng.commons.utils.PalmUtils;
import me.shouheng.commons.utils.TimeUtils;
import me.shouheng.data.entity.Attachment;
import me.shouheng.data.entity.Category;
import me.shouheng.data.entity.Model;
import me.shouheng.data.entity.Note;
import me.shouheng.notepal.Constants;
import me.shouheng.notepal.R;

/**
 * The manger for note witch provided many useful methods to handle the note.
 *
 * Created by WngShhng (shouheng2015@gmail.com) on 2017/11/4.
 * Refactored by WngShhng (shouheng2015@gmail.com) on 2018/12/02. */
public class NoteManager {

    /**
     * The split char used to connect the category code.
     */
    private final static String CATEGORY_SPLIT = ",";

    /**
     * The note title regex expression pattern, use the {@link Constants#REGEX_NOTE_TITLE}.
     */
    private static Pattern titlePattern;

    /**
     * The note preview image regex expression pattern,
     * use the {@link Constants#REGEX_NOTE_PREVIEW_IMAGE}.
     */
    private static Pattern imagePattern;

    /**
     * Get the standard time information for note and any other kinds of model when extends{@link Model}
     *
     * @param model the note
     * @param <T> the type of model
     * @return the time information string
     */
    public static <T extends Model> String getTimeInfo(T model) {
        return PalmUtils.getStringCompact(R.string.text_created)
                + " : " + TimeUtils.getPrettyTime(model.getAddedTime()) + "\n"
                + PalmUtils.getStringCompact(R.string.text_updated)
                + " : " + TimeUtils.getPrettyTime(model.getLastModifiedTime());
    }

    /**
     * Copy the content to tbe clipboard.
     *
     * @param activity the activity used to get the {@link android.content.ClipboardManager}
     * @param content the content to clip
     */
    public static void copy(Activity activity, String content) {
        ClipboardManager clipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        assert clipboardManager != null;
        clipboardManager.setText(content);
    }

    /**
     * Send the information to other app.
     *
     * @param context the context to send intent
     * @param title the title to send
     * @param content the content to send
     * @param attachments the attachments to send
     */
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

        context.startActivity(Intent.createChooser(shareIntent, PalmUtils.getStringCompact(R.string.text_send_to)));
    }

    /**
     * Send file to other app.
     *
     * @param context the context to send intent
     * @param file the file to send
     * @param mimeType the mime type of file
     */
    public static void sendFile(Context context, File file, String mimeType) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType(mimeType);
        shareIntent.putExtra(Intent.EXTRA_STREAM, FileManager.getUriFromFile(context, file));
        context.startActivity(Intent.createChooser(shareIntent, PalmUtils.getStringCompact(R.string.text_send_to)));
    }

    /**
     * Get note title according to the input title and content
     *
     * @param inputTitle the title input
     * @param noteContent the content of note
     * @return the title string
     */
    public static String getTitle(String inputTitle, String noteContent) {
        int titleLength = PalmUtils.getIntegerCompact(R.integer.note_title_max_length);
        if (!TextUtils.isEmpty(inputTitle)) {
            if (inputTitle.length() >= titleLength) {
                return inputTitle.substring(0, titleLength);
            }
            return inputTitle;
        }

        // Use default note title
        if (TextUtils.isEmpty(noteContent)) {
            return PalmUtils.getStringCompact(R.string.text_default_note_name);
        }

        // Get title from note content
        if (titlePattern == null) {
            titlePattern = Pattern.compile(Constants.REGEX_NOTE_TITLE);
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
                if (title.length() >= titleLength) {
                    title = title.substring(0, titleLength);
                }
                return title;
            }
        }

        return PalmUtils.getStringCompact(R.string.text_default_note_name);
    }

    /**
     * Get the preview content from the note content.
     *
     * @param noteContent the note content
     * @return the image file
     */
    public static String getPreview(String noteContent) {
        if (TextUtils.isEmpty(noteContent)) {
            return "";
        }

        int maxLength = PalmUtils.getIntegerCompact(R.integer.note_content_preview_max_length);
        if (noteContent.length() > maxLength) {
            return noteContent.substring(0, maxLength).trim().replace('\n', ' ');
        }

        return noteContent.trim().replace('\n', ' ');
    }

    /**
     * Get note preview image from the content.
     *
     * @param noteContent the note content
     * @return the uri of preview image.
     */
    public static Uri getPreviewImage(String noteContent) {
        if (TextUtils.isEmpty(noteContent)) {
            return null;
        }

        if (imagePattern == null) {
            imagePattern = Pattern.compile(Constants.REGEX_NOTE_PREVIEW_IMAGE);
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

    /**
     * Print the WebView for note to PDF.
     *
     * @param context the context to get {@link PrintManager}
     * @param webView the WebView
     * @param note the note
     */
    public static void printPDF(Context context, WebView webView, Note note) {
        if (PalmUtils.isKitKat()) {
            PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
            if (printManager != null) {
                if (PalmUtils.isLollipop()) {
                    printManager.print("PRINT-NOTE", webView.createPrintDocumentAdapter(note.getTitle()), null);
                    return;
                }
                printManager.print("PRINT-NOTE", webView.createPrintDocumentAdapter(), null);
            }
        }
    }

    /**
     * Connect the category code, which will then be used to get the categories of the note.
     *
     * @param categories the categories
     * @return the result string
     */
    public static String getCategoriesField(List<Category> categories) {
        if (categories == null || categories.isEmpty()) return null;
        int len = categories.size();
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<len; i++) {
            sb.append(categories.get(i).getCode());
            if (i != len - 1) sb.append(CATEGORY_SPLIT);
        }
        LogUtils.d(sb.toString());
        return sb.toString();
    }
}