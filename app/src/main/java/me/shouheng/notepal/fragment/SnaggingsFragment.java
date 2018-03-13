package me.shouheng.notepal.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.Collections;

import javax.annotation.Nonnull;

import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.MindSnaggingAdapter;
import me.shouheng.notepal.databinding.FragmentSnaggingsBinding;
import me.shouheng.notepal.dialog.AttachmentPickerDialog;
import me.shouheng.notepal.dialog.MindSnaggingDialog;
import me.shouheng.notepal.fragment.base.BaseFragment;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.model.MindSnagging;
import me.shouheng.notepal.model.enums.ModelType;
import me.shouheng.notepal.model.enums.Status;
import me.shouheng.notepal.util.AppWidgetUtils;
import me.shouheng.notepal.util.AttachmentHelper;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.ModelHelper;
import me.shouheng.notepal.util.PalmUtils;
import me.shouheng.notepal.util.PreferencesUtils;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.util.ViewUtils;
import me.shouheng.notepal.util.enums.MindSnaggingListType;
import me.shouheng.notepal.viewmodel.AttachmentViewModel;
import me.shouheng.notepal.viewmodel.SnaggingViewModel;
import me.shouheng.notepal.widget.tools.CustomItemAnimator;
import me.shouheng.notepal.widget.tools.DividerItemDecoration;
import me.shouheng.notepal.widget.tools.SpaceItemDecoration;

/**
 * Created by Wang Shouheng on 2017/12/30.*/
public class SnaggingsFragment extends BaseFragment<FragmentSnaggingsBinding> {

    private final static String ARG_STATUS = "arg_status";
    private final static int REFRESH_DELAY = 500;

    private RecyclerView.OnScrollListener scrollListener;

    private MindSnaggingDialog mindSnaggingDialog;

    private MindSnaggingListType mindSnaggingListType;
    private PreferencesUtils preferencesUtils;

    private MindSnaggingAdapter adapter;
    private SnaggingViewModel snaggingViewModel;
    private AttachmentViewModel attachmentViewModel;

    private Status status;

    public static SnaggingsFragment newInstance() {
        Bundle args = new Bundle();
        SnaggingsFragment fragment = new SnaggingsFragment();
        args.putSerializable(ARG_STATUS, Status.NORMAL);
        fragment.setArguments(args);
        return fragment;
    }

    public static SnaggingsFragment newInstance(@Nonnull Status status) {
        Bundle args = new Bundle();
        SnaggingsFragment fragment = new SnaggingsFragment();
        args.putSerializable(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_snaggings;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        preferencesUtils = PreferencesUtils.getInstance(getContext());

        snaggingViewModel = ViewModelProviders.of(this).get(SnaggingViewModel.class);
        attachmentViewModel = ViewModelProviders.of(this).get(AttachmentViewModel.class);

        configToolbar();

        configSnagging();
    }

    private void configToolbar() {
        if (getActivity() != null) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(R.string.drawer_menu_minds);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private void configSnagging() {
        mindSnaggingListType = preferencesUtils.getMindSnaggingListType();

        status = getArguments() == null || !getArguments().containsKey(ARG_STATUS) ?
                Status.NORMAL : (Status) getArguments().get(ARG_STATUS);
        getBinding().ivEmpty.setSubTitle(snaggingViewModel.getEmptySubTitle(status));

        adapter = new MindSnaggingAdapter(getContext(), mindSnaggingListType, Collections.emptyList());
        adapter.setOnItemClickListener((adapter1, view, position) -> showEditor(position));
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.iv_more:
                    popMoreMenu(view, position);
                    break;
            }
        });

        getBinding().rv.setItemAnimator(new CustomItemAnimator());
        getBinding().rv.setEmptyView(getBinding().ivEmpty);
        getBinding().rv.setAdapter(adapter);
        if (scrollListener != null) getBinding().rv.addOnScrollListener(scrollListener);

        switch (mindSnaggingListType) {
            case ONE_COL:configForOneCol();break;
            case TWO_COLS:configForTwoCols();break;
        }

