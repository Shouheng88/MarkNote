package me.shouheng.notepal.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.onedrive.sdk.concurrency.ICallback;
import com.onedrive.sdk.core.ClientException;
import com.onedrive.sdk.extensions.Item;

import java.util.LinkedList;
import java.util.List;

import me.shouheng.notepal.manager.one.drive.OneDriveManager;
import me.shouheng.notepal.model.Directory;
import me.shouheng.notepal.model.data.Resource;
import me.shouheng.notepal.util.LogUtils;

/**
 * Created by shouh on 2018/3/31.*/
public class DirectoryViewModel extends ViewModel {

    public LiveData<Resource<List<Directory>>> getDirectories(String itemId) {
        MutableLiveData<Resource<List<Directory>>> result = new MutableLiveData<>();
        OneDriveManager.getInstance().getDirectories(itemId, new ICallback<Item>() {
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
}
