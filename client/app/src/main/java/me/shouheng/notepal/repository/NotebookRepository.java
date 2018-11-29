package me.shouheng.notepal.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import me.shouheng.commons.model.data.Resource;
import me.shouheng.data.entity.Notebook;
import me.shouheng.data.model.enums.Status;
import me.shouheng.data.store.BaseStore;
import me.shouheng.data.store.NotebookStore;
import me.shouheng.notepal.async.NormalAsyncTask;

/**
 * Created by Employee on 2018/3/13. */
public class NotebookRepository extends BaseRepository<Notebook> {

    @Override
    protected BaseStore<Notebook> getStore() {
        return NotebookStore.getInstance();
    }

    public LiveData<Resource<Notebook>> update(Notebook model, Status fromStatus, Status toStatus) {
        MutableLiveData<Resource<Notebook>> result = new MutableLiveData<>();
        new NormalAsyncTask<>(result, () -> {
            ((NotebookStore) getStore()).update(model, fromStatus, toStatus);
            return model;
        }).execute();
        return result;
    }

    public LiveData<Resource<Notebook>> move(Notebook notebook, Notebook toNotebook) {
        MutableLiveData<Resource<Notebook>> result = new MutableLiveData<>();
        new NormalAsyncTask<>(result, () -> {
            ((NotebookStore) getStore()).move(notebook, toNotebook);
            return notebook;
        }).execute();
        return result;
    }
}
