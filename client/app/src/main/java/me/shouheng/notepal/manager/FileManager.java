package me.shouheng.notepal.manager;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import me.shouheng.commons.utils.LogUtils;
import me.shouheng.commons.utils.ToastUtils;
import me.shouheng.data.DBConfig;
import me.shouheng.data.ModelFactory;
import me.shouheng.data.entity.Attachment;
import me.shouheng.notepal.BuildConfig;
import me.shouheng.notepal.Constants;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;

import static java.lang.Long.parseLong;
import static me.shouheng.notepal.Constants.SHARE_IMAGE_FILE_PATH;

/**
 * Created by WngShhng (shouheng2015@gmail.com) on 2017/4/7.
 * Refactored by WngShhng (shouheng2015@gmail.com) on2018/12/2.
 */
public class FileManager {

    private static final String EXTERNAL_STORAGE_FOLDER = "NotePal";
    private static final String EXTERNAL_STORAGE_BACKUP_DIR = "Backup";
    private final static String DATE_FORMAT_SORTABLE = "yyyyMMdd_HHmmss_SSS";
    private static final String ANSI_INVALID_CHARACTERS = "\\/:*?\"<>|";

    private static boolean isStorageWritable() {
        boolean isExternalStorageAvailable;
        boolean isExternalStorageWritable;
        String state = Environment.getExternalStorageState();
        switch (state) {
            case Environment.MEDIA_MOUNTED:
                isExternalStorageAvailable = true;
                isExternalStorageWritable = true;
                break;
            case Environment.MEDIA_MOUNTED_READ_ONLY:
                isExternalStorageAvailable = true;
                isExternalStorageWritable = false;
                break;
            default:
                isExternalStorageAvailable = false;
                isExternalStorageWritable = false;
                break;
        }
        return isExternalStorageAvailable && isExternalStorageWritable;
    }

