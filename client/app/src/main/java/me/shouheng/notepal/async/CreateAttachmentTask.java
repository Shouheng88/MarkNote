package me.shouheng.notepal.async;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.listener.OnAttachingFileListener;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.util.FileHelper;
import me.shouheng.notepal.util.PalmUtils;

/**
 * The async task to create attachment from uri. */
public class CreateAttachmentTask extends AsyncTask<Void, Void, Attachment> {

    private WeakReference<Fragment> mFragmentWeakReference;

    private WeakReference<android.app.Fragment> mAppFragmentWeakReference;

    private WeakReference<Activity> mActivityWeakReference;

    private OnAttachingFileListener mOnAttachingFileListener;

    private Uri uri;

    public CreateAttachmentTask(Fragment mFragment, Uri uri, OnAttachingFileListener listener) {
        this.mFragmentWeakReference = new WeakReference<>(mFragment);
        this.uri = uri;
        this.mOnAttachingFileListener = listener;
    }

    public CreateAttachmentTask(android.app.Fragment mFragment, Uri uri, OnAttachingFileListener listener) {
        this.mAppFragmentWeakReference = new WeakReference<>(mFragment);
        this.uri = uri;
        this.mOnAttachingFileListener = listener;
    }

    public CreateAttachmentTask(Activity activity, Uri uri, OnAttachingFileListener listener) {
        mActivityWeakReference = new WeakReference<>(activity);
        this.uri = uri;
        this.mOnAttachingFileListener = listener;
    }

    @Override
    protected Attachment doInBackground(Void... params) {
        return FileHelper.createAttachmentFromUri(PalmApp.getContext(), uri);
    }

    @Override
    protected void onPostExecute(Attachment mAttachment) {
        if ((mFragmentWeakReference != null && PalmUtils.isAlive(mFragmentWeakReference.get()))
                || (mActivityWeakReference != null && PalmUtils.isAlive(mActivityWeakReference.get()))
                || (mAppFragmentWeakReference != null && PalmUtils.isAlive(mAppFragmentWeakReference.get()))) {
            if (mAttachment != null) {
                mOnAttachingFileListener.onAttachingFileFinished(mAttachment);
            } else {
                mOnAttachingFileListener.onAttachingFileErrorOccurred(null);
            }
        } else {
            if (mAttachment != null) {
                FileHelper.delete(PalmApp.getContext(), mAttachment.getUri().getPath());
            }
        }
    }
}