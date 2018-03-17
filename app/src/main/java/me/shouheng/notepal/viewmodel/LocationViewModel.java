package me.shouheng.notepal.viewmodel;

import android.arch.lifecycle.LiveData;

import me.shouheng.notepal.model.Location;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.model.data.Resource;
import me.shouheng.notepal.repository.BaseRepository;
import me.shouheng.notepal.repository.LocationRepository;

/**
 * Created by shouh on 2018/3/17.*/
public class LocationViewModel extends BaseViewModel<Location> {

    @Override
    protected BaseRepository<Location> getRepository() {
        return new LocationRepository();
    }

    public LiveData<Resource<Location>> getLocation(Note note) {
        return ((LocationRepository) getRepository()).getLocation(note);
    }
}