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

    public static void resolveClickEvent(Context context, Attachment attachment, 
                                         List<Attachment> attachments, String galleryTitle) {
        switch (attachment.getMineType()) {
            case Constants.MIME_TYPE_FILES: {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(attachment.getUri(), FileHelper.getMimeType(context, attachment.getUri()));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                if (IntentChecker.isAvailable(context.getApplicationContext(), intent, null)) {
                    context.startActivity(intent);
                } else {
                    ToastUtils.makeToast(context, R.string.activity_not_found_to_resolve);
                }
                break;
            }
            case Constants.MIME_TYPE_IMAGE:
            case Constants.MIME_TYPE_SKETCH:
            case Constants.MIME_TYPE_VIDEO: {
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
                break;
            }
        }
    }

    public static<T extends Fragment & OnAttachingFileListener> void resolveResult(
            T fragment, AttachmentPickerDialog dialog, int requestCode, int resultCode, Intent data, OnGetAttachmentListener onGetAttachmentListener) {
        switch (requestCode){
            case AttachmentPickerDialog.REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK){
                    Attachment photo = ModelFactory.getAttachment(fragment.getContext());
                    Uri photoUri = dialog.getAttachmentUri();
                    photo.setUri(photoUri);
                    photo.setMineType(Constants.MIME_TYPE_IMAGE);
                    photo.setPath(dialog.getFilePath());
                    onGetAttachmentListener.onGetAttachment(photo);
                }
                break;
            case AttachmentPickerDialog.REQUEST_SELECT_IMAGE:
                if (resultCode == Activity.RESULT_OK){
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
                break;
            case AttachmentPickerDialog.REQUEST_TAKE_VIDEO:
                if (resultCode == Activity.RESULT_OK){
                    Attachment video = ModelFactory.getAttachment(fragment.getContext());
                    Uri videoUri = data.getData();
                    video.setUri(videoUri);
                    video.setMineType(Constants.MIME_TYPE_VIDEO);
                    video.setPath(dialog.getFilePath());
                    onGetAttachmentListener.onGetAttachment(video);
                }
                break;
            case AttachmentPickerDialog.REQUEST_FILES:
                if (resultCode == Activity.RESULT_OK){
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
                break;
            case AttachmentPickerDialog.REQUEST_SKETCH:
                if (resultCode == Activity.RESULT_OK){
                    Attachment sketch = ModelFactory.getAttachment(fragment.getContext());
                    Uri sketchUri = dialog.getAttachmentUri();
                    sketch.setUri(sketchUri);
                    sketch.setMineType(Constants.MIME_TYPE_SKETCH);
                    sketch.setPath(dialog.getFilePath());
                    onGetAttachmentListener.onGetAttachment(sketch);
                }
                break;
        }
    }

    public static<T extends Activity & OnAttachingFileListener> void resolveResult(
            T activity, AttachmentPickerDialog dialog, int requestCode, int resultCode, Intent data, OnGetAttachmentListener onGetAttachmentListener) {
        switch (requestCode){
            case AttachmentPickerDialog.REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK){
                    Attachment photo = ModelFactory.getAttachment(activity);
                    Uri photoUri = dialog.getAttachmentUri();
                    photo.setUri(photoUri);
                    photo.setMineType(Constants.MIME_TYPE_IMAGE);
                    photo.setPath(dialog.getFilePath());
                    onGetAttachmentListener.onGetAttachment(photo);
                }
                break;
            case AttachmentPickerDialog.REQUEST_SELECT_IMAGE:
                if (resultCode == Activity.RESULT_OK){
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
                break;
            case AttachmentPickerDialog.REQUEST_TAKE_VIDEO:
                if (resultCode == Activity.RESULT_OK){
                    Attachment video = ModelFactory.getAttachment(activity);
                    Uri videoUri = data.getData();
                    video.setUri(videoUri);
                    video.setMineType(Constants.MIME_TYPE_VIDEO);
                    video.setPath(dialog.getFilePath());
                    onGetAttachmentListener.onGetAttachment(video);
                }
                break;
            case AttachmentPickerDialog.REQUEST_FILES:
                if (resultCode == Activity.RESULT_OK){
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
                break;
            case AttachmentPickerDialog.REQUEST_SKETCH:
                if (resultCode == Activity.RESULT_OK){
                    Attachment sketch = ModelFactory.getAttachment(activity);
                    Uri sketchUri = dialog.getAttachmentUri();
                    sketch.setUri(sketchUri);
                    sketch.setMineType(Constants.MIME_TYPE_SKETCH);
                    sketch.setPath(dialog.getFilePath());
                    onGetAttachmentListener.onGetAttachment(sketch);
                }
                break;
        }
    }

    public interface OnGetAttachmentListener {
        void onGetAttachment(Attachment attachment);
    }
}
