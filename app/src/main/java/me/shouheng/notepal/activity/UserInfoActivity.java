package me.shouheng.notepal.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.ActivityUserInfoBinding;
import me.shouheng.notepal.fragment.StatisticsFragment;
import me.shouheng.notepal.fragment.TimeLineFragment;
import me.shouheng.notepal.fragment.UserInfoFragment;
import me.shouheng.notepal.util.FragmentHelper;

public class UserInfoActivity extends CommonActivity<ActivityUserInfoBinding>
        implements UserInfoFragment.OnItemSelectedListener {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_user_info;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        configToolbar();

        configFragment();
    }

    private void configToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.user_info);
        }
        if (!isDarkTheme()) toolbar.setPopupTheme(R.style.AppTheme_PopupOverlay);
    }

    private void configFragment() {
        FragmentHelper.replace(this, new UserInfoFragment(), R.id.fragment_container);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTimelineSelected() {
        FragmentHelper.replaceWithCallback(this, new TimeLineFragment(), R.id.fragment_container);
    }

    @Override
    public void onChatHeaderSelected() {
        FragmentHelper.replaceWithCallback(this, new StatisticsFragment(), R.id.fragment_container);
    }
}