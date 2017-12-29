package me.shouheng.notepal.provider.helper;

import android.content.Context;

import java.util.List;

import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.provider.NotebookStore;
import me.shouheng.notepal.provider.NotesStore;
import me.shouheng.notepal.provider.schema.NoteSchema;
import me.shouheng.notepal.provider.schema.NotebookSchema;


/**
 * Created by WngShhng on 2017/12/12.*/
public class NotebookHelper {

    /**
     * Get the notebooks of given notebook. Note that this method can only get the information
     * of {@link me.shouheng.notepal.model.enums.Status#NORMAL} status.
     * If you want to get the notebooks of status {@link me.shouheng.notepal.model.enums.Status#ARCHIVED}
     * or status {@link me.shouheng.notepal.model.enums.Status#TRASHED},
     * call {@link ArchiveHelper#getNotebooks(Context, Notebook)}.
     * call {@link TrashHelper#getNotebooks(Context, Notebook)} to get the notebooks of
     * {@link me.shouheng.notepal.model.enums.Status#TRASHED}
     *
     * @param context context
     * @param notebook notebook, may be null,means get the notebooks of top level
     * @return notebooks
     */
    public static List<Notebook> getNotebooks(Context context, Notebook notebook) {
        return NotebookStore.getInstance(context).getNotebooks(notebook == null ?
                        " ( " + NotebookSchema.PARENT_CODE + " IS NULL OR " + NotebookSchema.PARENT_CODE + " = 0 ) " :
                        " ( " + NotebookSchema.PARENT_CODE  + " = " + notebook.getCode() +" ) ",
                NotebookSchema.ADDED_TIME + " DESC ");
    }

    /**
     * Get notes of notebook of status {@link me.shouheng.notepal.model.enums.Status#NORMAL}
     * Call {@link ArchiveHelper#getNotes(Context, Notebook)} to get the notes of notebook of status
     * {@link me.shouheng.notepal.model.enums.Status#ARCHIVED}.
     * Call {@link TrashHelper#getNotes(Context, Notebook)} to get the notes of notebook of status
     * {@link me.shouheng.notepal.model.enums.Status#TRASHED}.
     *
     * @param context context
     * @param notebook notebook
     * @return notes
     */
    public static List<Note> getNotes(Context context, Notebook notebook) {
        return NotesStore.getInstance(context).get(notebook == null ?
                        " ( " + NoteSchema.PARENT_CODE + " IS NULL OR " + NoteSchema.PARENT_CODE + " = 0 ) " :
                        " ( " + NoteSchema.PARENT_CODE  + " = " + notebook.getCode() +" ) ",
                NoteSchema.ADDED_TIME + " DESC ");
    }
}
