package me.shouheng.notepal.onedrive;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by shouh on 2018/4/2.
 */
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
            downLatch.await(10, TimeUnit.MINUTES);
            if (onWatchListener != null) {
                onWatchListener.onFinish();
            }
        } catch (InterruptedException e) {
            if (onWatchListener != null) {
                onWatchListener.onFail(e.getMessage());
            }
        }
    }

    public interface OnWatchListener {
        void onFinish();
        void onFail(String msg);
    }
}
