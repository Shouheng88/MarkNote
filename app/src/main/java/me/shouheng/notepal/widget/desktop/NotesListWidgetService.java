package me.shouheng.notepal.widget.desktop;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class NotesListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new NotesRemoteViewsFactory(this.getApplication(), intent);
    }
}
