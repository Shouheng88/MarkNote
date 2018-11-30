package me.shouheng.notepal.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.View;

import java.io.Serializable;

import me.shouheng.commons.activity.CommonActivity;
import me.shouheng.commons.activity.interaction.BackEventResolver;
import me.shouheng.commons.utils.LogUtils;
import me.shouheng.commons.utils.ToastUtils;
import me.shouheng.data.ModelFactory;
import me.shouheng.data.entity.Note;
import me.shouheng.data.store.NotesStore;
import me.shouheng.notepal.Constants;
import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.ActivityContentBinding;

public class ContentActivity extends CommonActivity<ActivityContentBinding> {

    public final static String EXTRA_HAS_TOOLBAR = "__extra_has_toolbar";
    private final static String BUNDLE_KEY_NOTE = "__key_bundle_note";
    private final static String TAG_NOTE_FRAGMENT = "__tag_fragment_note";

    private Note note;

    public static void editNote(Fragment fragment, @NonNull Note note) {
        Intent i = new Intent(fragment.getContext(), ContentActivity.class);
        i.putExtra(Constants.EXTRA_MODEL, (Serializable) note);
        fragment.startActivity(i);
    }

    public static void editNote(Activity activity, @NonNull Note note) {
        Intent i = new Intent(activity, ContentActivity.class);
        i.putExtra(Constants.EXTRA_MODEL, (Serializable) note);
        activity.startActivity(i);
    }

    public static void resolveThirdPart(Activity activity, Intent i) {
        i.setClass(activity, ContentActivity.class);
        i.setAction(Constants.ACTION_TO_NOTE_FROM_THIRD_PART);
        activity.startActivity(i);
    }

    public static void resolveAction(Activity activity, @NonNull Note note, String action) {
        Intent i = new Intent(activity, ContentActivity.class);
        i.setAction(action);
        i.putExtra(Constants.EXTRA_MODEL, (Parcelable) note);
        activity.startActivity(i);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_content;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            note = (Note) savedInstanceState.get(BUNDLE_KEY_NOTE);
        }

        handleIntent();

        configToolbar();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(BUNDLE_KEY_NOTE, note);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            ToastUtils.makeToast(R.string.content_failed_to_parse_intent);
            finish();
            return;
        }

        if (intent.hasExtra(Constants.EXTRA_MODEL)) {
            if (!(intent.getSerializableExtra(Constants.EXTRA_MODEL) instanceof Note)) {
                ToastUtils.makeToast(R.string.content_failed_to_parse_intent);
                LogUtils.d("Failed to resolve note intent : " + intent);
                finish();
                return;
            }
            note = note != null ? note : (Note) intent.getSerializableExtra(Constants.EXTRA_MODEL);
            toNoteFragment(note,  false);
        } else if (Constants.ACTION_TO_NOTE_FROM_THIRD_PART.equals(intent.getAction())) {
            note = note != null ? note : ModelFactory.getNote();
            toNoteFragment(note,  true);
        }

        // The case below mainly used for the intent from shortcut
        if (intent.hasExtra(Constants.EXTRA_CODE)) {
            long code = intent.getLongExtra(Constants.EXTRA_CODE, -1);
            note = note != null ? note : NotesStore.getInstance().get(code);
            if (note == null){
                ToastUtils.makeToast(R.string.text_no_such_note);
                LogUtils.d("Failed to resolve intent : " + intent);
                finish();
                return;
            }
            toNoteFragment(note, false);
        }
    }

    private void configToolbar() {
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_HAS_TOOLBAR) && intent.getBooleanExtra(EXTRA_HAS_TOOLBAR, false)) {
            getBinding().barLayout.setVisibility(View.VISIBLE);
            setSupportActionBar(getBinding().toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            getBinding().toolbar.setTitleTextColor(getThemeStyle().isDarkTheme ? Color.WHITE : Color.BLACK);
            getBinding().toolbar.setSubtitleTextColor(getThemeStyle().isDarkTheme ? Color.WHITE : Color.BLACK);
            if (getThemeStyle().isDarkTheme) {
                getBinding().toolbar.setPopupTheme(R.style.AppTheme_PopupOverlayDark);
            }
        }
    }

    private void toNoteFragment(Note note, boolean isThirdPart){
//        String action = getIntent() == null || TextUtils.isEmpty(getIntent().getAction()) ? null : getIntent().getAction();
//        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_NOTE_FRAGMENT);
//        if (fragment == null) {
//            fragment = NoteFragment.newInstance(note, isThirdPart, action);
//        }
//        FragmentHelper.replace(this, fragment, R.id.fragment_container, TAG_NOTE_FRAGMENT);
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getCurrentFragment(R.id.fragment_container);
        if (currentFragment instanceof BackEventResolver){
            ((BackEventResolver) currentFragment).resolve();
        }
    }
}
