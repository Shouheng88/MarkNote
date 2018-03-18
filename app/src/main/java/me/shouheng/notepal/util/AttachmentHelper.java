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
import me.shouheng.notepal.async.AttachmentTask;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.listener.OnAttachingFileListener;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.model.ModelFactory;

/**
 * Created by Wang Shouheng on 2017/12/30.*/
public class AttachmentHelper {

    /**
     * request code of attachment picker action */
    private final static int REQUEST_TAKE_PHOTO = 0x1000;
    private final static int REQUEST_SELECT_IMAGE = 0x1100;
    private final static int REQUEST_TAKE_VIDEO = 0x1200;
    private final static int REQUEST_FILES = 0x1300;
    private final static int REQUEST_SKETCH = 0x1400;

    /**
     * persist the current operation file information */
    private static Uri attachmentUri;
    private static String filePath;

    // region Resolve attachment click events
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

    // region Attachment adding events

    public static<T extends Fragment & OnAttachingFileListener> void resolveResult(
            T fragment,
            int requestCode,
            Intent data,
            OnGetAttachmentListener onGetAttachmentListener) {
        switch (requestCode){
            case REQUEST_TAKE_PHOTO:
                onGetAttachmentListener.onGetAttachment(
                        getAttachment(fragment.getContext(), Constants.MIME_TYPE_IMAGE));
                break;
            case REQUEST_SELECT_IMAGE:
                startTask(fragment, data);
                break;
            case REQUEST_TAKE_VIDEO:
                onGetAttachmentListener.onGetAttachment(
                        getVideo(fragment.getContext(), data));
                break;
            case REQUEST_FILES:
                startTask(fragment, data);
                break;
            case REQUEST_SKETCH:
                onGetAttachmentListener.onGetAttachment(
                        getAttachment(fragment.getContext(), Constants.MIME_TYPE_SKETCH));
                break;
        }
    }

    public static<T extends android.app.Fragment & OnAttachingFileListener> void resolveResult(
            T fragment,
            int requestCode,
            Intent data,
            OnGetAttachmentListener onGetAttachmentListener) {
        switch (requestCode){
            case REQUEST_TAKE_PHOTO:
                onGetAttachmentListener.onGetAttachment(
                        getAttachment(fragment.getActivity(), Constants.MIME_TYPE_IMAGE));
                break;
            case REQUEST_SELECT_IMAGE:
                startTask(fragment, data);
                break;
            case REQUEST_TAKE_VIDEO:
                onGetAttachmentListener.onGetAttachment(
                        getVideo(fragment.getActivity(), data));
                break;
            case REQUEST_FILES:
                startTask(fragment, data);
                break;
            case REQUEST_SKETCH:
                onGetAttachmentListener.onGetAttachment(
                        getAttachment(fragment.getActivity(), Constants.MIME_TYPE_SKETCH));
                break;
        }
    }

    public static<T extends Activity & OnAttachingFileListener> void resolveResult(
            T activity,
            int requestCode,
            Intent data,
            OnGetAttachmentListener onGetAttachmentListener) {
        switch (requestCode){
            case REQUEST_TAKE_PHOTO:
                onGetAttachmentListener.onGetAttachment(
                        getAttachment(activity, Constants.MIME_TYPE_IMAGE));
                break;
            case REQUEST_SELECT_IMAGE:
                startTask(activity, data);
                break;
            case REQUEST_TAKE_VIDEO:
                onGetAttachmentListener.onGetAttachment(
                        getVideo(activity, data));
                break;
            case REQUEST_FILES:
                startTask(activity, data);
                break;
            case REQUEST_SKETCH:
                onGetAttachmentListener.onGetAttachment(
                        getAttachment(activity, Constants.MIME_TYPE_SKETCH));
                break;
        }
    }

    private static Attachment getAttachment(Context context, String mimeType) {
        Attachment photo = ModelFactory.getAttachment(context);
        LogUtils.d("Attachment uri when get attachment:" + attachmentUri);
        photo.setUri(getAttachmentUri());
        photo.setMineType(mimeType);
        photo.setPath(getFilePath());
        return photo;
    }

    private static Attachment getVideo(Context context, Intent data) {
        Attachment attachment = ModelFactory.getAttachment(context);
        attachment.setUri(data.getData());
        attachment.setMineType(Constants.MIME_TYPE_VIDEO);
        attachment.setPath(getFilePath());
        return attachment;
    }

    private static <T extends Activity & OnAttachingFileListener> void startTask(T activity, Intent data) {
        for (Uri uri : getUrisFromIntent(data)) {
            String name = FileHelper.getNameFromUri(activity, uri);
            new AttachmentTask(activity, uri, name, activity).execute();
        }
    }

    private static <T extends Fragment & OnAttachingFileListener> void startTask(T fragment, Intent data) {
        for (Uri uri : getUrisFromIntent(data)) {
            String name = FileHelper.getNameFromUri(fragment.getContext(), uri);
            new AttachmentTask(fragment, uri, name, fragment).execute();
        }
    }

    private static <T extends android.app.Fragment & OnAttachingFileListener> void startTask(T fragment, Intent data) {
        for (Uri uri : getUrisFromIntent(data)) {
            String name = FileHelper.getNameFromUri(fragment.getActivity(), uri);
            new AttachmentTask(fragment, uri, name, fragment).execute();
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

    public interface OnGetAttachmentListener {
        void onGetAttachment(Attachment attachment);
    }
    // endregion

    // region Pick attachment

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

        Uri attachmentUri = FileHelper.getUriFromFile(context, file);
        LogUtils.d("Attachment uri when create file: " + attachmentUri);
        AttachmentHelper.setAttachmentUri(attachmentUri);
        AttachmentHelper.setFilePath(file.getPath());

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

        Uri attachmentUri = FileHelper.getUriFromFile(context, file);
        String filePath = file.getPath();
        AttachmentHelper.setAttachmentUri(attachmentUri);
        AttachmentHelper.setFilePath(filePath);

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

        Uri attachmentUri = FileHelper.getUriFromFile(context, file);
        String filePath = file.getPath();
        AttachmentHelper.setAttachmentUri(attachmentUri);
        AttachmentHelper.setFilePath(filePath);

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

    public static Uri getAttachmentUri() {
        if (attachmentUri == null) {
            /*
             * Get attachment uri from preferences. */
            String uriStr = PreferencesUtils.getInstance(PalmApp.getContext()).getAttachmentUri();
            if (!TextUtils.isEmpty(uriStr)) {
                attachmentUri = Uri.parse(uriStr);
            }
        }
        return attachmentUri;
    }

    public static void setAttachmentUri(Uri attachmentUri) {
        AttachmentHelper.attachmentUri = attachmentUri;
        /*
         * Persist the attachment uri at the preferences at the same time. */
        if (attachmentUri != null) {
            PreferencesUtils.getInstance(PalmApp.getContext()).setAttachmentUri(attachmentUri);
        }
    }

    public static String getFilePath() {
        if (TextUtils.isEmpty(filePath)) {
            filePath = PreferencesUtils.getInstance(PalmApp.getContext()).getAttachmentFilePath();
        }
        return filePath;
    }

    public static void setFilePath(String filePath) {
        AttachmentHelper.filePath = filePath;
        PreferencesUtils.getInstance(PalmApp.getContext()).setAttachmentFilePath(filePath);
    }
}
