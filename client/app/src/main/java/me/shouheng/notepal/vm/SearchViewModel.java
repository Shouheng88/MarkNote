package me.shouheng.notepal.vm;

import android.app.Application;
import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.shouheng.data.entity.Note;
import me.shouheng.data.model.enums.Status;
import me.shouheng.data.schema.BaseSchema;
import me.shouheng.data.schema.NoteSchema;
import me.shouheng.data.store.NotesStore;
import me.shouheng.mvvm.base.BaseViewModel;
import me.shouheng.mvvm.bean.Resources;

/**
 * Created by shouh on 2018/3/18.*/
public class SearchViewModel extends BaseViewModel {

    private String queryText;

    public SearchViewModel(@NonNull Application application) {
        super(application);
    }

    public Disposable fetchSearchResults() {
        getListObservable(Note.class).setValue(Resources.failed(null, ""));
        return Observable
                .create((ObservableOnSubscribe<List<Note>>) emitter -> {
                    String conditions = NoteSchema.TITLE + " LIKE '%'||'" + queryText + "'||'%'"
                            + " AND " + BaseSchema.STATUS + " != " + Status.TRASHED.id
                            + " AND " + BaseSchema.STATUS + " != " + Status.DELETED.id;
                    List<Note> notes = NotesStore.getInstance().get(conditions, NoteSchema.ADDED_TIME + " DESC ");
                    emitter.onNext(notes);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(notes -> getListObservable(Note.class).setValue(Resources.success(notes)));
    }

    public void notifyEmptyResult() {
        getListObservable(Note.class).setValue(Resources.success(Collections.emptyList()));
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }
}
