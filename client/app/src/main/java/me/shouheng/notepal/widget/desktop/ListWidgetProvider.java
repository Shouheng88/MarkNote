package me.shouheng.notepal.widget.desktop;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.SparseArray;
import android.view.View;
import android.widget.RemoteViews;

import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.utils.LogUtils;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.MainActivity;
import me.shouheng.notepal.activity.QuickNoteActivity;
import me.shouheng.notepal.Constants;

public class ListWidgetProvider extends WidgetProvider {

    @Override
    protected RemoteViews getRemoteViews(Context context, int widgetId, boolean isSmall, boolean isSingleLine, SparseArray<PendingIntent> map) {
        LogUtils.d(isSingleLine + " " + isSmall);

        if (isSmall) {
            return configSmall(context, map);
        } else if (isSingleLine) {
            return configSingleLine(context, map);
        } else {
            return configList(context, widgetId, map);
        }
    }

    private RemoteViews configSmall(Context context, SparseArray<PendingIntent> pendingIntentsMap) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout_small);
        views.setOnClickPendingIntent(R.id.iv_launch_app, pendingIntentsMap.get(R.id.iv_launch_app));
        return views;
    }

    private RemoteViews configSingleLine(Context context, SparseArray<PendingIntent> pendingIntentsMap) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        configToolbar(context, views, pendingIntentsMap);
        views.setViewVisibility(R.id.iv_setting, View.GONE);
        return views;
    }

    private RemoteViews configList(Context context, int widgetId, SparseArray<PendingIntent> pendingIntentsMap) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout_list);
        configToolbar(context, views, pendingIntentsMap);

        views.setViewVisibility(R.id.tv_app_name, View.GONE);

        Intent intent = new Intent(context, NotesListWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(R.id.widget_list, intent);

        views.setPendingIntentTemplate(R.id.widget_list, listClickPendingIntent(context, widgetId));

        return views;
    }

    private PendingIntent listClickPendingIntent(Context context, int widgetId) {
        SharedPreferences sharedPreferences = PalmApp.getContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS);
        int id = sharedPreferences.getInt(Constants.PREF_WIDGET_TYPE_PREFIX + String.valueOf(widgetId), ListWidgetType.NOTES_LIST.id);
        ListWidgetType listWidgetType = ListWidgetType.getListWidgetType(id);

        Intent clickIntent = new Intent(context, listWidgetType == ListWidgetType.MINDS_LIST ? QuickNoteActivity.class : MainActivity.class);
        clickIntent.setAction(Constants.ACTION_WIDGET_LIST);

        return PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void configToolbar(Context context, RemoteViews views, SparseArray<PendingIntent> pendingIntentsMap) {
        views.setOnClickPendingIntent(R.id.iv_launch_app, pendingIntentsMap.get(R.id.iv_launch_app));
        views.setOnClickPendingIntent(R.id.iv_add_note, pendingIntentsMap.get(R.id.iv_add_note));
        views.setOnClickPendingIntent(R.id.iv_add_mind, pendingIntentsMap.get(R.id.iv_add_mind));
        views.setOnClickPendingIntent(R.id.iv_add_photo, pendingIntentsMap.get(R.id.iv_add_photo));
        views.setOnClickPendingIntent(R.id.iv_add_sketch, pendingIntentsMap.get(R.id.iv_add_sketch));
        views.setOnClickPendingIntent(R.id.iv_setting, pendingIntentsMap.get(R.id.iv_setting));
        views.setInt(R.id.toolbar, "setBackgroundColor", ColorUtils.primaryColor(context) - 1342177280);
    }
}

