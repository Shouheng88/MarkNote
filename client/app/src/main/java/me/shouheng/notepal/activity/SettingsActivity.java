package me.shouheng.notepal.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;

import java.io.Serializable;

import me.shouheng.commons.activity.CommonActivity;
import me.shouheng.commons.event.PageName;
import me.shouheng.commons.helper.FragmentHelper;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.ActivitySettingsBinding;

import static me.shouheng.commons.event.UMEvent.*;

@PageName(name = PAGE_SETTINGS)
public class SettingsActivity extends CommonActivity<ActivitySettingsBinding> {

    public final static String ACTION_OPEN_FRAGMENT = "__action_open_fragment";
    public final static String ACTION_OPEN_FRAGMENT_EXTRA_NEED_TOOLBAR = "__action_open_fragment_extra_need_toolbar";
    public final static String ACTION_OPEN_FRAGMENT_EXTRA_CLASS = "__action_open_fragment_extra_class";
    public final static String ACTION_OPEN_FRAGMENT_EXTRA_BUNDLE = "__action_open_fragment_extra_bundle";

    public static <T extends Fragment> Builder<T> open(Class<T> withClz) {
        return new Builder<>(withClz);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_settings;
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
                FragmentHelper.replace(this, fragment, R.id.fragment_container, false);
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
                actionBar.setHomeAsUpIndicator(ColorUtils.tintDrawable(R.drawable.ic_arrow_back_black_24dp,
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

    /**
     * This is builder is different from that of {@link FragmentHelper.Builder} for the fragment type.
     *
     * @param <T> the fragment type of {@link android.app.Fragment}
     */
    public static class Builder<T extends Fragment> {

        /**
         * 要打开的 Fragment 的 Class
         */
        private Class<T> clz;

        /**
         * 用来传递给要打开的 Fragment 的 Bundle，作为 Arguments 传入，从 {@link Fragment#getArguments()}
         * 方法中获取该参数，要设置值的话可以使用 {@link #put(String, int)} 等方法设置值，如果下面的方法
         * 不够用的话，可以自行添加
         */
        private Bundle bundle = new Bundle();

        public Builder(Class<T> clz) {
            this.clz = clz;
        }

        public Builder<T> put(String key, int i) {
            bundle.putInt(key, i);
            return this;
        }

        public Builder<T> put(String key, long l) {
            bundle.putLong(key, l);
            return this;
        }

        public Builder<T> put(String key, double d) {
            bundle.putDouble(key, d);
            return this;
        }

        public Builder<T> put(String key, long[] ls) {
            bundle.putLongArray(key, ls);
            return this;
        }

        public Builder<T> put(String key, String s) {
            bundle.putString(key, s);
            return this;
        }

        public Builder<T> put(String key, Serializable s) {
            bundle.putSerializable(key, s);
            return this;
        }

        public Builder<T> put(String key, Parcelable p) {
            bundle.putParcelable(key, p);
            return this;
        }

        public T get() {
            try {
                T fragment = clz.newInstance();
                fragment.setArguments(bundle);
                return fragment;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

        private Intent getLaunchIntent() {
            Intent intent = new Intent();
            intent.setAction(SettingsActivity.ACTION_OPEN_FRAGMENT);
            intent.putExtra(SettingsActivity.ACTION_OPEN_FRAGMENT_EXTRA_CLASS, clz);
            intent.putExtra(SettingsActivity.ACTION_OPEN_FRAGMENT_EXTRA_BUNDLE, bundle);
            return intent;
        }

        public void launch(Context context) {
            if (clz == null) {
                throw new IllegalArgumentException("No destination found for ContainerActivity.");
            }
            if (context == null) {
                throw new IllegalArgumentException("The launch context can't be null.");
            }
            Intent intent = getLaunchIntent();
            intent.setClass(context, SettingsActivity.class);
            context.startActivity(intent);
        }

        public void launch(Activity activity, int requestCode) {
            if (clz == null) {
                throw new IllegalArgumentException("No destination found for SettingsActivity.");
            }
            if (activity == null) {
                throw new IllegalArgumentException("The launch activity can't be null.");
            }
            Intent intent = getLaunchIntent();
            intent.setClass(activity, SettingsActivity.class);
            activity.startActivityForResult(intent, requestCode);
        }

        public void launch(@NonNull Fragment fragment, int requestCode) {
            if (clz == null) {
                throw new IllegalArgumentException("No destination found for SettingsActivity.");
            }
            Intent intent = getLaunchIntent();
            intent.setClass(fragment.getActivity(), SettingsActivity.class);
            fragment.startActivityForResult(intent, requestCode);
        }
    }
}
