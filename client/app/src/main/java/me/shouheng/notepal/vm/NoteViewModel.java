package me.shouheng.notepal.vm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.shouheng.commons.model.data.Resource;
import me.shouheng.commons.utils.PersistData;
import me.shouheng.data.ModelFactory;
import me.shouheng.data.entity.Attachment;
import me.shouheng.data.entity.Category;
import me.shouheng.data.entity.Note;
import me.shouheng.data.model.enums.ModelType;
import me.shouheng.data.store.AttachmentsStore;
import me.shouheng.data.store.CategoryStore;
import me.shouheng.data.store.NotesStore;
import me.shouheng.notepal.Constants;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.manager.FileManager;
import me.shouheng.notepal.manager.NoteManager;

/**
 * The ViewModel for the note fragment.
 *
 * @author WngShhng (shouheng2015@gmail.com)
 * @version $Id: NoteViewModel, v 0.1 2018/12/1 0:49 shouh Exp$
 */
public class NoteViewModel extends ViewModel {

    private Note note;

    private List<Category> categories;

    private MutableLiveData<Resource<Note>> noteObservable;

    private MutableLiveData<Resource<String>> noteContentObservable;

    private MutableLiveData<Resource<Boolean>> saveOrUpdateObservable;

    public LiveData<Resource<Note>> getNoteObservable() {
        if (noteObservable == null) {
            noteObservable = new MutableLiveData<>();
        }
        return noteObservable;
    }

    public LiveData<Resource<String>> getNoteContentObservable() {
        if (noteContentObservable == null) {
            noteContentObservable = new MutableLiveData<>();
        }
        return noteContentObservable;
    }

    public LiveData<Resource<Boolean>> getSaveOrUpdateObservable() {
        if (saveOrUpdateObservable == null) {
            saveOrUpdateObservable = new MutableLiveData<>();
        }
        return saveOrUpdateObservable;
    }

    public void notifyNoteChanged(@NonNull Note note) {
        this.note = note;
        if (noteObservable != null) {
            noteObservable.setValue(Resource.success(note));
        }
    }

    /**
     * Get the associated content of note: the categories, note file attachment.
     *
     * @return the disposable
     */
    public Disposable fetchNoteContent() {
        return Observable.create((ObservableOnSubscribe<String>) emitter -> {
            /* Get the categories of note. */
            categories = CategoryStore.getInstance().getCategories(note);
            /* Get the note file of note. */
            Attachment atFile = AttachmentsStore.getInstance().get(note.getContentCode());
            if (atFile == null) {
                emitter.onNext(note.getContent() == null ? "" : note.getContent());
            } else {
                try {
                    File noteFile = new File(atFile.getPath());
                    String content = FileUtils.readFileToString(noteFile, Constants.NOTE_FILE_ENCODING);
                    emitter.onNext(content);
                } catch (IOException e) {
                    emitter.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    if (noteContentObservable != null) {
                        note.setContent(s);
                        noteContentObservable.setValue(Resource.success(s));
                    }
                }, throwable -> {
                    if (noteContentObservable != null) {
                        noteContentObservable.setValue(Resource.error(throwable.getMessage(), null));
                    }
                });
    }

    /**
     * Save or update the note with note content and title. The method will handle the note model
     * and the attachment of note at the same time. It will decide save or update action should be
     * called itself.
     *
     * @param title the title of note
     * @param content the content of note
     * @return the disposable
     */
    public Disposable saveOrUpdateNote(String title, String content) {
        return Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {

            /* The title is empty and the content is empty and the note is a new note, don't save it. */
            if (TextUtils.isEmpty(title) && TextUtils.isEmpty(content) && note.getContentCode() == 0) {
                emitter.onNext(false);
                return;
            }

            note.setContent(content);
            /* Get note title from title editor or note content. */
            note.setTitle(NoteManager.getTitle(title, content));
            /* Get preview image from note content. */
            note.setPreviewImage(NoteManager.getPreviewImage(content));
            note.setPreviewContent(NoteManager.getPreview(content));

            /* Get the note file and save the note content to it. */
            Attachment atFile = AttachmentsStore.getInstance().get(note.getContentCode());
            if (atFile == null) {
                String extension = "." + PersistData.getString(R.string.key_note_file_extension, "md");
                File noteFile = FileManager.createNewAttachmentFile(PalmApp.getContext(), extension);
                try {
                    FileUtils.writeStringToFile(noteFile, note.getContent(), Constants.NOTE_FILE_ENCODING);
                    atFile = ModelFactory.getAttachment();
                    atFile.setUri(FileManager.getUriFromFile(PalmApp.getContext(), noteFile));
                    atFile.setSize(FileUtils.sizeOf(noteFile));
                    atFile.setPath(noteFile.getPath());
                    atFile.setName(noteFile.getName());
                    atFile.setModelType(ModelType.NOTE);
                    atFile.setModelCode(note.getCode());
                    AttachmentsStore.getInstance().saveModel(atFile);
                    note.setContentCode(atFile.getCode());
                } catch (IOException e) {
                    emitter.onError(e);
                    return;
                }
            } else {
                try {
                    File noteFile = new File(atFile.getPath());
                    FileUtils.writeStringToFile(noteFile, note.getContent(), Constants.NOTE_FILE_ENCODING, false);
                    /* Whenever the attachment file is updated, remember to update its attachment. */
                    atFile.setLastModifiedTime(new Date());
                    AttachmentsStore.getInstance().update(atFile);
                } catch (IOException e) {
                    emitter.onError(e);
                    return;
                }
            }

            /* Save or update the note. */
            NotesStore.getInstance().saveOrUpdate(note);

            emitter.onNext(true);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(bool -> {
                    if (saveOrUpdateObservable != null) {
                        saveOrUpdateObservable.setValue(Resource.success(bool));
                    }
                }, throwable -> {
                    if (saveOrUpdateObservable != null) {
                        saveOrUpdateObservable.setValue(Resource.error(throwable.getMessage(), false));
                    }
                });
    }

    public Note getNote() {
        return note;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
