package me.shouheng.commons.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import me.shouheng.utils.permission.PermissionResultHandler;
import me.shouheng.utils.permission.PermissionResultResolver;
import me.shouheng.utils.permission.callback.OnGetPermissionCallback;
import me.shouheng.utils.permission.callback.PermissionResultCallbackImpl;

/**
 * @author shouh
 * @version $Id: PermissionActivity, v 0.1 2018/11/21 19:56 shouh Exp$
 */
public abstract class PermissionActivity extends AppCompatActivity implements PermissionResultResolver {

    private OnGetPermissionCallback onGetPermissionCallback;

    @Override
    public void setOnGetPermissionCallback(OnGetPermissionCallback onGetPermissionCallback) {
        this.onGetPermissionCallback = onGetPermissionCallback;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionResultHandler.handlePermissionsResult(this, requestCode, permissions, grantResults, new PermissionResultCallbackImpl(this, onGetPermissionCallback));
    }

}