        loadFirstPage();
    }

    // region More Menu
    private void popMoreMenu(View v, int position) {
        PopupMenu popupM = new PopupMenu(getContext(), v);
        popupM.inflate(R.menu.pop_menu);
        configPopMenu(popupM);
        popupM.getMenu().findItem(R.id.action_move).setVisible(false);
        popupM.getMenu().findItem(R.id.action_share).setVisible(true);
        popupM.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.action_share:
                    ModelHelper.share(getContext(), adapter.getItem(position));
                    break;
                case R.id.action_trash:
                    update(position, adapter.getItem(position), Status.TRASHED);
                    break;
                case R.id.action_archive:
                    update(position, adapter.getItem(position), Status.ARCHIVED);
                    break;
                case R.id.action_edit:
                    showEditor(position);
                    break;
                case R.id.action_move_out:
                    update(position, adapter.getItem(position), Status.NORMAL);
                    break;
                case R.id.action_delete:
                    update(position, adapter.getItem(position), Status.DELETED);
                    break;
            }
            return true;
        });
        popupM.show();
    }

    private void refreshLayout() {
        new Handler().postDelayed(() -> adapter.notifyDataSetChanged(), REFRESH_DELAY);
    }

    private void configPopMenu(PopupMenu popupMenu) {
        if (getArguments() == null || !getArguments().containsKey(ARG_STATUS)) return;
        Status status = (Status) getArguments().get(ARG_STATUS);
        popupMenu.getMenu().findItem(R.id.action_move_out).setVisible(status == Status.ARCHIVED || status == Status.TRASHED);
        popupMenu.getMenu().findItem(R.id.action_edit).setVisible(status == Status.ARCHIVED || status == Status.NORMAL);
        popupMenu.getMenu().findItem(R.id.action_trash).setVisible(status == Status.NORMAL || status == Status.ARCHIVED);
        popupMenu.getMenu().findItem(R.id.action_archive).setVisible(status == Status.NORMAL);
        popupMenu.getMenu().findItem(R.id.action_delete).setVisible(status == Status.TRASHED);
    }
    // endregion

    private void configForOneCol() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        getBinding().rv.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL_LIST, isDarkTheme()));
        getBinding().rv.setLayoutManager(layoutManager);
        getBinding().fastscroller.setRecyclerView(getBinding().rv);

        getBinding().rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();
                if (lastVisibleItem + 1 == totalItemCount && dy > 0) {
                    if (!snaggingViewModel.isLoadingMore()) {
                        recyclerView.post(() -> loadMoreData());
                    }
                }
            }
        });
    }

    private void configForTwoCols() {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(
                ViewUtils.getScreenOrientation(getContext()) == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2,
                StaggeredGridLayoutManager.VERTICAL);

        int margin = ViewUtils.dp2Px(getContext(), PalmUtils.isLollipop() ? 4 : 2);
        getBinding().rv.setLayoutManager(layoutManager);
        getBinding().rv.addItemDecoration(new SpaceItemDecoration(margin, margin, margin, margin));
        getBinding().fastscroller.setVisibility(View.GONE);

        getBinding().rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int lastVisibleItem = layoutManager.findLastVisibleItemPositions(null)[0];
                int totalItemCount = layoutManager.getItemCount();
                if (totalItemCount - lastVisibleItem < 10 && dy > 0) {
                    if (!snaggingViewModel.isLoadingMore()) {
                        recyclerView.post(() -> loadMoreData());
                    }
                }
            }
        });
    }

    // region Interaction with ViewModel
    private void loadFirstPage() {
        snaggingViewModel.getCount(null, null, false).observe(this, integerResource -> {
            if (integerResource == null) {
                return;
            }
            switch (integerResource.status) {
                case SUCCESS:
                    snaggingViewModel.setModelsCount(integerResource.data == null ? 0 : integerResource.data);
                    // Load the first page
                    reload();
                    break;
                case FAILED:
                    LogUtils.d("Failed to get models count!");
                    break;
            }
        });
    }

    /**
     * The reload method only load the first page. */
    public void reload() {
        snaggingViewModel.loadSnagging(status).observe(this, listResource -> {
            if (listResource == null) {
                ToastUtils.makeToast(R.string.text_failed_to_load_data);
                return;
            }
            switch (listResource.status) {
                case SUCCESS:
                    getBinding().sl.setVisibility(View.GONE);
                    adapter.setNewData(listResource.data);
                    break;
                case LOADING:
                    getBinding().sl.setVisibility(View.VISIBLE);
                    break;
                case FAILED:
                    getBinding().sl.setVisibility(View.GONE);
                    ToastUtils.makeToast(R.string.text_failed_to_load_data);
                    break;
            }
        });
    }

    private void loadMoreData() {
        snaggingViewModel.loadMore(status).observe(this, listResource -> {
            snaggingViewModel.setLoadingMore(false);
            if (listResource == null) {
                return;
            }
            switch (listResource.status) {
                case FAILED:
                    if (SnaggingViewModel.ERROR_MSG_NO_MODE_DATA.equals(listResource.message)) {
                        LogUtils.d(listResource.message);
                    } else {
                        ToastUtils.makeToast(R.string.text_failed_to_load_data);
                    }
                    break;
                case SUCCESS:
                    if (listResource.data != null) {
                        adapter.addData(listResource.data);
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case LOADING:
                    break;
            }
        });
    }

    private void update(int position, MindSnagging snagging, Status toStatus) {
        snaggingViewModel.update(snagging, toStatus).observe(this, resource -> {
            if (resource == null) {
                ToastUtils.makeToast(R.string.text_failed_to_modify_data);
                return;
            }
            switch (resource.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    adapter.remove(position);
                    refreshLayout();
                    notifyDataChanged();
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed_to_modify_data);
                    break;
            }
        });
    }

    private void saveMindSnagging(int position, MindSnagging mindSnagging, Attachment attachment) {
        if (attachment != null) {
            attachment.setModelCode(mindSnagging.getCode());
            attachment.setModelType(ModelType.MIND_SNAGGING);
            attachmentViewModel.saveIfNew(attachment);
        }

        snaggingViewModel.saveOrUpdate(mindSnagging).observe(this, mindSnaggingResource -> {
            if (mindSnaggingResource == null) {
                ToastUtils.makeToast(R.string.text_failed_to_modify_data);
                return;
            }
            switch (mindSnaggingResource.status) {
                case SUCCESS:
                    ToastUtils.makeToast(R.string.text_save_successfully);
                    notifyDataChanged();
                    adapter.notifyItemChanged(position);
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed_to_modify_data);
                    break;
                case LOADING:break;
            }
        });
    }
    // endregion

    private void notifyDataChanged() {
        /*
         * notify the app widget that the list is changed. */
        AppWidgetUtils.notifyAppWidgets(getContext());

        /*
         * Notify the attached activity that the list is changed. */
        if (getActivity() != null && getActivity() instanceof OnSnaggingInteractListener) {
            ((OnSnaggingInteractListener) getActivity()).onSnaggingDataChanged();
        }
    }

    private void showEditor(int position) {
        mindSnaggingDialog = new MindSnaggingDialog.Builder()
                .setOnAttachmentClickListener(attachment ->
                        AttachmentHelper.resolveClickEvent(getContext(),
                                attachment, Collections.singletonList(attachment), ""))
                .setOnConfirmListener((mindSnagging, attachment) ->
                        saveMindSnagging(position, mindSnagging, attachment))
                .setOnAddAttachmentListener(mindSnagging -> showAttachmentPicker())
                .setMindSnagging(adapter.getItem(position))
                .build();
        mindSnaggingDialog.show(getFragmentManager(), "snag");
    }

    public void addSnagging(MindSnagging snagging) {
        adapter.addData(0, snagging);
        getBinding().rv.scrollToPosition(0);
    }

    public void setScrollListener(RecyclerView.OnScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_list_type).setIcon(mindSnaggingListType == MindSnaggingListType.ONE_COL ?
                R.drawable.ic_view_module_white_24dp : R.drawable.ic_view_stream_white_24dp);

        /*
         * set the menu item invisible when in archive and trash list */
        if (status == Status.ARCHIVED || status == Status.TRASHED) {
            menu.findItem(R.id.action_list_type).setVisible(false);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.mind_snaggings, menu);
        /*
         * DISABLED FUNCTION REASON: the Glide in {@link MindSnaggingAdapter#convert(BaseViewHolder, Object)}
         * must be called in the main thread. */
