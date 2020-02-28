package me.shouheng.notepal.vm;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.shouheng.commons.model.data.Resource;
import me.shouheng.data.entity.Attachment;
import me.shouheng.data.entity.Category;
import me.shouheng.data.entity.Note;
import me.shouheng.data.store.AttachmentsStore;
import me.shouheng.data.store.CategoryStore;
import me.shouheng.data.store.NotesStore;
import me.shouheng.notepal.Constants;
import me.shouheng.notepal.common.exception.NoteFileReadException;

/**
 * @author WngShhng (shouheng2015@gmail.com)
 * @version $Id: NoteViewerViewModel, v 0.1 2018/12/1 0:49 shouh Exp$
 */
public class NoteViewerViewModel extends ViewModel {

    private Note note;

    private boolean isPreview = false;

    private String html;

    private MutableLiveData<Resource<String>> noteContentObservable;

    private MutableLiveData<Resource<List<Category>>> categoriesObservable;

    public MutableLiveData<Resource<String>> getNoteContentObservable() {
        if (noteContentObservable == null) {
            noteContentObservable = new MutableLiveData<>();
        }
        return noteContentObservable;
    }

    public MutableLiveData<Resource<List<Category>>> getCategoriesObservable() {
        if (categoriesObservable == null) {
            categoriesObservable = new MutableLiveData<>();
        }
        return categoriesObservable;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public boolean isPreview() {
        return isPreview;
    }

    public void setPreview(boolean preview) {
        isPreview = preview;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    /**
     * Read the note content according to the {@link #isPreview} field. To decide should
     * load note content from file system of just get the content from the field of note.
     *
     * @return the observable for note content
     */
    public Disposable readNoteContent() {
        return Observable.create((ObservableOnSubscribe<String>) emitter -> {
            if (isPreview) {
                emitter.onNext(note.getContent());
            } else {
                note = NotesStore.getInstance().get(note.getCode());
                /* Get Note Attachment File. */
                Attachment noteFile = AttachmentsStore.getInstance().get(note.getContentCode());
                if (noteFile == null) {
                    /* Set the note content empty string to avoid NPE. */
                    note.setContent("");
                    emitter.onError(new NoteFileReadException(note));
                    return;
                }
                File file = new File(noteFile.getPath());
                try {
                    String content = FileUtils.readFileToString(file, Constants.NOTE_FILE_ENCODING);
                    note.setContent(content);
                    emitter.onNext(content);
                } catch (IOException e) {
                    emitter.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(s -> {
            if (noteContentObservable != null) {
                noteContentObservable.setValue(Resource.success(s));
            }
        }, throwable -> {
            if (noteContentObservable != null) {
                noteContentObservable.setValue(Resource.error(throwable.getMessage(), null));
            }
        });
    }

    /**
     * Get the categories associated with note.
     *
     * @return the disposable for this event.
     */
    public Disposable getNoteCategories() {
        return Observable.create((ObservableOnSubscribe<List<Category>>) emitter -> {
            List<Category> categories = CategoryStore.getInstance().getCategories(note);
            emitter.onNext(categories);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(categories -> {
            if (categoriesObservable != null) {
                categoriesObservable.setValue(Resource.success(categories));
            }
        });
    }
}
