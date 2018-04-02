package me.shouheng.notepal.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import com.onedrive.sdk.concurrency.ICallback;
import com.onedrive.sdk.core.ClientException;
import com.onedrive.sdk.extensions.Folder;
import com.onedrive.sdk.extensions.Item;

import java.util.LinkedList;
import java.util.List;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.manager.one.drive.OneDriveManager;
import me.shouheng.notepal.model.Directory;
import me.shouheng.notepal.model.data.Resource;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.PreferencesUtils;

import static me.shouheng.notepal.config.Constants.BACKUP_DIR_NAME;
import static me.shouheng.notepal.config.Constants.FILES_BACKUP_DIR_NAME;

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
    public LiveData<Resource<Directory>> createBackupDir(Directory toDir) {
        MutableLiveData<Resource<Directory>> result = new MutableLiveData<>();
        new CreateBackupDirTask(new CreateBackupDirTask.OnGetResultListener() {
            @Override
            public void onGetBackupDir(Item item) {
                PreferencesUtils.getInstance(PalmApp.getContext()).setOneDriveBackupItemId(item.id);
                Resource<Directory> ret = Resource.success(OneDriveManager.getDirectory(item));
                ret.setUdf1(0L);
                result.setValue(ret);
            }

            @Override
            public void onGetFilesBackupDir(Item item) {
                PreferencesUtils.getInstance(PalmApp.getContext()).setOneDriveFilesBackupItemId(item.id);
                Resource<Directory> ret = Resource.success(OneDriveManager.getDirectory(item));
                ret.setUdf1(1L);
                result.setValue(ret);
            }

            @Override
            public void onError(String msg) {
                result.setValue(Resource.error(msg, null));
            }
        }).execute(toDir.getId());
        return result;
    }

    private static class CreateBackupDirTask extends AsyncTask<String, Integer, String> {

        private OnGetResultListener onGetResultListener;

        CreateBackupDirTask(OnGetResultListener onGetResultListener) {
            this.onGetResultListener = onGetResultListener;
        }

        @Override
        protected String doInBackground(String... params) {
            checkBackupFolder(params[0]);
            return "Executed";
        }

        private void checkBackupFolder(String itemId) {
            try {
                OneDriveManager.getInstance().getItems(itemId, new ICallback<Item>() {
                    /**
                     * @param item parent of "NotePal" folder. */
                    @Override
                    public void success(Item item) {
                        if (checkFolderExist(item, BACKUP_DIR_NAME)) {
                            for (final Item childItem : item.children.getCurrentPage()) {
                                if (BACKUP_DIR_NAME.equals(childItem.name)) {
                                    // Check "files" folder under "NotePal"
                                    checkFilesFolder(childItem);
                                    break;
                                }
                            }
                        } else {
                            createBackupFolder(itemId);
                        }
                    }

                    @Override
                    public void failure(ClientException ex) {
                        if (onGetResultListener != null) {
                            onGetResultListener.onError(ex.getMessage());
                        }
                    }
                });
            } catch (UnsupportedOperationException ex) {
                if (onGetResultListener != null) {
                    onGetResultListener.onError(ex.getMessage());
                }
            }
        }

        /**
         * Check "files" folder under "NotePal"
         *
         * @param toItem "NotePal" folder */
        private void checkFilesFolder(Item toItem) {
            try {
                OneDriveManager.getInstance().getItems(toItem.id, new ICallback<Item>() {
                    @Override
                    public void success(Item item) {
                        if (checkFolderExist(item, FILES_BACKUP_DIR_NAME)) {
                            if (onGetResultListener != null) {
                                onGetResultListener.onGetBackupDir(toItem);
                                for (final Item childItem : item.children.getCurrentPage()) {
                                    if (FILES_BACKUP_DIR_NAME.equals(childItem.name)) {
                                        onGetResultListener.onGetFilesBackupDir(childItem);
                                        break;
                                    }
                                }
                            }
                        } else {
                            createFilesFolder(toItem);
                        }
                    }

                    @Override
                    public void failure(ClientException ex) {
                        if (onGetResultListener != null) {
                            onGetResultListener.onError(ex.getMessage());
                        }
                    }
                });
            } catch (UnsupportedOperationException ex) {
                if (onGetResultListener != null) {
                    onGetResultListener.onError(ex.getMessage());
                }
            }
        }

        private boolean checkFolderExist(Item item, String dirName) {
            if (item.children == null || item.children.getCurrentPage().isEmpty()) {
                return false;
            } else {
                for (final Item childItem : item.children.getCurrentPage()) {
                    if (dirName.equals(childItem.name)) {
                        return true;
                    }
                }
                return false;
            }
        }

        private void createBackupFolder(String toItemId) {
            final Item newItem = new Item();
            newItem.name = BACKUP_DIR_NAME;
            newItem.folder = new Folder();
            OneDriveManager.getInstance().create(toItemId, newItem, new ICallback<Item>() {
                /**
                 * @param item "NotePal" folder item. */
                @Override
                public void success(Item item) {
                    createFilesFolder(item);
                }

                @Override
                public void failure(ClientException ex) {
                    if (onGetResultListener != null) {
                        onGetResultListener.onError(ex.getMessage());
                    }
                }
            });
        }

        private void createFilesFolder(Item toItem) {
            final Item newItem = new Item();
            newItem.name = FILES_BACKUP_DIR_NAME;
            newItem.folder = new Folder();
            OneDriveManager.getInstance().create(toItem.id, newItem, new ICallback<Item>() {

                @Override
                public void success(Item item) {
                    if (onGetResultListener != null) {
                        onGetResultListener.onGetBackupDir(toItem);
                        onGetResultListener.onGetFilesBackupDir(item);
                    }
                }

                @Override
                public void failure(ClientException ex) {
                    if (onGetResultListener != null) {
                        onGetResultListener.onError(ex.getMessage());
                    }
                }
            });
        }

        public interface OnGetResultListener {
            void onGetBackupDir(Item item);
            void onGetFilesBackupDir(Item item);
            void onError(String msg);
        }
    }
}
