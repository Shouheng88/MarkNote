package me.shouheng.notepal.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.model.MindSnagging;
import me.shouheng.notepal.model.data.Resource;
import me.shouheng.notepal.model.enums.Status;
import me.shouheng.notepal.provider.schema.MindSnaggingSchema;
import me.shouheng.notepal.repository.BaseRepository;
import me.shouheng.notepal.repository.SnaggingRepository;

/**
 * Created by Employee on 2018/3/13.*/
public class SnaggingViewModel extends BaseViewModel<MindSnagging> {

    public final static String ERROR_MSG_NO_MODE_DATA = "ERROR_NO_MORE_DATA";

    private int modelsCount, pageNumber = 20, startIndex = 0;

    private boolean isLoadingMore;

    @Override
    protected BaseRepository<MindSnagging> getRepository() {
        return new SnaggingRepository();
    }

    public LiveData<Resource<List<MindSnagging>>> loadSnagging(Status status) {
        SnaggingRepository snaggingRepository = new SnaggingRepository();
        return snaggingRepository.getPage(startIndex,
                pageNumber,
                MindSnaggingSchema.ADDED_TIME + " DESC ",
                status,
                false);
    }

    public LiveData<Resource<List<MindSnagging>>> loadMore(Status status) {
        isLoadingMore = true;
        startIndex += pageNumber;
        if (startIndex > modelsCount) {
            startIndex -= pageNumber;
        } else {
            SnaggingRepository snaggingRepository = new SnaggingRepository();
            return snaggingRepository.getPage(startIndex,
                    pageNumber,
                    MindSnaggingSchema.ADDED_TIME + " DESC ",
                    status,
                    false);
        }
        MutableLiveData<Resource<List<MindSnagging>>> result = new MutableLiveData<>();
        result.setValue(Resource.error(ERROR_MSG_NO_MODE_DATA, null));
        return result;
    }

    public String getEmptySubTitle(Status status) {
        if (status == null) return null;
        return PalmApp.getContext().getString(
                status == Status.NORMAL ? R.string.mind_snaggings_list_empty_sub_normal :
                        status == Status.TRASHED ? R.string.mind_snaggings_list_empty_sub_trashed :
                                status == Status.ARCHIVED ? R.string.mind_snaggings_list_empty_sub_archived :
                                        R.string.mind_snaggings_list_empty_sub_normal);
    }

    public void setModelsCount(int modelsCount) {
        this.modelsCount = modelsCount;
    }

    public boolean isLoadingMore() {
        return isLoadingMore;
    }

    public void setLoadingMore(boolean loadingMore) {
        isLoadingMore = loadingMore;
    }
}
