package me.shouheng.notepal.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.Collections;

import me.shouheng.commons.event.PageName;
import me.shouheng.commons.event.RxMessage;
import me.shouheng.commons.event.*;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.utils.ToastUtils;
import me.shouheng.commons.utils.ViewUtils;
import me.shouheng.commons.widget.recycler.CustomItemAnimator;
import me.shouheng.commons.widget.recycler.DividerItemDecoration;
import me.shouheng.data.entity.Category;
import me.shouheng.data.model.enums.Status;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.CategoriesAdapter;
import me.shouheng.notepal.databinding.FragmentCategoriesBinding;
import me.shouheng.notepal.dialog.CategoryEditDialog;
import me.shouheng.notepal.vm.CategoriesViewModel;

/**
 * Fragment used to display the categories.
 *
 * Created by WngShhng (shouheng2015@gmail.com) on 2017/3/29.
 */
@PageName(name = UMEvent.PAGE_CATEGORIES)
public class CategoriesFragment extends BaseFragment<FragmentCategoriesBinding> implements BaseQuickAdapter.OnItemClickListener {

    /**
     * The argument key for this fragment. The status of current categories list.
     * Or null of showing the normal categories.
     */
    public final static String ARGS_KEY_STATUS = "__args_key_status";

    private RecyclerView.OnScrollListener scrollListener;
    private CategoriesAdapter mAdapter;
    private CategoriesViewModel viewModel;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_categories;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        viewModel = getViewModel(CategoriesViewModel.class);
        if (savedInstanceState == null) {
            if (getArguments() != null && getArguments().containsKey(ARGS_KEY_STATUS)) {
                Status status = (Status) getArguments().get(ARGS_KEY_STATUS);
                viewModel.setStatus(status);
            }
        }

        configToolbar();

        /* Config the categories list. */
        mAdapter = new CategoriesAdapter(getContext(), Collections.emptyList());
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.iv_more:
                    Category category = mAdapter.getItem(position);
                    if (category != null) {
                        popMenu(view, category);
                    }
                    break;
            }
        });
        mAdapter.setOnItemClickListener(this);
        getBinding().rvCategories.setEmptyView(getBinding().ivEmpty);
        getBinding().rvCategories.setHasFixedSize(true);
        getBinding().rvCategories.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL_LIST, isDarkTheme()));
        getBinding().rvCategories.setItemAnimator(new CustomItemAnimator());
        getBinding().rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        getBinding().ivEmpty.setSubTitle(viewModel.getEmptySubTitle());
        getBinding().rvCategories.setAdapter(mAdapter);
        if (scrollListener != null) {
            getBinding().rvCategories.addOnScrollListener(scrollListener);
        }

        addSubscriptions();

        viewModel.fetchCategories();
    }

    private void configToolbar() {
        Activity activity = getActivity();
        if (activity != null) {
            ActionBar ab = ((AppCompatActivity) activity).getSupportActionBar();
            if (ab != null) {
                ab.setTitle(R.string.drawer_menu_categories);
                ab.setSubtitle(null);
                ab.setDisplayHomeAsUpEnabled(true);
                ab.setHomeAsUpIndicator(ColorUtils.tintDrawable(R.drawable.ic_menu_black,
                        getThemeStyle().isDarkTheme ? Color.WHITE : Color.BLACK));
            }
        }
    }

    private void addSubscriptions() {
        viewModel.getCategoriesLiveData().observe(this, resources -> {
            assert resources != null;
            switch (resources.status) {
                case SUCCESS:
                    mAdapter.setNewData(resources.data);
                    getBinding().ivEmpty.showEmptyIcon();
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed);
                    getBinding().ivEmpty.showEmptyIcon();
                    break;
                case LOADING:
                    getBinding().ivEmpty.showProgressBar();
                    break;
            }
        });
        viewModel.getCategoryUpdateObserver().observe(this, resource -> {
            assert resource != null;
            switch (resource.status) {
                case SUCCESS:
                    viewModel.fetchCategories();
                    break;
                case LOADING:
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed);
                    break;
            }
        });
        addSubscription(RxMessage.class, RxMessage.CODE_CATEGORY_DATA_CHANGED,
                rxMessage -> viewModel.fetchCategories());
        addSubscription(RxMessage.class, RxMessage.CODE_NOTE_DATA_CHANGED,
                rxMessage -> viewModel.fetchCategories());
    }

    public void addCategory(Category category) {
        mAdapter.addData(0, category);
        getBinding().rvCategories.smoothScrollToPosition(0);
    }

    public void setScrollListener(RecyclerView.OnScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    private void popMenu(View v, Category param) {
        PopupMenu popupM = new PopupMenu(getContext(), v);
        popupM.inflate(R.menu.category_pop_menu);
        popupM.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.action_edit:
                    CategoryEditDialog.newInstance(param,
                            category -> viewModel.updateCategory(category)
                    ).show(getChildFragmentManager(), "CATEGORY_EDIT_DIALOG");
                    break;
                case R.id.action_delete:
                    viewModel.updateCategory(param, Status.DELETED);
                    break;
            }
            return true;
        });
        popupM.show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // inflater.inflate(R.menu.capture, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_capture:
                createScreenCapture(getBinding().rvCategories, ViewUtils.dp2Px(PalmApp.getContext(), 60));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Activity activity = getActivity();
        if (activity instanceof CategoriesInteraction) {
            ((CategoriesInteraction) activity).onCategorySelected(mAdapter.getItem(position));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Activity activity = getActivity();
        if (activity instanceof CategoriesInteraction) {
            ((CategoriesInteraction) activity).onResumeToCategory();
        }
        configToolbar();
    }

    public interface CategoriesInteraction {

        default void onResumeToCategory() {}

        default void onCategorySelected(Category category) {}
    }
}