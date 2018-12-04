package me.shouheng.notepal.vm;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.shouheng.commons.model.data.Resource;
import me.shouheng.data.ModelFactory;
import me.shouheng.data.entity.Attachment;
import me.shouheng.data.entity.Category;
import me.shouheng.data.entity.Note;
import me.shouheng.data.entity.Notebook;
import me.shouheng.data.entity.QuickNote;
import me.shouheng.data.model.enums.ModelType;
import me.shouheng.data.store.AttachmentsStore;
import me.shouheng.data.store.CategoryStore;
import me.shouheng.data.store.NotebookStore;
import me.shouheng.data.store.NotesStore;
import me.shouheng.notepal.Constants;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.manager.FileManager;
import me.shouheng.notepal.manager.NoteManager;
import me.shouheng.notepal.common.preferences.UserPreferences;

/**
 * Created by WngShhng on 2018/11/29.
 */
public class MainViewModel extends ViewModel {

    private MutableLiveData<Resource<Notebook>> updateNotebookLiveData;

    private MutableLiveData<Resource<Note>> saveNoteLiveData;

    private MutableLiveData<Resource<Category>> saveCategoryLiveData;

    public MutableLiveData<Resource<Note>> getSaveNoteLiveData() {
        if (saveNoteLiveData == null) {
            saveNoteLiveData = new MutableLiveData<>();
        }
        return saveNoteLiveData;
    }

    public MutableLiveData<Resource<Notebook>> getUpdateNotebookLiveData() {
        if (updateNotebookLiveData == null) {
            updateNotebookLiveData = new MutableLiveData<>();
        }
        return updateNotebookLiveData;
    }

    public MutableLiveData<Resource<Category>> getSaveCategoryLiveData() {
        if (saveCategoryLiveData == null) {
            saveCategoryLiveData = new MutableLiveData<>();
        }
        return saveCategoryLiveData;
    }

    public Disposable saveNotebook(Notebook notebook) {
        return Observable.create((ObservableOnSubscribe<Notebook>) emitter -> {
            NotebookStore.getInstance().saveModel(notebook);
            emitter.onNext(notebook);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(notebook1 -> {
            if (updateNotebookLiveData != null) {
                updateNotebookLiveData.setValue(Resource.success(notebook1));
            }
        });
    }

    public Disposable saveCategory(Category category) {
        return Observable.create((ObservableOnSubscribe<Category>) emitter -> {
            CategoryStore.getInstance().saveModel(category);
            emitter.onNext(category);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(category1 -> {
            if (saveCategoryLiveData != null) {
                saveCategoryLiveData.setValue(Resource.success(category1));
            }
        });
    }


    /**
     * Save quick note to the database. This method will get information from the quick note to
     * the note and finally save the note to database and file system.
     *
     * @param note the note to save
     * @param quickNote the quick note contains note information
     * @param attachment the attachment of quick note
     * @return the disposable
     */
    public Disposable saveQuickNote(@NonNull Note note, QuickNote quickNote, @Nullable Attachment attachment) {
        return Observable.create((ObservableOnSubscribe<Note>) emitter -> {
            /* Prepare note content. */
            String content = quickNote.getContent();
            if (attachment != null) {
                attachment.setModelCode(note.getCode());
                attachment.setModelType(ModelType.NOTE);
                AttachmentsStore.getInstance().saveModel(attachment);
                if (Constants.MIME_TYPE_IMAGE.equalsIgnoreCase(attachment.getMineType())
                        || Constants.MIME_TYPE_SKETCH.equalsIgnoreCase(attachment.getMineType())) {
                    content = content + "![](" + quickNote.getPicture() + ")";
                } else {
                    content = content + "[](" + quickNote.getPicture() + ")";
                }
            }
            note.setContent(content);
            note.setTitle(NoteManager.getTitle(quickNote.getContent(), quickNote.getContent()));
            note.setPreviewImage(quickNote.getPicture());
            note.setPreviewContent(NoteManager.getPreview(note.getContent()));

            /* Save note to the file system. */
            String extension = UserPreferences.getInstance().getNoteFileExtension();
            File noteFile = FileManager.createNewAttachmentFile(PalmApp.getContext(), extension);
            try {
                Attachment atFile = ModelFactory.getAttachment();
                FileUtils.writeStringToFile(noteFile, note.getContent(), Constants.NOTE_FILE_ENCODING);
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
            }

            /* Save note. */
            NotesStore.getInstance().saveModel(note);

            emitter.onNext(note);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(note1 -> {
            if (saveNoteLiveData != null) {
                saveNoteLiveData.setValue(Resource.success(note1));
            }
        });
    }
}