    // region Name
    public static String getDefaultFileName(String extension) {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_SORTABLE, Locale.getDefault());
        String name = sdf.format(now.getTime());
        name += extension != null ? extension : "";
        return removeInvalidCharacters(name);
    }

    public static String getNameFromUri(Context mContext, Uri uri) {
        String fileName = "";
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } catch (Exception e) {
                    LogUtils.e("Error managing diskk cache", e);
                }
            } else {
                fileName = uri.getLastPathSegment();
            }
        } catch (SecurityException e) {
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return fileName;
    }

    public static String getFileName(File file) {
        return getFileName(file.getPath());
    }

    public static String getFileName(String path) {
        String name = path.substring(path.lastIndexOf(System.getProperty("file.separator")) + 1, path.length());
        return removeInvalidCharacters(name);
    }

    private static String removeInvalidCharacters(final String fileName) {
        String fixedUpString = Uri.decode(fileName);
        for (int i = 0; i < ANSI_INVALID_CHARACTERS.length(); i++) {
            fixedUpString = fixedUpString.replace(ANSI_INVALID_CHARACTERS.charAt(i), '_');
        }
        return Uri.encode(fixedUpString);
    }
    // endregion

    // region Path
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {
        if (uri == null) return null;

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), parseLong(DocumentsContract.getDocumentId(uri)));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = FileManager.createURI(type);

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
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
    // endregion

    // region Size
    public static long getSize(File directory) {
        StatFs statFs = new StatFs(directory.getAbsolutePath());
        long blockSize = 0;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = statFs.getBlockSizeLong();
            } else {
                blockSize = statFs.getBlockSize();
            }
            // Can't understand why on some devices this fails
        } catch (NoSuchMethodError e) {
            LogUtils.e("Mysterious error", e);
        }
        return getSize(directory, blockSize);
    }

    private static long getSize(File directory, long blockSize) {
        if (blockSize == 0) {
            throw new InvalidParameterException("Blocksize can't be 0");
        }
        File[] files = directory.listFiles();
        if (files != null) {

            // space used by directory itself
            long size = directory.length();

            for (File file : files) {
                if (file.isDirectory()) {
                    // space used by subdirectory
                    size += getSize(file, blockSize);
                } else {
                    // file size need to rounded up to full block sizes
                    // (not a perfect function, it adds additional block to 0 sized files
                    // and file who perfectly fill their blocks)
                    size += (file.length() / blockSize + 1) * blockSize;
                }
            }
            return size;
        } else {
            return 0;
        }
    }

    public static byte[] getFileBytes(File file) throws IOException, RemoteException {
        return getFileBytes(file, 0, (int) file.length());
    }

    private static byte[] getFileBytes(File file, final int offset, final int size) throws IOException, RemoteException {
        final FileInputStream fis = new FileInputStream(file);
        final ByteArrayOutputStream memorySteam = new ByteArrayOutputStream(size);
        int ret = copyStreamContents(offset, size, fis, memorySteam);
        return memorySteam.toByteArray();
    }

    private static int copyStreamContents(final long offset,
                                          final int size,
                                          final InputStream input,
                                          final OutputStream output) throws IOException {
        byte[] buffer = new byte[size];
        int count = 0, n;
        final long skipAmount = input.skip(offset);
        if (skipAmount != offset) {
            throw new RuntimeException(String.format(Locale.getDefault(),
                    "Unable to skip in the input stream actual %d, expected %d", skipAmount, offset));
        }
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
    // endregion

    // region MimeType

    /**
     * Get mime type of given uri.
     *
     * @param mContext the context
     * @param uri the uri belows to the file stored in the app file system, so the mime type got
     *            is the mime type of file in file system.
     * @return the mime type
     */
    public static String getMimeType(Context mContext, Uri uri) {
        ContentResolver resolver = mContext.getContentResolver();
        String mimeType = resolver.getType(uri);
        if (TextUtils.isEmpty(mimeType)) mimeType = getMimeType(uri.toString());
        return mimeType;
    }

    private static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    /**
     * Get file mime type and translate it into the mime type of this application.
     *
     * @param mContext the context
     * @param uri the uri of attachment
     * @return the mime type of this application
     */
    public static String getMimeTypeInternal(Context mContext, Uri uri) {
        String mimeType = getMimeType(mContext, uri);
        mimeType = getMimeTypeInternal(mimeType);
        return mimeType;
    }

    private static String getMimeTypeInternal(String mimeType) {
        if (mimeType != null) {
            if (mimeType.contains("image/")) {
                mimeType = Constants.MIME_TYPE_IMAGE;
            } else if (mimeType.contains("audio/")) {
                mimeType = Constants.MIME_TYPE_AUDIO;
            } else if (mimeType.contains("video/")) {
                mimeType = Constants.MIME_TYPE_VIDEO;
            } else {
                mimeType = Constants.MIME_TYPE_FILES;
            }
        }
        return mimeType;
    }

    public static Uri createURI(String type){
        switch (type) {
            case "image":
                return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            case "video":
                return  MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            case "audio":
                return  MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        return null;
    }
    // endregion

    // region extension
    /**
     * Get file extension from file name.
     *
     * @param fileName file name
     * @return the extension
     */
    private static String getFileExtension(String fileName) {
        if (TextUtils.isEmpty(fileName)) return "";
        String extension = "";
        int index = fileName.lastIndexOf(".");
        if (index != -1) extension = fileName.substring(index, fileName.length());
        return extension;
    }

    /**
     * Get file extension from uri.
     *
     * @param mContext the context
     * @param uri the uri
     * @return the extension
     */
    public static String getFileExtension(Context mContext, Uri uri) {
        String extension;
        if (TextUtils.isEmpty(extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString()))
                && TextUtils.isEmpty(extension = getFileExtension(getPath(mContext, uri)))) {
            String mimeType = getMimeType(mContext, uri);
            if (!TextUtils.isEmpty(mimeType)) {
                String subtype = mimeType.split("/")[1];
                LogUtils.d(mimeType);
                return "." + subtype;
            } else {
                return "";
            }
        } else {
            return extension;
        }
    }
    // endregion

    // region thumbnail
    public static Uri getThumbnailUri(Context mContext, Uri uri) {
        String mimeType = getMimeType(uri.toString());
        if (!TextUtils.isEmpty(mimeType)) {
            String type = mimeType.split("/")[0];
            String subtype = mimeType.split("/")[1];
            LogUtils.d(mimeType);
            switch (type) {
                case "image":
                case "video":
                    break;
                case "audio":
                    uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.play);
                    break;
                default:
                    if ("x-vcard".equals(subtype)) {
                        return Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.vcard);
                    } else {
                        return getThumbnailUri(mContext, uri, subtype);
                    }
            }
        } else {
            String extension, path;
            extension = TextUtils.isEmpty(path = getPath(mContext, uri)) ?  getFileExtension(uri.toString()) : getFileExtension(path);
            return getThumbnailUri(mContext, uri, extension);
        }
        return uri;
    }

    private static Uri getThumbnailUri(Context mContext, Uri uri, String extension) {
        switch (extension) {
            case "mp3":case "wav":case "aac":case "wma":// audio
                uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.play);
                break;
            case "avi":case "mov":case "wmv":case "3gp":case "rmvb":case "flv":case "mpeg":case "mp4":
                return uri;
            default:
                uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.files);
                break;
        }
        return uri;
    }
    // endregion

    public static File createNewAttachmentFile(Context context, String extension) {
        File file = null;
        if (isStorageWritable()) {
            file = new File(context.getExternalFilesDir(null), getDefaultFileName(extension));
        }
        return file;
    }

    public static Attachment createAttachmentFromUri(Context mContext, Uri uri) {
        return createAttachmentFromUri(mContext, uri, false);
    }


    /**
     * Create an attachment from the uri
     *
     * @param mContext the context to get the io stream
     * @param uri the uri of file
     * @param moveSource whether move the source file, the original file will be copied to
     *                   the app private storage and deleted if set true.
     * @return the attachment got
     */
    private static Attachment createAttachmentFromUri(Context mContext, Uri uri, boolean moveSource) {
        String name = FileManager.getNameFromUri(mContext, uri);
        String extension = FileManager.getFileExtension(name).toLowerCase(Locale.getDefault());

        /*
         * The name got from last step is the {@link OpenableColumns.DISPLAY_NAME} value.
         * That means, for a mp3 file "Music.mp3", we may only get the "Music", so the extension can be empty.
         * To avoid the extension empty, we should check it and try to get it from the mime type.
         */
        if (TextUtils.isEmpty(extension)) extension = getFileExtension(mContext, uri);

        File file;
        if (moveSource) {
            file = createNewAttachmentFile(mContext, extension);
            try {
                moveFile(new File(uri.getPath()), file);
            } catch (IOException e) {
                LogUtils.e("Can't move file " + uri.getPath());
            }
        } else {
            file = FileManager.createExternalStoragePrivateFile(mContext, uri, extension);
        }

        /* Create attachment object as return value. */
        Attachment mAttachment = ModelFactory.getAttachment();
        if (file != null) {
            mAttachment.setUri(getUriFromFile(mContext, file));
            mAttachment.setMineType(getMimeTypeInternal(mContext, uri));
            mAttachment.setName(name);
            mAttachment.setSize(file.length());
            mAttachment.setPath(file.getPath());
        }

        return mAttachment;
    }

    private static File createExternalStoragePrivateFile(Context mContext, Uri uri, String extension) {
        if (!isStorageWritable()) {
            ToastUtils.makeToast(R.string.text_storage_unavailable);
            return null;
        }
        File file = createNewAttachmentFile(mContext, extension);
        InputStream is;
        OutputStream os;
        try {
            is = mContext.getContentResolver().openInputStream(uri);
            os = new FileOutputStream(file);
            copyFile(is, os);
        } catch (IOException e) {
            try {
                is = new FileInputStream(FileManager.getPath(mContext, uri));
                os = new FileOutputStream(file);
                copyFile(is, os);
            } catch (NullPointerException e1) {
                try {
                    is = new FileInputStream(uri.getPath());
                    os = new FileOutputStream(file);
                    copyFile(is, os);
                } catch (FileNotFoundException e2) {
                    LogUtils.e("Error writing " + file, e2);
                    file = null;
                }
            } catch (FileNotFoundException e2) {
                LogUtils.e("Error writing " + file, e2);
                file = null;
            }
        }
        return file;
    }

    // region file operations
    public static void moveFile(File srcFile, File destFile) throws IOException {
        if (srcFile == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destFile == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (!srcFile.exists()) {
            throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
        }
        if (srcFile.isDirectory()) {
            throw new IOException("Source '" + srcFile + "' is a directory");
        }
        if (destFile.exists()) {
            throw new IOException("Destination '" + destFile + "' already exists");
        }
        if (destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' is a directory");
        }
        boolean rename = srcFile.renameTo(destFile);
        if (!rename) {
            copyFile(srcFile, destFile );
            if (!srcFile.delete()) {
                FileUtils.deleteQuietly(destFile);
                throw new IOException("Failed to delete original file '" + srcFile +
                        "' after copy to '" + destFile + "'");
            }
        }
    }

    public static boolean delete(Context mContext, String name) {
        boolean res = false;
        if (!isStorageWritable()) {
            ToastUtils.makeToast(R.string.text_storage_unavailable);
            return false;
        }
        File file = new File(name);
        if (file.isFile()) {
            res = file.delete();
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file2 : files) {
                res = delete(mContext, file2.getAbsolutePath());
            }
            res = file.delete();
        }
        return res;
    }

    public static boolean copyFile(File source, File destination) {
        try {
            return copyFile(new FileInputStream(source), new FileOutputStream(destination));
        } catch (FileNotFoundException e) {
            LogUtils.e("Error copying file", e);
            return false;
        }
    }

    public static boolean copyFile(InputStream is, OutputStream os) {
        boolean res = false;
        byte[] data = new byte[1024];
        int len;
        try {
            while ((len = is.read(data)) > 0) {
                os.write(data, 0, len);
            }
            is.close();
            os.close();
            res = true;
        } catch (IOException e) {
            LogUtils.e("Error copying file", e);
        }
        return res;
    }
    // endregion

    public static Uri getUriFromFile(Context context, File file) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
        } else {
            return Uri.fromFile(file);
        }
    }

    @Nullable
    public static Bitmap getImageFromAssetsFile(Context context, String fileName){
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Nullable
    public static Uri getShareImageUri(Bitmap bm, String picName) {
        if (!isStorageWritable()) {
            return null;
        }

        try {
            String dir = PalmApp.getContext().getExternalFilesDir(null)
                    + File.separator + SHARE_IMAGE_FILE_PATH  + File.separator + picName + ".jpg";
            File f = new File(dir);
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
                FileOutputStream out = new FileOutputStream(f);
                bm.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
            }
            return getUriFromFile(PalmApp.getContext(), f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // region save image external
    /**
     * 保存图标到相册中
     *
     * @param context 上下文
     * @param bmp 图片
     * @param isPng 是否是png格式的图片
     * @param onSavedToGalleryListener the callback of saving event
     * @return 是否成功执行保存操作
     */
    public static boolean saveImageToGallery(Context context, Bitmap bmp, boolean isPng, OnSavedToGalleryListener onSavedToGalleryListener) {
        LogUtils.d("saveImageToGallery: " + bmp);
        if (bmp == null) return false;
        File appDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), context.getString(R.string.app_name));
        if (!appDir.exists()) {
            if (!appDir.mkdir()) {
                return false;
            }
        }
        String fileName = System.currentTimeMillis() + (isPng ? ".png" : ".jpg");
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            if (isPng) {
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } else {
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            }
            bmp.recycle();
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            LogUtils.d("saveImageToGallery: FileNotFoundException");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            LogUtils.d("saveImageToGallery: IOException");
            e.printStackTrace();
            return false;
        }
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            LogUtils.d("saveImageToGallery: FileNotFoundException MediaStore");
            e.printStackTrace();
            return false;
        }
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, getUriFromFile(context, appDir)));
        if (onSavedToGalleryListener != null) {
            onSavedToGalleryListener.OnSavedToGallery(file);
        }
        return true;
    }

    public static void saveDrawableToGallery(Context context, @DrawableRes int drawableRes, OnSavedToGalleryListener onSavedToGalleryListener) {
        Resources res = context.getResources();
        BitmapDrawable d = (BitmapDrawable) res.getDrawable(drawableRes);
        Bitmap img = d.getBitmap();
        FileManager.saveImageToGallery(context, img, true, onSavedToGalleryListener);
    }
    // endregion

    // region export and import
    public static File getExternalStoragePublicDir() {
        String path = Environment.getExternalStorageDirectory() + File.separator + EXTERNAL_STORAGE_FOLDER + File.separator;
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public static File getHtmlExportDir() {
        File dir = new File(getExternalStoragePublicDir(), Constants.HTML_EXPORT_DIR_NAME);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public static File getTextExportDir() {
        File dir = new File(getExternalStoragePublicDir(), Constants.TEXT_EXPORT_DIR_NAME);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public static File getExternalBackupRootDir() {
        File backupDir = new File(getExternalStoragePublicDir(), EXTERNAL_STORAGE_BACKUP_DIR);
        if (!backupDir.exists()) backupDir.mkdirs();
        return backupDir;
    }

    public static File getExternalBackupDir(String backupName) {
        File root = getExternalBackupRootDir();
        File backupDir = new File(root, backupName);
        if (!backupDir.exists()) backupDir.mkdirs();
        return backupDir;
    }

    public static File getExternalFilesBackupDir(File backupDir) {
        File attachmentsDir = FileManager.getAttachmentDir(PalmApp.getContext());
        return new File(backupDir, attachmentsDir.getName());
    }

    public static File getAttachmentDir(Context mContext) {
        return mContext.getExternalFilesDir(null);
    }

    public static File copyToBackupDir(File backupDir, File file) {
        if (!isStorageWritable()) {
            return null;
        }
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
        File destination = new File(backupDir, file.getName());
        copyFile(file, destination);
        return destination;
    }

    public static File getDatabaseFile(Context context) {
        return context.getDatabasePath(DBConfig.DATABASE_NAME);
    }

    public static File getPreferencesFile(Context mContext) {
        return new File(getPreferencesPath(mContext));
    }

    private static String getPreferencesPath(Context mContext) {
        File appData = mContext.getFilesDir().getParentFile();
        return appData + System.getProperty("file.separator")
                + "shared_prefs"
                + System.getProperty("file.separator")
                + getPreferencesName(mContext);
    }

    public static String getPreferencesName(Context mContext) {
        return mContext.getApplicationContext().getPackageName() + "_preferences.xml";
    }
    // endregion

    public interface OnSavedToGalleryListener {
        void OnSavedToGallery(File file);
    }
}
