package me.shouheng.notepal.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import me.shouheng.commons.model.data.Resource;
import me.shouheng.commons.utils.LogUtils;
import me.shouheng.data.ModelFactory;
import me.shouheng.data.entity.Attachment;
import me.shouheng.data.entity.MindSnagging;
import me.shouheng.data.entity.Note;
import me.shouheng.data.model.enums.ModelType;
import me.shouheng.data.store.AttachmentsStore;
import me.shouheng.data.store.NotesStore;
import me.shouheng.notepal.Constants;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.async.ResourceAsyncTask;
import me.shouheng.notepal.manager.FileManager;
import me.shouheng.notepal.manager.NoteManager;
import me.shouheng.notepal.repository.BaseRepository;
import me.shouheng.notepal.repository.NoteRepository;
import me.shouheng.notepal.util.preferences.NotePreferences;

/**
 * Created by wang shouheng on 2018/3/13.*/
public class NoteViewModel extends BaseViewModel<Note> {

    @Override
    protected BaseRepository<Note> getRepository() {
        return new NoteRepository();
    }

    public LiveData<Resource<Note>> saveSnagging(@NonNull Note note, MindSnagging snagging, @Nullable Attachment attachment) {
        MutableLiveData<Resource<Note>> result = new MutableLiveData<>();
        new ResourceAsyncTask<>(result, () -> {
            String content = snagging.getContent();
            if (attachment != null) {
                // Save attachment
                attachment.setModelCode(note.getCode());
                attachment.setModelType(ModelType.NOTE);
                AttachmentsStore.getInstance().saveModel(attachment);

                // prepare note content
                if (Constants.MIME_TYPE_IMAGE.equalsIgnoreCase(attachment.getMineType())
                        || Constants.MIME_TYPE_SKETCH.equalsIgnoreCase(attachment.getMineType())) {
                    content = content + "![](" + snagging.getPicture() + ")";
                } else {
                    content = content + "[](" + snagging.getPicture() + ")";
                }
            }

            // Prepare note info
            note.setContent(content);
            note.setTitle(NoteManager.getTitle(snagging.getContent(), snagging.getContent()));
            note.setPreviewImage(snagging.getPicture());
            note.setPreviewContent(NoteManager.getPreview(snagging.getContent()));

            // Create note file and attach to note
            String extension = NotePreferences.getInstance().getNoteFileExtension();
            File noteFile = FileManager.createNewAttachmentFile(PalmApp.getContext(), extension);
            try {
                // Create note content attachment
                Attachment atFile = ModelFactory.getAttachment();
                FileUtils.writeStringToFile(noteFile, note.getContent(), "utf-8");
                atFile.setUri(FileManager.getUriFromFile(PalmApp.getContext(), noteFile));
                atFile.setSize(FileUtils.sizeOf(noteFile));
                atFile.setPath(noteFile.getPath());
                atFile.setName(noteFile.getName());
                atFile.setModelType(ModelType.NOTE);
                atFile.setModelCode(note.getCode());
                AttachmentsStore.getInstance().saveModel(atFile);

                note.setContentCode(atFile.getCode());
            } catch (IOException e) {
                LogUtils.e(e);
                return Resource.error(e.getMessage(), null);
            }

            NotesStore.getInstance().saveModel(note);

            // Return value
            return Resource.success(note);
        }).execute();
        return result;
    }
}
