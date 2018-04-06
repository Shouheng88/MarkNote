package me.shouheng.notepal.async;

import android.os.AsyncTask;

import java.io.File;

import me.shouheng.notepal.listener.OnAttachingFileListener;

/**
 * Created by shouh on 2018/4/6.*/
public class ImageCompressTask extends AsyncTask<Void, Void, Void>{

    private File file;

    private OnAttachingFileListener mOnAttachingFileListener;

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }
}
