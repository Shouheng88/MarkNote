package me.shouheng.notepal.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.LongSparseArray;

import java.util.List;

import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.model.enums.Status;
import me.shouheng.notepal.provider.schema.NoteSchema;
import me.shouheng.notepal.provider.schema.NotebookSchema;


/**
 * Created by wangshouheng on 2017/8/19. */
public class NotebookStore extends BaseStore<Notebook> {

    private static NotebookStore sInstance = null;

    public static NotebookStore getInstance(Context context){
        if (sInstance == null){
            synchronized (NotebookStore.class) {
                if (sInstance == null) {
                    sInstance = new NotebookStore(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    private NotebookStore(Context context) {
        super(context);
    }

    @Override
    protected void afterDBCreated(SQLiteDatabase db) {}

    @Override
    protected void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    @Override
    public void fillModel(Notebook model, Cursor cursor) {
        model.setTitle(cursor.getString(cursor.getColumnIndex(NotebookSchema.TITLE)));
        model.setColor(cursor.getInt(cursor.getColumnIndex(NotebookSchema.COLOR)));
        model.setParentCode(cursor.getLong(cursor.getColumnIndex(NotebookSchema.PARENT_CODE)));
        model.setTreePath(cursor.getString(cursor.getColumnIndex(NotebookSchema.TREE_PATH)));
        int nbCnt, nCnt;
        if ((nCnt = cursor.getColumnIndex(NotebookSchema.COUNT)) != -1)
            model.setCount(cursor.getInt(nCnt));
        if ((nbCnt = cursor.getColumnIndex(NotebookSchema.NOTEBOOK_COUNT)) != -1)
            model.setNotebookCount(cursor.getInt(nbCnt));
    }

    @Override
    protected void fillContentValues(ContentValues values, Notebook model) {
        values.put(NotebookSchema.TITLE, model.getTitle());
        values.put(NotebookSchema.COLOR, model.getColor());
        values.put(NotebookSchema.PARENT_CODE, model.getParentCode());
        values.put(NotebookSchema.TREE_PATH, model.getTreePath());
    }

    /**
     * Get notebooks of {@link Status#NORMAL}.
     *
     * @param whereSQL where SQL
     * @param orderSQL order SQL
     * @return the notebooks
     */
    public synchronized List<Notebook> getNotebooks(String whereSQL, String orderSQL){
        Cursor cursor = null;
        List<Notebook> notebooks;
        SQLiteDatabase database = getWritableDatabase();
        try {
            cursor = database.rawQuery(" SELECT *, " + getNotesCount(Status.NORMAL)
                    + " FROM " + tableName
                    + " WHERE " + NotebookSchema.USER_ID + " = ? "
                    + (TextUtils.isEmpty(whereSQL) ? "" : " AND " + whereSQL)
                    + " AND " + NotebookSchema.STATUS + " = " + Status.NORMAL.id
                    + " GROUP BY " + NotebookSchema.CODE
                    + (TextUtils.isEmpty(orderSQL) ? "" : " ORDER BY " + orderSQL),
                    new String[]{String.valueOf(userId)});
            notebooks = getList(cursor);
//            setupSubNotebooks(database, notebooks, Status.NORMAL);
        } finally {
            closeCursor(cursor);
            database.close();
        }
        return notebooks;
    }

    /**
     * Get the archived notebooks. The notebooks returned must satisfy one of the two conditions below:
     * 1).The count of archived notes associated > 0;
     * 2).The notebook itself is in {@link Status#ARCHIVED} status.
     *
     * @param whereSQL where SQL
     * @param orderSQL order sql
     * @return the notebooks list
     */
    @Override
    public synchronized List<Notebook> getArchived(String whereSQL, String orderSQL) {
        Cursor cursor = null;
        List<Notebook> notebooks = null;
        SQLiteDatabase database = getWritableDatabase();
        try {
            cursor = database.rawQuery(" SELECT *, " + getNotesCount(Status.ARCHIVED)
                    + " FROM " + tableName
                    + " WHERE " + NotebookSchema.USER_ID + " = ? "
                    + (TextUtils.isEmpty(whereSQL) ? "" : " AND " + whereSQL)
                    + " AND ( " + NotebookSchema.STATUS + " = " + Status.ARCHIVED.id
                    + " OR " + NotebookSchema.COUNT + " > 0 ) "
                    + " GROUP BY " + NotebookSchema.CODE
                    + (TextUtils.isEmpty(orderSQL) ? "" : " ORDER BY " + orderSQL),
                    new String[]{String.valueOf(userId)});
            notebooks = getList(cursor);
//            setupSubNotebooks(database, notebooks, Status.ARCHIVED);
        } finally {
            closeCursor(cursor);
            database.close();
        }
        return notebooks;
    }

    @Override
    public synchronized List<Notebook> getTrashed(String whereSQL, String orderSQL) {
        Cursor cursor = null;
        List<Notebook> notebooks = null;
        SQLiteDatabase database = getWritableDatabase();
        try {
            cursor = database.rawQuery(" SELECT *, " + getNotesCount(Status.TRASHED)
                    + " FROM " + tableName
                    + " WHERE " + NotebookSchema.USER_ID + " = ? "
                    + " AND " + NotebookSchema.COUNT + " > 0 "
                    + " AND " + NoteSchema.STATUS + " = " + Status.TRASHED.id
                    + (TextUtils.isEmpty(whereSQL) ? "" : " AND " + whereSQL)
                    + " GROUP BY " + NotebookSchema.CODE
                    + (TextUtils.isEmpty(orderSQL) ? "" : " ORDER BY " + orderSQL),
                    new String[]{String.valueOf(userId)});
            notebooks = getList(cursor);
//            setupSubNotebooks(database, notebooks, Status.TRASHED);
        } finally {
            closeCursor(cursor);
            closeDatabase(database);
        }
        return notebooks;
    }

    /**
     * Get notebooks count of given notebooks. We did`t use this method for temporary.
     *
     * @param database database
     * @param notebooks notebooks to get count
     * @param status the status of notebooks to calculate as count
     */
    private void setupSubNotebooks(SQLiteDatabase database, List<Notebook> notebooks, Status status) {
        LongSparseArray<Notebook> array = new LongSparseArray<>();

        StringBuilder sb = new StringBuilder(" ( ");
        int len = notebooks.size();
        for (int i=0;i<len;i++) {
            sb.append(notebooks.get(i).getCode());
            array.put(notebooks.get(i).getCode(), notebooks.get(i));
            if (i != len - 1) sb.append(",");
        }
        sb.append(" ) ");

        Cursor cursor = database.rawQuery(" SELECT " + NotebookSchema.PARENT_CODE + ", "
                + "COUNT(*) AS " + NotebookSchema.NOTEBOOK_COUNT
                + " FROM " + tableName
                + " WHERE " + NotebookSchema.USER_ID + " = ? "
                + " AND " + NotebookSchema.PARENT_CODE + " IN " + sb.toString()
                + " AND " + NotebookSchema.STATUS + " = " + status.id
                + " GROUP BY " + NotebookSchema.PARENT_CODE, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                int nbCnt = cursor.getInt(cursor.getColumnIndex(NotebookSchema.NOTEBOOK_COUNT));
                long nbCode = cursor.getLong(cursor.getColumnIndex(NotebookSchema.PARENT_CODE));
                array.get(nbCode).setNotebookCount(nbCnt);
            } while (cursor.moveToNext());
        }

        closeCursor(cursor);
    }

    /**
     * Get the count of notes of notebook of given status.
     *
     * @param status the status of notes to calculate
     * @return the count calculation sql
     */
    private String getNotesCount(Status status) {
        // todo test whether the connection strategy is acceptable
        return " (SELECT COUNT(*) FROM " + NoteSchema.TABLE_NAME + " AS t1 "
                + " WHERE t1." + NoteSchema.TREE_PATH + " LIKE " + tableName + "." + NotebookSchema.TREE_PATH + "||'%'"
                + " AND t1." + NoteSchema.USER_ID + " = " + userId
                + " AND t1." + NoteSchema.STATUS + " = " + (status == null ? Status.NORMAL.id : status.id) + " ) "
                + " AS " + NotebookSchema.COUNT;
    }
}
