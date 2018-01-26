package me.shouheng.notepal.widget.desktop;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.SparseArray;
import android.widget.RemoteViews;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.MainActivity;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.util.LogUtils;

public class ListWidgetProvider extends WidgetProvider {

    @Override
    protected RemoteViews getRemoteViews(Context context, int widgetId, boolean isSmall, boolean isSingleLine, SparseArray<PendingIntent> pendingIntentsMap) {
        LogUtils.d(isSingleLine + " " + isSmall);
        RemoteViews views;
        if (isSmall) {
            views = new RemoteViews(context.getPackageName(), R.layout.widget_layout_small);
            views.setOnClickPendingIntent(R.id.iv_launch_app, pendingIntentsMap.get(R.id.iv_launch_app));
        } else if (isSingleLine) {
            views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setOnClickPendingIntent(R.id.add, pendingIntentsMap.get(R.id.add));
            views.setOnClickPendingIntent(R.id.iv_launch_app, pendingIntentsMap.get(R.id.iv_launch_app));
            views.setOnClickPendingIntent(R.id.camera, pendingIntentsMap.get(R.id.camera));
            views.setInt(R.id.toolbar, "setBackgroundColor", ColorUtils.primaryColor(context));
        } else {
            views = new RemoteViews(context.getPackageName(), R.layout.widget_layout_list);
            views.setOnClickPendingIntent(R.id.add, pendingIntentsMap.get(R.id.add));
            views.setOnClickPendingIntent(R.id.iv_launch_app, pendingIntentsMap.get(R.id.iv_launch_app));
            views.setOnClickPendingIntent(R.id.camera, pendingIntentsMap.get(R.id.camera));

            Intent intent = new Intent(context, NotesListWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            views.setRemoteAdapter(R.id.widget_list, intent);

            Intent clickIntent = new Intent(context, MainActivity.class);
            clickIntent.setAction(Constants.ACTION_WIDGET);
            PendingIntent clickPI = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_list, clickPI);

            views.setInt(R.id.toolbar, "setBackgroundColor", ColorUtils.primaryColor(context));
        }
        return views;
    }
}

