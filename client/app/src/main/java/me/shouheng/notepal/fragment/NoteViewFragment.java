package me.shouheng.notepal.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kennyc.bottomsheet.BottomSheet;
import com.kennyc.bottomsheet.BottomSheetListener;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import me.shouheng.commons.activity.CommonActivity;
import me.shouheng.commons.activity.ContainerActivity;
import me.shouheng.commons.fragment.WebviewFragment;
import me.shouheng.commons.activity.interaction.BackEventResolver;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.utils.IntentUtils;
import me.shouheng.commons.utils.LogUtils;
import me.shouheng.commons.utils.ViewUtils;
import me.shouheng.commons.widget.Chip;
import me.shouheng.easymark.EasyMarkViewer;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.ContentActivity;
import me.shouheng.notepal.Constants;
import me.shouheng.notepal.databinding.FragmentNoteViewBinding;
import me.shouheng.notepal.dialog.OpenResolver;
import me.shouheng.notepal.fragment.base.BaseFragment;
import me.shouheng.data.entity.Attachment;
import me.shouheng.data.entity.Category;
import me.shouheng.data.ModelFactory;
import me.shouheng.data.entity.Note;
import me.shouheng.data.store.AttachmentsStore;
import me.shouheng.data.store.CategoryStore;
import me.shouheng.notepal.util.AttachmentHelper;
import me.shouheng.notepal.util.FileHelper;
import me.shouheng.notepal.util.ModelHelper;
import me.shouheng.notepal.util.PrintUtils;
import me.shouheng.notepal.util.ShortcutHelper;
import me.shouheng.commons.utils.ToastUtils;

import static me.shouheng.notepal.Constants.PDF_MIME_TYPE;
import static me.shouheng.notepal.Constants.SCHEME_HTTP;
import static me.shouheng.notepal.Constants.SCHEME_HTTPS;
import static me.shouheng.notepal.Constants.VIDEO_MIME_TYPE;
import static me.shouheng.notepal.Constants._3GP;
import static me.shouheng.notepal.Constants._MP4;
import static me.shouheng.notepal.Constants._PDF;

/**
 * Created by wangshouheng on 2017/5/13.*/
public class NoteViewFragment extends BaseFragment<FragmentNoteViewBinding> implements BackEventResolver {

    public final static String ARGS_KEY_NOTE = "__args_key_note";
    public final static String ARGS_KEY_IS_PREVIEW = "__args_key_is_preview";

    private final static int REQUEST_FOR_EDIT = 0x01;

    private Note note;
    private String content;
    private boolean isPreview = false;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_note_view;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        handleArguments();

        configToolbar();

