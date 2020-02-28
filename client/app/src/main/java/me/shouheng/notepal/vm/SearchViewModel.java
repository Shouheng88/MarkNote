package me.shouheng.notepal.vm;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.shouheng.commons.model.data.Resource;
import me.shouheng.data.entity.Note;
import me.shouheng.data.model.enums.Status;
import me.shouheng.data.schema.BaseSchema;
import me.shouheng.data.schema.NoteSchema;
import me.shouheng.data.store.NotesStore;

/**
 * Created by shouh on 2018/3/18.*/
public class SearchViewModel extends ViewModel {

    private String queryText;

    private MutableLiveData<Resource<List<Note>>> notesLiveData;

    public MutableLiveData<Resource<List<Note>>> getNotesLiveData() {
        if (notesLiveData == null) {
            notesLiveData = new MutableLiveData<>();
        }
        return notesLiveData;
    }

    public Disposable fetchSearchResults() {
        if (notesLiveData != null) {
            notesLiveData.setValue(Resource.loading(null));
        }
        return Observable
                .create((ObservableOnSubscribe<List<Note>>) emitter -> {
                    String conditions = NoteSchema.TITLE + " LIKE '%'||'" + queryText + "'||'%'"
                            + " AND " + BaseSchema.STATUS + " != " + Status.TRASHED.id
                            + " AND " + BaseSchema.STATUS + " != " + Status.DELETED.id;
                    List<Note> notes = NotesStore.getInstance().get(conditions, NoteSchema.ADDED_TIME + " DESC ");
                    emitter.onNext(notes);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(notes -> {
                    if (notesLiveData != null) {
                        notesLiveData.setValue(Resource.success(notes));
                    }
                });
    }

    public void notifyEmptyResult() {
        if (notesLiveData != null) {
            notesLiveData.setValue(Resource.success(Collections.emptyList()));
        }
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }
}