//        inflater.inflate(R.menu.capture, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_list_type:
                preferencesUtils.setMindSnaggingListType(getListTypeToSwitch());
                if (getActivity() != null) getActivity().invalidateOptionsMenu();
                if (getActivity() instanceof OnSnaggingInteractListener) {
                    ((OnSnaggingInteractListener) getActivity()).onListTypeChanged(mindSnaggingListType);
                }
                break;
            case R.id.action_capture:
                createScreenCapture(getBinding().rv);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private MindSnaggingListType getListTypeToSwitch() {
        mindSnaggingListType = mindSnaggingListType == MindSnaggingListType.ONE_COL ?
                MindSnaggingListType.TWO_COLS : MindSnaggingListType.ONE_COL;
        return mindSnaggingListType;
    }

    private void showAttachmentPicker() {
        new AttachmentPickerDialog.Builder(this)
                .setRecordVisible(false)
                .setVideoVisible(false)
                .build().show(getFragmentManager(), "Attachment picker");
    }

    @Override
    protected void onGetAttachment(@NonNull Attachment attachment) {
        mindSnaggingDialog.setAttachment(attachment);
    }

    @Override
    protected void onFailedGetAttachment(Attachment attachment) {
        ToastUtils.makeToast(R.string.failed_to_save_attachment);
    }

    public interface OnSnaggingInteractListener {

        /**
         * The method will be called when list changed between grid-style and list-style.
         *
         * @param listType current list type
         */
        default void onListTypeChanged(MindSnaggingListType listType){}

        /**
         * This method will be called, when the snagging list is changed. Do not try to call {@link #reload()}
         * This method as well as {@link NotesFragment.OnNotesInteractListener#onNoteDataChanged()} is only used
         * to record the list change message and handle in future.
         *
         * @see NotesFragment.OnNotesInteractListener#onNoteDataChanged()
         */
        default void onSnaggingDataChanged(){}
    }
}
