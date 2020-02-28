package me.shouheng.notepal.fragment;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.content.Context;
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
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.kennyc.bottomsheet.BottomSheet;
import com.kennyc.bottomsheet.BottomSheetListener;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import me.shouheng.commons.activity.CommonActivity;
import me.shouheng.commons.activity.ContainerActivity;
import me.shouheng.commons.activity.interaction.BackEventResolver;
import me.shouheng.commons.event.PageName;
import me.shouheng.commons.event.UMEvent;
import me.shouheng.commons.fragment.WebviewFragment;
import me.shouheng.commons.helper.FragmentHelper;
import me.shouheng.commons.model.data.Resource;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.utils.IntentUtils;
import me.shouheng.commons.utils.LogUtils;
import me.shouheng.commons.utils.PalmUtils;
import me.shouheng.commons.utils.ToastUtils;
import me.shouheng.commons.utils.ViewUtils;
import me.shouheng.commons.widget.Chip;
import me.shouheng.data.ModelFactory;
import me.shouheng.data.entity.Attachment;
import me.shouheng.data.entity.Category;
import me.shouheng.data.entity.Note;
import me.shouheng.easymark.EasyMarkViewer;
import me.shouheng.easymark.viewer.listener.LifecycleListener;
import me.shouheng.notepal.Constants;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.FragmentNoteViewBinding;
import me.shouheng.notepal.dialog.OpenResolver;
import me.shouheng.notepal.manager.FileManager;
import me.shouheng.notepal.manager.NoteManager;
import me.shouheng.notepal.util.AttachmentHelper;
import me.shouheng.notepal.util.ShortcutHelper;
import me.shouheng.notepal.vm.NoteViewerViewModel;

import static me.shouheng.notepal.Constants.EXTENSION_3GP;
import static me.shouheng.notepal.Constants.EXTENSION_MP4;
import static me.shouheng.notepal.Constants.EXTENSION_PDF;
import static me.shouheng.notepal.Constants.MIME_TYPE_OF_PDF;
import static me.shouheng.notepal.Constants.MIME_TYPE_OF_VIDEO;
import static me.shouheng.notepal.Constants.URI_SCHEME_HTTP;
import static me.shouheng.notepal.Constants.URI_SCHEME_HTTPS;

/**
 * The fragment used to display the parsed the markdown text, based on the WebView.
 *
 * Created by WngShhng (shouheng2015@gmail.com) on 2017/5/13.
 * Refactored by WngShhng (shouheng2015@gmail.com) on 2018/11/30
 */
@PageName(name = UMEvent.PAGE_NOTE_VIEW)
public class NoteViewFragment extends BaseFragment<FragmentNoteViewBinding> implements BackEventResolver {

    /**
     * The key for argument, used to send the note model to this fragment.
     */
    public final static String ARGS_KEY_NOTE = "__args_key_note";

    /**
     * The key for argument, used to set the behavior of this fragment. If true, the edit FAB
     * won't be displayed.
     */
    public final static String ARGS_KEY_IS_PREVIEW = "__args_key_is_preview";

    /**
     * The request code for editing this note.
     */
    private final int REQUEST_FOR_EDIT = 0x01;

    private NoteViewerViewModel viewModel;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_note_view;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        viewModel = getViewModel(NoteViewerViewModel.class);
        if (savedInstanceState == null) {
            /* Get the arguments. */
            Bundle arguments = getArguments();
            if (arguments == null || !arguments.containsKey(ARGS_KEY_NOTE)) {
                ToastUtils.makeToast(R.string.text_note_not_found);
                return;
            }
            Note note = (Note) arguments.getSerializable(ARGS_KEY_NOTE);
            boolean isPreview = getArguments().getBoolean(ARGS_KEY_IS_PREVIEW);
            viewModel.setNote(note);
            viewModel.setPreview(isPreview);
        }

        prepareViews();

        addSubscriptions();

