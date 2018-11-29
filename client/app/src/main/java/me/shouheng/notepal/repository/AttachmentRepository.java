package me.shouheng.notepal.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import me.shouheng.commons.model.data.Resource;
import me.shouheng.data.entity.Attachment;
import me.shouheng.data.store.AttachmentsStore;
import me.shouheng.data.store.BaseStore;
import me.shouheng.notepal.async.NormalAsyncTask;

/**
 * Created by WangShouheng on 2018/3/13.*/
public class AttachmentRepository extends BaseRepository<Attachment> {

    @Override
    protected BaseStore<Attachment> getStore() {
        return AttachmentsStore.getInstance();
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
