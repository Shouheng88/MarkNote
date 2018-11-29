package me.shouheng.notepal.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import me.shouheng.commons.model.data.Resource;
import me.shouheng.data.entity.Category;
import me.shouheng.data.entity.Note;
import me.shouheng.data.model.enums.Status;
import me.shouheng.data.schema.CategorySchema;
import me.shouheng.data.store.BaseStore;
import me.shouheng.data.store.CategoryStore;
import me.shouheng.notepal.async.NormalAsyncTask;

/**
 * Created by WangShouheng on 2018/3/13.*/
public class CategoryRepository extends BaseRepository<Category> {

    @Override
    protected BaseStore<Category> getStore() {
        return CategoryStore.getInstance();
    }

    public LiveData<Resource<List<Category>>> getCategories(Note note) {
        MutableLiveData<Resource<List<Category>>> result = new MutableLiveData<>();
        new NormalAsyncTask<>(result, () -> ((CategoryStore) getStore()).getCategories(note)).execute();
        return result;
    }

    public LiveData<Resource<List<Category>>> getCategories(Status status) {
        MutableLiveData<Resource<List<Category>>> result = new MutableLiveData<>();
        new NormalAsyncTask<>(result, () -> {
            if (status == Status.ARCHIVED) {
                return getStore().getArchived(null, CategorySchema.CATEGORY_ORDER);
            } else if (status == Status.TRASHED) {
                return getStore().getTrashed(null, CategorySchema.CATEGORY_ORDER);
            } else {
                return getStore().get(null, CategorySchema.CATEGORY_ORDER);
            }
        }).execute();
        return result;
    }

    public LiveData<Resource<List<Category>>> updateOrders(List<Category> categories) {
        MutableLiveData<Resource<List<Category>>> result = new MutableLiveData<>();
        new NormalAsyncTask<>(result, () -> {
            ((CategoryStore) getStore()).updateOrders(categories);
            return categories;
        }).execute();
        return result;
    }
}
