package me.shouheng.notepal.repository;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.model.MindSnagging;
import me.shouheng.notepal.provider.BaseStore;
import me.shouheng.notepal.provider.MindSnaggingStore;

/**
 * Created by Employee on 2018/3/13. */
public class SnaggingRepository extends BaseRepository<MindSnagging> {

    @Override
    protected BaseStore<MindSnagging> getStore() {
        return MindSnaggingStore.getInstance(PalmApp.getContext());
    }
}
