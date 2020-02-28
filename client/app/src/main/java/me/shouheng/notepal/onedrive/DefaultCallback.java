package me.shouheng.notepal.onedrive;

import android.app.AlertDialog;
import android.content.Context;

import com.onedrive.sdk.concurrency.ICallback;
import com.onedrive.sdk.core.ClientException;

import me.shouheng.commons.utils.LogUtils;
import me.shouheng.notepal.R;

/**
 * A default callback that logs errors
 *
 * @param <T> The type returned by this callback
 */
public class DefaultCallback<T> implements ICallback<T> {

    /**
     * The exception text for not implemented runtime exceptions */
    private static final String SUCCESS_MUST_BE_IMPLEMENTED = "Success must be implemented";

    /**
     * The context used for displaying toast notifications */
    private final Context mContext;

    /**
     * Default constructor
     *
     * @param context The context used for displaying toast notifications */
    public DefaultCallback(final Context context) {
        mContext = context;
    }

    @Override
    public void success(final T t) {
        throw new RuntimeException(SUCCESS_MUST_BE_IMPLEMENTED);
    }

    @Override
    public void failure(final ClientException error) {
        if (error != null) {
            LogUtils.e(getClass().getSimpleName(), error.getMessage());
            new AlertDialog
                .Builder(mContext)
                .setTitle(R.string.text_error)
                .setMessage(error.getMessage())
                .setNegativeButton(R.string.text_cancel, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
        }
    }
}
