package me.shouheng.notepal.desktop;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.List;

import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.utils.LogUtils;
import me.shouheng.commons.utils.TimeUtils;
import me.shouheng.data.entity.Note;
import me.shouheng.data.schema.NoteSchema;
import me.shouheng.data.store.NotesStore;
import me.shouheng.notepal.Constants;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;

public class NotesListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this, this.getApplication(), intent);
    }

    public static class ListRemoteViewsFactory implements
            RemoteViewsFactory, SharedPreferences.OnSharedPreferenceChangeListener {

        private PalmApp app;
        private int appWidgetId;
        private List<Note> notes;

        private SharedPreferences sharedPreferences;

        ListRemoteViewsFactory(RemoteViewsService remoteViewsService, Application app, Intent intent) {
            this.app = (PalmApp) app;
            appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            sharedPreferences = app.getSharedPreferences(Constants.APP_WIDGET_PREFERENCES_NAME, Context.MODE_MULTI_PROCESS);
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onCreate() {
            LogUtils.d("Created widget " + appWidgetId);
            loadFromDatabase();
        }

        private void loadFromDatabase() {
            String sqlKey = Constants.APP_WIDGET_PREFERENCE_KEY_SQL_PREFIX + String.valueOf(appWidgetId);
            String condition = sharedPreferences.getString(sqlKey, "");
            notes = NotesStore.getInstance().get(condition, NoteSchema.LAST_MODIFIED_TIME + " DESC ");
        }

        @Override
        public void onDataSetChanged() {
            loadFromDatabase();
        }

        @Override
        public void onDestroy() {
            String sqlKey = Constants.APP_WIDGET_PREFERENCE_KEY_SQL_PREFIX + String.valueOf(appWidgetId);
            String nbkey = Constants.APP_WIDGET_PREFERENCE_KEY_NOTEBOOK_CODE_PREFIX + String.valueOf(appWidgetId);
            sharedPreferences.edit().remove(sqlKey).apply();
            sharedPreferences.edit().remove(nbkey).apply();
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public int getCount() {
            return notes.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            return getNoteViewAt(position);
        }

        private RemoteViews getNoteViewAt(int position) {
            RemoteViews row = new RemoteViews(app.getPackageName(), R.layout.widget_item_note);

            Note note = notes.get(position);

            row.setTextViewText(R.id.tv_note_title, note.getTitle());
            row.setTextViewText(R.id.tv_added_time, note.getPreviewContent());
            row.setTextViewText(R.id.tv_sub_title, TimeUtils.getLongDateTime(app.getApplicationContext(), note.getAddedTime()));
            row.setTextColor(R.id.tv_sub_title, ColorUtils.accentColor());
//        row.setInt(R.id.root, "setBackgroundColor", app.getResources().getColor(R.color.white_translucent));
            row.setViewVisibility(R.id.iv_icon, View.GONE);

            Bundle extras = new Bundle();
            extras.putParcelable(Constants.APP_WIDGET_EXTRA_NOTE, note);
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);
            row.setOnClickFillInIntent(R.id.root, fillInIntent);

            return row;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return notes.get(position).getCode();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {}
    }
}
