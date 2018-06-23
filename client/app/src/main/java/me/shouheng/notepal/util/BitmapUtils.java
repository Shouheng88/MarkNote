package me.shouheng.notepal.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RemoteViewsService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import me.shouheng.notepal.R;

/**
 * Created by wang shouheng on 2018/1/25.*/
public class BitmapUtils {

    public static Bitmap getThumbnail(Context mContext, Uri uri, int reqWidth, int reqHeight) {
        Bitmap srcBmp = decodeSampledFromUri(mContext, uri, reqWidth, reqHeight);
        Bitmap dstBmp;
        if(srcBmp.getWidth() < reqWidth && srcBmp.getHeight() < reqHeight) {
            dstBmp = ThumbnailUtils.extractThumbnail(srcBmp, reqWidth, reqHeight);
        } else {
            int x = 0;
            int y = 0;
            int width = srcBmp.getWidth();
            int height = srcBmp.getHeight();
            float ratio = (float)reqWidth / (float)reqHeight * ((float)srcBmp.getHeight() / (float)srcBmp.getWidth());
            if(ratio < 1.0F) {
                x = (int)((float)srcBmp.getWidth() - (float)srcBmp.getWidth() * ratio) / 2;
                width = (int)((float)srcBmp.getWidth() * ratio);
            } else {
                y = (int)((float)srcBmp.getHeight() - (float)srcBmp.getHeight() / ratio) / 2;
                height = (int)((float)srcBmp.getHeight() / ratio);
            }

            String path = FileHelper.getPath(mContext, uri);
            int rotation = neededRotation(new File(path));
            if(rotation != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate((float)rotation);
                dstBmp = Bitmap.createBitmap(srcBmp, x, y, width, height, matrix, true);
            } else {
                dstBmp = Bitmap.createBitmap(srcBmp, x, y, width, height);
            }
        }

        return dstBmp;
    }

    public static Bitmap getThumbnail(Context mContext, RemoteViewsService remoteViewsService, Uri uri, int reqWidth, int reqHeight) {
        Bitmap srcBmp = decodeSampledFromUri(mContext, uri, reqWidth, reqHeight);
        Bitmap dstBmp;
        if(srcBmp.getWidth() < reqWidth && srcBmp.getHeight() < reqHeight) {
            dstBmp = ThumbnailUtils.extractThumbnail(srcBmp, reqWidth, reqHeight);
        } else {
            int x = 0;
            int y = 0;
            int width = srcBmp.getWidth();
            int height = srcBmp.getHeight();
            float ratio = (float)reqWidth / (float)reqHeight * ((float)srcBmp.getHeight() / (float)srcBmp.getWidth());
            if(ratio < 1.0F) {
                x = (int)((float)srcBmp.getWidth() - (float)srcBmp.getWidth() * ratio) / 2;
                width = (int)((float)srcBmp.getWidth() * ratio);
            } else {
                y = (int)((float)srcBmp.getHeight() - (float)srcBmp.getHeight() / ratio) / 2;
                height = (int)((float)srcBmp.getHeight() / ratio);
            }

            String path = BitmapHelper.getPath(mContext, remoteViewsService, uri);
            int rotation = neededRotation(new File(path));
            if(rotation != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate((float)rotation);
                dstBmp = Bitmap.createBitmap(srcBmp, x, y, width, height, matrix, true);
            } else {
                dstBmp = Bitmap.createBitmap(srcBmp, x, y, width, height);
            }
        }

        return dstBmp;
    }

    public static Bitmap getFullImage(Context mContext, Uri uri, int reqWidth, int reqHeight) {
        boolean TYPE_IMAGE = false;
        boolean TYPE_VIDEO = true;
        Bitmap dstBmp = null;
        boolean type = false;
        String extension = MimeTypeMap.getFileExtensionFromUrl(uri.getPath());
        if(extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            if(mime.getMimeTypeFromExtension(extension).contains("video/")) {
                type = true;
            }
        }

        if(!type) {
            dstBmp = decodeSampledFromUri(mContext, uri, reqWidth, reqHeight);
            int rotation = neededRotation(new File(uri.getPath()));
            if(rotation != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate((float)rotation);
                dstBmp = Bitmap.createBitmap(dstBmp, 0, 0, dstBmp.getWidth(), dstBmp.getHeight(), matrix, true);
            }
        } else if(type) {
            Bitmap srcBmp = ThumbnailUtils.createVideoThumbnail(uri.getPath(), 1);
            if(srcBmp == null) {
                srcBmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.image_broken);
            }

            dstBmp = createVideoThumbnail(mContext, srcBmp, reqWidth, reqHeight);
        }

