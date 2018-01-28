package me.shouheng.notepal.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore.Images.Thumbnails;

import me.shouheng.notepal.R;
import me.shouheng.notepal.config.Constants;

public class BitmapHelper {

	public static Bitmap getBitmap(Context mContext, Uri uri, int width, int height) {
		Bitmap bmp = null;
		String path;

		String mimeType = FileHelper.getMimeTypeInternal(mContext, uri);

		if (Constants.MIME_TYPE_VIDEO.equals(mimeType)) {
			path = FileHelper.getPath(mContext, uri);
			bmp = ThumbnailUtils.createVideoThumbnail(path, Thumbnails.MINI_KIND);
			bmp = bmp == null ? null : BitmapUtils.createVideoThumbnail(mContext, bmp, width, height);
		} else if (Constants.MIME_TYPE_IMAGE.equals(mimeType) || Constants.MIME_TYPE_SKETCH.equals(mimeType)) {
			try {
				bmp = BitmapUtils.getThumbnail(mContext, uri, width, height);
			} catch (NullPointerException e) {
				bmp = null;
			}
		} else if (Constants.MIME_TYPE_AUDIO.equals(mimeType)) {
			bmp = ThumbnailUtils.extractThumbnail(BitmapUtils.decodeSampledBitmapFromResourceMemOpt(
					mContext.getResources().openRawResource(R.raw.play), width, height), width, height);
		} else if (Constants.MIME_TYPE_FILES.equals(mimeType)) {
			if (Constants.MIME_TYPE_CONTACT_EXTENSION.equals(FileHelper.getFileExtension(mContext, uri))) {
				bmp = ThumbnailUtils.extractThumbnail(BitmapUtils.decodeSampledBitmapFromResourceMemOpt(
						mContext.getResources().openRawResource(R.raw.vcard), width, height), width, height);
			} else {
				bmp = ThumbnailUtils.extractThumbnail(BitmapUtils.decodeSampledBitmapFromResourceMemOpt(
						mContext.getResources().openRawResource(R.raw.files), width, height), width, height);
			}
		}

		return bmp;
	}
}
