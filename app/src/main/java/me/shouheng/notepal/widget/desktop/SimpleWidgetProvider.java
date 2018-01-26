package me.shouheng.notepal.widget.desktop;

import android.app.PendingIntent;
import android.content.Context;
import android.util.SparseArray;
import android.widget.RemoteViews;

import me.shouheng.notepal.R;
import me.shouheng.notepal.util.ColorUtils;

public class SimpleWidgetProvider extends WidgetProvider {

    @Override
    protected RemoteViews getRemoteViews(Context context, int widgetId, boolean isSmall, boolean isSingleLine, SparseArray<PendingIntent> pendingIntentsMap) {
        if (isSmall) {
            return configSmall(context, pendingIntentsMap);
        } else {
            return configSingleLine(context, pendingIntentsMap);
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
        return views;
    }

    private void configToolbar(Context context, RemoteViews views, SparseArray<PendingIntent> pendingIntentsMap) {
        views.setOnClickPendingIntent(R.id.iv_launch_app, pendingIntentsMap.get(R.id.iv_launch_app));
        views.setOnClickPendingIntent(R.id.iv_add_note, pendingIntentsMap.get(R.id.iv_add_note));
        views.setOnClickPendingIntent(R.id.iv_add_mind, pendingIntentsMap.get(R.id.iv_add_mind));
        views.setOnClickPendingIntent(R.id.iv_add_photo, pendingIntentsMap.get(R.id.iv_add_photo));
        views.setOnClickPendingIntent(R.id.iv_setting, pendingIntentsMap.get(R.id.iv_setting));
        views.setInt(R.id.toolbar, "setBackgroundColor", ColorUtils.primaryColor(context));
    }
}

