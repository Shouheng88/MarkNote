package me.shouheng.commons.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import me.shouheng.commons.R;
import me.shouheng.commons.activity.interaction.BackEventResolver;
import me.shouheng.commons.activity.interaction.FragmentKeyDown;
import me.shouheng.commons.databinding.ActivityContainerBinding;
import me.shouheng.commons.event.PageName;
import me.shouheng.commons.helper.FragmentHelper;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.utils.PalmUtils;

import static me.shouheng.commons.event.UMEvent.*;

@PageName(name = PAGE_CONTAINER)
public class ContainerActivity extends CommonActivity<ActivityContainerBinding> {

    public final static String ACTION_OPEN_FRAGMENT = "__action_open_fragment";
    public final static String ACTION_OPEN_FRAGMENT_EXTRA_NEED_TOOLBAR = "__action_open_fragment_extra_need_toolbar";
    public final static String ACTION_OPEN_FRAGMENT_EXTRA_CLASS = "__action_open_fragment_extra_class";
    public final static String ACTION_OPEN_FRAGMENT_EXTRA_BUNDLE = "__action_open_fragment_extra_bundle";

    public static <T extends Fragment> FragmentHelper.Builder<T> open(Class<T> withClz) {
        return new FragmentHelper.Builder<>(withClz);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_container;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String action = intent.getAction();
        if (ACTION_OPEN_FRAGMENT.equals(action)){
            Class<Fragment> fragmentClass = (Class<Fragment>) intent.getSerializableExtra(ACTION_OPEN_FRAGMENT_EXTRA_CLASS);
            Bundle bundle = intent.getParcelableExtra(ACTION_OPEN_FRAGMENT_EXTRA_BUNDLE);
            setupToolbar(savedInstanceState);
            try {
                Fragment fragment = fragmentClass.newInstance();
                fragment.setArguments(bundle);
                FragmentHelper.replace(this, fragment,  R.id.fragment_container, false);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupToolbar(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (!intent.hasExtra(ACTION_OPEN_FRAGMENT_EXTRA_NEED_TOOLBAR)
                || intent.getBooleanExtra(ACTION_OPEN_FRAGMENT_EXTRA_NEED_TOOLBAR, true)) {
            setSupportActionBar(getBinding().toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(ColorUtils.tintDrawable(
                        PalmUtils.getDrawableCompact(R.drawable.ic_arrow_back_black_24dp),
                        getThemeStyle().isDarkTheme ? Color.WHITE : Color.BLACK));
            }
            getBinding().toolbar.setTitleTextColor(getThemeStyle().isDarkTheme ? Color.WHITE : Color.BLACK);
            if (getThemeStyle().isDarkTheme) {
                getBinding().toolbar.setPopupTheme(R.style.AppTheme_PopupOverlayDark);
            }
        } else {
            getBinding().barLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Fragment fragment = getCurrentFragment(R.id.fragment_container);
        if (fragment instanceof FragmentKeyDown) {
            if (((FragmentKeyDown) fragment).onFragmentKeyDown(keyCode, event)) {
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getCurrentFragment(R.id.fragment_container);
        if (fragment instanceof BackEventResolver) {
            ((BackEventResolver) fragment).resolve();
        } else {
            superOnBackPressed();
        }
    }
}
