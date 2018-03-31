package me.shouheng.notepal.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.base.CommonActivity;
import me.shouheng.notepal.databinding.ActivityDirectoryBinding;
import me.shouheng.notepal.fragment.DirectoriesFragment;
import me.shouheng.notepal.model.Directory;
import me.shouheng.notepal.util.FragmentHelper;

public class DirectoryActivity extends CommonActivity<ActivityDirectoryBinding> implements
        DirectoriesFragment.OnFragmentInteractionListener{

    public final static String KEY_EXTRA_DATA = "key_extra_data";

    public static void startExplore(Activity activity, int req) {
        Intent intent = new Intent(activity, DirectoryActivity.class);
        activity.startActivityForResult(intent, req);
    }

    public static void startExplore(android.app.Fragment fragment, int req) {
        Intent intent = new Intent(fragment.getActivity(), DirectoryActivity.class);
        fragment.startActivityForResult(intent, req);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_directory;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        configToolbar();

        Directory directory = new Directory();
        directory.setId("root");
        directory.setPath("root");
        FragmentHelper.replace(this, DirectoriesFragment.newInstance(directory), R.id.fragment_container);
    }

    private void configToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (!isDarkTheme()) toolbar.setPopupTheme(R.style.AppTheme_PopupOverlay);
        if (ab != null) {
            ab.setTitle(R.string.text_folder_explore);
            ab.setDisplayHomeAsUpEnabled(true);
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
    public void onDirectoryClicked(Directory item) {
        FragmentHelper.replaceWithCallback(this, DirectoriesFragment.newInstance(item), R.id.fragment_container);
    }

    @Override
    public void onDirectoryPicked(Directory directory) {
        Intent intent = new Intent();
        intent.putExtra(KEY_EXTRA_DATA, directory);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
