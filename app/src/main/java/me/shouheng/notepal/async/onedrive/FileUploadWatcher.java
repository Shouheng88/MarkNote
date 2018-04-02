package me.shouheng.notepal.async.onedrive;

import java.util.concurrent.CountDownLatch;

/**
 * Created by shouh on 2018/4/2.*/
public class FileUploadWatcher implements Runnable {
    private CountDownLatch downLatch;
    private OnWatchListener onWatchListener;

    FileUploadWatcher(CountDownLatch downLatch, OnWatchListener onWatchListener) {
        this.downLatch = downLatch;
        this.onWatchListener = onWatchListener;
    }

    @Override
    public void run() {
        try {
            downLatch.await();
            if (onWatchListener != null) {
                onWatchListener.onState(State.FINISH, null);
            }
        } catch (InterruptedException e) {
            if (onWatchListener != null) {
                onWatchListener.onState(State.FAILED, e.getMessage());
            }
        }
    }

    public interface OnWatchListener {
        void onState(State watchedStatus, String msg);
    }

    public enum State {
        FINISH,
        FAILED
    }
}
