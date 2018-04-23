package me.shouheng.notepal.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.GalleryActivity;
import me.shouheng.notepal.activity.SketchActivity;
import me.shouheng.notepal.async.CreateAttachmentTask;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.listener.DefaultCompressListener;
import me.shouheng.notepal.listener.OnAttachingFileListener;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.model.ModelFactory;
import me.shouheng.notepal.util.preferences.PreferencesUtils;
import top.zibin.luban.Luban;

/**
 * Created by Wang Shouheng on 2017/12/30.*/
public class AttachmentHelper {

    private final static int REQUEST_TAKE_PHOTO = 0x1000;
    private final static int REQUEST_SELECT_IMAGE = 0x1100;
    private final static int REQUEST_TAKE_VIDEO = 0x1200;
    private final static int REQUEST_FILES = 0x1300;
    private final static int REQUEST_SKETCH = 0x1400;

    private static String filePath;

    // region Resolve attachment clicking event.
    public static void resolveClickEvent(
            Context context,
            Attachment attachment,
            List<Attachment> attachments,
            String galleryTitle) {
        if (attachment == null) {
            ToastUtils.makeToast(R.string.file_not_exist);
            return;
        }
        switch (attachment.getMineType()) {
            case Constants.MIME_TYPE_FILES: {
                resolveFiles(context, attachment);
                break;
            }
            case Constants.MIME_TYPE_IMAGE:
            case Constants.MIME_TYPE_SKETCH:
            case Constants.MIME_TYPE_VIDEO: {
                resolveImages(context, attachment, attachments, galleryTitle);
                break;
            }
        }
    }

