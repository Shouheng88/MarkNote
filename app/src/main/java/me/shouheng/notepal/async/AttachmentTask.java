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

public class AttachmentTask extends AsyncTask<Void, Void, Attachment> {

    private WeakReference<Fragment> mFragmentWeakReference;
    private WeakReference<android.app.Fragment> mAppFragmentWeakReference;
    private WeakReference<Activity> mActivityWeakReference;

    private OnAttachingFileListener mOnAttachingFileListener;

    private Uri uri;

    public AttachmentTask(Fragment mFragment, Uri uri, String fileName, OnAttachingFileListener listener) {
        this.mFragmentWeakReference = new WeakReference<>(mFragment);
        this.uri = uri;
        this.mOnAttachingFileListener = listener;
    }

    public AttachmentTask(android.app.Fragment mFragment, Uri uri, String fileName, OnAttachingFileListener listener) {
        this.mAppFragmentWeakReference = new WeakReference<>(mFragment);
        this.uri = uri;
        this.mOnAttachingFileListener = listener;
    }

    public AttachmentTask(Activity activity, Uri uri, String fileName, OnAttachingFileListener listener) {
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
        if ((mFragmentWeakReference != null && isAlive(mFragmentWeakReference.get()))
                || (mActivityWeakReference != null && isAlive(mActivityWeakReference.get()))
                || (mAppFragmentWeakReference != null && isAlive(mAppFragmentWeakReference.get()))) {
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

    private boolean isAlive(Fragment fragment) {
        return fragment != null
                && fragment.isAdded()
                && fragment.getActivity() != null
                && !fragment.getActivity().isFinishing();
    }

    private boolean isAlive(android.app.Fragment fragment) {
        return fragment != null
                && fragment.isAdded()
                && fragment.getActivity() != null
                && !fragment.getActivity().isFinishing();
    }

    private boolean isAlive(Activity activity) {
        return activity != null && !activity.isFinishing();
    }
}