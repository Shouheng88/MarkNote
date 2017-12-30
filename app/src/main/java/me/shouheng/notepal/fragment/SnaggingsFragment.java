package me.shouheng.notepal.fragment;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.MindSnaggingAdapter;
import me.shouheng.notepal.databinding.FragmentSnaggingsBinding;
import me.shouheng.notepal.dialog.AttachmentPickerDialog;
import me.shouheng.notepal.dialog.MindSnaggingDialog;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.model.MindSnagging;
import me.shouheng.notepal.model.enums.ModelType;
import me.shouheng.notepal.provider.AttachmentsStore;
import me.shouheng.notepal.provider.MindSnaggingStore;
import me.shouheng.notepal.provider.schema.MindSnaggingSchema;
import me.shouheng.notepal.util.AttachmentHelper;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.widget.tools.CustomItemAnimator;
import me.shouheng.notepal.widget.tools.DividerItemDecoration;

/**
 * Created by Wang Shouheng on 2017/12/30.*/
public class SnaggingsFragment extends BaseFragment<FragmentSnaggingsBinding> {

    private RecyclerView.OnScrollListener scrollListener;

    private MindSnaggingAdapter adapter;

    private MindSnaggingDialog snaggingDialog;

    private AttachmentPickerDialog attachmentPickerDialog;

    public static SnaggingsFragment newInstance() {
        Bundle args = new Bundle();
        SnaggingsFragment fragment = new SnaggingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_snaggings;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        configToolbar();

        configSnaggings();
    }

    private void configToolbar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.drawer_menu_minds);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void configSnaggings() {
        adapter = new MindSnaggingAdapter(getContext(), getSnaggings());
        adapter.setOnItemClickListener((adapter1, view, position) -> showEditor(position));

        getBinding().rv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST, isDarkTheme()));
        getBinding().rv.setItemAnimator(new CustomItemAnimator());
        getBinding().rv.setLayoutManager(new LinearLayoutManager(getContext()));
        if (scrollListener != null) getBinding().rv.addOnScrollListener(scrollListener);
        getBinding().rv.setEmptyView(getBinding().ivEmpty);
        getBinding().rv.setAdapter(adapter);
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
        return MindSnaggingStore.getInstance(getContext()).get(null,
                MindSnaggingSchema.ADDED_TIME + " DESC ");
    }

    public void addSnagging(MindSnagging snagging) {
        adapter.addData(0, snagging);
        getBinding().rv.scrollToPosition(0);
    }

    public void setScrollListener(RecyclerView.OnScrollListener scrollListener) {
        this.scrollListener = scrollListener;
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
}
