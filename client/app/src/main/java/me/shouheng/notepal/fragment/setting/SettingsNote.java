package me.shouheng.notepal.fragment.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import me.shouheng.commons.event.PageName;
import me.shouheng.commons.event.RxBus;
import me.shouheng.commons.event.RxMessage;
import me.shouheng.commons.event.*;
import me.shouheng.commons.fragment.BPreferenceFragment;
import me.shouheng.notepal.R;

import static me.shouheng.commons.event.RxMessage.CODE_NOTE_LIST_STYLE_CHANGED;

/**
 * Created by shouh on 2018/3/21.
 */
@PageName(name = UMEvent.PAGE_SETTING_NOTE)
public class SettingsNote extends BPreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) actionBar.setTitle(R.string.setting_category_universal_note);

        addPreferencesFromResource(R.xml.preferences_note);

        findPreference(R.string.key_note_expanded_note).setOnPreferenceClickListener(preference -> {
            RxBus.getRxBus().post(new RxMessage(CODE_NOTE_LIST_STYLE_CHANGED, null));
            return true;
        });
    }
}