        viewModel.readNoteContent();
        viewModel.getNoteCategories();
    }

    /**
     * Config basic behaviors of views, that is, values not associated with note information.
     *
     * In this method we used a base code of current system millis and then add the attachment index
     * to it to get the final code. Since we use the code to judge if two attachment are a same:
     * 1. {@link Attachment#equals(Object)} method has been override;
     * 2. {@link AttachmentHelper#resolveImages(Context, Attachment, List, String)} use the equal method
     * to judge attachment.
     */
    private void prepareViews() {
        /* Config Toolbar. */
        if (getActivity() != null) {
            final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (ab != null) {
                ab.setTitle(viewModel.getNote().getTitle());
                ab.setDisplayHomeAsUpEnabled(true);
            }
        }

        /* Config WebView. */
        getBinding().emv.getFastScrollDelegate().setThumbDrawable(PalmUtils.getDrawableCompact(
                isDarkTheme() ? R.drawable.fast_scroll_bar_dark : R.drawable.fast_scroll_bar_light));
        getBinding().emv.getFastScrollDelegate().setThumbSize(16, 40);
        getBinding().emv.getFastScrollDelegate().setThumbDynamicHeight(false);
        getBinding().emv.useStyleCss(isDarkTheme() ? EasyMarkViewer.DARK_STYLE_CSS : EasyMarkViewer.LIGHT_STYLE_CSS);
        getBinding().emv.setOnImageClickListener((url, urls) -> {
            LogUtils.d(url);
            LogUtils.d(Arrays.toString(urls));
            List<Attachment> attachments = new ArrayList<>();
            Attachment clickedAttachment = null, attachment;
            int index = 0;
            long codeBase = System.currentTimeMillis();
            for (String u : urls) {
                attachment = ModelFactory.getAttachment();
                /* Replace the code of default attachment, since we use the code to judge is two attachment the same. */
                attachment.setCode(codeBase + index++);
                attachment.setUri(Uri.parse(u));
                attachment.setMineType(Constants.MIME_TYPE_IMAGE);
                attachments.add(attachment);
                if (u.equals(url)) {
                    clickedAttachment = attachment;
                }
            }
            AttachmentHelper.resolveClickEvent(getContext(),
                    clickedAttachment,
                    attachments,
                    viewModel.getNote().getTitle());
        });
        getBinding().emv.setOnUrlClickListener(url -> {
            if (!TextUtils.isEmpty(url)) {
                Uri uri = Uri.parse(url);

                /* Handle the url in WebView. */
                if (URI_SCHEME_HTTPS.equalsIgnoreCase(uri.getScheme())
                        || URI_SCHEME_HTTP.equalsIgnoreCase(uri.getScheme())) {
                    ContainerActivity.open(WebviewFragment.class)
                            .put(WebviewFragment.ARGUMENT_KEY_URL, url)
                            .put(WebviewFragment.ARGUMENT_KEY_USE_PAGE_TITLE, true)
                            .launch(getContext());
                    return;
                }

                /* Handle the url of given mime type. */
                if (url.endsWith(EXTENSION_3GP) || url.endsWith(EXTENSION_MP4)) {
                    IntentUtils.startActivity(getContext(), uri, MIME_TYPE_OF_VIDEO);
                } else if (url.endsWith(EXTENSION_PDF)) {
                    IntentUtils.startActivity(getContext(), uri, MIME_TYPE_OF_PDF);
                } else {
                    OpenResolver.newInstance(mimeType ->
                            IntentUtils.startActivity(getContext(), uri, mimeType)
                    ).show(getChildFragmentManager(), "URL RESOLVER");
                }
            }
        });
        getBinding().emv.setLifecycleListener(new LifecycleListener() {
            @Override
            public void onLoadFinished(WebView webView, String str) { }

            @Override
            public void beforeProcessMarkdown(String content) { }

            @Override
            public void afterProcessMarkdown(String document) {
                viewModel.setHtml(document);
            }
        });
        getBinding().emv.setUseMathJax(true);

        /* Config FAB. */
        getBinding().fab.setVisibility(viewModel.isPreview() ? View.GONE : View.VISIBLE);
        getBinding().fab.setOnClickListener(v -> FragmentHelper.open(NoteFragment.class)
                .put(NoteFragment.ARGS_KEY_NOTE, (Serializable) viewModel.getNote())
                .launch(this, REQUEST_FOR_EDIT));

        /* Config Drawer. */
        getBinding().drawer.setIsDarkTheme(isDarkTheme());
        getBinding().drawer.llCopy.setOnClickListener(v -> {
            NoteManager.copy(getActivity(), viewModel.getNote().getContent());
            ToastUtils.makeToast(R.string.note_copied_success);
        });
        getBinding().drawer.llShortcut.setOnClickListener(v -> {
            if (viewModel.getNote().getContentCode() == 0L || TextUtils.isEmpty(viewModel.getNote().getTitle())) {
                ToastUtils.makeToast(R.string.note_shortcut_error_tips);
            } else {
                ShortcutHelper.createShortcut(getActivity().getApplicationContext(), viewModel.getNote());
            }
        });
        getBinding().drawer.llExport.setOnClickListener(v -> showExportDialog());
        getBinding().drawer.llShare.setOnClickListener(v -> showSendDialog());
    }

    private void addSubscriptions() {
        viewModel.getNoteContentObservable().observe(this, resources -> {
            assert resources != null;
            switch (resources.status) {
                case SUCCESS:
                    final ActionBar ab;
                    if (getActivity() != null
                            && (ab = ((AppCompatActivity) getActivity()).getSupportActionBar()) != null) {
                        ab.setTitle(viewModel.getNote().getTitle());
                    }
                    getBinding().emv.processMarkdown(viewModel.getNote().getContent());
                    String charsInfo = getString(R.string.text_chars)
                            + " : " + viewModel.getNote().getContent().length();
                    getBinding().drawer.tvChars.setText(charsInfo);
                    getBinding().drawer.tvNoteInfo.setText(NoteManager.getTimeInfo(viewModel.getNote()));
                    break;
                case LOADING:
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed_to_read_note_file);
                    break;
            }
        });
        viewModel.getCategoriesObservable().observe(this, new Observer<Resource<List<Category>>>() {
            int margin = ViewUtils.dp2Px(PalmApp.getContext(), 2f);
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            @Override
            public void onChanged(@Nullable Resource<List<Category>> resources) {
                assert resources != null;
                switch (resources.status) {
                    case SUCCESS:
                        assert resources.data != null;
                        getBinding().drawer.fl.removeAllViews();
                        Disposable disposable = Observable.fromIterable(resources.data).forEach(category -> {
                            Chip chip = new Chip(getContext());
                            chip.setIcon(category.getPortrait().iconRes);
                            chip.setText(category.getName());
                            chip.setBackgroundColor(category.getColor());
                            ViewGroup.MarginLayoutParams mp = new ViewGroup.MarginLayoutParams(params);
                            mp.setMargins(margin, margin, margin, margin);
                            chip.setLayoutParams(mp);
                            getBinding().drawer.fl.addView(chip);
                        });
                        break;
                    case LOADING:
                        break;
                    case FAILED:
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.note_viewer_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_find);
        initSearchView((SearchView) searchItem.getActionView());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_info:
                getBinding().drawerLayout.openDrawer(GravityCompat.START, true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSendDialog() {
        new BottomSheet.Builder(Objects.requireNonNull(getContext()))
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
                                // Send Raw Text
                                NoteManager.send(getContext(),
                                        viewModel.getNote().getTitle(),
                                        viewModel.getNote().getContent(),
                                        new ArrayList<>());
                                break;
                            case R.id.action_share_html:
                                // Send Html
                                outputHtml(true);
                                break;
                            case R.id.action_share_image:
                                // Send Captured Image
                                createWebCapture(getBinding().emv,
                                        file -> NoteManager.sendFile(getContext(), file, Constants.MIME_TYPE_IMAGE));
                                break;
                        }
                    }

                    @Override
                    public void onSheetDismissed(@NonNull BottomSheet bottomSheet, @Nullable Object o, int i) {}
                })
                .show();
    }

    private void showExportDialog() {
        new BottomSheet.Builder(Objects.requireNonNull(getContext()))
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
                                // Export Html
                                outputHtml(false);
                                break;
                            case R.id.capture:
                                // Export Captured WebView
                                createWebCapture(getBinding().emv,
                                        file -> ToastUtils.makeToast(String.format(getString(R.string.text_file_saved_to),
                                                file.getPath())));
                                break;
                            case R.id.print:
                                // Export Printed WebView
                                NoteManager.printPDF(getContext(), getBinding().emv, viewModel.getNote());
                                break;
                            case R.id.export_text:
                                // Export Raw Text
                                outputContent(false);
                                break;
                        }
                    }

                    @Override
                    public void onSheetDismissed(@NonNull BottomSheet bottomSheet, @Nullable Object o, int i) {}
                })
                .show();
    }

    private void outputHtml(boolean isShare) {
        try {
            File exDir = FileManager.getHtmlExportDir();
            File outFile = new File(exDir, FileManager.getDefaultFileName(Constants.EXPORTED_HTML_EXTENSION));
            FileUtils.writeStringToFile(outFile, viewModel.getHtml(), Constants.NOTE_FILE_ENCODING);
            if (isShare) {
                // Share, do share option
                NoteManager.sendFile(getContext(), outFile, Constants.MIME_TYPE_HTML);
            } else {
                // Not share, just show a message
                ToastUtils.makeToast(String.format(getString(R.string.text_file_saved_to), outFile.getPath()));
            }
        } catch (IOException e) {
            ToastUtils.makeToast(R.string.text_failed_to_save_file);
        }
    }

    private void outputContent(boolean isShare) {
        try {
            File exDir = FileManager.getTextExportDir();
            File outFile = new File(exDir, FileManager.getDefaultFileName(Constants.EXPORTED_TEXT_EXTENSION));
            FileUtils.writeStringToFile(outFile, viewModel.getNote().getContent(), "utf-8");
            if (isShare) {
                // Share, do share option
                NoteManager.sendFile(getContext(), outFile, Constants.MIME_TYPE_FILES);
            } else {
                // Not share, just show a message
                ToastUtils.makeToast(String.format(getString(R.string.text_file_saved_to), outFile.getPath()));
            }
        } catch (IOException e) {
            ToastUtils.makeToast(R.string.text_failed_to_save_file);
        }
    }

    private void initSearchView(SearchView searchView) {
        if (searchView != null) {
            searchView.setQueryHint(getString(R.string.text_find_in_page));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    getBinding().emv.findAllAsync(query);
                    Activity activity = getActivity();
                    if (activity != null) {
                        ((AppCompatActivity) activity).startSupportActionMode(new ActionModeCallback());
                    }
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
                    getBinding().emv.findNext(true);
                    break;
                case R.id.action_last:
                    getBinding().emv.findNext(false);
                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            getBinding().emv.clearMatches();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_FOR_EDIT:
                if (resultCode == Activity.RESULT_OK) {
                    viewModel.readNoteContent();
                    viewModel.getNoteCategories();
                }
                break;
        }
    }
}
