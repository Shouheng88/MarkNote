package me.shouheng.notepal.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.async.ResourceAsyncTask;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.model.ModelFactory;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.model.data.Resource;
import me.shouheng.notepal.provider.AttachmentsStore;
import me.shouheng.notepal.repository.AttachmentRepository;
import me.shouheng.notepal.repository.BaseRepository;
import me.shouheng.notepal.util.FileHelper;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.PreferencesUtils;

/**
 * Created by WangShouheng on 2018/3/13.*/
public class AttachmentViewModel extends BaseViewModel<Attachment> {

    @Override
    protected BaseRepository<Attachment> getRepository() {
        return new AttachmentRepository();
    }

    public LiveData<Resource<Attachment>> saveIfNew(Attachment attachment) {
        return ((AttachmentRepository) getRepository()).saveIfNew(attachment);
    }

    public LiveData<Resource<String>> readNoteContent(Note note) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        new ResourceAsyncTask<>(result, () -> {
            Attachment atFile = AttachmentsStore.getInstance(PalmApp.getContext()).get(note.getContentCode());
            if (atFile == null) {
                return Resource.success("");
            } else {
                try {
                    File noteFile = new File(atFile.getPath());
                    LogUtils.d(noteFile);
                    String content = FileUtils.readFileToString(noteFile, "utf-8");
                    return Resource.success(content);
                } catch (IOException e) {
                    return Resource.error(e.getMessage(), null);
                }
            }
        }).execute();
        return result;
    }

    public LiveData<Resource<Attachment>> writeNoteContent(Note note) {
        MutableLiveData<Resource<Attachment>> result = new MutableLiveData<>();
        new ResourceAsyncTask<>(result, () -> {
            Attachment atFile = AttachmentsStore.getInstance(PalmApp.getContext()).get(note.getContentCode());
            if (atFile == null) {
                // If the attachment is not exist, we will try to create a new one.
                String extension = PreferencesUtils.getInstance(PalmApp.getContext()).getNoteFileExtension();
                File noteFile = FileHelper.createNewAttachmentFile(PalmApp.getContext(), extension);
                try {
                    FileUtils.writeStringToFile(noteFile, note.getContent(), "utf-8");
                    atFile = ModelFactory.getAttachment();
                    atFile.setUri(FileHelper.getUriFromFile(PalmApp.getContext(), noteFile));
                    atFile.setSize(FileUtils.sizeOf(noteFile));
                    atFile.setPath(noteFile.getPath());
                    atFile.setName(noteFile.getName());
                    AttachmentsStore.getInstance(PalmApp.getContext()).saveModel(atFile);
                    return Resource.success(atFile);
                } catch (IOException e) {
                    return Resource.error(e.getMessage(), null);
                }
            } else {
                try {
                    File noteFile = new File(atFile.getPath());
                    FileUtils.writeStringToFile(noteFile, note.getContent(), "utf-8", false);
                    // Whenever the attachment file is updated, remember to update its attachment.
                    atFile.setLastModifiedTime(new Date());
                    AttachmentsStore.getInstance(PalmApp.getContext()).update(atFile);
                    return Resource.success(atFile);
                } catch (IOException e) {
                    return Resource.error(e.getMessage(), atFile);
                }
            }
        }).execute();
        return result;
    }
}
