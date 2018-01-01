package me.shouheng.notepal.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
import me.shouheng.notepal.util.AttachmentHelper;
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

    private MindSnaggingDialog snaggingDialog;

    private AttachmentPickerDialog attachmentPickerDialog;

    private MindSnaggingListType mindSnaggingListType;

    private PreferencesUtils preferencesUtils;

    public static SnaggingsFragment newInstance() {
        Bundle args = new Bundle();
        SnaggingsFragment fragment = new SnaggingsFragment();
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
        mindSnaggingListType = preferencesUtils.getMindSnaggingListType();

        adapter = new MindSnaggingAdapter(getContext(), mindSnaggingListType, getSnaggings());
        adapter.setOnItemClickListener((adapter1, view, position) -> showEditor(position));

        getBinding().rv.setItemAnimator(new CustomItemAnimator());
        getBinding().rv.setEmptyView(getBinding().ivEmpty);
        getBinding().rv.setAdapter(adapter);
        if (scrollListener != null) getBinding().rv.addOnScrollListener(scrollListener);

        switch (mindSnaggingListType) {
            case ONE_COL:configForOneCol();break;
            case TWO_COLS:configForTwoCols();break;
        }
    }

    private void configForOneCol() {
        getBinding().rv.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL_LIST, isDarkTheme()));
        getBinding().rv.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void configForTwoCols() {
        int dp4 = ViewUtils.dp2Px(getContext(), 4);
        getBinding().rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        getBinding().rv.addItemDecoration(new SpaceItemDecoration(dp4, dp4, dp4, dp4));
    }

    private void showEditor(int position) {
        snaggingDialog = new MindSnaggingDialog.Builder()
                .setOnAttachmentClickListener(attachment -> AttachmentHelper.resolveClickEvent(
                        getContext(), attachment, Arrays.asList(attachment), ""))
                .setOnConfirmListener((mindSnagging, attachment) ->
                        saveMindSnagging(position, mindSnagging, attachment))
                .setOnAddAttachmentListener(mindSnagging -> showSnaggingAttachmentPicker())
                .setMindSnagging(adapter.getItem(position))
                .build();
        snaggingDialog.show(getFragmentManager(), "snag");
    }

    private List<MindSnagging> getSnaggings() {
        MindSnaggingStore store = MindSnaggingStore.getInstance(getContext());
        if (getArguments() == null || !getArguments().containsKey(ARG_STATUS)) {
            return store.get(null, MindSnaggingSchema.ADDED_TIME + " DESC ");
        }
        Status status = (Status) getArguments().get(ARG_STATUS);
        return status == Status.ARCHIVED ?
                store.getArchived(null, MindSnaggingSchema.ADDED_TIME + " DESC ") :
                status == Status.TRASHED ?
                        store.getTrashed(null, MindSnaggingSchema.ADDED_TIME + " DESC ") :
                        store.get(null, MindSnaggingSchema.ADDED_TIME + " DESC ");
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
                R.drawable.ic_view_stream_white_24dp : R.drawable.ic_view_module_white_24dp);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.mind_snaggings, menu);
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
        attachmentPickerDialog = new AttachmentPickerDialog.Builder()
                .setRecordVisible(false)
                .setVideoVisible(false)
                .build();
        attachmentPickerDialog.show(getFragmentManager(), "Attachment picker");
    }

    public interface OnSnagginsInteractListener {
        void onListTypeChanged(MindSnaggingListType listType);
    }
}
