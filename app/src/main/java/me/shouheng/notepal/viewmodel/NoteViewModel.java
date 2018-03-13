package me.shouheng.notepal.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.NotesAdapter.MultiItem;
import me.shouheng.notepal.model.Category;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.model.data.Resource;
import me.shouheng.notepal.model.enums.Status;
import me.shouheng.notepal.repository.NoteRepository;
import me.shouheng.notepal.repository.NotebookRepository;

/**
 * Created by wang shouheng on 2018/3/13.*/
public class NoteViewModel extends ViewModel {

    public LiveData<Resource<List<MultiItem>>> getMultiItems(Category category, Status status, Notebook notebook) {
        NoteRepository noteRepository = new NoteRepository();
        return noteRepository.getMultiItems(category, status, notebook);
    }

    public String getEmptySubTitle(Status status) {
        return PalmApp.getContext().getString(
                status == Status.NORMAL ? R.string.notes_list_empty_sub_normal :
                        status == Status.TRASHED ? R.string.notes_list_empty_sub_trashed :
                                status == Status.ARCHIVED ? R.string.notes_list_empty_sub_archived :
                                        R.string.notes_list_empty_sub_normal);
    }

    public LiveData<Resource<Note>> update(Note note) {
        NoteRepository noteRepository = new NoteRepository();
        return noteRepository.update(note);
    }

    public LiveData<Resource<Note>> update(Note note, Status status) {
        NoteRepository noteRepository = new NoteRepository();
        return noteRepository.update(note, status);
    }

    public LiveData<Resource<Notebook>> update(Notebook notebook) {
        NotebookRepository notebookRepository = new NotebookRepository();
        return notebookRepository.update(notebook);
    }

    public LiveData<Resource<Notebook>> update(Notebook notebook, Status fromStatus, Status toStatus) {
        NotebookRepository notebookRepository = new NotebookRepository();
        return notebookRepository.update(notebook, fromStatus, toStatus);
    }

    public LiveData<Resource<Notebook>> move(Notebook notebook, Notebook toNotebook) {
        NotebookRepository notebookRepository = new NotebookRepository();
        return notebookRepository.move(notebook, toNotebook);
    }
}
