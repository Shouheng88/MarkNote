package me.shouheng.notepal.provider.helper;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;

import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.provider.NotebookStore;
import me.shouheng.notepal.provider.NotesStore;
import me.shouheng.notepal.provider.schema.NoteSchema;
import me.shouheng.notepal.provider.schema.NotebookSchema;

/**
 * Created by WngShhng on 2017/12/12.*/
public class ArchiveHelper {

    public static List<Notebook> getNotebooks(Context context, Notebook notebook) {
        return NotebookStore.getInstance(context).getArchived(notebook == null ?
                        " ( " + NotebookSchema.PARENT_CODE + " IS NULL OR " + NotebookSchema.PARENT_CODE + " = 0 ) " :
                        " ( " + NotebookSchema.PARENT_CODE  + " = " + notebook.getCode() +" ) ",
                NotebookSchema.LAST_MODIFIED_TIME + " DESC ");
    }

    public static List<Note> getNotes(Context context, Notebook notebook) {
        return NotesStore.getInstance(context).getArchived(notebook == null ?
                        " ( " + NoteSchema.PARENT_CODE + " IS NULL OR " + NoteSchema.PARENT_CODE + " = 0 ) " :
                        " ( " + NoteSchema.PARENT_CODE  + " = " + notebook.getCode() +" ) ",
                NoteSchema.LAST_MODIFIED_TIME + " DESC ");
    }

    public static List getNotebooksAndNotes(Context context, Notebook notebook) {
        List list = new LinkedList();
        list.addAll(getNotebooks(context, notebook));
        list.addAll(getNotes(context, notebook));
        return list;
    }
}
