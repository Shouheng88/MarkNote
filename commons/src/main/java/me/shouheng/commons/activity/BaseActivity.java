package me.shouheng.commons.view.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.umeng.analytics.MobclickAgent;

import me.shouheng.commons.R;
import me.shouheng.commons.tools.permission.OnGetPermissionCallback;
import me.shouheng.commons.tools.PalmUtils;
import me.shouheng.commons.tools.permission.PermissionUtils;
import me.shouheng.commons.tools.theme.ThemeUtils;
import me.shouheng.commons.tools.ToastUtils;

/**
 * Created by WngShhng on 2018/6/7.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private OnGetPermissionCallback onGetPermissionCallback;

    public void setOnGetPermissionCallback(OnGetPermissionCallback onGetPermissionCallback) {
        this.onGetPermissionCallback = onGetPermissionCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
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
                    ToastUtils.makeToast(getToastMessage(requestCode));
                }
            } else {
                ToastUtils.makeToast(getToastMessage(requestCode));
            }
        }
    }

    private void showPermissionSettingDialog(int requestCode) {
        String permissionName = PermissionUtils.getPermissionName(this, requestCode);
        String msg = String.format(getString(R.string.set_permission_in_setting), permissionName);
        new AlertDialog.Builder(this)
                .setTitle(R.string.setting_permission)
                .setMessage(msg)
                .setPositiveButton(R.string.text_to_set, (dialog, which) -> toSetPermission())
                .setNegativeButton(R.string.text_cancel, null)
                .create()
                .show();
    }

    private void toSetPermission() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", PalmUtils.getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    /**
     * Get the permission toast message according to request code. If the permission name can be found,
     * we will show the permission name in the message, otherwise show the default message.
     *
     * @param requestCode the request code
     * @return the message to toast */
    private String getToastMessage(int requestCode) {
        String permissionName = PermissionUtils.getPermissionName(this, requestCode);
        String defName = getString(R.string.permission_default_permission_name);
        if (defName.equals(permissionName)) {
            return getString(R.string.permission_denied_try_again_after_set);
        } else {
            return String.format(getString(R.string.permission_denied_try_again_after_set_given_permission), permissionName);
        }
    }

    public BaseActivity getContext() {
        return this;
    }
}