    private static void resolveFiles(Context context, Attachment attachment) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(attachment.getUri(), FileHelper.getMimeType(context, attachment.getUri()));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if (IntentUtils.isAvailable(context.getApplicationContext(), intent, null)) {
            context.startActivity(intent);
        } else {
            ToastUtils.makeToast(R.string.activity_not_found_to_resolve);
        }
    }

    private static void resolveImages(Context context,
                                      Attachment attachment,
                                      List<Attachment> attachments,
                                      String galleryTitle) {
        int clickedImage = 0;
        ArrayList<Attachment> images = new ArrayList<>();
        for (Attachment mAttachment : attachments) {
            if (Constants.MIME_TYPE_IMAGE.equals(mAttachment.getMineType())
                    || Constants.MIME_TYPE_SKETCH.equals(mAttachment.getMineType())
                    || Constants.MIME_TYPE_VIDEO.equals(mAttachment.getMineType())) {
                images.add(mAttachment);
                if (mAttachment.equals(attachment)) {
                    clickedImage = images.size() - 1;
                }
            }
        }
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra(GalleryActivity.EXTRA_GALLERY_TITLE, galleryTitle);
        intent.putParcelableArrayListExtra(GalleryActivity.EXTRA_GALLERY_IMAGES, images);
        intent.putExtra(GalleryActivity.EXTRA_GALLERY_CLICKED_IMAGE, clickedImage);
        context.startActivity(intent);
    }

    // endregion

    // region Resolve attachment picking result.
    public static<T extends Fragment & OnAttachingFileListener> void resolveResult(T fragment, int requestCode, Intent data) {
        switch (requestCode){
            case REQUEST_TAKE_PHOTO:
                getPhoto(fragment.getContext(), Constants.MIME_TYPE_IMAGE, new OnAttachingFileListener() {
                    @Override
                    public void onAttachingFileErrorOccurred(Attachment attachment) {
                        fragment.onAttachingFileErrorOccurred(attachment);
                    }

                    @Override
                    public void onAttachingFileFinished(Attachment attachment) {
                        if (PalmUtils.isAlive(fragment)) {
                            fragment.onAttachingFileFinished(attachment);
                        }
                    }
                });
                break;
            case REQUEST_SELECT_IMAGE:
                startTask(fragment, data);
                break;
            case REQUEST_TAKE_VIDEO:
                if (PalmUtils.isAlive(fragment)) {
                    fragment.onAttachingFileFinished(getVideo(data));
                }
                break;
            case REQUEST_FILES:
                startTask(fragment, data);
                break;
            case REQUEST_SKETCH:
                if (PalmUtils.isAlive(fragment)) {
                    fragment.onAttachingFileFinished(getSketch(Constants.MIME_TYPE_SKETCH));
                }
                break;
        }
    }

    public static<T extends android.app.Fragment & OnAttachingFileListener> void resolveResult(T fragment, int requestCode, Intent data) {
        switch (requestCode){
            case REQUEST_TAKE_PHOTO:
                getPhoto(fragment.getActivity(), Constants.MIME_TYPE_IMAGE, new OnAttachingFileListener() {
                    @Override
                    public void onAttachingFileErrorOccurred(Attachment attachment) {
                        fragment.onAttachingFileErrorOccurred(attachment);
                    }

                    @Override
                    public void onAttachingFileFinished(Attachment attachment) {
                        if (PalmUtils.isAlive(fragment)) {
                            fragment.onAttachingFileFinished(attachment);
                        }
                    }
                });
                break;
            case REQUEST_SELECT_IMAGE:
                startTask(fragment, data);
                break;
            case REQUEST_TAKE_VIDEO:
                if (PalmUtils.isAlive(fragment)) {
                    fragment.onAttachingFileFinished(getVideo(data));
                }
                break;
            case REQUEST_FILES:
                startTask(fragment, data);
                break;
            case REQUEST_SKETCH:
                if (PalmUtils.isAlive(fragment)) {
                    fragment.onAttachingFileFinished(getSketch(Constants.MIME_TYPE_SKETCH));
                }
                break;
        }
    }

    public static<T extends Activity & OnAttachingFileListener> void resolveResult(T activity, int requestCode, Intent data) {
        switch (requestCode){
            case REQUEST_TAKE_PHOTO:
                getPhoto(activity, Constants.MIME_TYPE_IMAGE, new OnAttachingFileListener() {
                    @Override
                    public void onAttachingFileErrorOccurred(Attachment attachment) {
                        activity.onAttachingFileErrorOccurred(attachment);
                    }

                    @Override
                    public void onAttachingFileFinished(Attachment attachment) {
                        if (PalmUtils.isAlive(activity)) {
                            activity.onAttachingFileFinished(attachment);
                        }
                    }
                });
                break;
            case REQUEST_SELECT_IMAGE:
                startTask(activity, data);
                break;
            case REQUEST_TAKE_VIDEO:
                if (PalmUtils.isAlive(activity)) {
                    activity.onAttachingFileFinished(getVideo(data));
                }
                break;
            case REQUEST_FILES:
                startTask(activity, data);
                break;
            case REQUEST_SKETCH:
                if (PalmUtils.isAlive(activity)) {
                    activity.onAttachingFileFinished(getSketch(Constants.MIME_TYPE_SKETCH));
                }
                break;
        }
    }

    private static void getPhoto(Context context, String mimeType, OnAttachingFileListener onAttachingFileListener) {
        Attachment photo = ModelFactory.getAttachment();
        photo.setMineType(mimeType);
        if (shouldCompressImage()) {
            compressImage(context, photo, new File(getFilePath()), onAttachingFileListener);
        } else {
            photo.setPath(getFilePath());
            photo.setUri(FileHelper.getUriFromFile(context, new File(getFilePath())));
            if (onAttachingFileListener != null) {
                onAttachingFileListener.onAttachingFileFinished(photo);
            }
        }
    }

    private static boolean shouldCompressImage() {
        return PreferencesUtils.getInstance().isImageAutoCompress();
    }

    private static void compressImage(Context context,
                                      Attachment attachment,
                                      File var,
                                      OnAttachingFileListener onAttachingFileListener) {
        Luban.with(context)
                .load(var)
                .ignoreBy(100)
                .setTargetDir(var.getParent())
                .setCompressListener(new DefaultCompressListener() {
                    @Override
                    public void onSuccess(File file) {
                        FileHelper.delete(PalmApp.getContext(), var.getPath());
                        attachment.setPath(file.getPath());
                        attachment.setUri(FileHelper.getUriFromFile(PalmApp.getContext(), file));
                        if (onAttachingFileListener != null) {
                            onAttachingFileListener.onAttachingFileFinished(attachment);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (onAttachingFileListener != null) {
                            onAttachingFileListener.onAttachingFileErrorOccurred(null);
                        }
                    }
                })
                .launch();
    }

    private static Attachment getSketch(String mimeType) {
        Attachment photo = ModelFactory.getAttachment();
        photo.setUri(FileHelper.getUriFromFile(PalmApp.getContext(), new File(getFilePath())));
        photo.setMineType(mimeType);
        photo.setPath(getFilePath());
        return photo;
    }

    private static Attachment getVideo(Intent data) {
        Attachment attachment = ModelFactory.getAttachment();
        attachment.setUri(data.getData());
        attachment.setMineType(Constants.MIME_TYPE_VIDEO);
        attachment.setPath(getFilePath());
        return attachment;
    }

    private static <T extends Activity & OnAttachingFileListener> void startTask(T activity, Intent data) {
        for (Uri uri : getUrisFromIntent(data)) {
            new CreateAttachmentTask(activity, uri, activity).execute();
        }
    }

    private static <T extends Fragment & OnAttachingFileListener> void startTask(T fragment, Intent data) {
        for (Uri uri : getUrisFromIntent(data)) {
            new CreateAttachmentTask(fragment, uri, fragment).execute();
        }
    }

    private static <T extends android.app.Fragment & OnAttachingFileListener> void startTask(T fragment, Intent data) {
        for (Uri uri : getUrisFromIntent(data)) {
            new CreateAttachmentTask(fragment, uri, fragment).execute();
        }
    }

    private static List<Uri> getUrisFromIntent(Intent data) {
        List<Uri> uris = new ArrayList<>();
        if (PalmUtils.isJellyBean() && data.getClipData() != null) {
            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                uris.add(data.getClipData().getItemAt(i).getUri());
            }
        } else {
            uris.add(data.getData());
        }
        return uris;
    }
    // endregion

    // region Start picking action.
    public static void pickFromAlbum(Activity activity) {
        activity.startActivityForResult(pickFromAlbum(), AttachmentHelper.REQUEST_SELECT_IMAGE);
    }

    public static void pickFromAlbum(Fragment fragment) {
        fragment.startActivityForResult(pickFromAlbum(), AttachmentHelper.REQUEST_SELECT_IMAGE);
    }

    public static void pickFromAlbum(android.app.Fragment fragment) {
        fragment.startActivityForResult(pickFromAlbum(), AttachmentHelper.REQUEST_SELECT_IMAGE);
    }

    private static Intent pickFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        return intent;
    }

    public static void pickFiles(Activity activity) {
        activity.startActivityForResult(pickFiles(), AttachmentHelper.REQUEST_FILES);
    }

    public static void pickFiles(Fragment fragment) {
        fragment.startActivityForResult(pickFiles(), AttachmentHelper.REQUEST_FILES);
    }

    private static Intent pickFiles() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        if (PalmUtils.isJellyBeanMR2()) intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.setType("*/*");
        return intent;
    }

    public static void capture(Activity activity) {
        Intent intent = captureIntent(activity);
        if (intent == null) return;
        activity.startActivityForResult(intent, AttachmentHelper.REQUEST_TAKE_PHOTO);
    }

    public static void capture(Fragment fragment) {
        Intent intent = captureIntent(fragment.getContext());
        if (intent == null) return;
        fragment.startActivityForResult(intent, AttachmentHelper.REQUEST_TAKE_PHOTO);
    }

    @Nullable
    private static Intent captureIntent(Context context) {
        File file = FileHelper.createNewAttachmentFile(context, Constants.MIME_TYPE_IMAGE_EXTENSION);
        if (file == null){
            ToastUtils.makeToast(R.string.failed_to_create_file);
            return null;
        }
        String filePath = file.getPath();
        setFilePath(file.getPath());
        Uri attachmentUri = FileHelper.getUriFromFile(PalmApp.getContext(), new File(filePath));
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, attachmentUri);
        return intent;
    }

    public static void recordVideo(Activity activity) {
        Intent intent = recordVideoIntent(activity);
        if (intent == null) return;
        activity.startActivityForResult(intent, AttachmentHelper.REQUEST_TAKE_VIDEO);
    }

    public static void recordVideo(Fragment fragment) {
        Intent intent = recordVideoIntent(fragment.getContext());
        if (intent == null) return;
        fragment.startActivityForResult(intent, AttachmentHelper.REQUEST_TAKE_VIDEO);
    }

    @Nullable
    private static Intent recordVideoIntent(Context context) {
        File file = FileHelper.createNewAttachmentFile(context, Constants.MIME_TYPE_SKETCH_EXTENSION);
        if (file == null) {
            ToastUtils.makeToast(R.string.failed_to_create_file);
            return null;
        }
        String filePath = file.getPath();
        setFilePath(filePath);
        Intent intent = new Intent(context, SketchActivity.class);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, filePath);
        return intent;
    }

    public static void sketch(Activity activity) {
        Intent intent = sketchIntent(activity);
        if (intent == null) return;
        activity.startActivityForResult(intent, AttachmentHelper.REQUEST_SKETCH);
    }

    public static void sketch(Fragment fragment) {
        Intent intent = sketchIntent(fragment.getContext());
        if (intent == null) return;
        fragment.startActivityForResult(intent, AttachmentHelper.REQUEST_SKETCH);
    }

    @Nullable
    private static Intent sketchIntent(Context context) {
        File file = FileHelper.createNewAttachmentFile(context, Constants.MIME_TYPE_SKETCH_EXTENSION);
        if (file == null) {
            ToastUtils.makeToast(R.string.failed_to_create_file);
            return null;
        }
        String filePath = file.getPath();
        setFilePath(filePath);
        Intent intent = new Intent(context, SketchActivity.class);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, filePath);
        return intent;
    }
    // endregion

    public static boolean checkAttachment(Attachment attachment) {
        LogUtils.d(attachment);
        if (attachment == null
                || attachment.getUri() == null
                || TextUtils.isEmpty(attachment.getUri().toString())) {
            ToastUtils.makeToast(R.string.failed_to_create_file);
            return false;
        }
        return true;
    }

    private static String getFilePath() {
        if (TextUtils.isEmpty(filePath)) {
            filePath = PreferencesUtils.getInstance().getAttachmentFilePath();
        }
        return filePath;
    }

    private static void setFilePath(String filePath) {
        AttachmentHelper.filePath = filePath;
        PreferencesUtils.getInstance().setAttachmentFilePath(filePath);
    }
}
