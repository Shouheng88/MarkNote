package me.shouheng.notepal.listener;

import java.io.File;

import me.shouheng.commons.utils.LogUtils;
import top.zibin.luban.OnCompressListener;

/**
 * Created by shouh on 2018/4/6.*/
public class DefaultCompressListener implements OnCompressListener {

    @Override
    public void onStart() {
        LogUtils.d("Compress start!");
    }

    @Override
    public void onSuccess(File file) {
        LogUtils.d("Compress success " + file.getPath());
    }

    @Override
    public void onError(Throwable e) {
        LogUtils.e("Compress error " + e);
    }
}
