package me.shouheng.commons.activity;

import com.umeng.analytics.MobclickAgent;

import me.shouheng.commons.event.PageName;
import me.shouheng.commons.utils.LogUtils;

/**
 * @author shouh
 * @version $Id: UMengActivity, v 0.1 2018/11/21 19:39 shouh Exp$
 */
public abstract class UMengActivity extends PermissionActivity {

    private String pageName;

    {
        Class<?> clazz = getClass();
        if (clazz.isAnnotationPresent(PageName.class)) {
            pageName = clazz.getAnnotation(PageName.class).name();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(pageName);
        LogUtils.d(pageName);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(pageName);
    }

    public UMengActivity getContext() {
        return this;
    }
}
