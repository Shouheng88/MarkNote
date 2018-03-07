package me.shouheng.notepal.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.webkit.WebView;

import org.polaric.colorful.BaseActivity;
import org.polaric.colorful.PermissionUtils;

import java.io.File;

import me.shouheng.notepal.R;
import me.shouheng.notepal.listener.OnAttachingFileListener;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.util.AttachmentHelper;
import me.shouheng.notepal.util.FileHelper;
import me.shouheng.notepal.util.LogUtils;
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
        /**
         * Can't get the file */
        LogUtils.d(attachment);
        if (attachment == null
                || attachment.getUri() == null
                || TextUtils.isEmpty(attachment.getUri().toString())) {
            ToastUtils.makeToast(R.string.failed_to_create_file);
            return;
        }
        onGetAttachment(attachment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            AttachmentHelper.resolveResult(this,
                    requestCode,
                    data,
                    BaseFragment.this::onGetAttachment);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    // endregion

}
