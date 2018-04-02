package me.shouheng.notepal.async.onedrive;

import com.onedrive.sdk.extensions.Item;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.util.LogUtils;

/**
 * Created by shouh on 2018/4/1.*/
public class BatchUploadUtil {

    /**
     * Core thread number, real thread number will increased one for count down latch water.
     * The same as per page files count. */
    private int threadCore = 2;

    /**
     * Thread pool */
    private ExecutorService executor;

    private String itemId;

    private int filesUploaded = 0;

    private Map<Attachment, Item> map;

    /**
     * One link list to record last time updated attachments */
    private List<Attachment> lastUpdated = new LinkedList<>();

    public BatchUploadUtil(String itemId, int threadCore) {
        this.threadCore = threadCore;
        this.itemId = itemId;
        executor = Executors.newFixedThreadPool(threadCore + 1);
    }

    public void begin() {
        new GetAllFilesTask(this::onGetAllFiles).execute(itemId);
    }

    private void onGetAllFiles(Map<String, Item> map) {
        new GetUploadFilesTask(map, this::onGetAllUploadFiles).execute();
    }

    private void onGetAllUploadFiles(Map<Attachment, Item> map) {
        this.map = map;
        doTask();
    }

    private void shutDown() {
        LogUtils.d("Executor shut down.");
        executor.shutdownNow();
    }

    private void doTask() {
        LogUtils.d("Upload begin!");

        /* Latch to control the upload event. */
        CountDownLatch downLatch = new CountDownLatch(threadCore);
        executor.submit(new FileUploadWatcher(downLatch, (watchedStatus, msg) -> {
            switch (watchedStatus) {
                case FAILED:
                    LogUtils.e("Error when upload: " + msg);
                    break;
                case FINISH:
                    rmUpdated();
                    if (map.isEmpty()) {
                        LogUtils.d("All uploaded!");
                        shutDown();
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
        int count = 0;
        for (Attachment attachment : map.keySet()) {
            Item item = map.get(attachment);
            lastUpdated.add(attachment);
            executor.submit(new FileUploadRunnable(attachment, itemId, item, countDownLatch, (state, msg) -> {
                switch (state) {
                    case FAILED:
                        LogUtils.e(msg);
                        break;
                    case FINISH:
                        LogUtils.e("Files uploaded : " + ++filesUploaded);
                        break;
                }
            }));
            if (++count == threadCore) {
                break;
            }
        }

        for (int i=0; i<10-count; i++) {
            countDownLatch.countDown();
        }
    }

    private void rmUpdated() {
        for (Attachment attachment : lastUpdated) {
            map.remove(attachment);
        }
    }
}
