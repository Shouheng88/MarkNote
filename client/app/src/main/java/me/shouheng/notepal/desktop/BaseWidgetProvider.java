package me.shouheng.notepal.desktop;

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
import me.shouheng.notepal.activity.QuickActivity;
import me.shouheng.notepal.Constants;

/**
 * Base widget provider. Used to provide the pending intent for the child app widgets.
 *
 * Refactored by WngShhng (shouheng2015@gmail.com)
 * on 2018/12/3
 */
public abstract class BaseWidgetProvider extends AppWidgetProvider {

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
    public void onAppWidgetOptionsChanged(
            Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        LogUtils.d("Widget size changed");
        setLayout(context, appWidgetManager, appWidgetId);
    }

    private void setLayout(Context context, AppWidgetManager appWidgetManager, int widgetId) {
        Bundle options = appWidgetManager.getAppWidgetOptions(widgetId);
        boolean isSmall = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) < 110;
        boolean isSingleLine = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT) < 110;

        SparseArray<PendingIntent> map = new SparseArray<>();
        map.put(R.id.iv_launch_app, launchApp(context, widgetId));
        map.put(R.id.iv_add_note, createNote(context, widgetId));
        map.put(R.id.iv_add_mind, createQuickNote(context, widgetId));
        map.put(R.id.iv_add_photo, takeAPhoto(context, widgetId));
        map.put(R.id.iv_add_sketch, createSketch(context, widgetId));
        map.put(R.id.iv_setting, configList(context, widgetId));

        RemoteViews views = getRemoteViews(context, widgetId, isSmall, isSingleLine, map);

        appWidgetManager.updateAppWidget(widgetId, views);
    }

    private PendingIntent createNote(Context context, int widgetId) {
        Intent intentAddNote = new Intent(context, MainActivity.class);
        intentAddNote.setAction(Constants.APP_WIDGET_ACTION_CREATE_NOTE);
        intentAddNote.putExtra(Constants.APP_WIDGET_EXTRA_WIDGET_ID, widgetId);
        return PendingIntent.getActivity(context, widgetId, intentAddNote, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private PendingIntent takeAPhoto(Context context, int widgetId) {
        Intent intentAddPhoto = new Intent(context, MainActivity.class);
        intentAddPhoto.setAction(Constants.APP_WIDGET_ACTION_CAPTURE);
        intentAddPhoto.putExtra(Constants.APP_WIDGET_EXTRA_WIDGET_ID, widgetId);
        return PendingIntent.getActivity(context, widgetId, intentAddPhoto, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private PendingIntent createSketch(Context context, int widgetId) {
        Intent intentAddPhoto = new Intent(context, MainActivity.class);
        intentAddPhoto.setAction(Constants.APP_WIDGET_ACTION_CREATE_SKETCH);
        intentAddPhoto.putExtra(Constants.APP_WIDGET_EXTRA_WIDGET_ID, widgetId);
        return PendingIntent.getActivity(context, widgetId, intentAddPhoto, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private PendingIntent launchApp(Context context, int widgetId) {
        Intent intentLaunchApp = new Intent(context, MainActivity.class);
        intentLaunchApp.setAction(Constants.APP_WIDGET_ACTION_LAUNCH_APP);
        intentLaunchApp.putExtra(Constants.APP_WIDGET_EXTRA_WIDGET_ID, widgetId);
        return PendingIntent.getActivity(context, widgetId, intentLaunchApp, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private PendingIntent createQuickNote(Context context, int widgetId) {
        Intent intentQuickNote = new Intent(context, QuickActivity.class);
        intentQuickNote.setAction(Constants.APP_WIDGET_ACTION_QUICK_NOTE);
        intentQuickNote.putExtra(Constants.APP_WIDGET_EXTRA_WIDGET_ID, widgetId);
        return PendingIntent.getActivity(context, widgetId, intentQuickNote, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private PendingIntent configList(Context context, int widgetId) {
        Intent intentSetting = new Intent(context, ConfigActivity.class);
        intentSetting.setAction(Constants.APP_WIDGET_ACTION_CONFIG_LIST);
        intentSetting.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        intentSetting.putExtra(Constants.APP_WIDGET_EXTRA_ALLOW_SWITCH_NOTEBOOK, false);
        return PendingIntent.getActivity(context, widgetId, intentSetting, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    /**
     * Method provied to the child app widget to implement the concrete logic.
     *
     * @param context the context
     * @param widgetId the widget id
     * @param isSmall is small widget
     * @param isSingleLine is single line widget
     * @param map the pending intent map
     * @return the remove views (the widget view)
     */
     protected abstract RemoteViews getRemoteViews(
            Context context, int widgetId, boolean isSmall, boolean isSingleLine, SparseArray<PendingIntent> map);
}
