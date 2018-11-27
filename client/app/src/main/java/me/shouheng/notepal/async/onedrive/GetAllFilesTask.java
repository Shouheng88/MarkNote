package me.shouheng.notepal.async.onedrive;

import android.os.AsyncTask;

import com.onedrive.sdk.concurrency.ICallback;
import com.onedrive.sdk.core.ClientException;
import com.onedrive.sdk.extensions.IItemCollectionPage;
import com.onedrive.sdk.extensions.IItemCollectionRequestBuilder;
import com.onedrive.sdk.extensions.Item;

import java.util.HashMap;
import java.util.Map;

import me.shouheng.commons.utils.LogUtils;
import me.shouheng.notepal.manager.onedrive.OneDriveManager;

/**
 * Prepare batch upload resources. Get all the items under files folder.
 *
 * Created by shouh on 2018/4/2. */
public class GetAllFilesTask extends AsyncTask<String, Integer, String> {

    private OnGetFilesListener onGetFilesListener;

    GetAllFilesTask(OnGetFilesListener onGetFilesListener) {
        this.onGetFilesListener = onGetFilesListener;
    }

    @Override
    protected String doInBackground(String... params) {
        OneDriveManager.getInstance().getFirstPageItems(params[0], new ICallback<IItemCollectionPage>() {
            @Override
            public void success(IItemCollectionPage iItemCollectionPage) {
                if (iItemCollectionPage == null || iItemCollectionPage.getCurrentPage().isEmpty()) {
                    if (onGetFilesListener != null) {
                        onGetFilesListener.onGetFiles(new HashMap<>());
                    }
                } else {
                    Map<String, Item> map = new HashMap<>();
                    for (final Item childItem : iItemCollectionPage.getCurrentPage()) {
                        map.put(childItem.name, childItem);
                    }
                    getNextPage(iItemCollectionPage.getNextPage(), map);
                }
            }

            @Override
            public void failure(ClientException ex) {
                LogUtils.e(ex);
            }
        });
        return "executed";
    }

    private void getNextPage(IItemCollectionRequestBuilder builder, Map<String, Item> map) {
        OneDriveManager.getInstance().getNextPageItems(builder, new ICallback<IItemCollectionPage>() {
            @Override
            public void success(IItemCollectionPage iItemCollectionPage) {
                for (final Item childItem : iItemCollectionPage.getCurrentPage()) {
                    map.put(childItem.name, childItem);
                }
                // Get next page if exist
                if (iItemCollectionPage.getNextPage() == null) {
                    if (onGetFilesListener != null) {
                        onGetFilesListener.onGetFiles(map);
                    }
                } else {
                    getNextPage(iItemCollectionPage.getNextPage(), map);
                }
            }

            @Override
            public void failure(ClientException ex) {
                LogUtils.e(ex);
                if (onGetFilesListener != null) {
                    onGetFilesListener.onGetFiles(map);
                }
            }
        });
    }

    public interface OnGetFilesListener {
        void onGetFiles(Map<String, Item> map);
    }
}
