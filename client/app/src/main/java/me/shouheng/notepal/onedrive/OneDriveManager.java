package me.shouheng.notepal.onedrive;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.onedrive.sdk.authentication.MSAAuthenticator;
import com.onedrive.sdk.concurrency.ICallback;
import com.onedrive.sdk.concurrency.IProgressCallback;
import com.onedrive.sdk.core.ClientException;
import com.onedrive.sdk.core.DefaultClientConfig;
import com.onedrive.sdk.core.IClientConfig;
import com.onedrive.sdk.core.OneDriveErrorCodes;
import com.onedrive.sdk.extensions.IItemCollectionPage;
import com.onedrive.sdk.extensions.IItemCollectionRequestBuilder;
import com.onedrive.sdk.extensions.IOneDriveClient;
import com.onedrive.sdk.extensions.Item;
import com.onedrive.sdk.extensions.OneDriveClient;
import com.onedrive.sdk.logger.LoggerLevel;
import com.onedrive.sdk.options.Option;
import com.onedrive.sdk.options.QueryOption;

import java.io.File;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import me.shouheng.notepal.R;
import me.shouheng.data.model.Directory;
import me.shouheng.notepal.manager.FileManager;
import me.shouheng.commons.utils.ToastUtils;

/**
 * Created by shouh on 2018/3/29.
 */
public class OneDriveManager {

    /**
     * Expansion options to get all children, thumbnails of children, and thumbnails
     */
    private static final String EXPAND_OPTIONS_FOR_CHILDREN_AND_THUMBNAILS = "children(expand=thumbnails),thumbnails";

    /**
     * Expansion options to get all children, thumbnails of children, and thumbnails when limited
     */
    private static final String EXPAND_OPTIONS_FOR_CHILDREN_AND_THUMBNAILS_LIMITED = "children,thumbnails";

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

    private synchronized IOneDriveClient getOneDriveClient() throws Exception {
        if (mClient.get() == null) {
            throw new Exception("The client is not initialized.");
        }
        return mClient.get();
    }

    private synchronized void createOneDriveClient(final Activity activity, @Nullable ICallback<Void> serviceCreated) {
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
     * @param activity activity
     */
    public void connectOneDrive(Activity activity, ICallback<Void> callback) {
        try {
            getOneDriveClient();
            if (callback != null) {
                callback.success(null);
            }
        } catch (final Exception ignored) {
            createOneDriveClient(activity, callback);
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
                ToastUtils.makeToast(R.string.text_succeed);
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
     * @param callback the callback to resolve calling result
     */
    public void getItems(String itemId, ICallback<Item> callback) {
        IOneDriveClient oneDriveClient;
        try {
            oneDriveClient = getOneDriveClient();
            oneDriveClient
                    .getDrive()
                    .getItems(itemId)
                    .buildRequest()
                    .expand(getExpansionOptions(oneDriveClient))
                    .get(callback);
        } catch (Exception e) {
            if (callback != null) {
                callback.failure(new ClientException(e.getMessage(), e, OneDriveErrorCodes.AuthenticationCancelled));
            }
        }
    }

    public void getFirstPageItems(String itemId, ICallback<IItemCollectionPage> callback) {
        try {
            IOneDriveClient oneDriveClient = getOneDriveClient();
            oneDriveClient
                    .getDrive()
                    .getItems(itemId)
                    .getChildren()
                    .buildRequest()
                    .get(callback);
        } catch (Exception e) {
            if (callback != null) {
                callback.failure(new ClientException(e.getMessage(), e, OneDriveErrorCodes.AuthenticationCancelled));
            }
        }
    }

    public void getNextPageItems(IItemCollectionRequestBuilder builder, ICallback<IItemCollectionPage> callback) {
        builder.buildRequest().get(callback);
    }

    public void create(String toItemId, Item newItem, ICallback<Item> callback) {
        IOneDriveClient oneDriveClient;
        try {
            oneDriveClient = getOneDriveClient();
            oneDriveClient
                    .getDrive()
                    .getItems(toItemId)
                    .getChildren()
                    .buildRequest()
                    .create(newItem, callback);
        } catch (Exception e) {
            if (callback != null) {
                callback.failure(new ClientException(e.getMessage(), e, OneDriveErrorCodes.AuthenticationCancelled));
            }
        }
    }

    public void delete(String itemId, ICallback<Void> callback) {
        try {
            getOneDriveClient()
                    .getDrive()
                    .getItems(itemId)
                    .buildRequest()
                    .delete(callback);
        } catch (Exception e) {
            if (callback != null) {
                callback.failure(new ClientException(e.getMessage(), e, OneDriveErrorCodes.AuthenticationCancelled));
            }
        }
    }

    public void upload(String toItemId, File file, String conflictBehavior, UploadProgressCallback<Item> callback) {
        final Option option = new QueryOption("@name.conflictBehavior", conflictBehavior);
        final String filename = FileManager.getFileName(file);
        final byte[] fileInMemory;
        try {
            fileInMemory = FileManager.getFileBytes(file);
            getOneDriveClient()
                    .getDrive()
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
        } catch (Exception e) {
            if (callback != null) {
                callback.failure(new ClientException(e.getMessage(), e, OneDriveErrorCodes.AuthenticationCancelled));
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
        /**
         * Use default method, don't force user to implement this method.
         *
         * @param current current progress.
         * @param max max progress.
         */
        default void progress(final long current, final long max){}
        void success(final Result result);
        void failure(final Exception e);
    }
}
