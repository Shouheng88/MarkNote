/*
 * Copyright (C) 2015 Federico Iosue (federico.iosue@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.shouheng.notepal.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Images.Thumbnails;

import org.apache.commons.io.FilenameUtils;

import me.shouheng.notepal.R;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.model.Attachment;


public class BitmapHelper {

	/**
	 * Retrieves a the bitmap relative to attachment based on mime type
	 */
	public static Bitmap getBitmapFromAttachment(Context mContext, Attachment mAttachment, int width, int height) {
		Bitmap bmp = null;
		String path;
		mAttachment.getUri().getPath();

		// Video
		if (Constants.MIME_TYPE_VIDEO.equals(mAttachment.getMineType())) {
			// Tries to retrieve full path from ContentResolver if is a new video
			path = mAttachment.getPath();
			// .. or directly from local directory otherwise
			if (path == null) {
				path = FileHelper.getPath(mContext, mAttachment.getUri());
			}
			bmp = ThumbnailUtils.createVideoThumbnail(path, Thumbnails.MINI_KIND);
			if (bmp == null) {
				return null;
			} else {
				bmp = BitmapUtils.createVideoThumbnail(mContext, bmp, width, height);
			}

			// Image
		} else if (Constants.MIME_TYPE_IMAGE.equals(mAttachment.getMineType())
				|| Constants.MIME_TYPE_SKETCH.equals(mAttachment.getMineType())) {
			try {
				bmp = BitmapUtils.getThumbnail(mContext, mAttachment.getUri(), width, height);
			} catch (NullPointerException e) {
				bmp = null;
			}
			// Audio
		} else if (Constants.MIME_TYPE_AUDIO.equals(mAttachment.getMineType())) {
			bmp = ThumbnailUtils.extractThumbnail(
					BitmapUtils.decodeSampledBitmapFromResourceMemOpt(mContext.getResources().openRawResource(R
							.raw.play), width, height), width, height);

			// File
		} else if (Constants.MIME_TYPE_FILES.equals(mAttachment.getMineType())) {
			// vCard
			if (Constants.MIME_TYPE_CONTACT_EXTENSION.equals(FilenameUtils.getExtension(mAttachment.getName()))) {
				bmp = ThumbnailUtils.extractThumbnail(
						BitmapUtils.decodeSampledBitmapFromResourceMemOpt(mContext.getResources().openRawResource(R
								.raw.vcard), width, height), width, height);
			} else {
				bmp = ThumbnailUtils.extractThumbnail(
						BitmapUtils.decodeSampledBitmapFromResourceMemOpt(mContext.getResources().openRawResource(R
								.raw.files), width, height), width, height);
			}
		}

		return bmp;
	}
}
