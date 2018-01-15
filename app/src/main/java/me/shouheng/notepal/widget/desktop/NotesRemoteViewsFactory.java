package me.shouheng.notepal.widget.desktop;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import java.util.List;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.provider.NotesStore;
import me.shouheng.notepal.provider.schema.NoteSchema;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.TimeUtils;

public class NotesRemoteViewsFactory implements RemoteViewsFactory {

    private PalmApp app;
    private int appWidgetId;
    private List<Note> notes;

    private SharedPreferences sharedPreferences;

    public NotesRemoteViewsFactory(Application app, Intent intent) {
        this.app = (PalmApp) app;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        sharedPreferences = app.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS);
    }

    @Override
    public void onCreate() {
        LogUtils.d("Created widget " + appWidgetId);
        notes = getNotes();
    }

    private List<Note> getNotes() {
        String condition = sharedPreferences.getString(Constants.PREF_WIDGET_PREFIX + String.valueOf(appWidgetId), "");
        return NotesStore.getInstance(PalmApp.getContext()).get(condition, NoteSchema.LAST_MODIFIED_TIME + " DESC ");
    }

    @Override
    public void onDataSetChanged() {
        LogUtils.d("onDataSetChanged widget " + appWidgetId);
        notes = getNotes();
    }

    @Override
    public void onDestroy() {
        sharedPreferences.edit().remove(Constants.PREF_WIDGET_PREFIX + String.valueOf(appWidgetId)).apply();
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row = new RemoteViews(app.getPackageName(), R.layout.widget_item_note);

        Note note = notes.get(position);

        row.setTextViewText(R.id.tv_note_title, note.getTitle());
        row.setTextViewText(R.id.tv_added_time, TimeUtils.getLongDateTime(app.getApplicationContext(), note.getAddedTime()));
        row.setImageViewResource(R.id.iv_icon, R.drawable.ic_doc_text_alpha);

        Bundle extras = new Bundle();
        extras.putParcelable(Constants.EXTRA_MODEL, note);
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

    public static void updateConfiguration(Context mContext, int mAppWidgetId, String sqlCondition, boolean thumbnails, boolean timestamps) {
        LogUtils.d("Widget configuration updated");
        mContext.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS).edit()
                .putString(Constants.PREF_WIDGET_PREFIX + String.valueOf(mAppWidgetId), sqlCondition).apply();
    }
}
