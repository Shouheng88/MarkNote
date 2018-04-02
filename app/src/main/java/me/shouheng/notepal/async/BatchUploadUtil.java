package me.shouheng.notepal.async;

import android.os.AsyncTask;

import com.onedrive.sdk.concurrency.ICallback;
import com.onedrive.sdk.core.ClientException;
import com.onedrive.sdk.extensions.Item;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.manager.one.drive.FileContent;
import me.shouheng.notepal.manager.one.drive.OneDriveManager;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.model.enums.Status;
import me.shouheng.notepal.provider.AttachmentsStore;
import me.shouheng.notepal.provider.schema.AttachmentSchema;
import me.shouheng.notepal.util.LogUtils;

/**
 * Created by shouh on 2018/4/1.*/
public class BatchUploadUtil {

    /**
     * Core thread number, real thread number will increased one for count down latch water.
     * The same as per page files count. */
    private int threadCore = 2;

    /**
     * Thread pool. */
    private ExecutorService executor;

    private int totalFiles;

    /**
     * Current page index. */
    private int startIndex = 0;

    private String itemId;

    private int filesUploaded = 0;

    private Map<String, Item> fileMap;

    public BatchUploadUtil(String itemId, int threadCore) {
        this.threadCore = threadCore;
        this.itemId = itemId;
        executor = Executors.newFixedThreadPool(threadCore + 1);
    }

    public void shutDown() {
        executor.shutdownNow();
    }

    public void begin() {
        totalFiles = AttachmentsStore.getInstance(PalmApp.getContext()).getCount(null, null, false);
        new PrepareTask(map -> {
            LogUtils.e("Batch upload task prepared.");
            fileMap = map;
            doTask();
        }).execute(itemId);
    }

    private static class PrepareTask extends AsyncTask<String, Integer, String> {

        private OnGetFilesListener onGetFilesListener;

        PrepareTask(OnGetFilesListener onGetFilesListener) {
            this.onGetFilesListener = onGetFilesListener;
        }

        @Override
        protected String doInBackground(String... params) {
            OneDriveManager.getInstance().getDirectories(params[0], new ICallback<Item>() {
                @Override
                public void success(Item item) {
                    if (item.children == null || item.children.getCurrentPage().isEmpty()) {
                        if (onGetFilesListener != null) {
                            onGetFilesListener.onGetFiles(new HashMap<>());
                        }
                    } else {
                        Map<String, Item> map = new HashMap<>();
                        for (final Item childItem : item.children.getCurrentPage()) {
                            map.put(childItem.name, childItem);
                        }
                        if (onGetFilesListener != null) {
                            onGetFilesListener.onGetFiles(map);
                        }
                    }
                }

                @Override
                public void failure(ClientException ex) {
                    LogUtils.e(ex);
                }
            });
            return "executed";
        }

        private interface OnGetFilesListener {
            void onGetFiles(Map<String, Item> map);
        }
    }

    private void doTask() {
        LogUtils.d("Upload task started!");
        /* Latch to control the upload event. */
        CountDownLatch downLatch = new CountDownLatch(threadCore);
        executor.submit(new FileUploadWatcher(downLatch, (watchedStatus, msg) -> {
            switch (watchedStatus) {
                case FAILED:
                    LogUtils.e("Error when upload: " + msg);
                    break;
                case FINISH:
                    startIndex += threadCore;
                    if (startIndex > totalFiles) {
                        LogUtils.d("=>=>=>=>=>=>=>=>=>=> All files uploaded! ");
                        executor.shutdown();
                        break;
                    } else {
                        doTask();
                    }
                    break;
            }
        }));

        // Start upload for the first time
        batchUpload(downLatch);
    }

    private void batchUpload(CountDownLatch countDownLatch) {
        // Get files to upload
        List<Attachment> attachments = AttachmentsStore.getInstance(PalmApp.getContext()).getPage(
                startIndex,
                threadCore,
                AttachmentSchema.ADDED_TIME + " DESC ",
                Status.NORMAL,
                false);

        // Submit upload task for each attachment
        int size = attachments.size();
        for (int i=0; i<threadCore; i++) {
            if (i < size) {
                Attachment attachment = attachments.get(i);
                String path = attachment.getPath();
                File file = new File(path);
                Item item = fileMap.get(FileContent.getFileName(path));
                if (item == null || file.lastModified() > item.lastModifiedDateTime.getTimeInMillis()) {
                    executor.submit(new FileUploadRunnable(attachments.get(i), itemId, item, countDownLatch, (state, msg) -> {
                        switch (state) {
                            case FAILED:
                                LogUtils.e(msg);
                                break;
                            case FINISH:
                                LogUtils.e("Files uploaded : " + ++filesUploaded);
                                break;
                        }
                    }));
                } else {
                    countDownLatch.countDown();
                }
            } else {
                countDownLatch.countDown();
            }
        }
    }

    public static class FileUploadRunnable implements Runnable {
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

    public static class FileUploadWatcher implements Runnable {
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
}
