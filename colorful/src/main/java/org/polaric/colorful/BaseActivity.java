package org.polaric.colorful;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (onGetPermissionCallback != null){
                onGetPermissionCallback.onGetPermission();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (!shouldShowRequestPermissionRationale(permissions[0])){
                    showPermissionSettingDialog(requestCode);
                } else {
                    makeToast(this, R.string.permission_denied_try_again_after_set);
                }
            } else {
                makeToast(this, R.string.permission_denied_try_again_after_set);
            }
        }
    }

    private void showPermissionSettingDialog(int requestCode) {
        String msg = String.format(getString(R.string.set_permission_in_setting),
                PermissionUtils.getPermissionName(this, requestCode));
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
}