        configViews();
    }

    @Override
    protected String umengPageName() {
        return "Note Viewer";
    }

    private void handleArguments() {
        Bundle arguments = getArguments();

        if (arguments == null || !arguments.containsKey(ARGS_KEY_NOTE)) {
            ToastUtils.makeToast(R.string.text_no_such_note);
            return;
        }

        note = (Note) arguments.getSerializable(ARGS_KEY_NOTE);
        isPreview = getArguments().getBoolean(ARGS_KEY_IS_PREVIEW);

        if (!isPreview) {
            Attachment noteFile = AttachmentsStore.getInstance().get(note.getContentCode());
            LogUtils.d("noteFile: " + noteFile);
            if (noteFile == null) {
                ToastUtils.makeToast(R.string.note_failed_to_get_note_content);
                // default content is empty string, to avoid NPE
                note.setContent("");
                return;
            }
            File file = new File(noteFile.getPath());
            LogUtils.d("file: " + file);
            try {
                content = FileUtils.readFileToString(file, "utf-8");
            } catch (IOException e) {
                LogUtils.d("IOException: " + e);
                ToastUtils.makeToast(R.string.note_failed_to_read_file);
            }
            note.setContent(content);
        }
    }

    private void configToolbar() {
        if (getActivity() != null) {
            final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (ab != null) {
                ab.setTitle(note.getTitle());
                ab.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private void configViews() {
        // config webview
        getBinding().mdView.getFastScrollDelegate().setThumbDrawable(PalmApp.getDrawableCompact(
                isDarkTheme() ? R.drawable.fast_scroll_bar_dark : R.drawable.fast_scroll_bar_light));
        getBinding().mdView.getFastScrollDelegate().setThumbSize(16, 40);
        getBinding().mdView.getFastScrollDelegate().setThumbDynamicHeight(false);
        getBinding().mdView.useStyleCss(isDarkTheme() ? EasyMarkViewer.DARK_STYLE_CSS : EasyMarkViewer.LIGHT_STYLE_CSS);
        getBinding().mdView.processMarkdown(note.getContent());
        getBinding().mdView.setOnImageClickListener((url, urls) -> {
            List<Attachment> attachments = new ArrayList<>();
            Attachment clickedAttachment = null;
            for (String u : urls) {
                Attachment attachment = getAttachmentFormUrl(u);
                attachments.add(attachment);
                if (u.equals(url)) clickedAttachment = attachment;
            }
            AttachmentHelper.resolveClickEvent(getContext(), clickedAttachment, attachments, note.getTitle());
        });
        getBinding().mdView.setOnUrlClickListener(url -> {
            if (!TextUtils.isEmpty(url)){
                Uri uri = Uri.parse(url);

                // Open the http or https link from chrome tab.
                if (SCHEME_HTTPS.equalsIgnoreCase(uri.getScheme())
                        || SCHEME_HTTP.equalsIgnoreCase(uri.getScheme())) {
                    ContainerActivity.open(WebviewFragment.class)
                            .put(WebviewFragment.ARGUMENT_KEY_URL, uri)
                            .put(WebviewFragment.ARGUMENT_KEY_USE_PAGE_TITLE, true)
                            .launch(getContext());
                    return;
                }

                // Open the files of given format.
                if (url.endsWith(_3GP) || url.endsWith(_MP4)) {
                    startActivity(uri, VIDEO_MIME_TYPE);
                } else if (url.endsWith(_PDF)) {
                    startActivity(uri, PDF_MIME_TYPE);
                } else {
                    OpenResolver.newInstance(mimeType -> startActivity(uri, mimeType.mimeType))
                            .show(getChildFragmentManager(), "OPEN RESOLVER");
                }
            }
        });

        getBinding().fab.setOnClickListener(v -> ContentActivity.editNote(this, note));

        // region config drawer
        getBinding().drawer.setIsDarkTheme(isDarkTheme());
        int margin = ViewUtils.dp2Px(getContext(), 2f);
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        List<Category> categories = CategoryStore.getInstance().getCategories(note);
        Disposable disposable = Observable.fromIterable(categories).forEach(category -> {
            Chip chip = new Chip(getContext());
            chip.setIcon(category.getPortrait().iconRes);
            chip.setText(category.getName());
            chip.setBackgroundColor(category.getColor());
            ViewGroup.MarginLayoutParams mp = new ViewGroup.MarginLayoutParams(params);
            mp.setMargins(margin, margin, margin, margin);
            chip.setLayoutParams(mp);
            getBinding().drawer.fl.addView(chip);
        });

        String charsInfo = getString(R.string.text_chars) + " : " +note.getContent().length();
        getBinding().drawer.tvChars.setText(charsInfo);
        getBinding().drawer.tvNoteInfo.setText(ModelHelper.getTimeInfo(note));

        getBinding().drawer.llCopy.setOnClickListener(v -> {
            ModelHelper.copyToClipboard(getActivity(), content);
            ToastUtils.makeToast(R.string.content_was_copied_to_clipboard);
        });
        getBinding().drawer.llShortcut.setOnClickListener(v -> {
            // TODO add short cut for new version bug....
            ShortcutHelper.addShortcut(getActivity().getApplicationContext(), note);
            ToastUtils.makeToast(R.string.successfully_add_shortcut);
        });
        getBinding().drawer.llExport.setOnClickListener(v -> export());
        getBinding().drawer.llShare.setOnClickListener(v -> share());
    }

    private void startActivity(Uri uri, String mimeType) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, mimeType);
        if (IntentUtils.isAvailable(getContext(), intent, null)) {
            startActivity(intent);
        } else {
            ToastUtils.makeToast(R.string.activity_not_found_to_resolve);
        }
    }

    private void refreshLayout() {
        if (!TextUtils.isEmpty(note.getContent())) {
            getBinding().mdView.processMarkdown(note.getContent());
        }

        if (getActivity() != null) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(note.getTitle());
            }
        }
    }

    private Attachment getAttachmentFormUrl(String url) {
        Uri uri = Uri.parse(url);
        Attachment attachment = ModelFactory.getAttachment();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.note_view_menu, menu);
