package me.shouheng.notepal.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import me.shouheng.notepal.R;
import me.shouheng.notepal.util.PalmUtils;
import me.shouheng.notepal.util.PermissionUtils;
import me.shouheng.notepal.util.ToastUtils;

/**
 * Created by wang shouheng on 2017/12/21.*/
@SuppressLint("Registered")
public abstract class CommonActivity<T extends ViewDataBinding> extends ThemedActivity {

    private T binding;

    private PermissionUtils.OnGetPermissionCallback onGetPermissionCallback;

    protected abstract int getLayoutResId();

    protected abstract void doCreateView(Bundle savedInstanceState);

    protected void beforeSetContentView(){}

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        if (getLayoutResId() <= 0 ) {
            throw new AssertionError("Subclass must provide a valid layout resource id");
        }

        binding = DataBindingUtil.inflate(getLayoutInflater(), getLayoutResId(), null, false);

        beforeSetContentView();

        setContentView(binding.getRoot());

        doCreateView(savedInstanceState);
    }

    protected final T getBinding() {
        return binding;
    }

    protected <M extends Activity> void startActivity(Class<M> activityClass) {
        startActivity(new Intent(this, activityClass));
    }

    protected <M extends Activity> void startActivityForResult(Class<M> activityClass, int requestCode) {
        startActivityForResult(new Intent(this, activityClass), requestCode);
    }

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
                    ToastUtils.makeToast(this, R.string.permission_denied_try_again_after_set);
                }
            } else {
                ToastUtils.makeToast(this, R.string.permission_denied_try_again_after_set);
            }
        }
    }

    private void showPermissionSettingDialog(int requestCode){
        String msg = String.format(getString(R.string.set_permission_in_setting), PermissionUtils.getPermissionName(this, requestCode));
        new AlertDialog.Builder(this)
                .setTitle(R.string.setting_permission)
                .setMessage(msg)
                .setPositiveButton(R.string.to_set, (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", PalmUtils.getPackageName(CommonActivity.this), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton(R.string.text_cancel, null)
                .create()
                .show();
    }
}
