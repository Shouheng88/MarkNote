package me.shouheng.data.helper;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import me.shouheng.data.entity.Category;
import me.shouheng.data.entity.Note;
import me.shouheng.data.entity.Notebook;
import me.shouheng.data.schema.NoteSchema;
import me.shouheng.data.schema.NotebookSchema;
import me.shouheng.data.store.NotebookStore;
import me.shouheng.data.store.NotesStore;

/**
 * Created by WngShhng on 2017/12/12.*/
public class TrashHelper {

    public static List<Notebook> getNotebooks(Context context, Notebook notebook) {
        return NotebookStore.getInstance().getTrashed(notebook == null ?
                        " ( " + NotebookSchema.PARENT_CODE + " IS NULL OR " + NotebookSchema.PARENT_CODE + " = 0 ) " :
                        " ( " + NotebookSchema.PARENT_CODE  + " = " + notebook.getCode() +" ) ",
                NotebookSchema.LAST_MODIFIED_TIME + " DESC ");
    }

    public static List<Note> getNotes(Context context, Notebook notebook) {
        return NotesStore.getInstance().getTrashed(notebook == null ?
                        " ( " + NoteSchema.PARENT_CODE + " IS NULL OR " + NoteSchema.PARENT_CODE + " = 0 ) " :
                        " ( " + NoteSchema.PARENT_CODE  + " = " + notebook.getCode() +" ) ",
                NoteSchema.LAST_MODIFIED_TIME + " DESC ");
    }

    public static List<Note> getNotes(Context context, @Nonnull Category category) {
        return NotesStore.getInstance().getTrashed(
                NoteSchema.TAGS + " LIKE '%'||'" + category.getCode() + "'||'%' ",
                NotebookSchema.ADDED_TIME + " DESC ");
    }

    public static List getNotebooksAndNotes(Context context, Notebook notebook) {
        List list = new LinkedList();
        list.addAll(getNotebooks(context, notebook));
        list.addAll(getNotes(context, notebook));
        return list;
    }

    public static List getNotebooksAndNotes(Context context, @Nonnull Category category) {
        return getNotes(context, category);
    }
}
