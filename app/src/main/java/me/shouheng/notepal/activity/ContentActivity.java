package me.shouheng.notepal.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog.ColorCallback;

import java.io.Serializable;

import me.shouheng.notepal.R;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.databinding.ActivityContentBinding;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.provider.NotesStore;
import me.shouheng.notepal.util.FragmentHelper;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.ToastUtils;


public class ContentActivity extends CommonActivity<ActivityContentBinding> implements ColorCallback {

    public static void startNoteEditForResult(Fragment fragment, @NonNull Note note, Integer position, @NonNull Integer requestCode){
        Intent intent = new Intent(fragment.getContext(), ContentActivity.class);
        intent.putExtra(Constants.EXTRA_MODEL, (Serializable) note);
        intent.putExtra(Constants.EXTRA_POSITION, position);
        intent.putExtra(Constants.EXTRA_REQUEST_CODE, requestCode);
        intent.putExtra(Constants.EXTRA_START_TYPE, Constants.VALUE_START_EDIT);
        intent.putExtra(Constants.EXTRA_FRAGMENT, Constants.VALUE_FRAGMENT_NOTE);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void startNoteViewForResult(Fragment fragment, @NonNull Note note, Integer position, @NonNull Integer requestCode){
        Intent intent = new Intent(fragment.getContext(), ContentActivity.class);
        intent.putExtra(Constants.EXTRA_MODEL, (Serializable) note);
        intent.putExtra(Constants.EXTRA_POSITION, position);
        intent.putExtra(Constants.EXTRA_REQUEST_CODE, requestCode);
        intent.putExtra(Constants.EXTRA_START_TYPE, Constants.VALUE_START_VIEW);
        intent.putExtra(Constants.EXTRA_FRAGMENT, Constants.VALUE_FRAGMENT_NOTE);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void startNoteEditForResult(Activity activity, @NonNull Note note, Integer position, @NonNull Integer requestCode){
        Intent intent = new Intent(activity, ContentActivity.class);
        intent.putExtra(Constants.EXTRA_MODEL, (Serializable) note);
        intent.putExtra(Constants.EXTRA_POSITION, position);
        intent.putExtra(Constants.EXTRA_REQUEST_CODE, requestCode);
        intent.putExtra(Constants.EXTRA_START_TYPE, Constants.VALUE_START_EDIT);
        intent.putExtra(Constants.EXTRA_FRAGMENT, Constants.VALUE_FRAGMENT_NOTE);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startNoteViewForResult(Activity activity, @NonNull Note note, Integer position, @NonNull Integer requestCode){
        Intent intent = new Intent(activity, ContentActivity.class);
        intent.putExtra(Constants.EXTRA_MODEL, (Serializable) note);
        intent.putExtra(Constants.EXTRA_POSITION, position);
        intent.putExtra(Constants.EXTRA_REQUEST_CODE, requestCode);
        intent.putExtra(Constants.EXTRA_START_TYPE, Constants.VALUE_START_VIEW);
        intent.putExtra(Constants.EXTRA_FRAGMENT, Constants.VALUE_FRAGMENT_NOTE);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_content;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        handleIntent();
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
            int position = intent.getIntExtra(Constants.EXTRA_POSITION, -1);
            int requestCode = intent.getIntExtra(Constants.EXTRA_REQUEST_CODE, -1);
            toNoteFragment(note,
                    position == -1 ? null : position,
                    requestCode == -1 ? null : requestCode,
                    Constants.VALUE_START_EDIT.equals(intent.getStringExtra(Constants.EXTRA_START_TYPE)));
        }

        // The case below mainly used for the intent from shortcut
        if (intent.hasExtra(Constants.EXTRA_CODE)) {
            long code = intent.getLongExtra(Constants.EXTRA_CODE, -1);
            int position = intent.getIntExtra(Constants.EXTRA_POSITION, -1);
            int requestCode = intent.getIntExtra(Constants.EXTRA_REQUEST_CODE, -1);
            Note note = NotesStore.getInstance(this).get(code);
            if (note == null){
                ToastUtils.makeToast(this, R.string.content_no_such_note);
                LogUtils.d("Failed to resolve intent : " + intent);
                finish();
                return;
            }
            toNoteFragment(note,
                    position == -1 ? null : position,
                    requestCode == -1 ? null : requestCode,
                    Constants.VALUE_START_EDIT.equals(intent.getStringExtra(Constants.EXTRA_START_TYPE)));
        }
    }

    private void toNoteFragment(Note note, @Nullable Integer position, @Nullable Integer requestCode, boolean isEdit){
        Fragment fragment = isEdit ? null : null;
        FragmentHelper.replace(this, fragment, R.id.fragment_container);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog colorChooserDialog, @ColorInt int i) {}

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog colorChooserDialog) {}
}
