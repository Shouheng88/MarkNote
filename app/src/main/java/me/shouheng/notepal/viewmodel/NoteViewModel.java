package me.shouheng.notepal.viewmodel;

import android.arch.lifecycle.LiveData;

import java.util.List;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.NotesAdapter.MultiItem;
import me.shouheng.notepal.model.Category;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.model.data.Resource;
import me.shouheng.notepal.model.enums.Status;
import me.shouheng.notepal.repository.BaseRepository;
import me.shouheng.notepal.repository.NoteRepository;

/**
 * Created by wang shouheng on 2018/3/13.*/
public class NoteViewModel extends BaseViewModel<Note> {

    @Override
    protected BaseRepository<Note> getRepository() {
        return new NoteRepository();
    }

    public LiveData<Resource<List<MultiItem>>> getMultiItems(Category category, Status status, Notebook notebook) {
        return ((NoteRepository) getRepository()).getMultiItems(category, status, notebook);
    }

    public String getEmptySubTitle(Status status) {
        return PalmApp.getContext().getString(
                status == Status.NORMAL ? R.string.notes_list_empty_sub_normal :
                        status == Status.TRASHED ? R.string.notes_list_empty_sub_trashed :
                                status == Status.ARCHIVED ? R.string.notes_list_empty_sub_archived :
                                        R.string.notes_list_empty_sub_normal);
    }
}
