package me.shouheng.notepal.fragment.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.webkit.WebView;

import java.io.File;

import me.shouheng.commons.activity.PermissionActivity;
import me.shouheng.commons.fragment.CommonFragment;
import me.shouheng.commons.utils.PermissionUtils;
import me.shouheng.notepal.R;
import me.shouheng.notepal.listener.OnAttachingFileListener;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.util.AttachmentHelper;
import me.shouheng.notepal.util.FileHelper;
import me.shouheng.notepal.util.ScreenShotHelper;
import me.shouheng.commons.utils.ToastUtils;
import me.shouheng.notepal.util.tools.Callback;
import me.shouheng.notepal.util.tools.Invoker;
import me.shouheng.notepal.util.tools.Message;

/**
 * Created by wang shouheng on 2017/12/29.*/
public abstract class BaseFragment<V extends ViewDataBinding> extends CommonFragment<V>
        implements OnAttachingFileListener  {

    // region Capture
    protected void createScreenCapture(final RecyclerView recyclerView) {
        if (recyclerView.getAdapter() == null || recyclerView.getAdapter().getItemCount() == 0) {
            ToastUtils.makeToast(R.string.empty_list_to_capture);
            return;
        }
        if (getActivity() == null) return;
        PermissionUtils.checkStoragePermission((PermissionActivity) getActivity(), () -> doCapture(recyclerView));
    }

    protected void createScreenCapture(final RecyclerView recyclerView, final int itemHeight) {
        if (recyclerView.getAdapter() == null || recyclerView.getAdapter().getItemCount() == 0) {
            ToastUtils.makeToast(R.string.empty_list_to_capture);
            return;
        }
        if (getActivity() == null) return;
        PermissionUtils.checkStoragePermission((PermissionActivity) getActivity(), () -> doCapture(recyclerView, itemHeight));
    }

    protected void createWebCapture(WebView webView, FileHelper.OnSavedToGalleryListener listener) {
        assert getActivity() != null;
        PermissionUtils.checkStoragePermission((PermissionActivity) getActivity(), () -> {
            final ProgressDialog pd = new ProgressDialog(getContext());
            pd.setTitle(R.string.capturing);
            pd.setCancelable(false);
            pd.show();

            new Handler().postDelayed(() -> doCapture(webView, pd, listener), 500);
        });
    }

    protected void onGetScreenCutFile(File file) {}

    private void doCapture(RecyclerView recyclerView) {
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
                    ToastUtils.makeToast(R.string.failed_to_create_file);
                }
            }
        }).start();
    }

    private void doCapture(RecyclerView recyclerView, int itemHeight) {
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
                Bitmap bitmap = ScreenShotHelper.shotRecyclerView(recyclerView, itemHeight);
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
                    ToastUtils.makeToast(R.string.failed_to_create_file);
                }
            }
        }).start();
    }

    private void doCapture(WebView webView, ProgressDialog pd, FileHelper.OnSavedToGalleryListener listener) {
        ScreenShotHelper.shotWebView(webView, listener);
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }
    // endregion

    // region Attachment

    /**
     * This method will called when the attachment is sure usable. For the check logic, you may refer
     * to {@link BaseFragment#onAttachingFileFinished(Attachment)}
     *
     * @param attachment the usable attachment */
    protected void onGetAttachment(@NonNull Attachment attachment) {}

    protected void onFailedGetAttachment(Attachment attachment) {}

    @Override
    public void onAttachingFileErrorOccurred(Attachment attachment) {
        onFailedGetAttachment(attachment);
    }

    @Override
    public void onAttachingFileFinished(Attachment attachment) {
        if (AttachmentHelper.checkAttachment(attachment)) {
            onGetAttachment(attachment);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            AttachmentHelper.resolveResult(this, requestCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    // endregion

}
