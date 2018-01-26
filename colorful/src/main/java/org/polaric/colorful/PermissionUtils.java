package org.polaric.colorful;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


/**
 * Created by wang shouheng on 2017/12/5.*/
public class PermissionUtils {

    private static final int REQUEST_PERMISSION_STORAGE = 10005;
    private static final int REQUEST_PERMISSION_PHONE_STATE = 10006;
    private static final int REQUEST_PERMISSION_LOCATION = 10007;
    private static final int REQUEST_PERMISSION_MICROPHONE = 10008;
    private static final int REQUEST_PERMISSION_SMS = 10009;
    private static final int REQUEST_PERMISSION_SENSORS = 10010;
    private static final int REQUEST_PERMISSION_CONTACTS = 10011;
    private static final int REQUEST_PERMISSION_CAMERA = 10012;
    private static final int REQUEST_PERMISSION_CALENDAR = 10013;

    public static <T extends BaseActivity> void checkStoragePermission(@NonNull T activity, OnGetPermissionCallback callback) {
        checkPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSION_STORAGE, callback);
    }

    public static <T extends BaseActivity> void checkPhonePermission(@NonNull T activity, OnGetPermissionCallback callback){
        checkPermission(activity, Manifest.permission.READ_PHONE_STATE, REQUEST_PERMISSION_PHONE_STATE, callback);
    }

    public static <T extends BaseActivity> void checkLocationPermission(@NonNull T activity, OnGetPermissionCallback callback){
        checkPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_PERMISSION_LOCATION, callback);
    }

    public static <T extends BaseActivity> void checkRecordPermission(@NonNull T activity, OnGetPermissionCallback callback){
        checkPermission(activity, Manifest.permission.RECORD_AUDIO, REQUEST_PERMISSION_MICROPHONE, callback);
    }

    public static <T extends BaseActivity> void checkSmsPermission(@NonNull T activity, OnGetPermissionCallback callback) {
        checkPermission(activity, Manifest.permission.SEND_SMS, REQUEST_PERMISSION_SMS, callback);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    public static <T extends BaseActivity> void checkSensorsPermission(@NonNull T activity, OnGetPermissionCallback callback) {
        checkPermission(activity, Manifest.permission.BODY_SENSORS, REQUEST_PERMISSION_SENSORS, callback);
    }

    public static <T extends BaseActivity> void checkContactsPermission(@NonNull T activity, OnGetPermissionCallback callback) {
        checkPermission(activity, Manifest.permission.READ_CONTACTS, REQUEST_PERMISSION_CONTACTS, callback);
    }

    public static <T extends BaseActivity> void checkCameraPermission(@NonNull T activity, OnGetPermissionCallback callback) {
        checkPermission(activity, Manifest.permission.CAMERA, REQUEST_PERMISSION_CAMERA, callback);
    }

    public static <T extends BaseActivity> void checkCalendarPermission(@NonNull T activity, OnGetPermissionCallback callback) {
        checkPermission(activity, Manifest.permission.READ_CALENDAR, REQUEST_PERMISSION_CALENDAR, callback);
    }

    private static <T extends BaseActivity> void checkPermission(@NonNull T activity, @NonNull String permission, int requestCode, OnGetPermissionCallback callback) {
        activity.setOnGetPermissionCallback(callback);
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        } else {
            if (callback != null) {
                callback.onGetPermission();
            }
        }
    }

    public static String getPermissionName(Context context, int requestCode) {
        switch (requestCode){
            case REQUEST_PERMISSION_STORAGE:
                return context.getString(R.string.permission_storage_permission);
            case REQUEST_PERMISSION_LOCATION:
                return context.getString(R.string.permission_location_permission);
            case REQUEST_PERMISSION_MICROPHONE:
                return context.getString(R.string.permission_microphone_permission);
            case REQUEST_PERMISSION_PHONE_STATE:
                return context.getString(R.string.permission_phone_permission);
            case REQUEST_PERMISSION_SMS:
                return context.getString(R.string.permission_sms_permission);
            case REQUEST_PERMISSION_SENSORS:
                return context.getString(R.string.permission_sensor_permission);
            case REQUEST_PERMISSION_CONTACTS:
                return context.getString(R.string.permission_contacts_permission);
            case REQUEST_PERMISSION_CAMERA:
                return context.getString(R.string.permission_camera_permission);
            case REQUEST_PERMISSION_CALENDAR:
                return context.getString(R.string.permission_calendar_permission);
        }
        return null;
    }

    public interface OnGetPermissionCallback{
        void onGetPermission();
    }
}