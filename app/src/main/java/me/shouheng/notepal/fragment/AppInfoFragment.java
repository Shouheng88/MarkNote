package me.shouheng.notepal.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import de.psdev.licensesdialog.LicensesDialog;
import me.shouheng.notepal.BuildConfig;
import me.shouheng.notepal.R;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.databinding.FragmentAppInfoBinding;
import me.shouheng.notepal.util.IntentChecker;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.util.ViewUtils;

/**
 * Created by wangshouheng on 2017/12/3.*/
public class AppInfoFragment extends BaseFragment<FragmentAppInfoBinding> {

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_app_info;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        configToolbar();

        configViews();
    }

    private void configToolbar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.app_info);
    }

    private void configViews() {
        getBinding().llTranslation.setOnClickListener(v -> openGithubProject());
        getBinding().llRating.setOnClickListener(v -> openInMarket());
        getBinding().llLicense.setOnClickListener(v -> showLicensesDialog());
        getBinding().llDeveloper.setOnClickListener(v -> viewDeveloper());

        getBinding().tvVersionName.setText(BuildConfig.VERSION_NAME);
    }

    private void showLicensesDialog() {
        new LicensesDialog.Builder(getContext())
                .setNotices(R.raw.notices)
                .setIncludeOwnLicense(true)
                .build()
                .showAppCompat();
    }

    private void openGithubProject() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.GITHUB_PAGE));
        if (IntentChecker.isAvailable(getContext(), intent, null)) {
            ViewUtils.launchUrl(getContext(), Constants.GITHUB_PAGE);
        } else {
            ToastUtils.makeToast(getContext(), R.string.failed_to_resolve_intent);
        }
    }

    private void openInMarket() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.MARKET_PAGE));
        if (IntentChecker.isAvailable(getContext(), intent, null)){
            startActivity(intent);
        } else if (IntentChecker.isAvailable(getContext(),
                new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.GOOGLE_PLAY_WEB_PAGE)), null)) {
            ViewUtils.launchUrl(getContext(), Constants.GOOGLE_PLAY_WEB_PAGE);
        } else {
            ToastUtils.makeToast(getContext(), R.string.failed_to_resolve_intent);
        }
    }

    private void viewDeveloper() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.GITHUB_DEVELOPER));
        if (IntentChecker.isAvailable(getContext(), intent, null)) {
            ViewUtils.launchUrl(getContext(), Constants.GITHUB_DEVELOPER);
        } else {
            ToastUtils.makeToast(getContext(), R.string.failed_to_resolve_intent);
        }
    }
}
