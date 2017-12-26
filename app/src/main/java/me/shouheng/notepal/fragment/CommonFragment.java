package me.shouheng.notepal.fragment;

import android.annotation.TargetApi;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.shouheng.notepal.util.ColorUtils;

/**
 * Created by wang shouheng on 2017/12/23. */
public abstract class CommonFragment<T extends ViewDataBinding> extends Fragment {

    private T binding;

    protected abstract int getLayoutResId();

    protected abstract void doCreateView(Bundle savedInstanceState);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (getLayoutResId() <= 0 ) {
            throw new AssertionError("Subclass must provide a valid layout resource id");
        }

        binding = DataBindingUtil.inflate(inflater, getLayoutResId(), container, false);

        doCreateView(savedInstanceState);

        return binding.getRoot();
    }

    protected View getRoot() {
        return binding.getRoot();
    }

    protected final T getBinding() {
        return binding;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && getActivity() != null) {
            getActivity().getWindow().setStatusBarColor(color);
        }
    }

    protected boolean isDarkTheme(){
        return ColorUtils.isDarkTheme(getContext());
    }

    protected int primaryColor(){
        return ColorUtils.primaryColor(getContext());
    }

    protected int accentColor(){
        return ColorUtils.accentColor(getContext());
    }
}
