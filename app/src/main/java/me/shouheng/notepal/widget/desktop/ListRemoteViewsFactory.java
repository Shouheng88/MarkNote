package me.shouheng.notepal.widget.desktop;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import java.util.List;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.model.MindSnagging;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.provider.MindSnaggingStore;
import me.shouheng.notepal.provider.NotesStore;
import me.shouheng.notepal.provider.schema.NoteSchema;
import me.shouheng.notepal.util.AppWidgetUtils;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.TimeUtils;

public class ListRemoteViewsFactory implements RemoteViewsFactory, SharedPreferences.OnSharedPreferenceChangeListener {

    private PalmApp app;
    private int appWidgetId;
    private List<Note> notes;
    private List<MindSnagging> minds;
    private ListWidgetType listWidgetType;

    private final int WIDTH = 128, HEIGHT = 128;

    private SharedPreferences sharedPreferences;

    public ListRemoteViewsFactory(Application app, Intent intent) {
        this.app = (PalmApp) app;

        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        sharedPreferences = app.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreate() {
        LogUtils.d("Created widget " + appWidgetId);
        setupFields();
        setupModels();
    }

    private void setupFields() {
        listWidgetType = ListWidgetType.getListWidgetType(sharedPreferences.getInt(
                Constants.PREF_WIDGET_TYPE_PREFIX + String.valueOf(appWidgetId), ListWidgetType.NOTES_LIST.id));
    }

    private void setupModels() {
        if (listWidgetType == ListWidgetType.NOTES_LIST) {
            notes = getNotes();
        } else if (listWidgetType == ListWidgetType.MINDS_LIST) {
            minds = getMinds();
        }
    }

    private List<Note> getNotes() {
        // fixme may throw connection pool close exception
        String condition = sharedPreferences.getString(Constants.PREF_WIDGET_SQL_PREFIX + String.valueOf(appWidgetId), "");
        return NotesStore.getInstance(app).get(condition, NoteSchema.LAST_MODIFIED_TIME + " DESC ");
    }

    private List<MindSnagging> getMinds() {
        String condition = sharedPreferences.getString(Constants.PREF_WIDGET_SQL_PREFIX + String.valueOf(appWidgetId), "");
        MindSnaggingStore store = MindSnaggingStore.getInstance(app);
        LogUtils.d(store);
        LogUtils.d(Thread.currentThread());
        return store.get(condition, NoteSchema.LAST_MODIFIED_TIME + " DESC ");
    }

    @Override
    public void onDataSetChanged() {
        LogUtils.d("onDataSetChanged widget " + appWidgetId);
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
        if (listWidgetType == ListWidgetType.MINDS_LIST) {
            return minds.size();
        } else if (listWidgetType == ListWidgetType.NOTES_LIST) {
            return notes.size();
        }
        return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        switch (listWidgetType) {
            case MINDS_LIST:
                return getMindsViewAt(position);
            case NOTES_LIST:
                return getNoteViewAt(position);
            default:
                return null;
        }
    }

    private RemoteViews getNoteViewAt(int position) {
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

    private RemoteViews getMindsViewAt(int position) {
        RemoteViews row = new RemoteViews(app.getPackageName(), R.layout.item_universal_layout_widget);

        MindSnagging mind = minds.get(position);

        row.setTextViewText(R.id.text_view_title, mind.getContent());
        row.setTextViewText(R.id.tv_added_time, TimeUtils.getLongDateTime(app.getApplicationContext(), mind.getAddedTime()));

//        Uri uri = mind.getPicture();
//        if (uri != null) {
//            Bitmap bmp = BitmapHelper.getBitmap(app, uri, WIDTH, HEIGHT);
//            row.setBitmap(R.id.image_view_cover, "setImageBitmap", bmp);
//            row.setInt(R.id.image_view_cover, "setVisibility", View.VISIBLE);
//        } else {
        // for current version, we don't show the picture in minds list widget
        row.setInt(R.id.image_view_cover, "setVisibility", View.GONE);
//        }

        Bundle extras = new Bundle();
        extras.putParcelable(Constants.EXTRA_MODEL, mind);
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
        if (listWidgetType == ListWidgetType.MINDS_LIST) {
            return minds.get(position).getCode();
        } else if (listWidgetType == ListWidgetType.NOTES_LIST) {
            return notes.get(position).getCode();
        }
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public static void updateConfiguration(Context mContext, int mAppWidgetId, String sqlCondition, ListWidgetType listWidgetType) {
        LogUtils.d("Widget configuration updated");
        Editor editor = mContext.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS).edit();
        editor.putString(Constants.PREF_WIDGET_SQL_PREFIX + String.valueOf(mAppWidgetId), sqlCondition).apply();
        editor.putInt(Constants.PREF_WIDGET_TYPE_PREFIX + String.valueOf(mAppWidgetId), listWidgetType.id).apply();

        AppWidgetUtils.notifyAppWidgets(mContext);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        setupFields();
    }
}
