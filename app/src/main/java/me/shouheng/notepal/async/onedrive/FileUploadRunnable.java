package me.shouheng.notepal.async.onedrive;

import com.onedrive.sdk.concurrency.ICallback;
import com.onedrive.sdk.core.ClientException;
import com.onedrive.sdk.extensions.Item;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import me.shouheng.notepal.manager.onedrive.OneDriveManager;
import me.shouheng.notepal.model.Attachment;

/**
 * Created by shouh on 2018/4/2.*/
public class FileUploadRunnable implements Runnable {
    private Attachment attachment;
    private Item item;
    private String toItemId;
    private CountDownLatch downLatch;
    private OnUploadListener onUploadListener;

    private File file;

    FileUploadRunnable(Attachment attachment, String toItemId, Item item, CountDownLatch downLatch, OnUploadListener onUploadListener) {
        this.attachment = attachment;
        this.downLatch = downLatch;
        this.item = item;
        this.toItemId = toItemId;
        this.onUploadListener = onUploadListener;
    }

    @Override
    public void run() {
        try {
            file = new File(attachment.getPath());
            if (item == null) {
                upload();
            } else if (file.lastModified() > item.lastModifiedDateTime.getTimeInMillis()) {
                deleteAndUpload();
            } else {
                onFinish();
            }
        } catch (Exception e) {
            onFailed(e.getMessage());
        }
    }

    private void deleteAndUpload() {
        OneDriveManager.getInstance().delete(item.id, new ICallback<Void>() {
            @Override
            public void success(Void aVoid) {
                upload();
            }

            @Override
            public void failure(ClientException ex) {
                onFailed(ex.getMessage());
            }
        });
    }

    private void upload() {
        OneDriveManager.getInstance().upload(toItemId, file, new OneDriveManager.UploadProgressCallback<Item>() {
            @Override
            public void progress(long current, long max) {}

            @Override
            public void success(Item item) {
                onFinish();
            }

            @Override
            public void failure(Exception e) {
                onFailed(e.getMessage());
            }
        });
    }

    private void onFinish() {
        this.downLatch.countDown();
        if (onUploadListener != null) {
            onUploadListener.onState(State.FINISH, "");
        }
    }

    private void onFailed(String msg) {
        this.downLatch.countDown();
        if (onUploadListener != null) {
            onUploadListener.onState(State.FAILED, msg);
        }
    }

    public interface OnUploadListener {
        void onState(State state, String msg);
    }

    public enum State {
        FINISH,
        FAILED
    }
}
