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
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog.ColorCallback;

import java.io.Serializable;

import me.shouheng.notepal.R;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.databinding.ActivityContentBinding;
import me.shouheng.notepal.fragment.CommonFragment;
import me.shouheng.notepal.fragment.NoteFragment;
import me.shouheng.notepal.fragment.NoteFragment.OnNoteInteractListener;
import me.shouheng.notepal.fragment.NoteViewFragment;
import me.shouheng.notepal.model.ModelFactory;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.provider.NotesStore;
import me.shouheng.notepal.util.FragmentHelper;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.ToastUtils;

public class ContentActivity extends CommonActivity<ActivityContentBinding> implements ColorCallback, OnNoteInteractListener {

    public final static String EXTRA_HAS_TOOLBAR = "extra_has_toolbar";

    public static void startNoteEditForResult(Fragment fragment, @NonNull Note note, Integer position, @NonNull Integer requestCode){
        fragment.startActivityForResult(getNoteEditIntent(fragment.getContext(), note, position, requestCode), requestCode);
    }

    public static void startNoteViewForResult(Fragment fragment, @NonNull Note note, Integer position, @NonNull Integer requestCode){
        fragment.startActivityForResult(getNoteViewIntent(fragment.getContext(), note, position, requestCode), requestCode);
    }

    public static void startNoteEditForResult(Activity activity, @NonNull Note note, Integer position, @NonNull Integer requestCode){
        activity.startActivityForResult(getNoteEditIntent(activity, note, position, requestCode), requestCode);
    }

    public static void startNoteViewForResult(Activity activity, @NonNull Note note, Integer position, @NonNull Integer requestCode){
        activity.startActivityForResult(getNoteViewIntent(activity, note, position, requestCode), requestCode);
    }

    public static void startThirdPartResult(Activity activity, Intent i, @NonNull Integer requestCode) {
        i.setClass(activity, ContentActivity.class);
        i.putExtra(Constants.EXTRA_IS_GOOGLE_NOW, Constants.INTENT_GOOGLE_NOW.equals(i.getAction()));
        i.setAction(Constants.ACTION_TO_NOTE_FROM_THIRD_PART);
        i.putExtra(Constants.EXTRA_FRAGMENT, Constants.VALUE_FRAGMENT_NOTE);
        i.putExtra(Constants.EXTRA_REQUEST_CODE, requestCode);
        i.putExtra(Constants.EXTRA_START_TYPE, Constants.VALUE_START_EDIT);
        activity.startActivity(i);
    }

    public static void startAction(Activity activity, String action, @Nullable Integer requestCode) {
        Intent i = new Intent(activity, ContentActivity.class);
        i.setAction(action);
        i.putExtra(Constants.EXTRA_MODEL, (Parcelable) ModelFactory.getNote(activity));
        i.putExtra(Constants.EXTRA_FRAGMENT, Constants.VALUE_FRAGMENT_NOTE);
        i.putExtra(Constants.EXTRA_REQUEST_CODE, requestCode);
        i.putExtra(Constants.EXTRA_START_TYPE, Constants.VALUE_START_EDIT);
        activity.startActivity(i);
    }

    private static Intent getNoteViewIntent(Context context, @NonNull Note note, Integer position, @NonNull Integer requestCode) {
        Intent intent = new Intent(context, ContentActivity.class);
        intent.putExtra(Constants.EXTRA_MODEL, (Serializable) note);
        intent.putExtra(Constants.EXTRA_POSITION, position);
        intent.putExtra(Constants.EXTRA_REQUEST_CODE, requestCode);
        intent.putExtra(Constants.EXTRA_START_TYPE, Constants.VALUE_START_VIEW);
        intent.putExtra(Constants.EXTRA_FRAGMENT, Constants.VALUE_FRAGMENT_NOTE);
        intent.putExtra(EXTRA_HAS_TOOLBAR, true);
        return intent;
    }

