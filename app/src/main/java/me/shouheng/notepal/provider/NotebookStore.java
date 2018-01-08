package me.shouheng.notepal.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.LongSparseArray;

import java.util.List;

import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.model.enums.Operation;
import me.shouheng.notepal.model.enums.Status;
import me.shouheng.notepal.provider.helper.StoreHelper;
import me.shouheng.notepal.provider.helper.TimelineHelper;
import me.shouheng.notepal.provider.schema.BaseSchema;
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

    public synchronized List<Notebook> getNotebooks(String whereSQL, String orderSQL){
        return getNotebooks(whereSQL, orderSQL, Status.NORMAL);
    }

    @Override
    public synchronized List<Notebook> getArchived(String whereSQL, String orderSQL) {
        return getNotebooks(whereSQL, orderSQL, Status.ARCHIVED);
    }

    @Override
    public synchronized List<Notebook> getTrashed(String whereSQL, String orderSQL) {
        return getNotebooks(whereSQL, orderSQL, Status.TRASHED);
    }

    @Override
    public synchronized void update(Notebook model, Status toStatus) {
        if (model == null || toStatus == null) return;
        TimelineHelper.addTimeLine(model, StoreHelper.getStatusOperation(toStatus));
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {
            Status fromStatus = model.getStatus();

            /**
             * Update the status of all associated notebooks OF GIVEN STATUS. */
            database.execSQL(" UPDATE " + tableName
                            + " SET " + BaseSchema.STATUS + " = " + toStatus.id + " , " + BaseSchema.LAST_MODIFIED_TIME + " = ? "
                            + " WHERE " + NotebookSchema.TREE_PATH + " LIKE " + tableName + "." + NotebookSchema.TREE_PATH + "||'%'"
                            + " AND " + BaseSchema.USER_ID + " = " + userId
                            + " AND " + BaseSchema.STATUS + " = " + fromStatus.id,
                    new String[]{String.valueOf(System.currentTimeMillis())});

            /**
             * Update the status of all associated notes OF GIVEN STATUS. */
            database.execSQL(" UPDATE " + NoteSchema.TABLE_NAME
                            + " SET " + BaseSchema.STATUS + " = " + toStatus.id + " , " + BaseSchema.LAST_MODIFIED_TIME + " = ? "
                            + " WHERE " + NoteSchema.TREE_PATH + " LIKE " + NoteSchema.TABLE_NAME + "." + NoteSchema.TREE_PATH + "||'%'"
                            + " AND " + BaseSchema.USER_ID + " = " + userId
                            + " AND " + BaseSchema.STATUS + " = " + fromStatus.id,
                    new String[]{String.valueOf(System.currentTimeMillis())});

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
            closeDatabase(database);
        }
    }

    /**
     * Move the notebook to another notebook need to modify its children`s tree path at the same time.
     *
     * @param notebook the notebook to update
     */
    public synchronized void move(Notebook notebook, Notebook toNotebook) {
        String oldTreePath = notebook.getTreePath();

        notebook.setParentCode(toNotebook.getCode());
        notebook.setTreePath(toNotebook.getTreePath() + "|" + notebook.getCode());

        TimelineHelper.addTimeLine(notebook, Operation.UPDATE);
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {

            /**
             * Update the notebook`s tree path itself. */
            database.update(tableName, getContentValues(notebook),
                    BaseSchema.CODE + " = ? " + " AND " + BaseSchema.USER_ID + " = ? ",
                    new String[]{String.valueOf(notebook.getCode()), String.valueOf(userId)});

            /**
             * Need to modify the tree path of all notebook children of all status. */
            database.execSQL(" UPDATE " + tableName
                    + " SET " + NotebookSchema.TREE_PATH + " = replace(" + NoteSchema.TREE_PATH + ", '" + oldTreePath + "', '" + notebook.getTreePath() + "') "
                    + " WHERE " + NotebookSchema.TREE_PATH + " LIKE '" + oldTreePath + "'||'%'"
                    + " AND " + BaseSchema.CODE + " != " + notebook.getCode() // exclude itself
                    + " AND " + BaseSchema.USER_ID + " = " + userId, new String[]{});

            /**
             * Need to modify the tree path of all note children of all status. */
            database.execSQL(" UPDATE " + NoteSchema.TABLE_NAME
                    + " SET " + NoteSchema.TREE_PATH + " = replace(" + NoteSchema.TREE_PATH + ", '" + oldTreePath + "', '" + notebook.getTreePath() + "') "
                    + " WHERE " + NoteSchema.TREE_PATH + " LIKE '" + oldTreePath + "'||'%'"
                    + " AND " + BaseSchema.USER_ID + " = " + userId, new String[]{});

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
            closeDatabase(database);
        }
    }

    /**
     * Get notebooks of given status. Here are mainly two cases match:
     * 1).Notes count of given notebook > 0;
     * 2).The notebook itself is in given status.
     *
     * @param whereSQL where SQL
     * @param orderSQL order SQL
     * @return the notebooks
     */
    private List<Notebook> getNotebooks(String whereSQL, String orderSQL, Status status) {
        Cursor cursor = null;
        List<Notebook> notebooks;
        SQLiteDatabase database = getWritableDatabase();
        try {
            cursor = database.rawQuery(" SELECT *, " + getNotesCount(status)
                            + " FROM " + tableName
                            + " WHERE " + NotebookSchema.USER_ID + " = ? "
                            + " AND ( " + NotebookSchema.STATUS + " = " + status.id + " OR " + NotebookSchema.COUNT + " > 0 ) "
                            + (TextUtils.isEmpty(whereSQL) ? "" : " AND " + whereSQL)
                            + " GROUP BY " + NotebookSchema.CODE
                            + (TextUtils.isEmpty(orderSQL) ? "" : " ORDER BY " + orderSQL),
                    new String[]{String.valueOf(userId)});
            notebooks = getList(cursor);
        } finally {
            closeCursor(cursor);
            database.close();
        }
        return notebooks;
    }

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

    private String getNotesCount(Status status) {
        return " (SELECT COUNT(*) FROM " + NoteSchema.TABLE_NAME + " AS t1 "
                + " WHERE t1." + NoteSchema.TREE_PATH + " LIKE " + tableName + "." + NotebookSchema.TREE_PATH + "||'%'"
                + " AND t1." + NoteSchema.USER_ID + " = " + userId
                + " AND t1." + NoteSchema.STATUS + " = " + (status == null ? Status.NORMAL.id : status.id) + " ) "
                + " AS " + NotebookSchema.COUNT;
    }
}
