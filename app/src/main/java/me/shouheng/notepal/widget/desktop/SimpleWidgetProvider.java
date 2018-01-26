package me.shouheng.notepal.widget.desktop;

import android.app.PendingIntent;
import android.content.Context;
import android.util.SparseArray;
import android.widget.RemoteViews;

import me.shouheng.notepal.R;
import me.shouheng.notepal.util.ColorUtils;

public class SimpleWidgetProvider extends WidgetProvider {

    @Override
    protected RemoteViews getRemoteViews(Context mContext, int widgetId, boolean isSmall, boolean isSingleLine, SparseArray<PendingIntent> pendingIntentsMap) {
        RemoteViews views;
        if (isSmall) {
            views = new RemoteViews(mContext.getPackageName(), R.layout.widget_layout_small);
            views.setOnClickPendingIntent(R.id.iv_launch_app, pendingIntentsMap.get(R.id.iv_launch_app));
        } else {
            views = new RemoteViews(mContext.getPackageName(), R.layout.widget_layout);
            views.setOnClickPendingIntent(R.id.iv_add_note, pendingIntentsMap.get(R.id.iv_add_note));
            views.setOnClickPendingIntent(R.id.iv_launch_app, pendingIntentsMap.get(R.id.iv_launch_app));
            views.setOnClickPendingIntent(R.id.iv_add_photo, pendingIntentsMap.get(R.id.iv_add_photo));
            views.setInt(R.id.toolbar, "setBackgroundColor", ColorUtils.primaryColor(mContext));
        }
        return views;
    }
}

