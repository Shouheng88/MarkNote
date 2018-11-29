package me.shouheng.notepal.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.MainThread;

import java.util.LinkedList;
import java.util.List;

import me.shouheng.data.model.enums.Status;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.adapter.NotesAdapter;
import me.shouheng.notepal.adapter.NotesAdapter.MultiItem;
import me.shouheng.data.entity.Category;
import me.shouheng.data.entity.Note;
import me.shouheng.data.entity.Notebook;
import me.shouheng.commons.model.data.Resource;
import me.shouheng.data.store.BaseStore;
import me.shouheng.data.store.NotesStore;
import me.shouheng.data.helper.ArchiveHelper;
import me.shouheng.data.helper.NotebookHelper;
import me.shouheng.data.helper.TrashHelper;

/**
 * Created by wang shouheng on 2018/3/13.*/
public class NoteRepository extends BaseRepository<Note> {

    @Override
    protected BaseStore<Note> getStore() {
        return NotesStore.getInstance();
    }

    public LiveData<Resource<List<MultiItem>>> getMultiItems(Category category, Status status, Notebook notebook) {
        MutableLiveData<Resource<List<MultiItem>>> result = new MutableLiveData<>();
        new NoteLoadTask(result, category, status, notebook).execute();
        return result;
    }

    public static class NoteLoadTask extends AsyncTask<Void, Integer, List<NotesAdapter.MultiItem>> {

        private MutableLiveData<Resource<List<MultiItem>>> result;
        private Category category;
        private me.shouheng.data.model.enums.Status status;
        private Notebook notebook;

        NoteLoadTask(MutableLiveData<Resource<List<MultiItem>>> result,
                     Category category,
                     me.shouheng.data.model.enums.Status status,
                     Notebook notebook) {
            this.result = result;
            this.category = category;
            this.status = status;
            this.notebook = notebook;
        }

        @Override
        protected List<NotesAdapter.MultiItem> doInBackground(Void... voids) {
            List<NotesAdapter.MultiItem> dataList = new LinkedList<>();
            for (Object obj : getNotesAndNotebooks(category, status, notebook)) {
                if (obj instanceof Note) {
                    dataList.add(new NotesAdapter.MultiItem((Note) obj));
                } else if (obj instanceof Notebook) {
                    dataList.add(new NotesAdapter.MultiItem((Notebook) obj));
                }
            }
            return dataList;
        }

        @Override
        protected void onPostExecute(List<NotesAdapter.MultiItem> dataList) {
            result.setValue(Resource.success(dataList));
        }
    }

    @MainThread
    private static List getNotesAndNotebooks(Category category, Status status, Notebook notebook) {
        if (category != null) {
            return status == Status.ARCHIVED ?
                    ArchiveHelper.getNotebooksAndNotes(PalmApp.getContext(), category) :
                    status == Status.TRASHED ?
                            TrashHelper.getNotebooksAndNotes(PalmApp.getContext(), category) :
                            NotebookHelper.getNotesAndNotebooks(PalmApp.getContext(), category);
        } else {
            return status == Status.ARCHIVED ?
                    ArchiveHelper.getNotebooksAndNotes(PalmApp.getContext(), notebook) :
                    status == Status.TRASHED ?
                            TrashHelper.getNotebooksAndNotes(PalmApp.getContext(), notebook) :
                            NotebookHelper.getNotesAndNotebooks(PalmApp.getContext(), notebook);
        }
    }
}
