package me.shouheng.notepal.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.ContentActivity;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.databinding.FragmentNoteViewBinding;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.model.Category;
import me.shouheng.notepal.model.Location;
import me.shouheng.notepal.model.ModelFactory;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.provider.AttachmentsStore;
import me.shouheng.notepal.provider.CategoryStore;
import me.shouheng.notepal.provider.LocationsStore;
import me.shouheng.notepal.util.AttachmentHelper;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.util.IntentUtils;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.ModelHelper;
import me.shouheng.notepal.util.PrintUtils;
import me.shouheng.notepal.util.ShortcutHelper;
import me.shouheng.notepal.util.ToastUtils;
import my.shouheng.palmmarkdown.dialog.OpenResolver;

/**
 * Created by wangshouheng on 2017/5/13.*/
public class NoteViewFragment extends BaseFragment<FragmentNoteViewBinding> {

    private final int REQUEST_FOR_EDIT = 0x01;

    private Note note;
    private String content;
    private String tags;

    private boolean isPreview = false;
    private boolean isContentChanged = false;

    public static NoteViewFragment newInstance(@Nonnull Note note, Integer requestCode){
        Bundle arg = new Bundle();
        arg.putSerializable(Constants.EXTRA_MODEL, note);
        if (requestCode != null) arg.putInt(Constants.EXTRA_REQUEST_CODE, requestCode);
        NoteViewFragment fragment = new NoteViewFragment();
        fragment.setArguments(arg);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_note_view;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        handleArguments();

        configToolbar();

        configViews();

        refreshLayout(false);
    }

    private void handleArguments() {
        Bundle arguments = getArguments();

        if (arguments == null || !arguments.containsKey(Constants.EXTRA_MODEL)) {
            throw new IllegalArgumentException("Model is required.");
        }

        note = (Note) arguments.getSerializable(Constants.EXTRA_MODEL);
        List<Category> selections = CategoryStore.getInstance(getContext()).getCategories(note);
        tags = CategoryStore.getTagsName(selections);

        if (TextUtils.isEmpty(note.getContent())) {
            Attachment noteFile = AttachmentsStore.getInstance(getContext()).get(note.getContentCode());
            LogUtils.d("noteFile: " + noteFile);
            if (noteFile == null) {
                ToastUtils.makeToast(getContext(), R.string.note_failed_to_get_note_content);
                return;
            }
            File file = new File(noteFile.getPath());
            LogUtils.d("file: " + file);
            try {
                content = FileUtils.readFileToString(file, "utf-8");
            } catch (IOException e) {
                LogUtils.d("IOException: " + e);
                ToastUtils.makeToast(getContext(), R.string.note_failed_to_read_file);
            }
            note.setContent(content);
        } else {
            isPreview = true;
        }
    }

