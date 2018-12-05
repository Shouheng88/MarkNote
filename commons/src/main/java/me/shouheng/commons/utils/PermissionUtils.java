package me.shouheng.commons.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import me.shouheng.commons.R;
import me.shouheng.commons.activity.PermissionActivity;

/**
 * The wrapped utils class to request for permission in runtime.
 * NOTE: The weakness is that the activity used to check permission must extends {@link PermissionActivity}
 *
 * Created by WngShhng on 2017/12/5.
 */
public class PermissionUtils {

    private final static int REQUEST_PERMISSIONS = 0xFF00;

    /**
     * The permission groups with int value.
     */
    public static class Permission {
        public final static int STORAGE = 0xFF01;
        public final static int PHONE_STATE = 0xFF02;
        public final static int LOCATION = 0xFF03;
        public final static int MICROPHONE = 0xFF04;
        public final static int SMS = 0xFF05;
        public final static int SENSORS = 0xFF06;
        public final static int CONTACTS = 0xFF07;
        public final static int CAMERA = 0xFF08;
        public final static int CALENDAR = 0xFF09;
    }

    /**
     * Check storage permission.
     *
     * @param activity the base activity
     * @param callback the callback of checking result
     * @param <T> the activity type, must implement {@link PermissionActivity}
     */
    public static <T extends PermissionActivity> void checkStoragePermission(
            @NonNull T activity, OnGetPermissionCallback callback) {
        checkPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, Permission.STORAGE, callback);
    }

    /**
     * Check phone permission.
     *
     * @param activity the base activity
     * @param callback the callback of checking result
     * @param <T> the activity type, must implement {@link PermissionActivity}
     */
    public static <T extends PermissionActivity> void checkPhonePermission(
            @NonNull T activity, OnGetPermissionCallback callback) {
        checkPermission(activity, Manifest.permission.READ_PHONE_STATE, Permission.PHONE_STATE, callback);
    }

    /**
     * Check location permission.
     *
     * @param activity the base activity
     * @param callback the callback of checking result
     * @param <T> the activity type, must implement {@link PermissionActivity}
     */
    public static <T extends PermissionActivity> void checkLocationPermission(
            @NonNull T activity, OnGetPermissionCallback callback) {
        checkPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION, Permission.LOCATION, callback);
    }

    /**
     * Check record permission.
     *
     * @param activity the base activity
     * @param callback the callback of checking result
     * @param <T> the activity type, must implement {@link PermissionActivity}
     */
    public static <T extends PermissionActivity> void checkRecordPermission(
            @NonNull T activity, OnGetPermissionCallback callback) {
        checkPermission(activity, Manifest.permission.RECORD_AUDIO, Permission.MICROPHONE, callback);
    }

    /**
     * Check sms permission.
     *
     * @param activity the base activity
     * @param callback the callback of checking result
     * @param <T> the activity type, must implement {@link PermissionActivity}
     */
    public static <T extends PermissionActivity> void checkSmsPermission(
            @NonNull T activity, OnGetPermissionCallback callback) {
        checkPermission(activity, Manifest.permission.SEND_SMS, Permission.SMS, callback);
    }

    /**
     * Check sensors permission.
     *
     * @param activity the base activity
     * @param callback the callback of checking result
     * @param <T> the activity type, must implement {@link PermissionActivity}
     */
    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    public static <T extends PermissionActivity> void checkSensorsPermission(
            @NonNull T activity, OnGetPermissionCallback callback) {
        checkPermission(activity, Manifest.permission.BODY_SENSORS, Permission.SENSORS, callback);
    }

    /**
     * Check contacts permission.
     *
     * @param activity the base activity
     * @param callback the callback of checking result
     * @param <T> the activity type, must implement {@link PermissionActivity}
     */
    public static <T extends PermissionActivity> void checkContactsPermission(
            @NonNull T activity, OnGetPermissionCallback callback) {
        checkPermission(activity, Manifest.permission.READ_CONTACTS, Permission.CONTACTS, callback);
    }

    /**
     * Check camera permission.
     *
     * @param activity the base activity
     * @param callback the callback of checking result
     * @param <T> the activity type, must implement {@link PermissionActivity}
     */
    public static <T extends PermissionActivity> void checkCameraPermission(
            @NonNull T activity, OnGetPermissionCallback callback) {
        checkPermission(activity, Manifest.permission.CAMERA, Permission.CAMERA, callback);
    }

    /**
     * Check calendar permission.
     *
     * @param activity the base activity
     * @param callback the callback of checking result
     * @param <T> the activity type, must implement {@link PermissionActivity}
     */
    public static <T extends PermissionActivity> void checkCalendarPermission(
            @NonNull T activity, OnGetPermissionCallback callback) {
        checkPermission(activity, Manifest.permission.READ_CALENDAR, Permission.CALENDAR, callback);
    }

    /**
     * The permission check method used to check one permission one time.
     *
     * @param activity the activity
     * @param permission the permission to check
     * @param requestCode the request code
     * @param callback the callback listener
     * @param <T> the activity type, must implement {@link PermissionActivity}
     */
    private static <T extends PermissionActivity> void checkPermission(
            @NonNull T activity, @NonNull String permission, int requestCode, OnGetPermissionCallback callback) {
        activity.setOnGetPermissionCallback(callback);
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        } else {
            if (callback != null) {
                callback.onGetPermission();
            }
        }
    }

    /**
     * Check multiple permissions at the same time.
     *
     * @param activity activity
     * @param permissions permissions to check, use fields from {@link Permission}
     * @param callback callback of permission result
     * @param <T> the activity type
     */
    public static <T extends PermissionActivity> void checkPermissions(
            @NonNull T activity, OnGetPermissionCallback callback, @NonNull Integer ...permissions) {
        activity.setOnGetPermissionCallback(callback);
        // Map permission code to permission name.
        int length = permissions.length;
        String[] standardPermissions = new String[length];
        for (int i=0; i<length; i++) {
            standardPermissions[i] = map(permissions[i]);
        }
        // Check every permission.
        int notGrantedCount = 0;
        for (String string : standardPermissions) {
            notGrantedCount += ((ContextCompat.checkSelfPermission(activity, string)
                    != PackageManager.PERMISSION_GRANTED) ? 1 : 0);
        }
        if (notGrantedCount == 0) {
            // All permissions granted.
            if (callback != null) {
                callback.onGetPermission();
            }
        } else {
            // At least one permission is not granted.
            ActivityCompat.requestPermissions(activity, standardPermissions, REQUEST_PERMISSIONS);
        }
    }

    /**
     * Map from permission code of {@link Permission} to name in {@link Manifest.permission}
     *
     * @param permission permission code
     * @return permission name
     */
    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    public static String map(int permission) {
        switch (permission) {
            case Permission.STORAGE: return Manifest.permission.WRITE_EXTERNAL_STORAGE;
            case Permission.PHONE_STATE: return Manifest.permission.READ_PHONE_STATE;
            case Permission.LOCATION: return Manifest.permission.ACCESS_FINE_LOCATION;
            case Permission.MICROPHONE: return Manifest.permission.RECORD_AUDIO;
            case Permission.SMS: return Manifest.permission.SEND_SMS;
            case Permission.SENSORS: return Manifest.permission.BODY_SENSORS;
            case Permission.CONTACTS: return Manifest.permission.READ_CONTACTS;
            case Permission.CAMERA: return Manifest.permission.CAMERA;
            case Permission.CALENDAR: return Manifest.permission.READ_CALENDAR;
            default:throw new IllegalArgumentException("Unrecognized permission code " + permission);
        }
    }

    /**
     * Get translated permission name.
     *
     * @param context the context to get string in xml
     * @param permission the permission from {@link Manifest.permission}
     * @return the translated permission
     */
    public static String name(Context context, String permission) {
        int resName;
        switch (permission) {
            case Manifest.permission.WRITE_EXTERNAL_STORAGE: resName = R.string.permission_storage_permission; break;
            case Manifest.permission.READ_PHONE_STATE: resName = R.string.permission_phone_permission; break;
            case Manifest.permission.ACCESS_FINE_LOCATION: resName = R.string.permission_location_permission; break;
            case Manifest.permission.RECORD_AUDIO: resName = R.string.permission_microphone_permission; break;
            case Manifest.permission.SEND_SMS: resName = R.string.permission_sms_permission; break;
            case Manifest.permission.BODY_SENSORS: resName = R.string.permission_sensor_permission; break;
            case Manifest.permission.READ_CONTACTS: resName = R.string.permission_contacts_permission; break;
            case Manifest.permission.CAMERA: resName = R.string.permission_camera_permission; break;
            case Manifest.permission.READ_CALENDAR: resName = R.string.permission_calendar_permission; break;
            default: throw new IllegalArgumentException("Unrecognized permission " + permission);
        }
        return context.getResources().getString(resName);
    }

    /**
     * Map multiple permission names to one single string used to display in toast and dialog.
     *
     * @param context the context used to get string in xml.
     * @param permissions the permission names
     * @return the single string of permission names connected by ','
     */
    public static String names(Context context, String[] permissions) {
        int length = permissions.length;
        StringBuilder names = new StringBuilder();
        for (int i=0; i<length; i++) {
            names.append(name(context, permissions[i]));
            if (i != length - 1) {
                names.append(", ");
            }
        }
        return names.toString();
    }

    /**
     * Get the package name
     *
     * @param context context to get package name
     * @return the package name
     */
    public static String getPackageName(Context context) {
        return context.getApplicationContext().getPackageName();
    }

    public interface OnGetPermissionCallback {
        void onGetPermission();
    }
}