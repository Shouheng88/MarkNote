package me.shouheng.commons.activity;

import com.umeng.analytics.MobclickAgent;

/**
 * @author shouh
 * @version $Id: UMengActivity, v 0.1 2018/11/21 19:39 shouh Exp$
 */
public abstract class UMengActivity extends PermissionActivity {

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

    public PermissionActivity getContext() {
        return this;
    }
}
