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
import me.shouheng.notepal.databinding.ActivityContentBinding;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.provider.NotesStore;
import me.shouheng.notepal.util.FragmentHelper;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.ToastUtils;


public class ContentActivity extends CommonActivity<ActivityContentBinding> implements ColorCallback {

    private final static String EXTRA_MODEL = "extra_model";
    private final static String EXTRA_CODE = "extra_code";

    private final static String EXTRA_POSITION = "extra_position";

    private final static String EXTRA_REQUEST_CODE = "extra_request_code";

    private final static String EXTRA_START_TYPE = "extra_start_type";
    private final static String VALUE_START_VIEW = "value_start_view";
    private final static String VALUE_START_EDIT = "value_start_edit";

    private final static String EXTRA_FRAGMENT = "extra_fragment";
    private final static String VALUE_FRAGMENT_NOTE = "value_fragment_note";

    public static void startNoteEditForResult(Fragment fragment, @NonNull Note note, Integer position, @NonNull Integer requestCode){
        Intent intent = new Intent(fragment.getContext(), ContentActivity.class);
        intent.putExtra(EXTRA_MODEL, (Serializable) note);
        intent.putExtra(EXTRA_POSITION, position);
        intent.putExtra(EXTRA_REQUEST_CODE, requestCode);
        intent.putExtra(EXTRA_START_TYPE, VALUE_START_EDIT);
        intent.putExtra(EXTRA_FRAGMENT, VALUE_FRAGMENT_NOTE);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void startNoteViewForResult(Fragment fragment, @NonNull Note note, Integer position, @NonNull Integer requestCode){
        Intent intent = new Intent(fragment.getContext(), ContentActivity.class);
        intent.putExtra(EXTRA_MODEL, (Serializable) note);
        intent.putExtra(EXTRA_POSITION, position);
        intent.putExtra(EXTRA_REQUEST_CODE, requestCode);
        intent.putExtra(EXTRA_START_TYPE, VALUE_START_VIEW);
        intent.putExtra(EXTRA_FRAGMENT, VALUE_FRAGMENT_NOTE);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void startNoteViewForResult(Activity activity, @NonNull Note note, Integer position, @NonNull Integer requestCode){
        Intent intent = new Intent(activity, ContentActivity.class);
        intent.putExtra(EXTRA_MODEL, (Serializable) note);
        intent.putExtra(EXTRA_POSITION, position);
        intent.putExtra(EXTRA_REQUEST_CODE, requestCode);
        intent.putExtra(EXTRA_START_TYPE, VALUE_START_VIEW);
        intent.putExtra(EXTRA_FRAGMENT, VALUE_FRAGMENT_NOTE);
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
        if (intent == null || !intent.hasExtra(EXTRA_FRAGMENT)) {
            ToastUtils.makeToast(this, R.string.content_failed_to_parse_intent);
            LogUtils.d("Faile to handle intent : " + intent);
            finish();
            return;
        }
        switch (intent.getStringExtra(EXTRA_FRAGMENT)) {
            case VALUE_FRAGMENT_NOTE:
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
        if (intent.hasExtra(EXTRA_MODEL)) {
            if (!(intent.getSerializableExtra(EXTRA_MODEL) instanceof Note)) {
                ToastUtils.makeToast(this, R.string.content_failed_to_parse_intent);
                LogUtils.d("Failed to resolve note intent : " + intent);
                finish();
                return;
            }
            Note note = (Note) intent.getSerializableExtra(EXTRA_MODEL);
            int position = intent.getIntExtra(EXTRA_POSITION, -1);
            int requestCode = intent.getIntExtra(EXTRA_REQUEST_CODE, -1);
            toNoteFragment(note,
                    position == -1 ? null : position,
                    requestCode == -1 ? null : requestCode,
                    VALUE_START_EDIT.equals(intent.getStringExtra(EXTRA_START_TYPE)));
        }

        // The case below mainly used for the intent from shortcut
        if (intent.hasExtra(EXTRA_CODE)) {
            long code = intent.getLongExtra(EXTRA_CODE, -1);
            int position = intent.getIntExtra(EXTRA_POSITION, -1);
            int requestCode = intent.getIntExtra(EXTRA_REQUEST_CODE, -1);
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
                    VALUE_START_EDIT.equals(intent.getStringExtra(EXTRA_START_TYPE)));
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
