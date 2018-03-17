package me.shouheng.notepal.viewmodel;

import android.arch.lifecycle.LiveData;

import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.model.data.Resource;
import me.shouheng.notepal.repository.AttachmentRepository;
import me.shouheng.notepal.repository.BaseRepository;

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
}
