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

        // Create an Intent to add note
        Intent intentAddNote = new Intent(context, MainActivity.class);
        intentAddNote.setAction(Constants.ACTION_WIDGET);
        intentAddNote.putExtra(Constants.INTENT_WIDGET, widgetId);
        PendingIntent pendingIntentAddNote = PendingIntent.getActivity(context, widgetId, intentAddNote, PendingIntent.FLAG_CANCEL_CURRENT);

        // Create an Intent to add a photo
        Intent intentAddPhoto = new Intent(context, MainActivity.class);
        intentAddPhoto.setAction(Constants.ACTION_TAKE_PHOTO);
        intentAddPhoto.putExtra(Constants.INTENT_WIDGET, widgetId);
        PendingIntent pendingIntentAddPhoto = PendingIntent.getActivity(context, widgetId, intentAddPhoto, PendingIntent.FLAG_CANCEL_CURRENT);

        // Create an Intent to launch App
        Intent intentLaunchApp = new Intent(context, MainActivity.class);
        intentLaunchApp.setAction(Constants.ACTION_WIDGET_LAUNCH_APP);
        intentLaunchApp.putExtra(Constants.INTENT_WIDGET, widgetId);
        PendingIntent pendingIntentLaunchApp = PendingIntent.getActivity(context, widgetId, intentLaunchApp, PendingIntent.FLAG_CANCEL_CURRENT);

        // Check various dimensions aspect of widget to choose between layouts
        Bundle options = appWidgetManager.getAppWidgetOptions(widgetId);
        boolean isSmall = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) < 110;
        boolean isSingleLine = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT) < 110;

        // Creation of a map to associate PendingIntent(s) to views
        SparseArray<PendingIntent> map = new SparseArray<>();
        map.put(R.id.iv_launch_app, pendingIntentLaunchApp);
        map.put(R.id.iv_add_note, pendingIntentAddNote);
        map.put(R.id.iv_add_photo, pendingIntentAddPhoto);

        RemoteViews views = getRemoteViews(context, widgetId, isSmall, isSingleLine, map);

        // Tell the AppWidgetManager to perform an update on the current app widget
        appWidgetManager.updateAppWidget(widgetId, views);
    }

    abstract protected RemoteViews getRemoteViews(Context context, int widgetId, boolean isSmall, boolean isSingleLine, SparseArray<PendingIntent> pendingIntentsMap);
}
