package org.polaric.colorful;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by wang shouheng on 2018/1/26. */
public class BaseActivity extends AppCompatActivity {

    private static Toast toast;

    private PermissionUtils.OnGetPermissionCallback onGetPermissionCallback;

    public void setOnGetPermissionCallback(PermissionUtils.OnGetPermissionCallback onGetPermissionCallback) {
        this.onGetPermissionCallback = onGetPermissionCallback;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (onGetPermissionCallback != null){
                onGetPermissionCallback.onGetPermission();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                // Add array length check logic to avoid ArrayIndexOutOfBoundsException
                if (permissions.length > 0 && !shouldShowRequestPermissionRationale(permissions[0])){
                    showPermissionSettingDialog(requestCode);
                } else {
                    makeToast(this, getToastMessage(requestCode));
                }
            } else {
                makeToast(this, getToastMessage(requestCode));
            }
        }
    }

    private void showPermissionSettingDialog(int requestCode) {
        String permissionName = PermissionUtils.getPermissionName(this, requestCode);
        String msg = String.format(getString(R.string.set_permission_in_setting), permissionName);
        new AlertDialog.Builder(this)
                .setTitle(R.string.setting_permission)
                .setMessage(msg)
                .setPositiveButton(R.string.to_set, (dialog, which) -> toSetPermission())
                .setNegativeButton(R.string.text_cancel, null)
                .create()
                .show();
    }

    private void toSetPermission() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", Util.getPackageName(BaseActivity.this), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private static void makeToast(Context context, @StringRes int msgRes) {
        if (toast == null){
            toast = Toast.makeText(context.getApplicationContext(), msgRes, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            toast.setText(msgRes);
            toast.show();
        }
    }

    private static void makeToast(Context context, String msg) {
        if (toast == null){
            toast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            toast.setText(msg);
            toast.show();
        }
    }

    /**
     * Get the permission toast message according to request code. If the permission name can be found,
     * we will show the permission name in the message, otherwise show the default message.
     *
     * @param requestCode the request code
     * @return the message to toast
     */
    private String getToastMessage(int requestCode) {
        String permissionName = PermissionUtils.getPermissionName(this, requestCode);
        String defName = getString(R.string.permission_default_permission_name);
        if (defName.equals(permissionName)) {
            return getString(R.string.permission_denied_try_again_after_set);
        } else {
            return String.format(getString(R.string.permission_denied_try_again_after_set_given_permission), permissionName);
        }
    }
}
