package me.shouheng.notepal.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.async.NormalAsyncTask;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.model.data.Resource;
import me.shouheng.notepal.provider.AttachmentsStore;
import me.shouheng.notepal.provider.BaseStore;

/**
 * Created by WangShouheng on 2018/3/13.*/
public class AttachmentRepository extends BaseRepository<Attachment> {

    @Override
    protected BaseStore<Attachment> getStore() {
        return AttachmentsStore.getInstance(PalmApp.getContext());
    }

    public LiveData<Resource<Attachment>> saveIfNew(Attachment attachment) {
        MutableLiveData<Resource<Attachment>> result = new MutableLiveData<>();
        new NormalAsyncTask<>(result, () -> {
            if (getStore().isNewModel(attachment.getCode())) {
                getStore().saveModel(attachment);
            }
            return attachment;
        }).execute();
        return result;
    }
}
