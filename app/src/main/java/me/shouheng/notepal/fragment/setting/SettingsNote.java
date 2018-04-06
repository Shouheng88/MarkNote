package me.shouheng.notepal.fragment.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import me.shouheng.notepal.R;
import me.shouheng.notepal.listener.OnFragmentDestroyListener;
import me.shouheng.notepal.listener.OnSettingsChangedListener;

/**
 * Created by shouh on 2018/3/21.*/
public class SettingsNote extends BaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configToolbar();

        addPreferencesFromResource(R.xml.preferences_note);

        setPreferenceClickListeners();
    }

    private void configToolbar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) actionBar.setTitle(R.string.setting_note);
    }

    private void setPreferenceClickListeners() {
        findPreference(getKey(R.string.key_key_show_note_expanded)).setOnPreferenceClickListener(preference -> {
            notifyDashboardChanged();
            return true;
        });
    }

    private void notifyDashboardChanged() {
        if (getActivity() != null && getActivity() instanceof OnSettingsChangedListener) {
            ((OnSettingsChangedListener) getActivity()).onDashboardSettingChanged(OnSettingsChangedListener.ChangedType.NOTE_LIST_TYPE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() instanceof OnFragmentDestroyListener) {
            ((OnFragmentDestroyListener) getActivity()).onFragmentDestroy();
        }
    }
}
