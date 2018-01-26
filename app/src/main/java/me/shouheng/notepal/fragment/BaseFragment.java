package me.shouheng.notepal.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.webkit.WebView;

import org.polaric.colorful.BaseActivity;
import org.polaric.colorful.PermissionUtils;

import java.io.File;

import me.shouheng.notepal.R;
import me.shouheng.notepal.dialog.AttachmentPickerDialog;
import me.shouheng.notepal.listener.OnAttachingFileListener;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.util.AttachmentHelper;
import me.shouheng.notepal.util.AttachmentHelper.OnGetAttachmentListener;
import me.shouheng.notepal.util.FileHelper;
import me.shouheng.notepal.util.ScreenShotHelper;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.util.tools.Callback;
import me.shouheng.notepal.util.tools.Invoker;
import me.shouheng.notepal.util.tools.Message;

/**
 * Created by wang shouheng on 2017/12/29.*/
public abstract class BaseFragment<V extends ViewDataBinding> extends CommonFragment<V> implements OnAttachingFileListener  {

    // region capture
    protected void createScreenCapture(final RecyclerView recyclerView) {
        if (recyclerView.getAdapter() == null || recyclerView.getAdapter().getItemCount() == 0) {
            ToastUtils.makeToast(getContext(), R.string.empty_list_to_capture);
            return;
        }
        assert getActivity() != null;
        PermissionUtils.checkStoragePermission((BaseActivity) getActivity(), () -> {
            final ProgressDialog pd = new ProgressDialog(getContext());
            pd.setMessage(getString(R.string.capturing));
            new Invoker<>(new Callback<File>() {
                @Override
                public void onBefore() {
                    pd.setCancelable(false);
                    pd.show();
                }

                @Override
                public Message<File> onRun() {
                    Message<File> message = new Message<>();
                    Bitmap bitmap = ScreenShotHelper.shotRecyclerView(recyclerView);
                    boolean succeed = FileHelper.saveImageToGallery(getContext(), bitmap, true, message::setObj);
                    message.setSucceed(succeed);
                    return message;
                }

                @Override
                public void onAfter(Message<File> message) {
                    pd.dismiss();
                    if (message.isSucceed()) {
                        ToastUtils.makeToast(String.format(getString(R.string.text_file_saved_to), message.getObj().getPath()));
                        onGetScreenCutFile(message.getObj());
                    } else {
                        ToastUtils.makeToast(getActivity(), R.string.failed_to_create_file);
                    }
                }
            }).start();
        });
    }

    protected void createScreenCapture(final RecyclerView recyclerView, final int itemHeight) {
        if (recyclerView.getAdapter() == null || recyclerView.getAdapter().getItemCount() == 0) {
            ToastUtils.makeToast(getContext(), R.string.empty_list_to_capture);
            return;
        }
        assert getActivity() != null;
        PermissionUtils.checkStoragePermission((BaseActivity) getActivity(), () -> {
            final ProgressDialog pd = new ProgressDialog(getContext());
            pd.setTitle(R.string.capturing);
            new Invoker<>(new Callback<File>() {
                @Override
                public void onBefore() {
                    pd.setCancelable(false);
                    pd.show();
                }
                @Override
                public Message<File> onRun() {
                    Message<File> message = new Message<>();
                    Bitmap bitmap = ScreenShotHelper.shotRecyclerView(recyclerView);
                    boolean succeed = FileHelper.saveImageToGallery(getContext(), bitmap, true, message::setObj);
                    message.setSucceed(succeed);
                    return message;
                }
                @Override
                public void onAfter(Message<File> message) {
                    pd.dismiss();
                    if (message.isSucceed()) {
                        ToastUtils.makeToast(String.format(getString(R.string.text_file_saved_to), message.getObj().getPath()));
                        onGetScreenCutFile(message.getObj());
                    } else {
                        ToastUtils.makeToast(getActivity(), R.string.failed_to_create_file);
                    }
                }
            }).start();
        });
    }

    protected void createWebCapture(WebView webView) {
        assert getActivity() != null;
        PermissionUtils.checkStoragePermission((BaseActivity) getActivity(), () -> {
            final ProgressDialog pd = new ProgressDialog(getContext());
            pd.setTitle(R.string.capturing);
            Bitmap bitmap = ScreenShotHelper.shotWebView(webView);
            new Invoker<>(new Callback<File>(){
                @Override
                public void onBefore() {
                    pd.setCancelable(false);
                    pd.show();
                }

                @Override
                public Message<File> onRun() {
                    Message<File> message = new Message<>();
                    boolean succeed = FileHelper.saveImageToGallery(getContext(), bitmap, true, message::setObj);
                    message.setSucceed(succeed);
                    return message;
                }

                @Override
                public void onAfter(Message<File> message) {
                    pd.dismiss();
                    if (message.isSucceed()) {
                        ToastUtils.makeToast(String.format(getString(R.string.text_file_saved_to), message.getObj().getPath()));
                        onGetScreenCutFile(message.getObj());
                    } else {
                        ToastUtils.makeToast(getActivity(), R.string.failed_to_create_file);
                    }
                }
            }).start();
        });
    }

    protected void onGetScreenCutFile(File file) {}
    // endregion

    // region attachment

    /**
     * Note of usage of this program structure:
     * 1. These methods are called when you clicked some items on the {@link AttachmentPickerDialog}
     * 2. When you finally selected some images or videos, the {@link #onActivityResult(int, int, Intent)}
     * will be called first, in this method we called
     * {@link AttachmentHelper#resolveResult(Fragment, AttachmentPickerDialog, int, int, Intent, OnGetAttachmentListener)}
     * to resolve this event. When the logic is completed in this method, methods in {@link OnAttachingFileListener} will be called.
     * That means {@link #onAttachingFileErrorOccurred(Attachment)} and {@link #onAttachingFileFinished(Attachment)}.
     * 3. In these two methods will called {@link #onGetAttachment(Attachment)} and {@link #onFailedGetAttachment(Attachment)}
     * to let the child fragment to resolve these events.
     * 4. As for {@link #getAttachmentPickerDialog()}, it is necessary to get the uri of given image,
     * So the child fragment must override this method, and return the attachment picker dialog.
     *
     * @return given attachment picker
     */
    protected AttachmentPickerDialog getAttachmentPickerDialog() {
        return null;
    }

    protected void onGetAttachment(Attachment attachment) {}

    protected void onFailedGetAttachment(Attachment attachment) {}

    @Override
    public void onAttachingFileErrorOccurred(Attachment attachment) {
        onFailedGetAttachment(attachment);
    }

    @Override
    public void onAttachingFileFinished(Attachment attachment) {
        onGetAttachment(attachment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        AttachmentHelper.resolveResult(this,
                getAttachmentPickerDialog(),
                requestCode,
                resultCode,
                data,
                BaseFragment.this::onGetAttachment);
        super.onActivityResult(requestCode, resultCode, data);
    }
    // endregion

}