    private void configToolbar() {
        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) ab.setDisplayHomeAsUpEnabled(false);
        if (!isDarkTheme()) getBinding().toolbar.setPopupTheme(R.style.AppTheme_PopupOverlay);
    }

    private void configViews() {
        getBinding().mdView.getDelegate().setThumbDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recyclerview_fastscroller_handle));
        getBinding().mdView.getDelegate().setThumbSize(8, 32);
        getBinding().mdView.getDelegate().setThumbDynamicHeight(false);
        getBinding().mdView.setWebViewTheme(isDarkTheme());
        getBinding().mdView.setPrimaryColor(primaryColor());
        getBinding().mdView.setPrimaryDark(ColorUtils.calStatusBarColor(primaryColor()));
        getBinding().mdView.setOnImageClickedListener((url, urls) -> {
            List<Attachment> attachments = new ArrayList<>();
            Attachment clickedAttachment = null;
            for (String u : urls) {
                Attachment attachment = getAttachmentFormUrl(u);
                attachments.add(attachment);
                if (u.equals(url)) clickedAttachment = attachment;
            }
            AttachmentHelper.resolveClickEvent(getContext(),
                    clickedAttachment, attachments, note.getTitle());
        });
        getBinding().mdView.setOnAttachmentClickedListener(uri -> {
            OpenResolver.newInstance(mimeType -> {
                startActivity(uri, mimeType.mimeType);
            }).show(getFragmentManager(), "open resolver");
        });
    }

    private void startActivity(Uri uri, String mimeType) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, mimeType);
        if (IntentUtils.isAvailable(getContext(), intent, null)) {
            startActivity(intent);
        } else {
            ToastUtils.makeToast(getContext(), R.string.activity_not_found_to_resolve);
        }
    }

    private void refreshLayout(boolean reload){
        if (!TextUtils.isEmpty(note.getContent())) {
            if (reload) {
                getBinding().mdView.parseMarkdown(note.getContent(), true);
            } else {
                getBinding().mdView.setOnLoadingFinishListener(() ->
                        getBinding().mdView.parseMarkdown(note.getContent(), true));
            }
        }

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(note.getTitle());
        }
    }

    private Attachment getAttachmentFormUrl(String url) {
        Uri uri = Uri.parse(url);
        Attachment attachment = ModelFactory.getAttachment(getContext());
        attachment.setUri(uri);
        attachment.setMineType(Constants.MIME_TYPE_IMAGE);
        return attachment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(!isPreview);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.note_view_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_find);
        initSearchView((SearchView) searchItem.getActionView());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_edit:
                ContentActivity.startNoteEditForResult(this, note, null, REQUEST_FOR_EDIT);
                break;
            case R.id.action_share:
                ModelHelper.share(getContext(), note.getTitle(), content, new ArrayList<>());
                break;
            case R.id.capture:
                createWebCapture(getBinding().mdView);
                break;
            case R.id.print:
                PrintUtils.print(getContext(), getBinding().mdView, note);
                break;
            case R.id.font_cursive:
                getBinding().mdView.getSettings().setCursiveFontFamily("cursive");
                break;
            case R.id.font_fantasy:
                getBinding().mdView.getSettings().setFantasyFontFamily("fantasy");
                break;
            case R.id.font_fixed:
                getBinding().mdView.getSettings().setFixedFontFamily("monospace");
                break;
            case R.id.font_sans_serif:
                getBinding().mdView.getSettings().setSansSerifFontFamily("sans-serif");
                break;
            case R.id.font_serif:
                getBinding().mdView.getSettings().setSerifFontFamily("sans-serif");
                break;
            case R.id.action_labs:
                ModelHelper.showLabels(getContext(), tags);
                break;
            case R.id.action_location:
                showLocation();
                break;
            case R.id.action_copy_link:
                ModelHelper.copyLink(getActivity(), note);
                break;
            case R.id.action_copy_content:
                ModelHelper.copyToClipboard(getActivity(), content);
                ToastUtils.makeToast(getContext(), R.string.content_was_copied_to_clipboard);
                break;
            case R.id.action_add_shortcut:
                ShortcutHelper.addShortcut(getActivity().getApplicationContext(), note);
                ToastUtils.makeToast(getContext(), R.string.successfully_add_shortcut);
                break;
            case R.id.action_statistic:
                note.setContent(content);
                ModelHelper.showStatistic(getContext(), note);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLocation() {
        Location location = LocationsStore.getInstance(getContext()).getLocation(note);
        if (location == null) {
            ToastUtils.makeToast(R.string.text_no_location_info);
            return;
        }
        String strLocation = location.getCountry() + "|" + location.getProvince() + "|" + location.getCity() + "|" + location.getDistrict();
        new MaterialDialog.Builder(getContext())
                .title(R.string.text_location_info)
                .positiveText(R.string.text_confirm)
                .content(strLocation)
                .build()
                .show();
    }

    private void initSearchView(SearchView searchView) {
        if (searchView != null) {
            searchView.setQueryHint(getString(R.string.text_find_in_page));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    getBinding().mdView.findAllAsync(query);
                    ((AppCompatActivity) getActivity()).startSupportActionMode(new ActionModeCallback());
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.note_find_action, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_close:
                    actionMode.finish();
                    break;
                case R.id.action_next:
                    getBinding().mdView.findNext(true);
                    break;
                case R.id.action_last:
                    getBinding().mdView.findNext(false);
                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            getBinding().mdView.clearMatches();
        }
    }

    @Override
    public void onBackPressed() {
        assert getActivity() != null;
        if (isPreview) {
            getActivity().finish();
        } else {
            if (isContentChanged) {
                if (getArguments().containsKey(Constants.EXTRA_REQUEST_CODE)) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.EXTRA_MODEL, (Serializable) note);
                    if (getArguments().containsKey(Constants.EXTRA_POSITION)){
                        intent.putExtra(Constants.EXTRA_POSITION, getArguments().getInt(Constants.EXTRA_POSITION, 0));
                    }
                    getActivity().setResult(Activity.RESULT_OK, intent);
                }
            }
            getActivity().finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_FOR_EDIT:
                if (resultCode == Activity.RESULT_OK){
                    isContentChanged = true;
                    note = (Note) data.getSerializableExtra(Constants.EXTRA_MODEL);
                    refreshLayout(true);
                }
                break;
        }
    }
}
