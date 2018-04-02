package me.shouheng.notepal.async.onedrive;

import android.os.AsyncTask;

import com.onedrive.sdk.extensions.Item;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.shouheng.notepal.manager.one.drive.FileContent;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.provider.AttachmentsStore;

/**
 * Created by shouh on 2018/4/2.*/
public class GetUploadFilesTask extends AsyncTask<Void, Integer, Map<Attachment, Item>>{

    private Map<String, Item> fileMap;

    private OnGetUploadFilesListener onGetUploadFilesListener;

    public GetUploadFilesTask(Map<String, Item> fileMap, OnGetUploadFilesListener onGetUploadFilesListener) {
        this.fileMap = fileMap;
        this.onGetUploadFilesListener = onGetUploadFilesListener;
    }

    @Override
    protected Map<Attachment, Item> doInBackground(Void... voids) {
        List<Attachment> attachments = AttachmentsStore.getInstance().get(null, null);
        Map<Attachment, Item> map = new HashMap<>();
        for (Attachment attachment : attachments) {
            String path = attachment.getPath();
            File file = new File(path);
            Item item = fileMap.get(FileContent.getFileName(path));
            if (item == null || file.lastModified() > item.lastModifiedDateTime.getTimeInMillis()) {
                map.put(attachment, item);
            }
        }
        return map;
    }

    @Override
    protected void onPostExecute(Map<Attachment, Item> map) {
        if (onGetUploadFilesListener != null) {
            onGetUploadFilesListener.onGetUploadFiles(map);
        }
    }

    public interface OnGetUploadFilesListener {
        void onGetUploadFiles(Map<Attachment, Item> map);
    }
}
