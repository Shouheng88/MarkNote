package me.shouheng.notepal.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.GalleryActivity;
import me.shouheng.notepal.async.AttachmentTask;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.dialog.AttachmentPickerDialog;
import me.shouheng.notepal.listener.OnAttachingFileListener;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.model.ModelFactory;

/**
 * Created by Wang Shouheng on 2017/12/30.*/
public class AttachmentHelper {

    /**
     * request code of attachment picker action */
    public final static int REQUEST_TAKE_PHOTO = 0x1000;
    public final static int REQUEST_SELECT_IMAGE = 0x1100;
    public final static int REQUEST_TAKE_VIDEO = 0x1200;
    public final static int REQUEST_FILES = 0x1300;
    public final static int REQUEST_SKETCH = 0x1400;

    /**
     * persist the current operation file information */
    private Uri attachmentUri;
    private String filePath;

    // region Resolve attachment click events
    public static void resolveClickEvent(
            Context context,
            Attachment attachment,
            List<Attachment> attachments,
            String galleryTitle) {
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
            ToastUtils.makeToast(context, R.string.activity_not_found_to_resolve);
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
            AttachmentPickerDialog dialog,
            int requestCode,
            Intent data,
            OnGetAttachmentListener onGetAttachmentListener) {
        switch (requestCode){
            case REQUEST_TAKE_PHOTO:
                onGetAttachmentListener.onGetAttachment(getAttachment(fragment.getContext(), dialog, Constants.MIME_TYPE_IMAGE));
                break;
            case REQUEST_SELECT_IMAGE:
                startTask(fragment, data);
                break;
            case REQUEST_TAKE_VIDEO:
                onGetAttachmentListener.onGetAttachment(getVideo(fragment.getContext(), dialog, data));
                break;
            case REQUEST_FILES:
                startTask(fragment, data);
                break;
            case REQUEST_SKETCH:
                onGetAttachmentListener.onGetAttachment(getAttachment(fragment.getContext(), dialog, Constants.MIME_TYPE_SKETCH));
                break;
        }
    }

    public static<T extends Activity & OnAttachingFileListener> void resolveResult(
            T activity,
            AttachmentPickerDialog dialog,
            int requestCode,
            int resultCode,
            Intent data,
            OnGetAttachmentListener onGetAttachmentListener) {
        if (resultCode != Activity.RESULT_OK) return; // not handle this event
        switch (requestCode){
            case REQUEST_TAKE_PHOTO:
                onGetAttachmentListener.onGetAttachment(getAttachment(activity, dialog, Constants.MIME_TYPE_IMAGE));
                break;
            case REQUEST_SELECT_IMAGE:
                startTask(activity, data);
                break;
            case REQUEST_TAKE_VIDEO:
                onGetAttachmentListener.onGetAttachment(getVideo(activity, dialog, data));
                break;
            case REQUEST_FILES:
                startTask(activity, data);
                break;
            case REQUEST_SKETCH:
                onGetAttachmentListener.onGetAttachment(getAttachment(activity, dialog, Constants.MIME_TYPE_SKETCH));
                break;
        }
    }

    private static Attachment getAttachment(Context context, AttachmentPickerDialog dialog, String mimeType) {
        Attachment photo = ModelFactory.getAttachment(context);
        photo.setUri(dialog.getAttachmentUri());
        photo.setMineType(mimeType);
        photo.setPath(dialog.getFilePath());
        return photo;
    }

    private static Attachment getVideo(Context context, AttachmentPickerDialog dialog, Intent data) {
        Attachment attachment = ModelFactory.getAttachment(context);
        attachment.setUri(data.getData());
        attachment.setMineType(Constants.MIME_TYPE_VIDEO);
        attachment.setPath(dialog.getFilePath());
        return attachment;
    }

    private static <T extends Activity & OnAttachingFileListener> void startTask(T activity, Intent data) {
        List<Uri> uris = new ArrayList<>();
        if (PalmUtils.isJellyBean() && data.getClipData() != null) {
            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                uris.add(data.getClipData().getItemAt(i).getUri());
            }
        } else {
            uris.add(data.getData());
        }
        for (Uri uri : uris) {
            String name = FileHelper.getNameFromUri(activity, uri);
            new AttachmentTask(activity, uri, name, activity).execute();
        }
    }

    private static <T extends Fragment & OnAttachingFileListener> void startTask(T fragment, Intent data) {
        List<Uri> uris = new ArrayList<>();
        if (PalmUtils.isJellyBean() && data.getClipData() != null) {
            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                uris.add(data.getClipData().getItemAt(i).getUri());
            }
        } else {
            uris.add(data.getData());
        }
        for (Uri uri : uris) {
            String name = FileHelper.getNameFromUri(fragment.getContext(), uri);
            new AttachmentTask(fragment, uri, name, fragment).execute();
        }
    }

    public interface OnGetAttachmentListener {
        void onGetAttachment(Attachment attachment);
    }
    // endregion

    public Uri getAttachmentUri() {
        return attachmentUri;
    }

    public void setAttachmentUri(Uri attachmentUri) {
        this.attachmentUri = attachmentUri;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
