package me.shouheng.notepal.onedrive;

import android.os.AsyncTask;

import com.onedrive.sdk.concurrency.ICallback;
import com.onedrive.sdk.core.ClientException;
import com.onedrive.sdk.extensions.Folder;
import com.onedrive.sdk.extensions.Item;

import java.util.List;

import me.shouheng.data.DBConfig;
import me.shouheng.data.model.Directory;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.Constants;
import me.shouheng.notepal.manager.FileManager;

import static me.shouheng.notepal.Constants.BACKUP_DIR_NAME;
import static me.shouheng.notepal.Constants.FILES_BACKUP_DIR_NAME;

/**
 * Created by shouh on 2018/4/5.
 */
public class PrepareBackupDirTask extends AsyncTask<Void, Integer, String> {

    private OnGetResultListener onGetResultListener;

    /**
     * Current directory.
     */
    private Directory directory;

    /**
     * The child directory of current directory
     */
    private List<Directory> children;

    public PrepareBackupDirTask(Directory directory, List<Directory> children, OnGetResultListener onGetResultListener) {
        this.directory = directory;
        this.children = children;
        this.onGetResultListener = onGetResultListener;
    }

    @Override
    protected String doInBackground(Void... params) {
        checkBackupFolder();
        return "Executed";
    }

    /**
     * Get the backup folder children and then to check NotePal folder exist.
     *
     * @param itemId item id of folder to create
     */
    private void checkBackupFolder(String itemId) {
        try {
            OneDriveManager.getInstance().getItems(itemId, new ICallback<Item>() {
                /**
                 * @param item parent of "NotePal" folder.
                 */
                @Override
                public void success(Item item) {
                    if (checkFolderExist(item, BACKUP_DIR_NAME)) {
                        for (final Item childItem : item.children.getCurrentPage()) {
                            if (BACKUP_DIR_NAME.equals(childItem.name)) {
                                // Check "files" folder under "NotePal"
                                checkUnderBackDir(childItem.id);
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
     * Check NotePal folder use the children folders of this directory.
     */
    private void checkBackupFolder() {
        for (Directory dir : children) {
            if (Constants.BACKUP_DIR_NAME.equals(dir.getName())) {
                // Found NotePal folder, check Files folder under it.
                checkUnderBackDir(dir.getId());
                break;
            }
        }

        // No NotePal folder, create a new one.
        createBackupFolder(directory.getId());
    }

    /**
     * Check "files" folder under "NotePal"
     *
     * @param toItemId "NotePal" folder
     */
    private void checkUnderBackDir(String toItemId) {
        try {
            OneDriveManager.getInstance().getItems(toItemId, new ICallback<Item>() {
                @Override
                public void success(Item item) {
                    if (item.children != null && !item.children.getCurrentPage().isEmpty()) {
                        if (onGetResultListener != null) {
                            // Return the NotePal folder id.
                            String prefName = FileManager.getPreferencesName(PalmApp.getContext());
                            boolean isFilesGet = false;
                            // Check the other files and folder
                            for (final Item childItem : item.children.getCurrentPage()) {
                                if (FILES_BACKUP_DIR_NAME.equals(childItem.name)) {
                                    // Return the Files folder id.
                                    isFilesGet = true;
                                    onGetResultListener.onGetBackupDir(toItemId);
                                    onGetResultListener.onGetFilesBackupDir(childItem.id);
                                } else if (DBConfig.DATABASE_NAME.equals(childItem.name)) {
                                    // Return the database file id
                                    onGetResultListener.onGetDatabaseFile(childItem.id);
                                } else if (prefName.equals(childItem.name)) {
                                    // Return the preferences file id
                                    onGetResultListener.onGetPreferencesFile(childItem.id);
                                }
                            }
                            // Create files folder if not found.
                            if (!isFilesGet) {
                                createFilesFolder(toItemId);
                            }
                        }
                    } else {
                        // Create Files folder.
                        createFilesFolder(toItemId);
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

    private void createBackupFolder(String toItemId) {
        final Item newItem = new Item();
        newItem.name = BACKUP_DIR_NAME;
        newItem.folder = new Folder();
        OneDriveManager.getInstance().create(toItemId, newItem, new ICallback<Item>() {
            /**
             * @param item "NotePal" folder item.
             */
            @Override
            public void success(Item item)   {
                createFilesFolder(item.id);
            }

            @Override
            public void failure(ClientException ex) {
                if (onGetResultListener != null) {
                    onGetResultListener.onError(ex.getMessage());
                }
            }
        });
    }

    private void createFilesFolder(String toItemId) {
        final Item newItem = new Item();
        newItem.name = FILES_BACKUP_DIR_NAME;
        newItem.folder = new Folder();
        OneDriveManager.getInstance().create(toItemId, newItem, new ICallback<Item>() {

            @Override
            public void success(Item item) {
                if (onGetResultListener != null) {
                    onGetResultListener.onGetBackupDir(toItemId);
                    onGetResultListener.onGetFilesBackupDir(item.id);
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

    public interface OnGetResultListener {
        void onGetBackupDir(String itemId);
        void onGetFilesBackupDir(String itemId);
        void onGetDatabaseFile(String itemId);
        void onGetPreferencesFile(String itemId);
        void onError(String msg);
    }
}
