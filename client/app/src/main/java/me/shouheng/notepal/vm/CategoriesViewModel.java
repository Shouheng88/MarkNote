package me.shouheng.notepal.vm;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.shouheng.commons.model.data.Resource;
import me.shouheng.commons.utils.PalmUtils;
import me.shouheng.data.entity.Category;
import me.shouheng.data.model.enums.Status;
import me.shouheng.data.schema.CategorySchema;
import me.shouheng.data.store.CategoryStore;
import me.shouheng.notepal.R;

/**
 * @author WngShhng (shouheng2015@gmail.com)
 * @version $Id: CategoriesViewModel, v 0.1 2018/12/2 20:13 shouh Exp$
 */
public class CategoriesViewModel extends ViewModel {

    private Status status;

    private MutableLiveData<Resource<List<Category>>> categoriesLiveData;

    private MutableLiveData<Resource<Category>> categoryUpdateObserver;

    public MutableLiveData<Resource<List<Category>>> getCategoriesLiveData() {
        if (categoriesLiveData == null) {
            categoriesLiveData = new MutableLiveData<>();
        }
        return categoriesLiveData;
    }

    public MutableLiveData<Resource<Category>> getCategoryUpdateObserver() {
        if (categoryUpdateObserver == null) {
            categoryUpdateObserver = new MutableLiveData<>();
        }
        return categoryUpdateObserver;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Get the sub title for the empty view.
     *
     * @return the sub title.
     */
    public String getEmptySubTitle() {
        if (status == null) return null;
        return PalmUtils.getStringCompact(
                status == Status.NORMAL ? R.string.category_list_empty_subtitle :
                        status == Status.TRASHED ? R.string.category_list_empty_for_trashed :
                                status == Status.ARCHIVED ? R.string.category_list_empty_for_archived :
                                        R.string.category_list_empty_subtitle);
    }

    /**
     * Fetch the categories
     */
    public Disposable fetchCategories() {
        if (categoriesLiveData != null) {
            categoriesLiveData.setValue(Resource.loading(null));
        }
        return Observable.create((ObservableOnSubscribe<List<Category>>) emitter -> {
            List<Category> categories;
            if (status == Status.ARCHIVED) {
                categories = CategoryStore.getInstance().getArchived(null, CategorySchema.CATEGORY_ORDER);
            } else if (status == Status.TRASHED) {
                categories = CategoryStore.getInstance().getTrashed(null, CategorySchema.CATEGORY_ORDER);
            } else {
                categories = CategoryStore.getInstance().get(null, CategorySchema.CATEGORY_ORDER);
            }
            emitter.onNext(categories);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(categories -> {
            if (categoriesLiveData != null) {
                categoriesLiveData.setValue(Resource.success(categories));
            }
        });
    }

    /**
     * Update the category
     *
     * @param category the category to update
     * @return the disposable
     */
    public Disposable updateCategory(Category category) {
        return Observable.create((ObservableOnSubscribe<Category>) emitter -> {
            CategoryStore.getInstance().update(category);
            emitter.onNext(category);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(category1 -> {
            if (categoryUpdateObserver != null) {
                categoryUpdateObserver.setValue(Resource.success(category1));
            }
        });
    }

    /**
     * Update category to status
     *
     * @param category the category to update
     * @param toStatus the status to update to
     * @return the disposable
     */
    public Disposable updateCategory(Category category, Status toStatus) {
        return Observable.create((ObservableOnSubscribe<Category>) emitter -> {
            CategoryStore.getInstance().update(category, toStatus);
            emitter.onNext(category);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(category1 -> {
            if (categoryUpdateObserver != null) {
                categoryUpdateObserver.setValue(Resource.success(category1));
            }
        });
    }
}
