package me.shouheng.notepal.vm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.onedrive.sdk.concurrency.ICallback;
import com.onedrive.sdk.core.ClientException;
import com.onedrive.sdk.extensions.Item;

import java.util.LinkedList;
import java.util.List;

import me.shouheng.commons.utils.LogUtils;
import me.shouheng.notepal.onedrive.PrepareBackupDirTask;
import me.shouheng.notepal.onedrive.OneDriveManager;
import me.shouheng.data.model.Directory;
import me.shouheng.commons.model.data.Resource;
import me.shouheng.notepal.common.preferences.SyncPreferences;

/**
 * Created by shouh on 2018/3/31.*/
public class DirectoryViewModel extends ViewModel {

    public LiveData<Resource<List<Directory>>> getDirectories(String itemId) {
        MutableLiveData<Resource<List<Directory>>> result = new MutableLiveData<>();
        OneDriveManager.getInstance().getItems(itemId, new ICallback<Item>() {
            @Override
            public void success(Item item) {
                if (item.children == null || item.children.getCurrentPage().isEmpty()) {
                    // The folder is empty
                    result.setValue(Resource.success(new LinkedList<>()));
                } else {
                    // Return the children folder
                    List<Directory> list = new LinkedList<>();
                    LogUtils.d(item.children);
                    for (final Item childItem : item.children.getCurrentPage()) {
                        if (childItem.folder == null) continue;
                        list.add(OneDriveManager.getDirectory(childItem));
                    }
                    result.setValue(Resource.success(list));
                }
            }

            @Override
            public void failure(ClientException ex) {
                result.setValue(Resource.error(ex.getMessage(), null));
            }
        });
        return result;
    }

    /**
     * Create backup directory if not exist, otherwise use the default one and get the item id.
     * 
     * @param toDir the directory to create to
     * @return the result */
    public LiveData<Resource<Directory>> createBackupDir(Directory toDir, List<Directory> children) {
        MutableLiveData<Resource<Directory>> result = new MutableLiveData<>();
        new PrepareBackupDirTask(toDir, children, new PrepareBackupDirTask.OnGetResultListener() {

            SyncPreferences syncPreferences = SyncPreferences.getInstance();

            private void onGetResult(String itemId, long udf1) {
                Resource<Directory> ret = Resource.success(new Directory(itemId));
                ret.setUdf1(udf1);
                result.setValue(ret);
            }

            @Override
            public void onGetBackupDir(String itemId) {
                syncPreferences.setOneDriveBackupItemId(itemId);
                syncPreferences.setOneDriveLastBackupItemId(itemId);
                onGetResult(itemId, 0L);
            }

            @Override
            public void onGetFilesBackupDir(String itemId) {
                syncPreferences.setOneDriveFilesBackupItemId(itemId);
                onGetResult(itemId, 1L);
            }

            @Override
            public void onGetDatabaseFile(String itemId) {
                syncPreferences.setOneDriveDatabaseItemId(itemId);
                onGetResult(itemId, 2L);
            }

            @Override
            public void onGetPreferencesFile(String itemId) {
                syncPreferences.setOneDrivePreferencesItemId(itemId);
                onGetResult(itemId, 3L);
            }

            @Override
            public void onError(String msg) {
                result.setValue(Resource.error(msg, null));
            }
        }).execute();
        return result;
    }
}
