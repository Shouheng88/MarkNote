package me.shouheng.notepal.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import me.shouheng.notepal.model.Category;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.model.enums.Portrait;
import me.shouheng.notepal.provider.schema.CategorySchema;

/**
 * Created by wangshouheng on 2017/8/19. */
public class CategoryStore extends BaseStore<Category> {

    private static CategoryStore sInstance = null;

    public static CategoryStore getInstance(Context context){
        if (sInstance == null){
            synchronized (CategoryStore.class) {
                if (sInstance == null) {
                    sInstance = new CategoryStore(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }
    
    private CategoryStore(Context context) {
        super(context);
    }

    @Override
    protected void afterDBCreated(SQLiteDatabase db) {}

    @Override
    protected void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    @Override
    public void fillModel(Category model, Cursor cursor) {
        model.setName(cursor.getString(cursor.getColumnIndex(CategorySchema.NAME)));
        model.setColor(cursor.getInt(cursor.getColumnIndex(CategorySchema.COLOR)));
        model.setPortrait(Portrait.getPortraitById(cursor.getInt(cursor.getColumnIndex(CategorySchema.PORTRAIT))));
        model.setCategoryOrder(cursor.getInt(cursor.getColumnIndex(CategorySchema.CATEGORY_ORDER)));

        int cntIndex = cursor.getColumnIndex(CategorySchema.COUNT);
        if (cntIndex != -1) model.setCount(cursor.getInt(cntIndex));
    }

    @Override
    protected void fillContentValues(ContentValues values, Category model) {
        values.put(CategorySchema.NAME, model.getName());
        values.put(CategorySchema.COLOR, model.getColor());
        values.put(CategorySchema.PORTRAIT, model.getPortrait().id);
        values.put(CategorySchema.CATEGORY_ORDER, model.getCategoryOrder());
    }

    public synchronized void updateOrders(List<Category> categories){
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {
            int size = categories.size();
            for (int i = 0; i < size; i++){
                database.execSQL(" UPDATE " + tableName +
                        " SET " + CategorySchema.CATEGORY_ORDER + " = " + i +
                        " WHERE " + CategorySchema.CODE + " = " + categories.get(i).getCode());
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
            closeDatabase(database);
        }
    }

    /**
     * Get labels of note.
     *
     * @param note the note
     * @return categories of note
     */
    public synchronized List<Category> getCategories(Note note) {
        // note -> note labels -> category codes -> categories -> labels text
        return null;
    }
}
