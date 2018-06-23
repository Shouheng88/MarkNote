package me.shouheng.notepal.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import me.shouheng.notepal.model.Model;
import me.shouheng.notepal.model.data.Resource;
import me.shouheng.notepal.model.enums.Status;
import me.shouheng.notepal.repository.BaseRepository;

/**
 * Created by shouh on 2018/3/17.*/
public abstract class BaseViewModel<T extends Model> extends ViewModel {

    protected abstract BaseRepository<T> getRepository();

    public LiveData<Resource<T>> get(long code) {
        return getRepository().get(code);
    }

    public LiveData<Resource<T>> get(long code, Status status, boolean exclude) {
        return getRepository().get(code, status, exclude);
    }

    public LiveData<Resource<List<T>>> get(String whereSQL, String orderSQL) {
        return getRepository().get(whereSQL, orderSQL);
    }

    public LiveData<Resource<List<T>>> get(String whereSQL, String orderSQL, Status status, boolean exclude) {
        return getRepository().get(whereSQL, orderSQL, status, exclude);
    }

    public LiveData<Resource<List<T>>> get(String whereSQL, String[] whereArgs, String orderSQL) {
        return getRepository().get(whereSQL, whereArgs, orderSQL);
    }

    public LiveData<Resource<List<T>>> getArchived(String whereSQL, String orderSQL) {
        return getRepository().getArchived(whereSQL, orderSQL);
    }

    public LiveData<Resource<List<T>>> getTrashed(String whereSQL, String orderSQL) {
        return getRepository().getTrashed(whereSQL, orderSQL);
    }

    public LiveData<Resource<Integer>> getCount(String whereSQL, Status status, boolean exclude) {
        return getRepository().getCount(whereSQL, status, exclude);
    }

    public LiveData<Resource<Boolean>> isNewModel(Long code) {
        return getRepository().isNewModel(code);
    }

    public LiveData<Resource<List<T>>> getPage(int index, int pageCount, String orderSQL, Status status, boolean exclude) {
        return getRepository().getPage(index, pageCount, orderSQL, status, exclude);
    }

    public LiveData<Resource<T>> saveModel(T model) {
        return getRepository().saveModel(model);
    }

    public LiveData<Resource<T>> update(T model) {
        return getRepository().update(model);
    }

    public LiveData<Resource<T>> update(T model, Status toStatus) {
        return getRepository().update(model, toStatus);
    }

    public LiveData<Resource<T>> saveOrUpdate(T model) {
        return getRepository().saveOrUpdate(model);
    }

    public LiveData<Resource<List<T>>> batchUpdate(List<T> models, Status toStatus) {
        return getRepository().batchUpdate(models, toStatus);
    }
}
