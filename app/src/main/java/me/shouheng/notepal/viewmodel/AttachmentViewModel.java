package me.shouheng.notepal.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.model.data.Resource;
import me.shouheng.notepal.repository.AttachmentRepository;

/**
 * Created by WangShouheng on 2018/3/13.*/
public class AttachmentViewModel extends ViewModel {

    public LiveData<Resource<Attachment>> saveIfNew(Attachment attachment) {
        AttachmentRepository attachmentRepository = new AttachmentRepository();
        return attachmentRepository.saveIfNew(attachment);
    }
}
