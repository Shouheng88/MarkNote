package me.shouheng.notepal.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
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

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.MindSnaggingAdapter;
import me.shouheng.notepal.databinding.FragmentSnaggingsBinding;
import me.shouheng.notepal.dialog.AttachmentPickerDialog;
import me.shouheng.notepal.dialog.MindSnaggingDialog;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.model.MindSnagging;
import me.shouheng.notepal.model.enums.ModelType;
import me.shouheng.notepal.model.enums.Status;
import me.shouheng.notepal.provider.AttachmentsStore;
import me.shouheng.notepal.provider.MindSnaggingStore;
import me.shouheng.notepal.provider.schema.MindSnaggingSchema;
import me.shouheng.notepal.util.AppWidgetUtils;
import me.shouheng.notepal.util.AttachmentHelper;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.PreferencesUtils;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.util.ViewUtils;
import me.shouheng.notepal.util.enums.MindSnaggingListType;
import me.shouheng.notepal.widget.tools.CustomItemAnimator;
import me.shouheng.notepal.widget.tools.DividerItemDecoration;
import me.shouheng.notepal.widget.tools.SpaceItemDecoration;

/**
 * Created by Wang Shouheng on 2017/12/30.*/
public class SnaggingsFragment extends BaseFragment<FragmentSnaggingsBinding> {

    private final static String ARG_STATUS = "arg_status";

    private RecyclerView.OnScrollListener scrollListener;

    private MindSnaggingAdapter adapter;

    private MindSnaggingDialog mindSnaggingDialog;

    private AttachmentPickerDialog attachmentPickerDialog;

    private MindSnaggingListType mindSnaggingListType;

    private PreferencesUtils preferencesUtils;

    private int modelsCount, pageNumber = 20, startIndex = 0;
    private boolean isLoadingMore = false;

    private Status status;

    private MindSnaggingStore store;

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

        configToolbar();

