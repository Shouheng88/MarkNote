package me.shouheng.notepal.fragment;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import de.psdev.licensesdialog.LicensesDialog;
import me.shouheng.notepal.BuildConfig;
import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.FragmentAppInfoBinding;
import me.shouheng.notepal.listener.OnFragmentDestroyListener;
import me.shouheng.notepal.util.IntentUtils;

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
        String verName = BuildConfig.FLAVOR + "-" + BuildConfig.VERSION_NAME + "-" + BuildConfig.BUILD_TYPE;
        getBinding().tvVersionName.setText(verName);

        getBinding().ctvTranslation.setOnCardTitleClickListener(() -> IntentUtils.openGithubProject(getActivity()));
        getBinding().ctvTranslation.setSubTitle(String.format(getString(R.string.translate_to_other_languages), getString(R.string.app_name)));

        getBinding().ctvRating.setOnCardTitleClickListener(() -> IntentUtils.openInMarket(getActivity()));
        getBinding().ctvRating.setSubTitle(String.format(getString(R.string.give_good_rating_if_you_like), getString(R.string.app_name)));

        getBinding().ctvDeveloper.setOnCardTitleClickListener(() -> IntentUtils.openDeveloperPage(getActivity()));

        getBinding().ctvLicense.setOnCardTitleClickListener(this::showLicensesDialog);
    }

    private void showLicensesDialog() {
        new LicensesDialog.Builder(getContext())
                .setNotices(R.raw.notices)
                .setIncludeOwnLicense(true)
                .build()
                .showAppCompat();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() instanceof OnFragmentDestroyListener) {
            ((OnFragmentDestroyListener) getActivity()).onFragmentDestroy();
        }
    }
}
