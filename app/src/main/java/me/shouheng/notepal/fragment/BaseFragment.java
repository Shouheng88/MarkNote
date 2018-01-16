package me.shouheng.notepal.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.webkit.WebView;

import java.io.File;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.CommonActivity;
import me.shouheng.notepal.dialog.AttachmentPickerDialog;
import me.shouheng.notepal.listener.OnAttachingFileListener;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.util.AttachmentHelper;
import me.shouheng.notepal.util.FileHelper;
import me.shouheng.notepal.util.PermissionUtils;
import me.shouheng.notepal.util.ScreenShotHelper;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.util.tools.Callback;
import me.shouheng.notepal.util.tools.Invoker;

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
        PermissionUtils.checkStoragePermission((CommonActivity) getActivity(), () -> {
            final ProgressDialog pd = new ProgressDialog(getContext());
            pd.setMessage(getString(R.string.capturing));
            final File file = null;
            new Invoker(new Callback() {
                @Override
                public void onBefore() {
                    pd.setCancelable(false);
                    pd.show();
                }
                @Override
                public boolean onRun() {
                    Bitmap bitmap = ScreenShotHelper.shotRecyclerView(recyclerView);
                    return FileHelper.saveImageToGallery(getContext(), bitmap, true, file);
                }
                @Override
                public void onAfter(boolean b) {
                    pd.dismiss();
                    if (b) {
                        ToastUtils.makeToast(getActivity(), R.string.text_save_successfully);
                        onGetScreenCutFile(file);
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
        PermissionUtils.checkStoragePermission((CommonActivity) getActivity(), () -> {
            final ProgressDialog pd = new ProgressDialog(getContext());
            pd.setTitle(R.string.capturing);
            final File file = null;
            new Invoker(new Callback() {
                @Override
                public void onBefore() {
                    pd.setCancelable(false);
                    pd.show();
                }
                @Override
                public boolean onRun() {
                    Bitmap bitmap = ScreenShotHelper.shotRecyclerView(recyclerView, itemHeight);
                    return FileHelper.saveImageToGallery(getContext(), bitmap, true, file);
                }
                @Override
                public void onAfter(boolean b) {
                    pd.dismiss();
                    if (b) {
                        ToastUtils.makeToast(getActivity(), R.string.text_save_successfully);
                        onGetScreenCutFile(file);
                    } else {
                        ToastUtils.makeToast(getActivity(), R.string.failed_to_create_file);
                    }
                }
            }).start();
        });
    }

    protected void createWebCapture(WebView webView) {
        assert getActivity() != null;
        PermissionUtils.checkStoragePermission((CommonActivity) getActivity(), () -> {
            final ProgressDialog pd = new ProgressDialog(getContext());
            pd.setTitle(R.string.capturing);
            final File file = null;
            new Invoker(new Callback(){
                @Override
                public void onBefore() {
                    pd.setCancelable(false);
                    pd.show();
                }

                @Override
                public boolean onRun() {
                    Bitmap bitmap = ScreenShotHelper.shotWebView(webView);
                    return FileHelper.saveImageToGallery(getContext(), bitmap, true, file);
                }

                @Override
                public void onAfter(boolean b) {
                    pd.dismiss();
                    if (b) {
                        ToastUtils.makeToast(getActivity(), R.string.text_save_successfully);
                        onGetScreenCutFile(file);
                    } else {
                        ToastUtils.makeToast(getActivity(), R.string.failed_to_create_file);
                    }
                }
            }).start();
        });
    }

    protected void createWebCaptureB(WebView webView) {
        assert getActivity() != null;
        PermissionUtils.checkStoragePermission((CommonActivity) getActivity(), () -> {
            ToastUtils.makeToast(getContext(), R.string.capturing);
            final File file = null;
            new Thread(() -> {
                Looper.prepare();
                getActivity().runOnUiThread(() -> {
                    Bitmap bitmap = ScreenShotHelper.shotWebView(webView);
                    boolean ret = FileHelper.saveImageToGallery(getContext(), bitmap, true, file);
                    if (ret) {
                        ToastUtils.makeToast(getActivity(), R.string.text_save_successfully);
                        onGetScreenCutFile(file);
                    } else {
                        ToastUtils.makeToast(getActivity(), R.string.failed_to_create_file);
                    }
                });
            }).start();
        });
    }

    protected void onGetScreenCutFile(File file) {}
    // endregion

    // region attachment
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
        AttachmentHelper.resolveResult(this, getAttachmentPickerDialog(), requestCode,
                resultCode, data, BaseFragment.this::onGetAttachment);
        super.onActivityResult(requestCode, resultCode, data);
    }
    // endregion

}
