package me.shouheng.notepal.repository;

import android.support.annotation.MainThread;

import java.util.List;

import me.shouheng.commons.utils.LogUtils;
import me.shouheng.data.model.enums.Status;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.provider.NotesStore;
import me.shouheng.notepal.provider.schema.BaseSchema;
import me.shouheng.notepal.provider.schema.NoteSchema;
import me.shouheng.notepal.util.tools.SearchConditions;


/**
 * Created by WngShhng on 2017/12/11.*/
public class QueryRepository {

    private SearchConditions conditions;

    private NotesStore notesStore;

    public QueryRepository(SearchConditions conditions) {
        this.conditions = conditions;
        LogUtils.d(conditions);
        notesStore = NotesStore.getInstance(PalmApp.getContext());
    }

    @MainThread
    public List<Note> getNotes(String queryString) {
        return notesStore.get(getNoteQuerySQL(queryString), NoteSchema.ADDED_TIME + " DESC ");
    }

    private String getNoteQuerySQL(String queryString) {
        return (conditions.isIncludeTags() ?
                " ( " + NoteSchema.TITLE + " LIKE '%'||'" + queryString + "'||'%' "
                        + " OR " + NoteSchema.TAGS + " LIKE '%'||'" + queryString + "'||'%' ) "
                : NoteSchema.TITLE + " LIKE '%'||'" + queryString + "'||'%'"
        ) + getQueryConditions();
    }

    private String getQueryConditions() {
        // should not query the deleted item out
        return (conditions.isIncludeArchived() ? "" : " AND " + BaseSchema.STATUS + " != " + Status.ARCHIVED.id)
                + (conditions.isIncludeTrashed() ? "" : " AND " + BaseSchema.STATUS + " != " + Status.TRASHED.id)
                + " AND " + BaseSchema.STATUS + " != " + Status.DELETED.id;
    }

    public void setConditions(SearchConditions conditions) {
        this.conditions = conditions;
    }
}
