package me.shouheng.notepal.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.provider.schema.NoteSchema;


/**
 * Created by wangshouheng on 2017/5/12.*/
public class NotesStore extends BaseStore<Note> {

    private static NotesStore sInstance = null;

    public static NotesStore getInstance(final Context context){
        if (sInstance == null){
            synchronized (NotesStore.class) {
                if (sInstance == null) {
                    sInstance = new NotesStore(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    private NotesStore(final Context context){
        super(context);
    }

    @Override
    protected void afterDBCreated(SQLiteDatabase db) {}

    @Override
    protected void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    @Override
    public void fillModel(Note note, Cursor cursor) {
        note.setParentCode(cursor.getLong(cursor.getColumnIndex(NoteSchema.PARENT_CODE)));
        note.setTitle(cursor.getString(cursor.getColumnIndex(NoteSchema.TITLE)));
        note.setContentCode(cursor.getLong(cursor.getColumnIndex(NoteSchema.CONTENT_CODE)));
        note.setTags(cursor.getString(cursor.getColumnIndex(NoteSchema.TAGS)));
        note.setTreePath(cursor.getString(cursor.getColumnIndex(NoteSchema.TREE_PATH)));
        note.setPreviewCode(cursor.getLong(cursor.getColumnIndex(NoteSchema.PREVIEW_CODE)));
    }

    @Override
    protected void fillContentValues(ContentValues values, Note note) {
        values.put(NoteSchema.PARENT_CODE, note.getParentCode());
        values.put(NoteSchema.TITLE, note.getTitle());
        values.put(NoteSchema.CONTENT_CODE, note.getContentCode());
        values.put(NoteSchema.TAGS, note.getTags());
        values.put(NoteSchema.TREE_PATH, note.getTreePath());
        values.put(NoteSchema.PREVIEW_CODE, note.getPreviewCode());
    }
}
