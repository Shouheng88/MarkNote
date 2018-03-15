package me.shouheng.notepal.fragment;

import android.arch.lifecycle.ViewModelProviders;
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

import javax.annotation.Nonnull;

import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.CategoriesAdapter;
import me.shouheng.notepal.databinding.FragmentCategoriesBinding;
import me.shouheng.notepal.dialog.CategoryEditDialog;
import me.shouheng.notepal.fragment.base.BaseFragment;
import me.shouheng.notepal.model.Category;
import me.shouheng.notepal.model.enums.Status;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.util.ViewUtils;
import me.shouheng.notepal.viewmodel.CategoryViewModel;
import me.shouheng.notepal.widget.tools.CustomItemAnimator;
import me.shouheng.notepal.widget.tools.CustomItemTouchHelper;
import me.shouheng.notepal.widget.tools.DividerItemDecoration;

/**
 * Created by wangshouheng on 2017/3/29.*/
public class CategoriesFragment extends BaseFragment<FragmentCategoriesBinding> implements
        BaseQuickAdapter.OnItemClickListener {

    private final static String ARG_STATUS = "arg_status";

    private RecyclerView.OnScrollListener scrollListener;
    private CategoriesAdapter mAdapter;

    private CategoryEditDialog categoryEditDialog;

    private Status status;
    private CategoryViewModel categoryViewModel;

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
        if (getArguments() != null && getArguments().containsKey(ARG_STATUS))
            status = (Status) getArguments().get(ARG_STATUS);

        categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);

        configToolbar();

        configCategories();
    }

    private void configToolbar() {
        if (getActivity() != null) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(R.string.drawer_menu_tags);
                actionBar.setSubtitle(null);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
            }
        }
    }

    private void configCategories() {
        status = getArguments() == null || !getArguments().containsKey(ARG_STATUS) ? Status.NORMAL : (Status) getArguments().get(ARG_STATUS);

        mAdapter = new CategoriesAdapter(getContext(), Collections.emptyList());
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.iv_more:
                    popCategoryMenu(view, position, mAdapter.getItem(position));
                    break;
            }
        });
        mAdapter.setOnItemClickListener(this);

        getBinding().ivEmpty.setSubTitle(categoryViewModel.getEmptySubTitle(status));

        getBinding().rvCategories.setEmptyView(getBinding().ivEmpty);
        getBinding().rvCategories.setHasFixedSize(true);
        getBinding().rvCategories.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST, isDarkTheme()));
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
                    me.shouheng.notepal.model.data.Status.LOADING);
        }

        categoryViewModel.getCategories(status).observe(this, listResource -> {
            if (listResource == null) {
                ToastUtils.makeToast(R.string.text_failed_to_load_data);
                return;
            }
            if (getActivity() instanceof OnCategoriesInteractListener) {
                ((OnCategoriesInteractListener) getActivity()).onCategoryLoadStateChanged(listResource.status);
            }
            switch (listResource.status) {
                case SUCCESS:
                    mAdapter.setNewData(listResource.data);
                    break;
                case LOADING:
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed_to_load_data);
                    break;
            }
        });

        notifyDataChanged();
    }

    private void update(int position, Category category) {
        categoryViewModel.update(category).observe(this, categoryResource -> {
            if (categoryResource == null) {
                ToastUtils.makeToast(R.string.text_failed_to_modify_data);
                return;
            }
            switch (categoryResource.status) {
                case SUCCESS:
                    mAdapter.notifyItemChanged(position);
                    ToastUtils.makeToast(R.string.text_save_successfully);
                    break;
                case LOADING:
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed_to_modify_data);
                    break;
            }
        });
    }

    private void update(int position, Category category, Status toStatus) {
        categoryViewModel.update(category, toStatus).observe(this, categoryResource -> {
            if (categoryResource == null) {
                ToastUtils.makeToast(R.string.text_failed_to_modify_data);
                return;
            }
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
        categoryViewModel.updateOrders(mAdapter.getData()).observe(this, listResource -> {
            if (listResource == null) {
                LogUtils.d("listResource is null");
                return;
            }
            LogUtils.d(listResource.message);
        });
    }
    // endregion

    private void notifyDataChanged() {
          /*
         * Notify the snagging list is changed. The activity need to record the message, and
         * use it when set result to caller. */
        if (getActivity() != null && getActivity() instanceof OnCategoriesInteractListener) {
            ((OnCategoriesInteractListener) getActivity()).onCategoryDataChanged();
        }
    }

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
        categoryEditDialog = CategoryEditDialog.newInstance(getContext(), param, category ->
                update(position, category));
        categoryEditDialog.show(getFragmentManager(), "CATEGORY_EDIT_DIALOG");
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

    public void setSelectedColor(int color) {
        if (categoryEditDialog != null) categoryEditDialog.updateUIBySelectedColor(color);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.capture, menu);
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
    }

    public interface OnCategoriesInteractListener {

        /**
         * This method will be called, when the snagging list is changed. Do not try to call {@link #reload()}
         * This method as well as {@link NotesFragment.OnNotesInteractListener#onNoteDataChanged()} is only used
         * to record the list change message and handle in future.
         *
         * @see NotesFragment.OnNotesInteractListener#onNoteDataChanged() */
        default void onCategoryDataChanged(){}

        default void onResumeToCategory() {}

        default void onCategorySelected(Category category) {}

        default void onCategoryLoadStateChanged(me.shouheng.notepal.model.data.Status status) {}
    }
}