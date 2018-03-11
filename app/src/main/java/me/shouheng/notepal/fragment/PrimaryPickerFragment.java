package me.shouheng.notepal.fragment;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import org.polaric.colorful.Colorful;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.base.ThemedActivity;
import me.shouheng.notepal.adapter.ThemesListAdapter;
import me.shouheng.notepal.databinding.FragmentPrimaryPickerBinding;
import me.shouheng.notepal.listener.OnFragmentDestroyListener;
import me.shouheng.notepal.listener.OnThemeSelectedListener;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.util.PreferencesUtils;

/**
 * Created by wangshouheng on 2017/8/5. */
public class PrimaryPickerFragment extends BaseFragment<FragmentPrimaryPickerBinding> implements OnThemeSelectedListener {

    private ThemesListAdapter adapter;

    public static PrimaryPickerFragment newInstance(){
        return new PrimaryPickerFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_primary_picker;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        configToolbar();

        configViews();

        updateUIByTheme();
    }

    private void configToolbar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) actionBar.setTitle(R.string.setting_primary_color);
    }

    private void configViews() {
        adapter = new ThemesListAdapter(getContext(), this);
        getBinding().tvThemes.setLayoutManager(new LinearLayoutManager(getContext()));
        getBinding().tvThemes.setAdapter(adapter);
    }

    private void updateUIByTheme() {
        getBinding().getRoot().setBackgroundResource(isDarkTheme() ?
                R.color.dark_theme_foreground : R.color.light_theme_background);
    }

    @Override
    public void onThemeSelected(Colorful.ThemeColor themeColor) {
        PreferencesUtils.getInstance(getContext()).setThemeColor(themeColor);

        ColorUtils.forceUpdateThemeStatus(getContext());

        ((ThemedActivity) getActivity()).updateTheme();

        if (getActivity() instanceof OnThemeSelectedListener)
            ((OnThemeSelectedListener) getActivity()).onThemeSelected(themeColor);

        setStatusBarColor(ColorUtils.calStatusBarColor(primaryColor()));

        adapter.setSelectionChanged(themeColor);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() instanceof OnFragmentDestroyListener) {
            ((OnFragmentDestroyListener) getActivity()).onFragmentDestroy();
        }
    }
}
