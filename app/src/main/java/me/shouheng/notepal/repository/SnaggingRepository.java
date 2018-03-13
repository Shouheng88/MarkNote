package me.shouheng.notepal.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.model.MindSnagging;
import me.shouheng.notepal.model.data.Resource;
import me.shouheng.notepal.provider.BaseStore;
import me.shouheng.notepal.provider.MindSnaggingStore;

/**
 * Created by Employee on 2018/3/13. */
public class SnaggingRepository extends BaseRepository<MindSnagging> {

    @Override
    protected BaseStore<MindSnagging> getStore() {
        return MindSnaggingStore.getInstance(PalmApp.getContext());
    }

    public LiveData<Resource<MindSnagging>> saveOrUpdate(MindSnagging mindSnagging) {
        MutableLiveData<Resource<MindSnagging>> result = new MutableLiveData<>();
        new NormalAsyncTask<>(result, () -> {
            if (getStore().isNewModel(mindSnagging.getCode())) {
                getStore().saveModel(mindSnagging);
            }
            return mindSnagging;
        }).execute();
        return result;
    }
}
