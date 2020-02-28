package me.shouheng.notepal.desktop;

import android.app.PendingIntent;
import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.widget.RemoteViews;

import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.notepal.R;

public class SimpleWidgetProvider extends BaseWidgetProvider {

    @Override
    protected RemoteViews getRemoteViews(
            Context context, int widgetId, boolean isSmall,
            boolean isSingleLine, SparseArray<PendingIntent> map) {
        if (isSmall) {
            return configSmall(context, map);
        } else {
            return configSingleLine(context, map);
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

    private void configToolbar(Context context, RemoteViews views, SparseArray<PendingIntent> pendingIntentsMap) {
        views.setOnClickPendingIntent(R.id.iv_launch_app, pendingIntentsMap.get(R.id.iv_launch_app));
        views.setOnClickPendingIntent(R.id.iv_add_note, pendingIntentsMap.get(R.id.iv_add_note));
        views.setOnClickPendingIntent(R.id.iv_add_mind, pendingIntentsMap.get(R.id.iv_add_mind));
        views.setOnClickPendingIntent(R.id.iv_add_photo, pendingIntentsMap.get(R.id.iv_add_photo));
        views.setOnClickPendingIntent(R.id.iv_add_sketch, pendingIntentsMap.get(R.id.iv_add_sketch));
        views.setOnClickPendingIntent(R.id.iv_setting, pendingIntentsMap.get(R.id.iv_setting));
        views.setInt(R.id.toolbar, "setBackgroundColor", ColorUtils.primaryColor() - 1342177280);
    }
}

