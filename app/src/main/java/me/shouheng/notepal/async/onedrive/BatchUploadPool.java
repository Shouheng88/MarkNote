package me.shouheng.notepal.async.onedrive;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.PreferencesUtils;

/**
 * todo find out why two files are not synchronized to OneDrive
 *
 * Created by shouh on 2018/4/1.*/
public class BatchUploadPool {

    /**
     * Core thread number, real thread number will increased one for count down latch water.
     * The same as per page files count. */
    private final int threadCore;

    /**
     * Thread pool */
    private ExecutorService executor;

    private String itemId;

    private int filesUploaded = 0;

    private int releasedCount = 0;

    private boolean isExecuting = false;

    private CountDownLatch downLatch;

    private List<Attachment> attachmentToUpload;

//    private List<Attachment> attachmentUploaded = new LinkedList<>();

    private static BatchUploadPool instance;

    public static BatchUploadPool getInstance(String itemId, int threadCore) {
        if (instance == null) {
            synchronized (BatchUploadPool.class) {
                if (instance == null) {
                    instance = new BatchUploadPool(itemId, threadCore);
                }
            }
        }
        return instance;
    }

    private BatchUploadPool(String itemId, int threadCore) {
        this.threadCore = threadCore > 5 ? 5 : threadCore < 2 ? 2 : threadCore;
        this.itemId = itemId;
        executor = Executors.newFixedThreadPool(this.threadCore + 1);
    }

    void begin() {
        new GetUploadAttachmentTask(attachments -> {
            attachmentToUpload = attachments;
            isExecuting = true;
            doTask();
        }).execute();
    }

    boolean isExecuting() {
        return isExecuting;
    }

    void shutDown() {
        isExecuting = false;
        LogUtils.d("Executor shut down.");
        executor.shutdown();
    }

    private void doTask() {
        /* Latch to control the upload event. */
        downLatch = new CountDownLatch(attachmentToUpload.size());
        // Initialize the released thread count.
        releasedCount = 0;
        // Submit a new task to watch the executor.
        executor.submit(new FileUploadWatcher(downLatch, new FileUploadWatcher.OnWatchListener() {
            @Override
            public void onFinish() {
                LogUtils.d("Batch upload finished!");
                // On upload finished. Remove the items from upload list and update database.
//                onUploaded();
                // If the list to update is empty, shutdown the executor else continue to upload.
//                if (attachmentToUpload.isEmpty()) {
                    LogUtils.d("All uploaded!");
                    PreferencesUtils.getInstance().setOneDriveLastSyncTime(System.currentTimeMillis());
                    shutDown();
//                }
//                else if (isExecuting) {
//                    doTask();
//                }
            }

            @Override
            public void onFail(String msg) {
                LogUtils.e("Error in watcher : " + msg);
            }
        }));

        // Start upload for the first time
        batchUpload(downLatch);
    }

    private void batchUpload(CountDownLatch countDownLatch) {
        LogUtils.d("Batch upload started!");
//        int count = 0;
        for (Attachment attachment : attachmentToUpload) {
            LogUtils.d(attachment.getCode() + " to upload.");
//            attachmentUploaded.add(attachment);
            executor.submit(new FileUploadRunnable(attachment, itemId, countDownLatch, new FileUploadRunnable.OnUploadListener() {
                @Override
                public void onSuccess() {
                    releasedCount++;
                    LogUtils.d(attachment.getCode() + " uploaded.");
                    LogUtils.d(++filesUploaded + " files are synchronized.");
                }

                @Override
                public void onFail(String msg) {
                    releasedCount++;
                    LogUtils.e("Error when synchronize file : " + msg);
                }
            }));
//            if (++count == threadCore) {
//                break;
//            }
        }

//        int left = threadCore - count;
//        for (int i=0; i<left; i++) {
//            countDownLatch.countDown();
//            releasedCount++;
//        }
    }
//
//    private void onUploaded() {
//        LogUtils.d("attachmentUploaded/attachmentToUpload = " + attachmentUploaded.size() + "/" + attachmentToUpload.size());
//        // Remove items from list to update
//        attachmentToUpload.removeAll(attachmentUploaded);
//        attachmentUploaded.clear();
//    }
}
