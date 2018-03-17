package me.shouheng.notepal.viewmodel;

import android.arch.lifecycle.LiveData;

import java.util.List;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.model.Category;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.model.data.Resource;
import me.shouheng.notepal.model.enums.Status;
import me.shouheng.notepal.repository.BaseRepository;
import me.shouheng.notepal.repository.CategoryRepository;

/**
 * Created by WangShouheng on 2018/3/13.*/
public class CategoryViewModel extends BaseViewModel<Category> {

    @Override
    protected BaseRepository<Category> getRepository() {
        return new CategoryRepository();
    }

    public String getEmptySubTitle(Status status) {
        if (status == null) return null;
        return PalmApp.getContext().getString(
                status == Status.NORMAL ? R.string.tags_list_empty_sub_normal :
                        status == Status.TRASHED ? R.string.tags_list_empty_sub_trashed :
                                status == Status.ARCHIVED ? R.string.tags_list_empty_sub_archived :
                                        R.string.tags_list_empty_sub_normal);
    }

    public LiveData<Resource<List<Category>>> getCategories(Note note) {
        return ((CategoryRepository) getRepository()).getCategories(note);
    }

    public LiveData<Resource<List<Category>>> getCategories(Status status) {
        return ((CategoryRepository) getRepository()).getCategories(status);
    }

    public LiveData<Resource<List<Category>>> updateOrders(List<Category> categories) {
        CategoryRepository categoryRepository = new CategoryRepository();
        return categoryRepository.updateOrders(categories);
    }
}
