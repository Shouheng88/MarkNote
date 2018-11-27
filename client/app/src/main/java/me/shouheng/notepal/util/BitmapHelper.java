package me.shouheng.notepal.util;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore.Images.Thumbnails;
import android.widget.RemoteViewsService;

import me.shouheng.commons.utils.LogUtils;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.manager.MediaStoreFactory;

import static java.lang.Long.parseLong;

public class BitmapHelper {

	public static Bitmap getBitmap(Context mContext, RemoteViewsService remoteViewsService, Uri uri, int width, int height) {
		Bitmap bmp = null;
		String path;

		String mimeType = FileHelper.getMimeTypeInternal(mContext, uri);

		if (Constants.MIME_TYPE_VIDEO.equals(mimeType)) {
			path = getPath(mContext, remoteViewsService, uri);
			bmp = ThumbnailUtils.createVideoThumbnail(path, Thumbnails.MINI_KIND);
			bmp = bmp == null ? null : BitmapUtils.createVideoThumbnail(mContext, bmp, width, height);
		} else if (Constants.MIME_TYPE_IMAGE.equals(mimeType) || Constants.MIME_TYPE_SKETCH.equals(mimeType)) {
			try {
				bmp = BitmapUtils.getThumbnail(mContext, remoteViewsService, uri, width, height);
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

	@SuppressLint("NewApi")
	public static String getPath(final Context context, RemoteViewsService remoteViewsService, final Uri uri) {
		if (uri == null) return null;

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			if (FileHelper.isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}
			} else if (FileHelper.isDownloadsDocument(uri)) {
				final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), parseLong(DocumentsContract.getDocumentId(uri)));
				return getDataColumn(remoteViewsService, contentUri, null, null);
			} else if (FileHelper.isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];
				Uri contentUri = MediaStoreFactory.getInstance().createURI(type);

				final String selection = "_id=?";
				final String[] selectionArgs = new String[]{split[1]};

				return getDataColumn(remoteViewsService, contentUri, selection, selectionArgs);
			}
		} else if ("content".equalsIgnoreCase(uri.getScheme())) {
			return getDataColumn(remoteViewsService, uri, null, null);
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
		return null;
	}

	private static String getDataColumn(RemoteViewsService remoteViewsService, Uri uri, String selection, String[] selectionArgs) {
		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {column};
		try {
			cursor = PalmApp.getContext().getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} catch (Exception e) {
			LogUtils.e("Error retrieving uri path", e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}
}
