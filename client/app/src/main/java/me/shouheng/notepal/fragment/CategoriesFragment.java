package me.shouheng.notepal.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.Collections;
import java.util.Objects;

import javax.annotation.Nonnull;

import me.shouheng.commons.event.RxMessage;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.utils.LogUtils;
import me.shouheng.commons.utils.ViewUtils;
import me.shouheng.data.model.enums.Status;
import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.CategoriesAdapter;
import me.shouheng.notepal.databinding.FragmentCategoriesBinding;
import me.shouheng.notepal.dialog.CategoryEditDialog;
import me.shouheng.notepal.fragment.base.BaseFragment;
import me.shouheng.data.entity.Category;
import me.shouheng.commons.utils.ToastUtils;
import me.shouheng.notepal.viewmodel.CategoryViewModel;
import me.shouheng.commons.widget.recycler.CustomItemAnimator;
import me.shouheng.commons.widget.recycler.CustomItemTouchHelper;
import me.shouheng.commons.widget.recycler.DividerItemDecoration;

/**
 * Created by wangshouheng on 2017/3/29.*/
public class CategoriesFragment extends BaseFragment<FragmentCategoriesBinding> implements BaseQuickAdapter.OnItemClickListener {

    private final static String ARG_STATUS = "__args_key_status";

    private RecyclerView.OnScrollListener scrollListener;
    private CategoriesAdapter mAdapter;

    private Status status;
    private CategoryViewModel viewModel;

    public static CategoriesFragment newInstance() {
        Bundle args = new Bundle();
        CategoriesFragment fragment = new CategoriesFragment();
        args.putSerializable(ARG_STATUS, Status.NORMAL);
        fragment.setArguments(args);
        return fragment;
    }

    public static CategoriesFragment newInstance(@Nonnull Status status) {
        Bundle args = new Bundle();
        CategoriesFragment fragment = new CategoriesFragment();
        args.putSerializable(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_categories;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);

        // handle arguments
        if (getArguments() != null && getArguments().containsKey(ARG_STATUS)) {
            status = (Status) getArguments().get(ARG_STATUS);
        }

        configToolbar();

        configCategories();
    }

    private void configToolbar() {
        if (getActivity() != null) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(R.string.drawer_menu_categories);
                actionBar.setSubtitle(null);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(ColorUtils.tintDrawable(R.drawable.ic_menu_black,
                        getThemeStyle().isDarkTheme ? Color.WHITE : Color.BLACK));
            }
        }
    }

    private void configCategories() {
        mAdapter = new CategoriesAdapter(getContext(), Collections.emptyList());
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.iv_more:
                    popCategoryMenu(view, position, Objects.requireNonNull(mAdapter.getItem(position)));
                    break;
            }
        });
        mAdapter.setOnItemClickListener(this);

        getBinding().ivEmpty.setSubTitle(viewModel.getEmptySubTitle(status));

        getBinding().rvCategories.setEmptyView(getBinding().ivEmpty);
        getBinding().rvCategories.setHasFixedSize(true);
        getBinding().rvCategories.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL_LIST, isDarkTheme()));
        getBinding().rvCategories.setItemAnimator(new CustomItemAnimator());
        getBinding().rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        getBinding().rvCategories.setAdapter(mAdapter);
        if (scrollListener != null) getBinding().rvCategories.addOnScrollListener(scrollListener);

        ItemTouchHelper.Callback callback = new CustomItemTouchHelper(true, false, mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(getBinding().rvCategories);

        reload();
    }

    // region ViewModel
    public void reload() {
        if (getActivity() instanceof OnCategoriesInteractListener) {
            ((OnCategoriesInteractListener) getActivity()).onCategoryLoadStateChanged(
                    me.shouheng.commons.model.data.Status.LOADING);
        }

        viewModel.getCategories(status).observe(this, listResource -> {
            assert listResource != null;
            if (getActivity() instanceof OnCategoriesInteractListener) {
                ((OnCategoriesInteractListener) getActivity()).onCategoryLoadStateChanged(listResource.status);
            }
            switch (listResource.status) {
                case SUCCESS:
                    mAdapter.setNewData(listResource.data);
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed_to_load_data);
                    break;
            }
        });

        postEvent(new RxMessage(RxMessage.CODE_CATEGORY_DATA_CHANGED, null));
    }

    private void update(int position, Category category) {
        viewModel.update(category).observe(this, categoryResource -> {
            assert categoryResource != null;
            switch (categoryResource.status) {
                case SUCCESS:
                    mAdapter.notifyItemChanged(position);
                    ToastUtils.makeToast(R.string.text_save_successfully);
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed_to_modify_data);
                    break;
            }
        });
    }

    private void update(int position, Category category, Status toStatus) {
        viewModel.update(category, toStatus).observe(this, categoryResource -> {
            assert categoryResource != null;
            switch (categoryResource.status) {
                case SUCCESS:
                    mAdapter.remove(position);
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed_to_modify_data);
                    break;
                case LOADING:
                    break;
            }
        });
    }

    private void updateOrders() {
        viewModel.updateOrders(mAdapter.getData()).observe(this, listResource -> {
            if (listResource == null) {
                LogUtils.d("listResource is null");
                return;
            }
            LogUtils.d(listResource.message);
        });
    }
    // endregion

    public void addCategory(Category category) {
        mAdapter.addData(0, category);
        getBinding().rvCategories.smoothScrollToPosition(0);
    }

    public void setScrollListener(RecyclerView.OnScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    private void popCategoryMenu(View v, int position, Category param) {
        final int categoryColor = param.getColor();
        PopupMenu popupM = new PopupMenu(getContext(), v);
        popupM.inflate(R.menu.category_pop_menu);
        popupM.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.action_edit:
                    showEditor(position, mAdapter.getItem(position));
                    break;
                case R.id.action_delete:
                    showDeleteDialog(position, mAdapter.getItem(position));
                    break;
            }
            return true;
        });
        popupM.show();
    }

    private void showEditor(int position, Category param) {
        CategoryEditDialog.newInstance(param, category -> update(position, category))
                .show(getFragmentManager(), "CATEGORY_EDIT_DIALOG");
    }

    private void showDeleteDialog(int position, Category param) {
        new MaterialDialog.Builder(getContext())
                .title(R.string.text_warning)
                .content(R.string.tag_delete_message)
                .positiveText(R.string.text_confirm)
                .onPositive((materialDialog, dialogAction) -> update(position, param, Status.DELETED))
                .negativeText(R.string.text_cancel)
                .onNegative((materialDialog, dialogAction) -> materialDialog.dismiss())
                .show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // disabled capture function currently
//        inflater.inflate(R.menu.capture, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_capture:
                createScreenCapture(getBinding().rvCategories, ViewUtils.dp2Px(getContext(), 60));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter.isPositionChanged()){
            updateOrders();
        }
        configToolbar();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (getActivity() != null && getActivity() instanceof OnCategoriesInteractListener) {
            ((OnCategoriesInteractListener) getActivity()).onCategorySelected(mAdapter.getItem(position));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity() instanceof OnCategoriesInteractListener) {
            ((OnCategoriesInteractListener) getActivity()).onResumeToCategory();
        }
        configToolbar();
    }

    public interface OnCategoriesInteractListener {

        default void onResumeToCategory() {}

        default void onCategorySelected(Category category) {}

        default void onCategoryLoadStateChanged(me.shouheng.commons.model.data.Status status) {}
    }
}