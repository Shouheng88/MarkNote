package me.shouheng.notepal.widget.desktop;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import java.util.List;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.provider.NotesStore;
import me.shouheng.notepal.provider.schema.NoteSchema;
import me.shouheng.notepal.util.AppWidgetUtils;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.TimeUtils;

public class ListRemoteViewsFactory implements RemoteViewsFactory, SharedPreferences.OnSharedPreferenceChangeListener {

    private PalmApp app;
    private int appWidgetId;
    private List<Note> notes;

    private final int WIDTH = 128, HEIGHT = 128;

    private SharedPreferences sharedPreferences;

    ListRemoteViewsFactory(Application app, Intent intent) {
        this.app = (PalmApp) app;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        sharedPreferences = app.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreate() {
        LogUtils.d("Created widget " + appWidgetId);
        setupModels();
    }

    private void setupModels() {
        String condition = sharedPreferences.getString(Constants.PREF_WIDGET_SQL_PREFIX + String.valueOf(appWidgetId), "");
        NotesStore store = NotesStore.getInstance(app);
        notes = store.get(condition, NoteSchema.LAST_MODIFIED_TIME + " DESC ");
    }

    @Override
    public void onDataSetChanged() {
        setupModels();
    }

    @Override
    public void onDestroy() {
        sharedPreferences.edit().remove(Constants.PREF_WIDGET_SQL_PREFIX + String.valueOf(appWidgetId)).apply();
        sharedPreferences.edit().remove(Constants.PREF_WIDGET_TYPE_PREFIX + String.valueOf(appWidgetId)).apply();
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
        row.setTextViewText(R.id.tv_added_time, TimeUtils.getLongDateTime(app.getApplicationContext(), note.getAddedTime()));
        row.setImageViewResource(R.id.iv_icon, R.drawable.ic_doc_text_alpha);
        row.setInt(R.id.root, "setBackgroundColor", app.getResources().getColor(R.color.white_translucent));

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

    public static void updateConfiguration(Context mContext, int mAppWidgetId, Notebook notebook, ListWidgetType listWidgetType) {
        Editor editor = mContext.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS).edit();
        String sqlCondition = null;
        if (notebook != null) {
            sqlCondition = NoteSchema.TREE_PATH + " LIKE '" + notebook.getTreePath() + "'||'%'";
            editor.putLong(Constants.PREF_WIDGET_NOTEBOOK_CODE_PREFIX + String.valueOf(mAppWidgetId), notebook.getCode());
        }
        editor.putString(Constants.PREF_WIDGET_SQL_PREFIX + String.valueOf(mAppWidgetId), sqlCondition).apply();
        editor.putInt(Constants.PREF_WIDGET_TYPE_PREFIX + String.valueOf(mAppWidgetId), listWidgetType.id).apply();
        AppWidgetUtils.notifyAppWidgets(mContext);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {}
}
