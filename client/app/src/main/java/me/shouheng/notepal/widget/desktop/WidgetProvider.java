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

import me.shouheng.commons.utils.LogUtils;
import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.MainActivity;
import me.shouheng.notepal.activity.ConfigActivity;
import me.shouheng.notepal.activity.QuickActivity;
import me.shouheng.notepal.Constants;

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
        Bundle options = appWidgetManager.getAppWidgetOptions(widgetId);
        boolean isSmall = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) < 110;
        boolean isSingleLine = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT) < 110;

        SparseArray<PendingIntent> map = new SparseArray<>();
        map.put(R.id.iv_launch_app, pendingIntentLaunchApp(context, widgetId));
        map.put(R.id.iv_add_note, pendingIntentAddNote(context, widgetId));
        map.put(R.id.iv_add_mind, pendingIntentAddMind(context, widgetId));
        map.put(R.id.iv_add_photo, pendingIntentAddPhoto(context, widgetId));
        map.put(R.id.iv_add_sketch, pendingIntentAddSketch(context, widgetId));
        map.put(R.id.iv_setting, pendingIntentSetting(context, widgetId));

        RemoteViews views = getRemoteViews(context, widgetId, isSmall, isSingleLine, map);

        appWidgetManager.updateAppWidget(widgetId, views);
    }

    private PendingIntent pendingIntentAddNote(Context context, int widgetId) {
        Intent intentAddNote = new Intent(context, MainActivity.class);
        intentAddNote.setAction(Constants.ACTION_ADD_NOTE);
        intentAddNote.putExtra(Constants.INTENT_WIDGET, widgetId);
        return PendingIntent.getActivity(context, widgetId, intentAddNote, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private PendingIntent pendingIntentAddPhoto(Context context, int widgetId) {
        Intent intentAddPhoto = new Intent(context, MainActivity.class);
        intentAddPhoto.setAction(Constants.ACTION_TAKE_PHOTO);
        intentAddPhoto.putExtra(Constants.INTENT_WIDGET, widgetId);
        return PendingIntent.getActivity(context, widgetId, intentAddPhoto, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private PendingIntent pendingIntentAddSketch(Context context, int widgetId) {
        Intent intentAddPhoto = new Intent(context, MainActivity.class);
        intentAddPhoto.setAction(Constants.ACTION_ADD_SKETCH);
        intentAddPhoto.putExtra(Constants.INTENT_WIDGET, widgetId);
        return PendingIntent.getActivity(context, widgetId, intentAddPhoto, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private PendingIntent pendingIntentLaunchApp(Context context, int widgetId) {
        Intent intentLaunchApp = new Intent(context, MainActivity.class);
        intentLaunchApp.setAction(Constants.ACTION_WIDGET_LAUNCH_APP);
        intentLaunchApp.putExtra(Constants.INTENT_WIDGET, widgetId);
        return PendingIntent.getActivity(context, widgetId, intentLaunchApp, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private PendingIntent pendingIntentAddMind(Context context, int widgetId) {
        Intent intentAddMind = new Intent(context, QuickActivity.class);
        intentAddMind.setAction(Constants.ACTION_ADD_MIND);
        intentAddMind.putExtra(Constants.INTENT_WIDGET, widgetId);
        return PendingIntent.getActivity(context, widgetId, intentAddMind, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private PendingIntent pendingIntentSetting(Context context, int widgetId) {
        Intent intentSetting = new Intent(context, ConfigActivity.class);
        intentSetting.setAction(Constants.ACTION_CONFIG);
        intentSetting.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        intentSetting.putExtra(Constants.EXTRA_CONFIG_SWITCH_ENABLE, false);
        return PendingIntent.getActivity(context, widgetId, intentSetting, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    abstract protected RemoteViews getRemoteViews(
            Context context, int widgetId, boolean isSmall, boolean isSingleLine, SparseArray<PendingIntent> map);
}
