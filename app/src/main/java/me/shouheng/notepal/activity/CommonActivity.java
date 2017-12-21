package me.shouheng.notepal.activity;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by wang shouheng on 2017/12/21.*/
@SuppressLint("Registered")
public abstract class CommonActivity<T extends ViewDataBinding> extends ThemedActivity {

    private T binding;

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
}
