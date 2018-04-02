package me.shouheng.notepal.async;

import android.os.AsyncTask;

import com.onedrive.sdk.concurrency.ICallback;
import com.onedrive.sdk.core.ClientException;
import com.onedrive.sdk.extensions.Item;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.manager.one.drive.OneDriveManager;
import me.shouheng.notepal.provider.PalmDB;
import me.shouheng.notepal.util.LogUtils;

/**
 * Created by shouh on 2018/4/1.*/
public class DelDBAndSettingTask extends AsyncTask<String, Integer, String> {

    private OnFilesDeletedListener onFilesDeletedListener;

    private int delCount;

    private int toDelCount;

    DelDBAndSettingTask(OnFilesDeletedListener onFilesDeletedListener) {
        this.onFilesDeletedListener = onFilesDeletedListener;
    }

    @Override
    protected String doInBackground(String... params) {
        OneDriveManager.getInstance().getDirectories(params[0], new ICallback<Item>() {

            @Override
            public void success(Item item) {
                String prefName = PalmApp.getContext().getPackageName() + "_preferences.xml";
                LogUtils.d(prefName);
                for (final Item childItem : item.children.getCurrentPage()) {
                    if (PalmDB.DATABASE_NAME.equals(childItem.name)
                            || prefName.equals(childItem.name)) {
                        toDelCount++;
                        deleteItem(childItem);
                    }
                }
                if (toDelCount == 0 && onFilesDeletedListener != null) {
                    onFilesDeletedListener.onDeleted();
                }
            }

            @Override
            public void failure(ClientException ex) {
                LogUtils.e(ex);
            }
        });
        return null;
    }

    private void deleteItem(Item item) {
        OneDriveManager.getInstance().delete(item.id, new ICallback<Void>() {
            @Override
            public void success(Void aVoid) {
                if (++delCount == toDelCount && onFilesDeletedListener != null) {
                    onFilesDeletedListener.onDeleted();
                }
            }

            @Override
            public void failure(ClientException ex) {
                LogUtils.e(ex);
            }
        });
    }

    public interface OnFilesDeletedListener {
        void onDeleted();
    }
}
