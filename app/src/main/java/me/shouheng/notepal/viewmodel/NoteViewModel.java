package me.shouheng.notepal.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.NotesAdapter.MultiItem;
import me.shouheng.notepal.async.ResourceAsyncTask;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.model.Category;
import me.shouheng.notepal.model.MindSnagging;
import me.shouheng.notepal.model.ModelFactory;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.model.data.Resource;
import me.shouheng.notepal.model.enums.ModelType;
import me.shouheng.notepal.model.enums.Status;
import me.shouheng.notepal.provider.AttachmentsStore;
import me.shouheng.notepal.provider.NotesStore;
import me.shouheng.notepal.repository.BaseRepository;
import me.shouheng.notepal.repository.NoteRepository;
import me.shouheng.notepal.util.FileHelper;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.ModelHelper;
import me.shouheng.notepal.util.PreferencesUtils;

/**
 * Created by wang shouheng on 2018/3/13.*/
public class NoteViewModel extends BaseViewModel<Note> {

    @Override
    protected BaseRepository<Note> getRepository() {
        return new NoteRepository();
    }

    public LiveData<Resource<List<MultiItem>>> getMultiItems(Category category, Status status, Notebook notebook) {
        return ((NoteRepository) getRepository()).getMultiItems(category, status, notebook);
    }

    public String getEmptySubTitle(Status status) {
        return PalmApp.getContext().getString(
                status == Status.NORMAL ? R.string.notes_list_empty_sub_normal :
                        status == Status.TRASHED ? R.string.notes_list_empty_sub_trashed :
                                status == Status.ARCHIVED ? R.string.notes_list_empty_sub_archived :
                                        R.string.notes_list_empty_sub_normal);
    }

    public LiveData<Resource<Note>> saveSnagging(@NonNull Note note, MindSnagging snagging, @Nullable Attachment attachment) {
        MutableLiveData<Resource<Note>> result = new MutableLiveData<>();
        new ResourceAsyncTask<>(result, () -> {
            String content = snagging.getContent();
            if (attachment != null) {
                // Save attachment
                attachment.setModelCode(note.getCode());
                attachment.setModelType(ModelType.NOTE);
                AttachmentsStore.getInstance(PalmApp.getContext()).saveModel(attachment);

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
            note.setTitle(ModelHelper.getNoteTitle(snagging.getContent(), snagging.getContent()));
            note.setPreviewImage(snagging.getPicture());
            note.setPreviewContent(ModelHelper.getNotePreview(snagging.getContent()));

            // Create note file and attach to note
            String extension = PreferencesUtils.getInstance(PalmApp.getContext()).getNoteFileExtension();
            File noteFile = FileHelper.createNewAttachmentFile(PalmApp.getContext(), extension);
            try {
                // Create note content attachment
                Attachment atFile = ModelFactory.getAttachment();
                FileUtils.writeStringToFile(noteFile, note.getContent(), "utf-8");
                atFile.setUri(FileHelper.getUriFromFile(PalmApp.getContext(), noteFile));
                atFile.setSize(FileUtils.sizeOf(noteFile));
                atFile.setPath(noteFile.getPath());
                atFile.setName(noteFile.getName());
                atFile.setModelType(ModelType.NOTE);
                atFile.setModelCode(note.getCode());
                AttachmentsStore.getInstance(PalmApp.getContext()).saveModel(atFile);

                note.setContentCode(atFile.getCode());
            } catch (IOException e) {
                LogUtils.e(e);
                return Resource.error(e.getMessage(), null);
            }

            NotesStore.getInstance(PalmApp.getContext()).saveModel(note);

            // Return value
            return Resource.success(note);
        }).execute();
        return result;
    }
}
