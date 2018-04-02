package me.shouheng.notepal.manager.onedrive;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.onedrive.sdk.authentication.MSAAuthenticator;
import com.onedrive.sdk.concurrency.ICallback;
import com.onedrive.sdk.concurrency.IProgressCallback;
import com.onedrive.sdk.core.ClientException;
import com.onedrive.sdk.core.DefaultClientConfig;
import com.onedrive.sdk.core.IClientConfig;
import com.onedrive.sdk.extensions.IItemCollectionPage;
import com.onedrive.sdk.extensions.IItemCollectionRequestBuilder;
import com.onedrive.sdk.extensions.IOneDriveClient;
import com.onedrive.sdk.extensions.Item;
import com.onedrive.sdk.extensions.OneDriveClient;
import com.onedrive.sdk.logger.LoggerLevel;
import com.onedrive.sdk.options.Option;
import com.onedrive.sdk.options.QueryOption;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import me.shouheng.notepal.R;
import me.shouheng.notepal.model.Directory;
import me.shouheng.notepal.util.PreferencesUtils;
import me.shouheng.notepal.util.ToastUtils;

/**
 * Created by shouh on 2018/3/29.*/
public class OneDriveManager {

    /**
     * Expansion options to get all children, thumbnails of children, and thumbnails */
    private static final String EXPAND_OPTIONS_FOR_CHILDREN_AND_THUMBNAILS = "children(expand=thumbnails),thumbnails";

    /**
     * Expansion options to get all children, thumbnails of children, and thumbnails when limited */
    private static final String EXPAND_OPTIONS_FOR_CHILDREN_AND_THUMBNAILS_LIMITED = "children,thumbnails";

    private final Option option = new QueryOption("@name.conflictBehavior", "fail");

    private static OneDriveManager instance;

    public static synchronized OneDriveManager getInstance() {
        if (instance == null) {
            synchronized (OneDriveManager.class) {
                if (instance == null) {
                    instance = new OneDriveManager();
                }
            }
        }
        return instance;
    }

    private OneDriveManager() {}

    private final AtomicReference<IOneDriveClient> mClient = new AtomicReference<>();

    public synchronized IOneDriveClient getOneDriveClient() {
        if (mClient.get() == null) {
            throw new UnsupportedOperationException("Unable to generate a new service object");
        }
        return mClient.get();
    }

    public synchronized void createOneDriveClient(final Activity activity, @Nullable ICallback<Void> serviceCreated) {
        new OneDriveClient.Builder()
                .fromConfig(createConfig())
                .loginAndBuildClient(activity, new DefaultCallback<IOneDriveClient>(activity) {
                    @Override
                    public void success(final IOneDriveClient result) {
                        // add one instance to the atomic preference
                        mClient.set(result);
                        if (serviceCreated != null) {
                            serviceCreated.success(null);
                        }
                    }

                    @Override
                    public void failure(final ClientException error) {
                        if (serviceCreated != null) {
                            serviceCreated.failure(error);
                        }
                    }
                });
    }

    /**
     * Init one drive account, that is put the IOneDriveClient to mClient.
     *
     * @param activity activity */
    public void connectOneDrive(Activity activity) {
        String itemId = PreferencesUtils.getInstance().getOneDriveBackupItemId();
        String filesItemId = PreferencesUtils.getInstance().getOneDriveFilesBackupItemId();
        if (TextUtils.isEmpty(itemId) || TextUtils.isEmpty(filesItemId)) {
            // No backup requirement, return
            return;
        }

        new OneDriveInitTask(activity, this).execute();
    }

    private static class OneDriveInitTask extends AsyncTask<Void, Integer, String> {

        private WeakReference<OneDriveManager> oneDriveManagerWeakReference;
        private WeakReference<Activity> activityWeakReference;

        OneDriveInitTask(Activity activity, OneDriveManager oneDriveManager) {
            activityWeakReference = new WeakReference<>(activity);
            oneDriveManagerWeakReference = new WeakReference<>(oneDriveManager);
        }

        @Override
        protected String doInBackground(Void... voids) {
            OneDriveManager oneDriveManager;
            if ((oneDriveManager = oneDriveManagerWeakReference.get()) != null) {
                try {
                    oneDriveManager.getOneDriveClient();
                } catch (UnsupportedOperationException e) {
                    Activity activity = activityWeakReference.get();
                    if (activity != null) {
                        oneDriveManager.createOneDriveClient(activity, null);
                    }
                }
            }
            return "executed";
        }
    }

