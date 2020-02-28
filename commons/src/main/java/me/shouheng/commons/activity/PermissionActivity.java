package me.shouheng.commons.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import me.shouheng.commons.R;
import me.shouheng.commons.utils.PermissionUtils;

/**
 * @author shouh
 * @version $Id: PermissionActivity, v 0.1 2018/11/21 19:56 shouh Exp$
 */
public abstract class PermissionActivity extends AppCompatActivity {

    /**
     * The permission callback.
     */
    private PermissionUtils.OnGetPermissionCallback onGetPermissionCallback;

    /**
     * Set the permission callback. Call this method every time when you call check permission methods.
     *
     * @param onGetPermissionCallback the permission callback
     */
    public void setOnGetPermissionCallback(PermissionUtils.OnGetPermissionCallback onGetPermissionCallback) {
        this.onGetPermissionCallback = onGetPermissionCallback;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check permission results.
        int notGrantedCount = 0, length = grantResults.length;
        List<String> notGranted = new LinkedList<>();
        for (int i =0; i<length; i++) {
            notGrantedCount += ((grantResults[i] != PackageManager.PERMISSION_GRANTED) ? 1 : 0);
            notGranted.add(permissions[i]);
        }
        if (notGrantedCount == 0) {
            // All permissions granted.
            if (onGetPermissionCallback != null) {
                onGetPermissionCallback.onGetPermission();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Permission list to show rationale request.
                List<String> rationaleList = new LinkedList<>();
                for (String permission : notGranted) {
                    if (shouldShowRequestPermissionRationale(permission)) {
                        rationaleList.add(permission);
                    }
                }
                if (rationaleList.isEmpty()) {
                    showNoPermissionToast(notGranted.toArray(new String[0]));
                } else {
                    showPermissionsRationale(rationaleList.toArray(new String[0]));
                }
            } else {
                showNoPermissionToast(notGranted.toArray(new String[0]));
            }
        }
    }

    /**
     * Show permission rationale dialog.
     *
     * @param permissions permissions
     */
    private void showPermissionsRationale(String[] permissions) {
        String names = PermissionUtils.names(this, permissions);
        String message = String.format(getString(R.string.permission_set_in_settings_message), names);
        new AlertDialog.Builder(this)
                .setTitle(R.string.permission_set_permission)
                .setMessage(message)
                .setPositiveButton(R.string.permission_to_set, (dialog, which) -> setPermission())
                .setNegativeButton(R.string.text_cancel, null)
                .create().show();
    }

    /**
     * Show no permission toast.
     *
     * @param permissions permissions.
     */
    private void showNoPermissionToast(String[] permissions) {
        String names = PermissionUtils.names(this, permissions);
        String message = String.format(getString(R.string.permission_denied_message), names);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * To set permission in setting page of this application.
     */
    private void setPermission() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", PermissionUtils.getPackageName(this), null);
        intent.setData(uri);
        startActivity(intent);
    }
}


