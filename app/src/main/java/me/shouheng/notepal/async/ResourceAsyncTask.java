package me.shouheng.notepal.async;

import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import me.shouheng.notepal.listener.OnResourceExecutingListener;
import me.shouheng.notepal.model.data.Resource;

/**
 * Created by shouh on 2018/3/17. */
public class ResourceAsyncTask<T, M extends Resource<T>> extends AsyncTask<Void, Integer, M> {

    private MutableLiveData<M> result;

    private OnResourceExecutingListener<M> onResourceExecutingListener;

    public ResourceAsyncTask(MutableLiveData<M> result, OnResourceExecutingListener<M> listener) {
        this.result = result;
        this.onResourceExecutingListener = listener;
    }

    @Override
    protected M doInBackground(Void... voids) {
        if (onResourceExecutingListener != null) {
            return onResourceExecutingListener.onExecuting();
        }
        return null;
    }

    @Override
    protected void onPostExecute(M mResource) {
        result.setValue(mResource);
    }
}
