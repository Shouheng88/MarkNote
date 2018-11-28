package me.shouheng.data.store;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import me.shouheng.commons.BaseApplication;
import me.shouheng.data.entity.Note;
import me.shouheng.data.model.enums.NoteType;
import me.shouheng.data.schema.NoteSchema;

/**
 * Created by wangshouheng on 2017/5/12.*/
public class NotesStore extends BaseStore<Note> {

    private static volatile NotesStore sInstance = null;

    public static NotesStore getInstance() {
        if (sInstance == null) {
            synchronized (NotesStore.class) {
                if (sInstance == null) {
                    sInstance = new NotesStore(BaseApplication.getContext());
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
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
            case 2:
                db.execSQL("ALTER TABLE gt_note ADD COLUMN " + NoteSchema.PREVIEW_IMAGE + " TEXT");
                db.execSQL("ALTER TABLE gt_note ADD COLUMN " + NoteSchema.NOTE_TYPE + " INTEGER");
            case 4:
                db.execSQL("ALTER TABLE gt_note ADD COLUMN " + NoteSchema.PREVIEW_CONTENT + " TEXT");
                break;
            case 5:
                // 判断指定的两个列是否存在，如果不存在的话就创建列
                Cursor cursor = null ;
                try{
                    cursor = db.rawQuery( "SELECT * FROM " + tableName + " LIMIT 0 ", null );
                    boolean isExist = cursor != null && cursor.getColumnIndex(NoteSchema.PREVIEW_IMAGE) != -1 ;
                    if (!isExist) {
                        db.execSQL("ALTER TABLE gt_note ADD COLUMN " + NoteSchema.PREVIEW_IMAGE + " TEXT");
                        db.execSQL("ALTER TABLE gt_note ADD COLUMN " + NoteSchema.NOTE_TYPE + " INTEGER");
                    }
                } finally{
                    if(null != cursor && !cursor.isClosed()){
                        closeCursor(cursor);
                    }
                }
                break;
        }
    }

    @Override
    public void fillModel(Note note, Cursor cursor) {
        note.setParentCode(cursor.getLong(cursor.getColumnIndex(NoteSchema.PARENT_CODE)));
        note.setTitle(cursor.getString(cursor.getColumnIndex(NoteSchema.TITLE)));
        note.setContentCode(cursor.getLong(cursor.getColumnIndex(NoteSchema.CONTENT_CODE)));
        note.setTags(cursor.getString(cursor.getColumnIndex(NoteSchema.TAGS)));
        note.setTreePath(cursor.getString(cursor.getColumnIndex(NoteSchema.TREE_PATH)));
        String preUri = cursor.getString(cursor.getColumnIndex(NoteSchema.PREVIEW_IMAGE));
        note.setPreviewImage(TextUtils.isEmpty(preUri) ? null : Uri.parse(preUri));
        note.setNoteType(NoteType.getTypeById(cursor.getInt(cursor.getColumnIndex(NoteSchema.NOTE_TYPE))));
        note.setPreviewContent(cursor.getString(cursor.getColumnIndex(NoteSchema.PREVIEW_CONTENT)));
    }

    @Override
    protected void fillContentValues(ContentValues values, Note note) {
        values.put(NoteSchema.PARENT_CODE, note.getParentCode());
        values.put(NoteSchema.TITLE, note.getTitle());
        values.put(NoteSchema.CONTENT_CODE, note.getContentCode());
        values.put(NoteSchema.TAGS, note.getTags());
        values.put(NoteSchema.TREE_PATH, note.getTreePath());
        Uri uri = note.getPreviewImage();
        values.put(NoteSchema.PREVIEW_IMAGE, uri == null ? null : uri.toString());
        values.put(NoteSchema.NOTE_TYPE, note.getNoteType().getId());
        values.put(NoteSchema.PREVIEW_CONTENT, note.getPreviewContent());
    }
}
