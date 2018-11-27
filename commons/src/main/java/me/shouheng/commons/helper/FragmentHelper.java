package me.shouheng.commons.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;

import me.shouheng.commons.activity.ContainerActivity;

public class FragmentHelper {

    public static void replace(AppCompatActivity activity, Fragment fragment,
                               @IdRes int id, boolean addToBackStack) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(id, fragment);
        if (addToBackStack) transaction.addToBackStack(null);
        transaction.commit();
    }

    public static void replace(AppCompatActivity activity, android.app.Fragment fragment,
                               @IdRes int containerId, boolean addToBackStack) {
        if (activity.isFinishing()) return;
        android.app.FragmentManager fragmentManager = activity.getFragmentManager();
        android.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (addToBackStack) transaction.addToBackStack(null);
        transaction.replace(containerId, fragment).commit();
    }

    public static void remove(AppCompatActivity activity, Fragment fragment) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction t = fm.beginTransaction();
        t.remove(fragment);
        t.commit();
    }

    public static void clear(AppCompatActivity activity) {
        FragmentManager fm = activity.getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public static void pop(AppCompatActivity activity) {
        FragmentManager fm = activity.getSupportFragmentManager();
        if (fm.getFragments() != null && fm.getFragments().size() > 1) {
            fm.popBackStackImmediate();
        } else {
            activity.finish();
        }
    }

    public static <M extends Fragment> Builder<M> open(@NonNull Class<M> clz) {
        return new Builder<>(clz);
    }

    /**
     * 用于构建一个打开 {@link ContainerActivity} 的请求，主要设计用来减轻 {@link ContainerActivity}
     * 的静态方法的数量
     *
     * @param <T> 要打开的 Fragment 的类型
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
            intent.setAction(ContainerActivity.ACTION_OPEN_FRAGMENT);
            intent.putExtra(ContainerActivity.ACTION_OPEN_FRAGMENT_EXTRA_CLASS, clz);
            intent.putExtra(ContainerActivity.ACTION_OPEN_FRAGMENT_EXTRA_BUNDLE, bundle);
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
            intent.setClass(context, ContainerActivity.class);
            context.startActivity(intent);
        }

        public void launch(Activity activity, int requestCode) {
            if (clz == null) {
                throw new IllegalArgumentException("No destination found for ContainerActivity.");
            }
            if (activity == null) {
                throw new IllegalArgumentException("The launch activity can't be null.");
            }
            Intent intent = getLaunchIntent();
            intent.setClass(activity, ContainerActivity.class);
            activity.startActivityForResult(intent, requestCode);
        }

        public void launch(@NonNull Fragment fragment, int requestCode) {
            if (clz == null) {
                throw new IllegalArgumentException("No destination found for ContainerActivity.");
            }
            Intent intent = getLaunchIntent();
            intent.setClass(fragment.getContext(), ContainerActivity.class);
            fragment.startActivityForResult(intent, requestCode);
        }
    }
}
