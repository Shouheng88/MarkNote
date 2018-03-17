package me.shouheng.notepal.repository;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.model.Location;
import me.shouheng.notepal.provider.BaseStore;
import me.shouheng.notepal.provider.LocationsStore;

/**
 * Created by shouh on 2018/3/17.*/
public class LocationRepository extends BaseRepository<Location> {

    @Override
    protected BaseStore<Location> getStore() {
        return LocationsStore.getInstance(PalmApp.getContext());
    }
}
