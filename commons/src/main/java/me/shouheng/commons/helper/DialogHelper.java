package me.shouheng.commons.helper;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;

public class DialogHelper {

    public static <M extends DialogFragment> Builder<M> open(@NonNull Class<M> clz) {
        return new Builder<>(clz);
    }

    public static class Builder<T extends DialogFragment> {

        private Class<T> clz;

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

        public void show(AppCompatActivity activity, String tag) {
            if (clz == null) {
                throw new IllegalArgumentException("No destination found for ContainerActivity.");
            }
            if (activity == null) {
                throw new IllegalArgumentException("The launch activity can't be null.");
            }
            DialogFragment dialogFragment = get();
            dialogFragment.show(activity.getSupportFragmentManager(), tag);
        }

        public void show(@NonNull Fragment fragment, String tag) {
            if (clz == null) {
                throw new IllegalArgumentException("No destination found for ContainerActivity.");
            }
            DialogFragment dialogFragment = get();
            dialogFragment.show(fragment.getFragmentManager(), tag);
        }
    }
}
