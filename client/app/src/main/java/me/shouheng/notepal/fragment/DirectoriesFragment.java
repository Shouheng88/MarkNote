package me.shouheng.notepal.fragment;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import me.shouheng.commons.event.PageName;
import me.shouheng.commons.event.*;
import me.shouheng.commons.utils.PalmUtils;
import me.shouheng.commons.utils.ToastUtils;
import me.shouheng.commons.widget.recycler.CustomItemAnimator;
import me.shouheng.commons.widget.recycler.DividerItemDecoration;
import me.shouheng.data.model.Directory;
import me.shouheng.notepal.Constants;
import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.DirectoriesAdapter;
import me.shouheng.notepal.databinding.FragmentDirectoriesBinding;
import me.shouheng.notepal.vm.DirectoryViewModel;

/**
 * Created by shouh on 2018/3/30.*/
@PageName(name = UMEvent.PAGE_DIRECTORIES)
public class DirectoriesFragment extends BaseFragment<FragmentDirectoriesBinding> {

    private final static String KEY_DIRECTORY = "key_item_id";

    private DirectoryViewModel viewModel;

    private Directory directory;

    private DirectoriesAdapter adapter;

    public static DirectoriesFragment newInstance(Directory directory) {
        Bundle args = new Bundle();
        DirectoriesFragment fragment = new DirectoriesFragment();
        args.putSerializable(KEY_DIRECTORY, directory);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_directories;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        assert getArguments() != null;
        directory = (Directory) getArguments().get(KEY_DIRECTORY);
        viewModel = ViewModelProviders.of(this).get(DirectoryViewModel.class);

        adapter = new DirectoriesAdapter(getContext());
        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (getActivity() != null && getActivity() instanceof OnFragmentInteractionListener) {
                ((OnFragmentInteractionListener) getActivity()).onDirectoryClicked((Directory) adapter.getItem(position));
            }
        });

        getBinding().rvDirectories.addItemDecoration(new DividerItemDecoration(
                getContext(), DividerItemDecoration.VERTICAL_LIST, isDarkTheme()));
        getBinding().rvDirectories.setItemAnimator(new CustomItemAnimator());
        getBinding().rvDirectories.setLayoutManager(new LinearLayoutManager(getContext()));
        getBinding().rvDirectories.setEmptyView(getBinding().ivEmpty);
        getBinding().rvDirectories.setAdapter(adapter);

        fetchDirectories();

        AppCompatActivity app = (AppCompatActivity) getActivity();
        if (app != null) {
            ActionBar actionBar = app.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setSubtitle(directory.getPath());
            }
        }
    }

    private void fetchDirectories() {
        getBinding().sl.setVisibility(View.VISIBLE);
        viewModel.getDirectories(directory.getId()).observe(this, listResource -> {
            getBinding().sl.setVisibility(View.GONE);
            if (listResource == null) {
                ToastUtils.makeToast(R.string.setting_backup_onedrive_failed_query_sync_cloud);
                return;
            }
            switch (listResource.status) {
                case FAILED:
                    ToastUtils.makeToast(listResource.message);
                    break;
                case SUCCESS:
                    assert listResource.data != null;
                    adapter.setNewData(listResource.data);
                    for (Directory dir : listResource.data) {
                        if (Constants.BACKUP_DIR_NAME.equals(dir.getName())) {
                            showSelectionTips();
                            break;
                        }
                    }
                    break;
            }
        });
    }

    private void showSelectionTips() {
        new MaterialDialog.Builder(getContext())
                .title(R.string.text_tips)
                .content(R.string.setting_backup_onedrive_available_directory_found)
                .positiveText(R.string.text_get_it)
                .show();
    }

    public Directory getDirectory() {
        return directory;
    }

    public void addCategory(Directory directory) {
        adapter.addData(directory);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.directory, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                createBackupFolder();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createBackupFolder() {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setTitle(R.string.text_please_wait);
        pd.setCancelable(false);
        pd.show();

        viewModel.createBackupDir(directory, adapter.getData()).observe(this, directoryResource -> {
            pd.dismiss();
            if (directoryResource == null) {
                ToastUtils.makeToast(R.string.setting_backup_onedrive_failed_query_sync_cloud);
                return;
            }
            switch (directoryResource.status) {
                case FAILED:
                    ToastUtils.makeToast(String.format(
                            PalmUtils.getStringCompact(R.string.setting_backup_onedrive_error_when_try_to_backup),
                            directoryResource.message));
                    break;
                case SUCCESS:
                    ToastUtils.makeToast(R.string.setting_backup_onedrive_backup_dir_selected_message);
                    if (getActivity() != null && getActivity() instanceof OnFragmentInteractionListener) {
                        ((OnFragmentInteractionListener) getActivity()).onDirectoryPicked(directory);
                    }
                    break;
            }
        });
    }

    public interface OnFragmentInteractionListener {
        void onDirectoryClicked(final Directory item);
        void onDirectoryPicked(Directory directory);
    }
}