    private static Intent getNoteEditIntent(Context context, @NonNull Note note, Integer position, @NonNull Integer requestCode) {
        Intent intent = new Intent(context, ContentActivity.class);
        intent.putExtra(Constants.EXTRA_MODEL, (Serializable) note);
        intent.putExtra(Constants.EXTRA_POSITION, position);
        intent.putExtra(Constants.EXTRA_REQUEST_CODE, requestCode);
        intent.putExtra(Constants.EXTRA_START_TYPE, Constants.VALUE_START_EDIT);
        intent.putExtra(Constants.EXTRA_FRAGMENT, Constants.VALUE_FRAGMENT_NOTE);
        return intent;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_content;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        handleIntent();

        configToolbar();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(Constants.EXTRA_FRAGMENT)) {
            ToastUtils.makeToast(this, R.string.content_failed_to_parse_intent);
            LogUtils.d("Faile to handle intent : " + intent);
            finish();
            return;
        }
        switch (intent.getStringExtra(Constants.EXTRA_FRAGMENT)) {
            case Constants.VALUE_FRAGMENT_NOTE:
                handleNoteIntent();
                break;
            default:
                ToastUtils.makeToast(this, R.string.content_failed_to_parse_intent);
                finish();
                break;
        }
    }

    private void configToolbar() {
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_HAS_TOOLBAR) && intent.getBooleanExtra(EXTRA_HAS_TOOLBAR, false)) {
            Toolbar toolbar = getBinding().bar.findViewById(R.id.toolbar);
            getBinding().bar.setVisibility(View.VISIBLE);
            setSupportActionBar(toolbar);
            if (!isDarkTheme()) toolbar.setPopupTheme(R.style.AppTheme_PopupOverlay);
        }
    }

    private void handleNoteIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.EXTRA_MODEL)) {
            if (!(intent.getSerializableExtra(Constants.EXTRA_MODEL) instanceof Note)) {
                ToastUtils.makeToast(this, R.string.content_failed_to_parse_intent);
                LogUtils.d("Failed to resolve note intent : " + intent);
                finish();
                return;
            }
            Note note = (Note) intent.getSerializableExtra(Constants.EXTRA_MODEL);
            int requestCode = intent.getIntExtra(Constants.EXTRA_REQUEST_CODE, -1);
            boolean isEdit = Constants.VALUE_START_EDIT.equals(intent.getStringExtra(Constants.EXTRA_START_TYPE));
            toNoteFragment(note, requestCode == -1 ? null : requestCode, isEdit, false);
        } else if (Constants.ACTION_TO_NOTE_FROM_THIRD_PART.equals(intent.getAction())) {
            boolean isEdit = Constants.VALUE_START_EDIT.equals(intent.getStringExtra(Constants.EXTRA_START_TYPE));
            toNoteFragment(ModelFactory.getNote(this), null, isEdit, true);
        }

        // The case below mainly used for the intent from shortcut
        if (intent.hasExtra(Constants.EXTRA_CODE)) {
            long code = intent.getLongExtra(Constants.EXTRA_CODE, -1);
            int requestCode = intent.getIntExtra(Constants.EXTRA_REQUEST_CODE, -1);
            Note note = NotesStore.getInstance(this).get(code);
            if (note == null){
                ToastUtils.makeToast(this, R.string.content_no_such_note);
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
        Fragment fragment = isEdit ?
                NoteFragment.newInstance(note, requestCode, isThirdPart, action) :
                NoteViewFragment.newInstance(note, requestCode);
        FragmentHelper.replace(this, fragment, R.id.fragment_container);
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog colorChooserDialog, @ColorInt int i) {}

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog colorChooserDialog) {}

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment instanceof CommonFragment){
            ((CommonFragment) currentFragment).onBackPressed();
        }
    }

    @Override
    public Intent getIntentForThirdPart() {
        return getIntent();
    }
}
