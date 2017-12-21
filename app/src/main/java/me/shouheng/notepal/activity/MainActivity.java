package me.shouheng.notepal.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.ActivityMainBinding;
import me.shouheng.notepal.databinding.ActivityMainNavHeaderBinding;
import me.shouheng.notepal.intro.IntroActivity;

public class MainActivity extends CommonActivity<ActivityMainBinding> {

    private ActivityMainNavHeaderBinding headerBinding;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void beforeSetContentView() {
        setTranslucentStatusBar();
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {

        IntroActivity.launchIfNecessary(this);

        initHeaderView();
    }

    private void initHeaderView() {
        View header = getBinding().nav.inflateHeaderView(R.layout.activity_main_nav_header);
        headerBinding = DataBindingUtil.bind(header);
        header.setOnClickListener(v -> {});
        header.setOnLongClickListener(v -> true);
    }
}
