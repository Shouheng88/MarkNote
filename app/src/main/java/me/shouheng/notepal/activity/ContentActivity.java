package me.shouheng.notepal.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog.ColorCallback;

import java.io.Serializable;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.base.CommonActivity;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.databinding.ActivityContentBinding;
import me.shouheng.notepal.fragment.NoteFragment;
import me.shouheng.notepal.fragment.NoteFragment.OnNoteInteractListener;
import me.shouheng.notepal.fragment.NoteViewFragment;
import me.shouheng.notepal.fragment.base.BaseModelFragment;
import me.shouheng.notepal.fragment.base.CommonFragment;
import me.shouheng.notepal.model.ModelFactory;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.provider.NotesStore;
import me.shouheng.notepal.util.FragmentHelper;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.ToastUtils;

public class ContentActivity extends CommonActivity<ActivityContentBinding> implements
        ColorCallback,
        OnNoteInteractListener {

    public final static String EXTRA_HAS_TOOLBAR = "extra_has_toolbar";

    private final String BUNDLE_KEY_NOTE = "key_bundle_note";

    /**
     * Current working note */
    private Note note;

    // region edit and view note
    public static void editNote(Fragment fragment, @NonNull Note note, int requestCode){
        fragment.startActivityForResult(noteEditIntent(fragment.getContext(), note, requestCode), requestCode);
    }

    public static void editNote(Activity activity, @NonNull Note note, int requestCode){
        activity.startActivityForResult(noteEditIntent(activity, note, requestCode), requestCode);
    }

    public static void viewNote(Fragment fragment, @NonNull Note note, int requestCode){
        fragment.startActivityForResult(noteViewIntent(fragment.getContext(), note, requestCode),requestCode);
    }

    public static void viewNote(Activity activity, @NonNull Note note, int requestCode){
        activity.startActivityForResult(noteViewIntent(activity, note, requestCode), requestCode);
    }

    private static Intent noteViewIntent(Context context, @NonNull Note note, int requestCode) {
        Intent intent = new Intent(context, ContentActivity.class);
        intent.putExtra(Constants.EXTRA_MODEL, (Serializable) note);
        intent.putExtra(Constants.EXTRA_REQUEST_CODE, requestCode);
        intent.putExtra(Constants.EXTRA_START_TYPE, Constants.VALUE_START_VIEW);
        intent.putExtra(Constants.EXTRA_FRAGMENT, Constants.VALUE_FRAGMENT_NOTE);
        intent.putExtra(EXTRA_HAS_TOOLBAR, true);
        return intent;
    }

    private static Intent noteEditIntent(Context context, @NonNull Note note, int requestCode) {
        Intent intent = new Intent(context, ContentActivity.class);
        intent.putExtra(Constants.EXTRA_MODEL, (Serializable) note);
        intent.putExtra(Constants.EXTRA_REQUEST_CODE, requestCode);
        intent.putExtra(Constants.EXTRA_START_TYPE, Constants.VALUE_START_EDIT);
        intent.putExtra(Constants.EXTRA_FRAGMENT, Constants.VALUE_FRAGMENT_NOTE);
        return intent;
    }
    // endregion

    public static void resolveThirdPart(Activity activity, Intent i, int requestCode) {
        i.setClass(activity, ContentActivity.class);
        i.putExtra(Constants.EXTRA_IS_GOOGLE_NOW, Constants.INTENT_GOOGLE_NOW.equals(i.getAction()));
        i.setAction(Constants.ACTION_TO_NOTE_FROM_THIRD_PART);
        i.putExtra(Constants.EXTRA_FRAGMENT, Constants.VALUE_FRAGMENT_NOTE);
        i.putExtra(Constants.EXTRA_REQUEST_CODE, requestCode);
        i.putExtra(Constants.EXTRA_START_TYPE, Constants.VALUE_START_EDIT);
        activity.startActivity(i);
    }

    public static void resolveAction(Activity activity, @NonNull Note note, String action, int requestCode) {
        Intent i = new Intent(activity, ContentActivity.class);
        i.setAction(action);
        i.putExtra(Constants.EXTRA_MODEL, (Parcelable) note);
        i.putExtra(Constants.EXTRA_FRAGMENT, Constants.VALUE_FRAGMENT_NOTE);
        i.putExtra(Constants.EXTRA_REQUEST_CODE, requestCode);
        i.putExtra(Constants.EXTRA_START_TYPE, Constants.VALUE_START_EDIT);
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
        if (intent == null || !intent.hasExtra(Constants.EXTRA_FRAGMENT)) {
            ToastUtils.makeToast(R.string.content_failed_to_parse_intent);
            LogUtils.d("Failed to handle intent : " + intent);
            finish();
            return;
        }
        switch (intent.getStringExtra(Constants.EXTRA_FRAGMENT)) {
            case Constants.VALUE_FRAGMENT_NOTE:
                handleNoteIntent();
                break;
            default:
                ToastUtils.makeToast(R.string.content_failed_to_parse_intent);
                finish();
                break;
        }
    }

    private void configToolbar() {
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_HAS_TOOLBAR) && intent.getBooleanExtra(EXTRA_HAS_TOOLBAR, false)) {
            getBinding().bar.getRoot().setVisibility(View.VISIBLE);
            getBinding().vShadow.setVisibility(View.VISIBLE);
            setSupportActionBar(getBinding().bar.toolbar);
            if (!isDarkTheme()) getBinding().bar.toolbar.setPopupTheme(R.style.AppTheme_PopupOverlay);
        }
    }

    private void handleNoteIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.EXTRA_MODEL)) {
            if (!(intent.getSerializableExtra(Constants.EXTRA_MODEL) instanceof Note)) {
                ToastUtils.makeToast(R.string.content_failed_to_parse_intent);
                LogUtils.d("Failed to resolve note intent : " + intent);
                finish();
                return;
            }
            note = note != null ? note : (Note) intent.getSerializableExtra(Constants.EXTRA_MODEL);
            int requestCode = intent.getIntExtra(Constants.EXTRA_REQUEST_CODE, -1);
            boolean isEdit = Constants.VALUE_START_EDIT.equals(intent.getStringExtra(Constants.EXTRA_START_TYPE));
            toNoteFragment(note, requestCode == -1 ? null : requestCode, isEdit, false);
        } else if (Constants.ACTION_TO_NOTE_FROM_THIRD_PART.equals(intent.getAction())) {
            note = note != null ? note : ModelFactory.getNote();
            boolean isEdit = Constants.VALUE_START_EDIT.equals(intent.getStringExtra(Constants.EXTRA_START_TYPE));
            toNoteFragment(note, null, isEdit, true);
        }

        // The case below mainly used for the intent from shortcut
        if (intent.hasExtra(Constants.EXTRA_CODE)) {
            long code = intent.getLongExtra(Constants.EXTRA_CODE, -1);
            int requestCode = intent.getIntExtra(Constants.EXTRA_REQUEST_CODE, -1);
            note = note != null ? note : NotesStore.getInstance(this).get(code);
            if (note == null){
                ToastUtils.makeToast(R.string.text_no_such_note);
                LogUtils.d("Failed to resolve intent : " + intent);
                finish();
                return;
            }
            boolean isEdit = Constants.VALUE_START_EDIT.equals(intent.getStringExtra(Constants.EXTRA_START_TYPE));
            toNoteFragment(note, requestCode == -1 ? null : requestCode, isEdit,false);
        }
    }

    private void toNoteFragment(Note note, @Nullable Integer requestCode, boolean isEdit, boolean isThirdPart){
        String action = getIntent() == null || TextUtils.isEmpty(getIntent().getAction()) ? null : getIntent().getAction();
        Fragment fragment;
        if (isEdit) {
            String TAG_NOTE_FRAGMENT = "note_fragment_tag";
            fragment = getSupportFragmentManager().findFragmentByTag(TAG_NOTE_FRAGMENT);
            if (fragment == null) {
                fragment = NoteFragment.newInstance(note, requestCode, isThirdPart, action);
            }
            FragmentHelper.replace(this, fragment, R.id.fragment_container, TAG_NOTE_FRAGMENT);
        } else {
            fragment = NoteViewFragment.newInstance(note, requestCode);
            FragmentHelper.replace(this, fragment, R.id.fragment_container);
        }
    }

    /**
     * Register your events here to receive the color selection message.
     *
     * @param colorChooserDialog the dialog
     * @param i the color selected */
    @Override
    public void onColorSelection(@NonNull ColorChooserDialog colorChooserDialog, @ColorInt int i) {
        Fragment fragment = getCurrentFragment(R.id.fragment_container);
        if (fragment instanceof BaseModelFragment) {
            ((BaseModelFragment) fragment).onColorSelection(i);
        }
    }

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog colorChooserDialog) {}

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getCurrentFragment(R.id.fragment_container);
        if (currentFragment instanceof CommonFragment){
            ((CommonFragment) currentFragment).onBackPressed();
        }
    }

    @Override
    public Intent getIntentForThirdPart() {
        return getIntent();
    }
}
