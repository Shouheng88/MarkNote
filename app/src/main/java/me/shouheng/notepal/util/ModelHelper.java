package me.shouheng.notepal.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.model.Location;
import me.shouheng.notepal.model.MindSnagging;
import me.shouheng.notepal.model.Model;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.viewmodel.CategoryViewModel;
import me.shouheng.notepal.widget.FlowLayout;

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

    public static void share(Context context, MindSnagging mindSnagging) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, mindSnagging.getContent());

        if (mindSnagging.getPicture() != null) {
            shareIntent.setType(FileHelper.getMimeTypeInternal(context, mindSnagging.getPicture()));
            shareIntent.putExtra(Intent.EXTRA_STREAM, mindSnagging.getPicture());
        }

        context.startActivity(Intent.createChooser(shareIntent, context.getResources().getString(R.string.share_message_chooser)));
    }

    public static <T extends Model> void copyLink(Activity ctx, T model) {
        if (model.getLastSyncTime().getTime() == 0) {
            ToastUtils.makeToast(R.string.cannot_get_link_of_not_synced_item);
            return;
        }

        ClipboardManager clipboardManager = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(null);
    }

    public static String getFormatedLocation(Location location) {
        return location.getCountry() + "|"
                + location.getProvince() + "|"
                + location.getCity() + "|"
                + location.getDistrict();
    }

    public static void showStatistic(Context context, Note note) {
        View root = LayoutInflater.from(context).inflate(R.layout.dialog_stats, null, false);
        LinearLayout llStats = root.findViewById(R.id.ll_stats);
        addStat(context, llStats, context.getString(R.string.text_created_time), TimeUtils.getPrettyTime(note.getAddedTime()));
        addStat(context, llStats, context.getString(R.string.text_last_modified_time), TimeUtils.getPrettyTime(note.getLastModifiedTime()));
        addStat(context, llStats, context.getString(R.string.text_last_sync_time), (note.getLastSyncTime().getTime() == 0 ? "--" : TimeUtils.getPrettyTime(note.getLastModifiedTime())));
        addStat(context, llStats, context.getString(R.string.text_chars_number), String.valueOf(note.getContent().length()));
        new AlertDialog.Builder(context)
                .setTitle(R.string.text_statistic)
                .setView(root)
                .setPositiveButton(R.string.text_confirm, null)
                .create()
                .show();
    }

    private static void addStat(Context context, LinearLayout llStats, String name, String result) {
        LinearLayout llStat = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.item_stat, null, false);
        ((TextView) llStat.findViewById(R.id.tv_name)).setText(name);
        ((TextView) llStat.findViewById(R.id.tv_result)).setText(result);
        llStats.addView(llStat);
    }

    public static void showLabels(Context context, String tags) {
        View root = LayoutInflater.from(context).inflate(R.layout.dialog_tags, null, false);
        FlowLayout flowLayout = root.findViewById(R.id.fl_labels);
        addTagsToLayout(context, flowLayout, tags);
        new AlertDialog.Builder(context)
                .setTitle(R.string.text_tags)
                .setView(root)
                .setPositiveButton(R.string.text_confirm, null)
                .create()
                .show();
    }

    private static void addTagsToLayout(Context context, FlowLayout flowLayout, String stringTags){
        if (TextUtils.isEmpty(stringTags)) return;
        String[] tags = stringTags.split(CategoryViewModel.CATEGORY_SPLIT);
        for (String tag : tags) addTagToLayout(context, flowLayout, tag);
    }

    private static  void addTagToLayout(Context context, FlowLayout flowLayout, String tag){
        int margin = ViewUtils.dp2Px(context, 2f);
        int padding = ViewUtils.dp2Px(context, 5f);
        TextView tvLabel = new TextView(context);
        tvLabel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(tvLabel.getLayoutParams());
        params.setMargins(margin, margin, margin, margin);
        tvLabel.setLayoutParams(params);
        tvLabel.setPadding(padding, 0, padding, 0);
        tvLabel.setBackgroundResource(ColorUtils.isDarkTheme(context) ? R.drawable.label_background_dark : R.drawable.label_background);
        tvLabel.setText(tag);
        flowLayout.addView(tvLabel);
    }
}