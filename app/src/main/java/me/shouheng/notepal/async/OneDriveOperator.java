package me.shouheng.notepal.async;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import me.shouheng.notepal.util.NetworkUtils;
import me.shouheng.notepal.util.PreferencesUtils;

/**
 * Created by shouh on 2018/3/30.*/
public class OneDriveOperator extends IntentService {

    public final static String BACKUP_DIR = "NotePal";

    public OneDriveOperator() {
        super("OneDriveOperator");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        boolean isNetworkAvailable = NetworkUtils.isNetworkAvailable(getApplicationContext());
        boolean isWifi = NetworkUtils.isWifi(getApplicationContext());
        boolean isOnlyWifi = PreferencesUtils.getInstance(getApplicationContext()).isBackupOnlyInWifi();

        if (isNetworkAvailable && (!isOnlyWifi || isWifi)) {
            // do backup

        }


        // step2. 比较本地文件和网盘文件的修改时间
        // step3. 删除式上传
    }
}
