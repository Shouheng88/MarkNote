package me.shouheng.notepal.async;

import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import me.shouheng.notepal.listener.OnTaskExecutingListener;
import me.shouheng.commons.model.data.Resource;

/**
 * Created by shouh on 2018/3/16.*/
public class NormalAsyncTask<M> extends AsyncTask<Void, Integer, Resource<M>> {

    private MutableLiveData<Resource<M>> result;

    private OnTaskExecutingListener<M> onTaskExecutingListener;

    public NormalAsyncTask(MutableLiveData<Resource<M>> result, OnTaskExecutingListener<M> listener) {
        this.result = result;
        this.onTaskExecutingListener = listener;
    }

    @Override
    protected Resource<M> doInBackground(Void... voids) {
        if (onTaskExecutingListener != null) {
            M ret = onTaskExecutingListener.onExecuting();
            return Resource.success(ret);
        }
        return Resource.error("Failed to load data", null);
    }

    @Override
    protected void onPostExecute(Resource<M> mResource) {
        result.setValue(mResource);
    }
}
