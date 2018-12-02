package me.shouheng.notepal.async;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;

import me.shouheng.commons.utils.PalmUtils;
import me.shouheng.data.entity.Attachment;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.manager.FileManager;
import me.shouheng.notepal.util.AttachmentHelper;

/**
 * The async task to create attachment from uri. */
public class CreateAttachmentTask extends AsyncTask<Void, Void, Attachment> {

    private WeakReference<Fragment> mFragmentWeakReference;

    private WeakReference<Activity> mActivityWeakReference;

    private AttachmentHelper.OnAttachingFileListener mOnAttachingFileListener;

    private Uri uri;

    public CreateAttachmentTask(Fragment mFragment, Uri uri, AttachmentHelper.OnAttachingFileListener listener) {
        this.mFragmentWeakReference = new WeakReference<>(mFragment);
        this.uri = uri;
        this.mOnAttachingFileListener = listener;
    }

    public CreateAttachmentTask(Activity activity, Uri uri, AttachmentHelper.OnAttachingFileListener listener) {
        mActivityWeakReference = new WeakReference<>(activity);
        this.uri = uri;
        this.mOnAttachingFileListener = listener;
    }

    @Override
    protected Attachment doInBackground(Void... params) {
        return FileManager.createAttachmentFromUri(PalmApp.getContext(), uri);
    }

    @Override
    protected void onPostExecute(Attachment mAttachment) {
        if ((mFragmentWeakReference != null && PalmUtils.isAlive(mFragmentWeakReference.get()))
                || (mActivityWeakReference != null && PalmUtils.isAlive(mActivityWeakReference.get()))) {
            if (mAttachment != null) {
                mOnAttachingFileListener.onAttachingFileFinished(mAttachment);
            } else {
                mOnAttachingFileListener.onAttachingFileErrorOccurred(null);
            }
        } else {
            if (mAttachment != null) {
                FileManager.delete(PalmApp.getContext(), mAttachment.getUri().getPath());
            }
        }
    }
}