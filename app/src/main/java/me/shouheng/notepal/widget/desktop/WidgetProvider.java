package me.shouheng.notepal.widget.desktop;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.RemoteViews;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.MainActivity;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.util.LogUtils;

public abstract class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        ComponentName thisWidget = new ComponentName(context, getClass());
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int appWidgetId : allWidgetIds) {
            LogUtils.d("WidgetProvider onUpdate() widget " + appWidgetId);
            setLayout(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        LogUtils.d("Widget size changed");
        setLayout(context, appWidgetManager, appWidgetId);
    }

    private void setLayout(Context context, AppWidgetManager appWidgetManager, int widgetId) {

        // Create an Intent to launch DetailActivity
        Intent intentDetail = new Intent(context, MainActivity.class);
        intentDetail.setAction(Constants.ACTION_WIDGET);
        intentDetail.putExtra(Constants.INTENT_WIDGET, widgetId);
        PendingIntent pendingIntentDetail = PendingIntent.getActivity(context, widgetId, intentDetail, PendingIntent.FLAG_CANCEL_CURRENT);

        // Create an Intent to launch ListActivity
        Intent intentList = new Intent(context, MainActivity.class);
        intentList.setAction(Constants.ACTION_WIDGET_SHOW_LIST);
        intentList.putExtra(Constants.INTENT_WIDGET, widgetId);
        PendingIntent pendingIntentList = PendingIntent.getActivity(context, widgetId, intentList, PendingIntent.FLAG_CANCEL_CURRENT);

        // Create an Intent to launch DetailActivity to take a photo
        Intent intentDetailPhoto = new Intent(context, MainActivity.class);
        intentDetailPhoto.setAction(Constants.ACTION_TAKE_PHOTO);
        intentDetailPhoto.putExtra(Constants.INTENT_WIDGET, widgetId);
        PendingIntent pendingIntentDetailPhoto = PendingIntent.getActivity(context, widgetId, intentDetailPhoto, PendingIntent.FLAG_CANCEL_CURRENT);

        // Check various dimensions aspect of widget to choose between layouts
        Bundle options = appWidgetManager.getAppWidgetOptions(widgetId);
        boolean isSmall = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) < 110;
        boolean isSingleLine = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT) < 110;

        // Creation of a map to associate PendingIntent(s) to views
        SparseArray<PendingIntent> map = new SparseArray<>();
        map.put(R.id.list, pendingIntentList);
        map.put(R.id.add, pendingIntentDetail);
        map.put(R.id.camera, pendingIntentDetailPhoto);

        RemoteViews views = getRemoteViews(context, widgetId, isSmall, isSingleLine, map);

        // Tell the AppWidgetManager to perform an update on the current app widget
        appWidgetManager.updateAppWidget(widgetId, views);
    }

    abstract protected RemoteViews getRemoteViews(Context context, int widgetId, boolean isSmall, boolean isSingleLine, SparseArray<PendingIntent> pendingIntentsMap);
}