    private IClientConfig createConfig() {
        IClientConfig config = DefaultClientConfig.createWithAuthenticator(new MSAAuthenticator() {
            @Override
            public String getClientId() {
                return "493c9a39-1906-4205-96eb-444911bd7e37";
            }

            @Override
            public String[] getScopes() {
                return new String[] {"onedrive.readwrite", "onedrive.appfolder", "wl.offline_access"};
            }
        });
        config.getLogger().setLoggingLevel(LoggerLevel.Debug);
        return config;
    }

    public void signOut() {
        if (mClient.get() == null) {
            return;
        }
        mClient.get().getAuthenticator().logout(new ICallback<Void>() {
            @Override
            public void success(final Void result) {
                ToastUtils.makeToast(R.string.text_successfully_logout);
            }

            @Override
            public void failure(final ClientException ex) {
                ToastUtils.makeToast("Logout error " + ex);
            }
        });
    }

    /**
     * Get directories of given directory
     *
     * @param itemId the directory id
     * @param callback the callback to resolve calling result */
    public void getItems(String itemId, ICallback<Item> callback) {
        IOneDriveClient oneDriveClient = OneDriveManager.getInstance().getOneDriveClient();
        oneDriveClient.getDrive()
                .getItems(itemId)
                .buildRequest()
                .expand(getExpansionOptions(oneDriveClient))
                .get(callback);
    }

    public void getFirstPageItems(String itemId, ICallback<IItemCollectionPage> callback) {
        IOneDriveClient oneDriveClient = OneDriveManager.getInstance().getOneDriveClient();
        oneDriveClient.getDrive()
                .getItems(itemId)
                .getChildren()
                .buildRequest()
                .get(callback);
    }

    public void getNextPageItems(IItemCollectionRequestBuilder builder, ICallback<IItemCollectionPage> callback) {
        builder.buildRequest().get(callback);
    }

    public void create(String toItemId, Item newItem, ICallback<Item> callback) {
        IOneDriveClient oneDriveClient = OneDriveManager.getInstance().getOneDriveClient();
        oneDriveClient.getDrive()
                .getItems(toItemId)
                .getChildren()
                .buildRequest()
                .create(newItem, callback);
    }

    public void delete(String itemId, ICallback<Void> callback) {
        OneDriveManager.getInstance().getOneDriveClient()
                .getDrive()
                .getItems(itemId)
                .buildRequest()
                .delete(callback);
    }

    public void upload(String toItemId, File file, UploadProgressCallback<Item> callback) {
        final String filename = FileContent.getFileName(file);
        final byte[] fileInMemory;
        try {
            fileInMemory = FileContent.getFileBytes(file);
            getOneDriveClient().getDrive()
                    .getItems(toItemId)
                    .getChildren()
                    .byId(filename)
                    .getContent()
                    .buildRequest(Collections.singletonList(option))
                    .put(fileInMemory, new IProgressCallback<Item>() {
                        @Override
                        public void progress(long current, long max) {
                            if (callback != null) {
                                callback.progress(current, max);
                            }
                        }

                        @Override
                        public void success(Item item) {
                            if (callback != null) {
                                callback.success(item);
                            }
                        }

                        @Override
                        public void failure(ClientException ex) {
                            if (callback != null) {
                                callback.failure(ex);
                            }
                        }
                    });
        } catch (IOException e) {
            if (callback != null) {
                callback.failure(e);
            }
        } catch (RemoteException e) {
            if (callback != null) {
                callback.failure(e);
            }
        }
    }

    public static Directory getDirectory(Item item) {
        Directory directory = new Directory();
        directory.setId(item.id);
        directory.setName(item.name);
        directory.setPath(item.parentReference.path + "/" + item.name);
        directory.setLastModifiedDateTime(item.lastModifiedDateTime.getTime());
        return directory;
    }

    private String getExpansionOptions(final IOneDriveClient oneDriveClient) {
        final String expansionOption;
        switch (oneDriveClient.getAuthenticator().getAccountInfo().getAccountType()) {
            case MicrosoftAccount:
                expansionOption = EXPAND_OPTIONS_FOR_CHILDREN_AND_THUMBNAILS;
                break;
            default:
                expansionOption = EXPAND_OPTIONS_FOR_CHILDREN_AND_THUMBNAILS_LIMITED;
                break;
        }
        return expansionOption;
    }

    public interface UploadProgressCallback<Result> {
        void progress(final long current, final long max);
        void success(final Result result);
        void failure(final Exception e);
    }
}
