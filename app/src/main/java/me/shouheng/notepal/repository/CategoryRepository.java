package me.shouheng.notepal.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.model.Category;
import me.shouheng.notepal.model.data.Resource;
import me.shouheng.notepal.model.enums.Status;
import me.shouheng.notepal.provider.BaseStore;
import me.shouheng.notepal.provider.CategoryStore;
import me.shouheng.notepal.provider.schema.CategorySchema;

/**
 * Created by WangShouheng on 2018/3/13.*/
public class CategoryRepository extends BaseRepository<Category> {

    @Override
    protected BaseStore<Category> getStore() {
        return CategoryStore.getInstance(PalmApp.getContext());
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