//        MenuItem searchItem = menu.findItem(R.id.action_find);
//        initSearchView((SearchView) searchItem.getActionView());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                resolve();
                break;
            case R.id.action_info:
                getBinding().drawerLayout.openDrawer(GravityCompat.START, true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void share() {
        new BottomSheet.Builder(Objects.requireNonNull(getActivity()))
                .setStyle(isDarkTheme() ? R.style.BottomSheet_Dark : R.style.BottomSheet)
                .setMenu(ColorUtils.getThemedBottomSheetMenu(getContext(), R.menu.share))
                .setTitle(R.string.text_share)
                .setListener(new BottomSheetListener() {
                    @Override
                    public void onSheetShown(@NonNull BottomSheet bottomSheet, @Nullable Object o) {}

                    @Override
                    public void onSheetItemSelected(@NonNull BottomSheet bottomSheet, MenuItem menuItem, @Nullable Object o) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_share_text:
                                ModelHelper.share(getContext(), note.getTitle(), content, new ArrayList<>());
                                break;
                            case R.id.action_share_html:
                                outHtml(true);
                                break;
                            case R.id.action_share_image:
                                createWebCapture(getBinding().mdView, file -> ModelHelper.shareFile(getContext(), file, Constants.MIME_TYPE_IMAGE));
                                break;
                        }
                    }

                    @Override
                    public void onSheetDismissed(@NonNull BottomSheet bottomSheet, @Nullable Object o, int i) {}
                })
                .show();
    }

    private void export() {
        new BottomSheet.Builder(Objects.requireNonNull(getActivity()))
                .setStyle(isDarkTheme() ? R.style.BottomSheet_Dark : R.style.BottomSheet)
                .setMenu(ColorUtils.getThemedBottomSheetMenu(getContext(), R.menu.export))
                .setTitle(R.string.text_export)
                .setListener(new BottomSheetListener() {
                    @Override
                    public void onSheetShown(@NonNull BottomSheet bottomSheet, @Nullable Object o) {}

                    @Override
                    public void onSheetItemSelected(@NonNull BottomSheet bottomSheet, MenuItem menuItem, @Nullable Object o) {
                        switch (menuItem.getItemId()) {
                            case R.id.export_html:
                                // Export html
                                outHtml(false);
                                break;
                            case R.id.capture:
                                createWebCapture(getBinding().mdView, file -> ToastUtils.makeToast(String.format(getString(R.string.text_file_saved_to), file.getPath())));
                                break;
                            case R.id.print:
                                PrintUtils.print(getContext(), getBinding().mdView, note);
                                break;
                            case R.id.export_text:
                                outText(false);
                                break;
                        }
                    }

                    @Override
                    public void onSheetDismissed(@NonNull BottomSheet bottomSheet, @Nullable Object o, int i) {}
                })
                .show();
    }

    // TODO
    private void outHtml(boolean isShare) {
//        getBinding().mdView.outHtml(html -> {
//            try {
//                File exDir = FileHelper.getHtmlExportDir();
//                File outFile = new File(exDir, FileHelper.getDefaultFileName(Constants.EXPORTED_HTML_EXTENSION));
//                FileUtils.writeStringToFile(outFile, html, "utf-8");
//                if (isShare) {
//                    // Share, do share option
//                    ModelHelper.shareFile(getContext(), outFile, Constants.MIME_TYPE_HTML);
//                } else {
//                    // Not share, just show a message
//                    ToastUtils.makeToast(String.format(getString(R.string.text_file_saved_to), outFile.getPath()));
//                }
//            } catch (IOException e) {
//                ToastUtils.makeToast(R.string.failed_to_create_file);
//            }
//        });
    }

    private void outText(boolean isShare) {
        try {
            File exDir = FileHelper.getTextExportDir();
            File outFile = new File(exDir, FileHelper.getDefaultFileName(Constants.EXPORTED_TEXT_EXTENSION));
            FileUtils.writeStringToFile(outFile, note.getContent(), "utf-8");
            if (isShare) {
                // Share, do share option
                ModelHelper.shareFile(getContext(), outFile, Constants.MIME_TYPE_FILES);
            } else {
                // Not share, just show a message
                ToastUtils.makeToast(String.format(getString(R.string.text_file_saved_to), outFile.getPath()));
            }
        } catch (IOException e) {
            ToastUtils.makeToast(R.string.failed_to_create_file);
        }
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

    @Override
    public void resolve() {
        Activity activity = getActivity();
        if (activity instanceof CommonActivity) {
            ((CommonActivity) activity).superOnBackPressed();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_FOR_EDIT:
                if (resultCode == Activity.RESULT_OK) {
                    // TODO change categories when note data changed !
                    note = (Note) data.getSerializableExtra(Constants.EXTRA_MODEL);
                    refreshLayout();
                }
                break;
        }
    }
}
