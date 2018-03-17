package me.shouheng.notepal.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.async.NormalAsyncTask;
import me.shouheng.notepal.model.Location;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.model.data.Resource;
import me.shouheng.notepal.provider.BaseStore;
import me.shouheng.notepal.provider.LocationsStore;

/**
 * Created by shouh on 2018/3/17.*/
public class LocationRepository extends BaseRepository<Location> {

    @Override
    protected BaseStore<Location> getStore() {
        return LocationsStore.getInstance(PalmApp.getContext());
    }

    public LiveData<Resource<Location>> getLocation(Note note) {
        MutableLiveData<Resource<Location>> result = new MutableLiveData<>();
        new NormalAsyncTask<>(result, () -> ((LocationsStore) getStore()).getLocation(note)).execute();
        return result;
    }
}