        return dstBmp;
    }

    public static int neededRotation(File ff) {
        try {
            ExifInterface exif = new ExifInterface(ff.getAbsolutePath());
            int orientation = exif.getAttributeInt("Orientation", 1);
            return orientation == 8?270:(orientation == 3?180:(orientation == 6?90:0));
        } catch (IOException var3) {
            var3.printStackTrace();
            return 0;
        }
    }

    public static Bitmap decodeSampledFromUri(Context mContext, Uri uri, int reqWidth, int reqHeight) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        InputStream inputStream = null;
        InputStream inputStreamSampled = null;

        Bitmap var8;
        try {
            inputStream = mContext.getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(inputStream, (Rect)null, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            inputStreamSampled = mContext.getContentResolver().openInputStream(uri);
            Bitmap var7 = BitmapFactory.decodeStream(inputStreamSampled, (Rect)null, options);
            return var7;
        } catch (IOException var18) {
            Log.e("BitmapUtils", "Error");
            var8 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.image_broken);
        } catch (Exception e) {
            var8 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.image_broken);
        } finally {
            try {
                inputStream.close();
                inputStreamSampled.close();
            } catch (NullPointerException | IOException var17) {
                Log.e("BitmapUtils", "Failed to close streams");
            }

        }

        return var8;
    }

    public static Bitmap decodeSampledBitmapFromResourceMemOpt(InputStream inputStream, int reqWidth, int reqHeight) {
        byte[] byteArr = new byte[0];
        byte[] buffer = new byte[1024];
        int count = 0;

        try {
            int len;
            while((len = inputStream.read(buffer)) > -1) {
                if(len != 0) {
                    if(count + len > byteArr.length) {
                        byte[] newbuf = new byte[(count + len) * 2];
                        System.arraycopy(byteArr, 0, newbuf, 0, count);
                        byteArr = newbuf;
                    }

                    System.arraycopy(buffer, 0, byteArr, count, len);
                    count += len;
                }
            }

            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(byteArr, 0, count, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Config.ARGB_8888;
            return BitmapFactory.decodeByteArray(byteArr, 0, count, options);
        } catch (Exception var8) {
            Log.d("BitmapUtils", "Explosion processing upgrade!", var8);
            return null;
        }
    }

    public static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) throws FileNotFoundException {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if(height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;

            int halfWidth;
            for(halfWidth = width / 2; halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth; inSampleSize *= 2) {}

            while(halfHeight / inSampleSize > reqHeight * 2 || halfWidth / inSampleSize > reqWidth * 2) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap drawTextToBitmap(Context mContext, Bitmap bitmap, String text, Integer offsetX, Integer offsetY, float textSize, Integer textColor) {
        Resources resources = mContext.getResources();
        float scale = resources.getDisplayMetrics().density;
        Config bitmapConfig = bitmap.getConfig();
        if(bitmapConfig == null) {
            bitmapConfig = Config.RGB_565;
        }

        if(!bitmap.isMutable()) {
            bitmap = bitmap.copy(bitmapConfig, true);
        }

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(1);
        paint.setColor(textColor.intValue());
        textSize = (float)((int)(textSize * scale * (float)bitmap.getWidth() / 100.0F));
        textSize = textSize < 15.0F?textSize:15.0F;
        paint.setTextSize(textSize);
        paint.setShadowLayer(1.0F, 0.0F, 1.0F, -1);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int x;
        if(offsetX == null) {
            x = (bitmap.getWidth() - bounds.width()) / 2;
        } else if(offsetX.intValue() >= 0) {
            x = offsetX.intValue();
        } else {
            x = bitmap.getWidth() - bounds.width() - offsetX.intValue();
        }

        int y;
        if(offsetY == null) {
            y = (bitmap.getHeight() - bounds.height()) / 2;
        } else if(offsetY.intValue() >= 0) {
            y = offsetY.intValue();
        } else {
            y = bitmap.getHeight() - bounds.height() + offsetY.intValue();
        }

        canvas.drawText(text, (float)x, (float)y, paint);
        return bitmap;
    }

    public static Uri getUri(Context mContext, int resource_id) {
        Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + resource_id);
        return uri;
    }

    private static Bitmap scaleImage(Context mContext, Bitmap bitmap, int reqWidth, int reqHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int boundingX = dpToPx(mContext, reqWidth);
        int boundingY = dpToPx(mContext, reqHeight);
        float xScale = (float)boundingX / (float)width;
        float yScale = (float)boundingY / (float)height;
        float scale = xScale >= yScale?xScale:yScale;
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return scaledBitmap;
    }

    public static Bitmap rotateImage(Bitmap bitmap, String filePath) {
        Bitmap resultBitmap = bitmap;

        try {
            ExifInterface exifInterface = new ExifInterface(filePath);
            int orientation = exifInterface.getAttributeInt("Orientation", 1);
            Matrix matrix = new Matrix();
            if(orientation == 6) {
                matrix.postRotate(6.0F);
            } else if(orientation == 3) {
                matrix.postRotate(3.0F);
            } else if(orientation == 8) {
                matrix.postRotate(8.0F);
            }

            resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (Exception var6) {
            Log.d("AndroidTouchGallery", "Could not rotate the image");
        }

        return resultBitmap;
    }

    public static InputStream getBitmapInputStream(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 0, bos);
        byte[] bitmapdata = bos.toByteArray();
        ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
        return bs;
    }

    public static Bitmap createVideoThumbnail(Context mContext, Bitmap video, int width, int height) {
        video = ThumbnailUtils.extractThumbnail(video, width, height);
        Bitmap thumbnail = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(thumbnail);
        canvas.drawBitmap(video, 0.0F, 0.0F, (Paint)null);
        int markSize = calculateVideoMarkSize(width, height);
        Bitmap mark = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.play_no_bg), markSize, markSize);
        int x = video.getWidth() / 2 - mark.getWidth() / 2;
        int y = video.getHeight() / 2 - mark.getHeight() / 2;
        canvas.drawBitmap(mark, (float)x, (float)y, (Paint)null);
        return thumbnail;
    }

    private static int calculateVideoMarkSize(int width, int height) {
        int referredSize = Math.min(width, height);
        int result = referredSize / 9;
        if(result < 30) {
            result = 30;
        }

        if(result > 200) {
            result = 200;
        }

        return result;
    }

    public static int dpToPx(Context mContext, int dp) {
        float density = mContext.getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }

    public static int getDominantColor(Bitmap source) {
        return getDominantColor(source, true);
    }

    public static int getDominantColor(Bitmap source, boolean applyThreshold) {
        if(source == null) {
            return Color.argb(255, 255, 255, 255);
        } else {
            int[] colorBins = new int[36];
            int maxBin = -1;
            float[] sumHue = new float[36];
            float[] sumSat = new float[36];
            float[] sumVal = new float[36];
            float[] hsv = new float[3];
            int height = source.getHeight();
            int width = source.getWidth();
            int[] pixels = new int[width * height];
            source.getPixels(pixels, 0, width, 0, 0, width, height);

            for(int row = 0; row < height; row += 2) {
                for(int col = 0; col < width; col += 2) {
                    int c = pixels[col + row * width];
                    Color.colorToHSV(c, hsv);
                    if(!applyThreshold || hsv[1] > 0.05F && hsv[2] > 0.35F) {
                        int bin = (int)Math.floor((double)(hsv[0] / 10.0F));
                        sumHue[bin] += hsv[0];
                        sumSat[bin] += hsv[1];
                        sumVal[bin] += hsv[2];
                        ++colorBins[bin];
                        if(maxBin < 0 || colorBins[bin] > colorBins[maxBin]) {
                            maxBin = bin;
                        }
                    }
                }
            }

            if(maxBin < 0) {
                return Color.argb(255, 255, 255, 255);
            } else {
                hsv[0] = sumHue[maxBin] / (float)colorBins[maxBin];
                hsv[1] = sumSat[maxBin] / (float)colorBins[maxBin];
                hsv[2] = sumVal[maxBin] / (float)colorBins[maxBin];
                return Color.HSVToColor(hsv);
            }
        }
    }

    public static void changeImageViewDrawableColor(ImageView imageView, int color) {
        imageView.getDrawable().mutate().setColorFilter(color, Mode.SRC_ATOP);
    }
}
