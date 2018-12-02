package me.shouheng.notepal.repository;

import me.shouheng.data.entity.Note;
import me.shouheng.data.store.BaseStore;
import me.shouheng.data.store.NotesStore;

/**
 * Created by wang shouheng on 2018/3/13.*/
public class NoteRepository extends BaseRepository<Note> {

    @Override
    protected BaseStore<Note> getStore() {
        return NotesStore.getInstance();
    }
}
