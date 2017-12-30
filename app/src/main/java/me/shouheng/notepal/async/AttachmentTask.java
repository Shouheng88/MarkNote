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

    private WeakReference<Activity> mActivityWeakReference;

    private OnAttachingFileListener mOnAttachingFileListener;

    private Uri uri;

    public AttachmentTask(Fragment mFragment, Uri uri, String fileName, OnAttachingFileListener mOnAttachingFileListener) {
        mFragmentWeakReference = new WeakReference<>(mFragment);
        this.uri = uri;
        this.mOnAttachingFileListener = mOnAttachingFileListener;
    }

    public AttachmentTask(Activity activity, Uri uri, String fileName, OnAttachingFileListener mOnAttachingFileListener) {
        mActivityWeakReference = new WeakReference<>(activity);
        this.uri = uri;
        this.mOnAttachingFileListener = mOnAttachingFileListener;
    }

    @Override
    protected Attachment doInBackground(Void... params) {
        return FileHelper.createAttachmentFromUri(PalmApp.getContext(), uri);
    }

    @Override
    protected void onPostExecute(Attachment mAttachment) {
        if ((mFragmentWeakReference != null && isAlive(mFragmentWeakReference.get()))
                || (mActivityWeakReference != null && isAlive(mActivityWeakReference.get()))) {
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

    private boolean isAlive(Activity activity) {
        return activity != null && !activity.isFinishing();
    }
}