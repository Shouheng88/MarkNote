package me.shouheng.notepal.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import me.shouheng.notepal.model.Location;
import me.shouheng.notepal.model.data.Resource;
import me.shouheng.notepal.repository.LocationRepository;

/**
 * Created by shouh on 2018/3/17.*/
public class LocationViewModel extends ViewModel {

    public LiveData<Resource<Location>> get(long code) {
        LocationRepository locationRepository = new LocationRepository();
        return locationRepository.get(code);
    }
}