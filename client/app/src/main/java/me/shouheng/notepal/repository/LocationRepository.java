package me.shouheng.notepal.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import me.shouheng.commons.model.data.Resource;
import me.shouheng.data.entity.Location;
import me.shouheng.data.entity.Note;
import me.shouheng.data.store.BaseStore;
import me.shouheng.data.store.LocationsStore;
import me.shouheng.notepal.async.NormalAsyncTask;

/**
 * Created by shouh on 2018/3/17.*/
public class LocationRepository extends BaseRepository<Location> {

    @Override
    protected BaseStore<Location> getStore() {
        return LocationsStore.getInstance();
    }

    public LiveData<Resource<Location>> getLocation(Note note) {
        MutableLiveData<Resource<Location>> result = new MutableLiveData<>();
        new NormalAsyncTask<>(result, () -> ((LocationsStore) getStore()).getLocation(note)).execute();
        return result;
    }
}