        configSnaggings();
    }

    private void configToolbar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.drawer_menu_minds);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void configSnaggings() {
        getBinding().ivEmpty.setSubTitle(getEmptySubTitle());

        mindSnaggingListType = preferencesUtils.getMindSnaggingListType();

        adapter = new MindSnaggingAdapter(getContext(), mindSnaggingListType, getSnaggings());
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
    }

    // region More Menu
    private void popMoreMenu(View v, int position) {
        PopupMenu popupM = new PopupMenu(getContext(), v);
        popupM.inflate(R.menu.pop_menu);
        configPopMenu(popupM);
        popupM.getMenu().findItem(R.id.action_move).setVisible(false);
        popupM.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.action_trash:
                    MindSnaggingStore.getInstance(getContext()).update(adapter.getItem(position), Status.TRASHED);
                    adapter.remove(position);
                    refreshLayout();
                    break;
                case R.id.action_archive:
                    MindSnaggingStore.getInstance(getContext()).update(adapter.getItem(position), Status.ARCHIVED);
                    adapter.remove(position);
                    refreshLayout();
                    break;
                case R.id.action_edit:
                    showEditor(position);
                    break;
                case R.id.action_move_out:
                    MindSnaggingStore.getInstance(getContext()).update(adapter.getItem(position), Status.NORMAL);
                    adapter.remove(position);
                    refreshLayout();
                    break;
                case R.id.action_delete:
                    MindSnaggingStore.getInstance(getContext()).update(adapter.getItem(position), Status.DELETED);
                    adapter.remove(position);
                    refreshLayout();
                    break;
            }
            return true;
        });
        popupM.show();
    }

    private void refreshLayout() {
        AppWidgetUtils.notifyAppWidgets(getContext());
        new Handler().postDelayed(() -> adapter.notifyDataSetChanged(), 500);
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
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();
                if (lastVisibleItem + 1 == totalItemCount && dy > 0) {
                    if (!isLoadingMore) {
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

        int dp4 = ViewUtils.dp2Px(getContext(), 4);
        getBinding().rv.setLayoutManager(layoutManager);
        getBinding().rv.addItemDecoration(new SpaceItemDecoration(dp4, dp4, dp4, dp4));
        getBinding().fastscroller.setVisibility(View.GONE);

        getBinding().rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = layoutManager.findLastVisibleItemPositions(null)[0];
                int totalItemCount = layoutManager.getItemCount();
                if (totalItemCount - lastVisibleItem < 10 && dy > 0) {
                    if (!isLoadingMore) {
                        recyclerView.post(() -> loadMoreData());
                    }
                }
            }
        });
    }

    private void showEditor(int position) {
        mindSnaggingDialog = new MindSnaggingDialog.Builder()
                .setOnAttachmentClickListener(attachment -> AttachmentHelper.resolveClickEvent(getContext(), attachment, Arrays.asList(attachment), ""))
                .setOnConfirmListener((mindSnagging, attachment) -> {
                    AppWidgetUtils.notifyAppWidgets(getContext());
                    saveMindSnagging(position, mindSnagging, attachment);
                })
                .setOnAddAttachmentListener(mindSnagging -> showSnaggingAttachmentPicker())
                .setMindSnagging(adapter.getItem(position))
                .build();
        mindSnaggingDialog.show(getFragmentManager(), "snag");
    }

    private List<MindSnagging> getSnaggings() {
        status = getArguments() == null || !getArguments().containsKey(ARG_STATUS) ? Status.NORMAL : (Status) getArguments().get(ARG_STATUS);
        String orderSQL = MindSnaggingSchema.ADDED_TIME + " DESC ";

        store = MindSnaggingStore.getInstance(getContext());

        modelsCount = store.getCount(null, status, false);
        List<MindSnagging> snaggingList;
        if (modelsCount <= pageNumber) { // per page count > total cont -> LOAD ALL
            snaggingList = store.get(null, orderSQL, status, false);
        } else { // load first page
            snaggingList = store.getPage(startIndex, pageNumber, orderSQL, status, false);
        }

        return snaggingList;
    }

    private void loadMoreData() {
        LogUtils.d("startIndex:" + startIndex);
        isLoadingMore = true;
        // 初始位置移动20
        startIndex += pageNumber;
        List<MindSnagging> timeLines;
        if (startIndex > modelsCount) {
            isLoadingMore = false;
            startIndex -= pageNumber;
            return;
        } else if (startIndex + pageNumber > modelsCount) { // 如果将要加载的总数超出了数目总数
            timeLines = store.getPage(startIndex, startIndex + pageNumber - modelsCount, MindSnaggingSchema.ADDED_TIME + " DESC ", Status.NORMAL, false);
        } else {
            timeLines = store.getPage(startIndex, pageNumber, MindSnaggingSchema.ADDED_TIME + " DESC ", Status.NORMAL, false);
        }
        adapter.addData(timeLines);
        adapter.notifyDataSetChanged();
        isLoadingMore = false;
    }

    public void addSnagging(MindSnagging snagging) {
        adapter.addData(0, snagging);
        getBinding().rv.scrollToPosition(0);
    }

    public void setScrollListener(RecyclerView.OnScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    private String getEmptySubTitle() {
        if (getArguments() == null || !getArguments().containsKey(ARG_STATUS)) return null;
        Status status = (Status) getArguments().get(ARG_STATUS);
        if (status == null) return null;
        switch (status) {
            case NORMAL:
                return getString(R.string.mind_snaggings_list_empty_sub_normal);
            case TRASHED:
                return getString(R.string.mind_snaggings_list_empty_sub_trashed);
            case ARCHIVED:
                return getString(R.string.mind_snaggings_list_empty_sub_archived);
        }
        return getString(R.string.mind_snaggings_list_empty_sub_normal);
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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.mind_snaggings, menu);
        /**
         * DISABLED FUNCTION REASON: the Glide in {@link MindSnaggingAdapter#convert(BaseViewHolder, Object)}
         * must be called in the main thread. */
//        inflater.inflate(R.menu.capture, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_list_type:
                preferencesUtils.setMindSnaggingListType(getListTypeToSwitch());
                getActivity().invalidateOptionsMenu();
                if (getActivity() instanceof OnSnagginsInteractListener) {
                    ((OnSnagginsInteractListener) getActivity()).onListTypeChanged(mindSnaggingListType);
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

    @Override
    protected AttachmentPickerDialog getAttachmentPickerDialog() {
        return attachmentPickerDialog;
    }

    private void saveMindSnagging(int position, MindSnagging mindSnagging, Attachment attachment) {
        if (attachment != null && AttachmentsStore.getInstance(getContext()).isNewModel(attachment.getCode())) {
            attachment.setModelCode(mindSnagging.getCode());
            attachment.setModelType(ModelType.MIND_SNAGGING);
            AttachmentsStore.getInstance(getContext()).saveModel(attachment);
        }
        if (MindSnaggingStore.getInstance(getContext()).isNewModel(mindSnagging.getCode())) {
            MindSnaggingStore.getInstance(getContext()).saveModel(mindSnagging);
        } else {
            MindSnaggingStore.getInstance(getContext()).update(mindSnagging);
        }
        ToastUtils.makeToast(getContext(), R.string.text_save_successfully);
        adapter.notifyItemChanged(position);
    }

    private void showSnaggingAttachmentPicker() {
        attachmentPickerDialog = new AttachmentPickerDialog.Builder(this)
                .setRecordVisible(false)
                .setVideoVisible(false)
                .build();
        attachmentPickerDialog.show(getFragmentManager(), "Attachment picker");
    }

    @Override
    protected void onGetAttachment(Attachment attachment) {
        mindSnaggingDialog.setAttachment(attachment);
    }

    @Override
    protected void onFailedGetAttachment(Attachment attachment) {
        ToastUtils.makeToast(R.string.failed_to_save_attachment);
    }

    public interface OnSnagginsInteractListener {
        void onListTypeChanged(MindSnaggingListType listType);
    }
}
