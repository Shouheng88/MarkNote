package me.shouheng.notepal.vm;

import android.app.Application;
import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.shouheng.data.entity.Category;
import me.shouheng.data.model.enums.Status;
import me.shouheng.data.schema.CategorySchema;
import me.shouheng.data.store.CategoryStore;
import me.shouheng.mvvm.base.BaseViewModel;
import me.shouheng.mvvm.bean.Resources;
import me.shouheng.notepal.R;
import me.shouheng.utils.app.ResUtils;

/**
 * @author WngShhng (shouheng2015@gmail.com)
 * @version $Id: CategoriesViewModel, v 0.1 2018/12/2 20:13 shouh Exp$
 */
public class CategoriesViewModel extends BaseViewModel {

    private Status status;

    public CategoriesViewModel(@NonNull Application application) {
        super(application);
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
        int res;
        switch (status) {
            case TRASHED:
                res = R.string.category_list_empty_for_trashed;
                break;
            case ARCHIVED:
                res = R.string.category_list_empty_for_archived;
                break;
            default:
                res = R.string.category_list_empty_subtitle;
        }
        return ResUtils.getString(res);
    }

    /**
     * Fetch the categories
     */
    public Disposable fetchCategories() {
        getListObservable(Category.class).setValue(Resources.loading(null));
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
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(categories ->
                getListObservable(Category.class).setValue(Resources.success(categories)));
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
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(category1 -> getObservable(Category.class).setValue(Resources.success(category1)));
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
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(category1 -> getObservable(Category.class).setValue(Resources.success(category1)));
    }
}
