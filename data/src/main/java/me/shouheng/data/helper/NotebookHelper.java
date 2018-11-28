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
public class NotebookHelper {

    /**
     * Get the notebooks of given notebook. Note that this method can only get the information
     * of {@link me.shouheng.data.model.enums.Status#NORMAL} status.
     * If you want to get the notebooks of status {@link me.shouheng.data.model.enums.Status#ARCHIVED}
     * or status {@link me.shouheng.data.model.enums.Status#TRASHED},
     * call {@link ArchiveHelper#getNotebooks(Context, Notebook)}.
     * call {@link TrashHelper#getNotebooks(Context, Notebook)} to get the notebooks of
     * {@link me.shouheng.data.model.enums.Status#TRASHED}
     *
     * @param context context
     * @param notebook notebook, may be null,means get the notebooks of top level
     * @return notebooks
     */
    public static List<Notebook> getNotebooks(Context context, Notebook notebook) {
        return NotebookStore.getInstance().getNotebooks(notebook == null ?
                        " ( " + NotebookSchema.PARENT_CODE + " IS NULL OR " + NotebookSchema.PARENT_CODE + " = 0 ) " :
                        " ( " + NotebookSchema.PARENT_CODE  + " = " + notebook.getCode() +" ) ",
                NotebookSchema.ADDED_TIME + " DESC ");
    }

    /**
     * Get notes of notebook of status {@link me.shouheng.data.model.enums.Status#NORMAL}
     * Call {@link ArchiveHelper#getNotes(Context, Notebook)} to get the notes of notebook of status
     * {@link me.shouheng.data.model.enums.Status#ARCHIVED}.
     * Call {@link TrashHelper#getNotes(Context, Notebook)} to get the notes of notebook of status
     * {@link me.shouheng.data.model.enums.Status#TRASHED}.
     *
     * @param context context
     * @param notebook notebook
     * @return notes
     */
    public static List<Note> getNotes(Context context, Notebook notebook) {
        return NotesStore.getInstance().get(notebook == null ?
                        " ( " + NoteSchema.PARENT_CODE + " IS NULL OR " + NoteSchema.PARENT_CODE + " = 0 ) " :
                        " ( " + NoteSchema.PARENT_CODE  + " = " + notebook.getCode() +" ) ",
                NoteSchema.ADDED_TIME + " DESC ");
    }

    /**
     * Get notmal notes of given category : the {@link NoteSchema#TAGS} contains {@link Category#code}.
     *
     * @param context context
     * @param category the category
     * @return the notes list
     */
    public static List<Note> getNotes(Context context, @Nonnull Category category) {
        return NotesStore.getInstance().get(
                NoteSchema.TAGS + " LIKE '%'||'" + category.getCode() + "'||'%' ",
                NotebookSchema.ADDED_TIME + " DESC ");
    }

    public static List getNotesAndNotebooks(Context context, Notebook notebook) {
        List data = new LinkedList();
        data.addAll(NotebookHelper.getNotebooks(context, notebook));
        data.addAll(NotebookHelper.getNotes(context, notebook));
        return data;
    }

    public static List getNotesAndNotebooks(Context context, @Nonnull Category category) {
        return NotebookHelper.getNotes(context, category);
    }
}
