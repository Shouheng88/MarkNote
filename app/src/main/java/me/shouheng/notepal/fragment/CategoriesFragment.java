package me.shouheng.notepal.fragment;

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

import java.util.List;

import javax.annotation.Nonnull;

import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.CategoriesAdapter;
import me.shouheng.notepal.databinding.FragmentCategoriesBinding;
import me.shouheng.notepal.dialog.CategoryEditDialog;
import me.shouheng.notepal.model.Category;
import me.shouheng.notepal.model.enums.Status;
import me.shouheng.notepal.provider.CategoryStore;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.util.ViewUtils;
import me.shouheng.notepal.widget.tools.CustomItemAnimator;
import me.shouheng.notepal.widget.tools.CustomItemTouchHelper;
import me.shouheng.notepal.widget.tools.DividerItemDecoration;

/**
 * Created by wangshouheng on 2017/3/29.*/
public class CategoriesFragment extends BaseFragment<FragmentCategoriesBinding> implements BaseQuickAdapter.OnItemClickListener {

    private final static String ARG_STATUS = "arg_status";

    private RecyclerView.OnScrollListener scrollListener;
    private CategoriesAdapter mAdapter;

    private CategoryEditDialog categoryEditDialog;

    private CategoryStore categoryStore;
    private Status status;

    public static CategoriesFragment newInstance() {
        Bundle args = new Bundle();
        CategoriesFragment fragment = new CategoriesFragment();
        args.putSerializable(ARG_STATUS, Status.NORMAL);
        fragment.setArguments(args);
        return new CategoriesFragment();
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

        configToolbar();

        configCategories();
    }

    private void configToolbar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.drawer_menu_labels);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void configCategories() {
        getBinding().ivEmpty.setSubTitle(getEmptySubTitle());

        categoryStore = CategoryStore.getInstance(getContext());
        status = getArguments() == null || !getArguments().containsKey(ARG_STATUS) ? Status.NORMAL : (Status) getArguments().get(ARG_STATUS);

        mAdapter = new CategoriesAdapter(getContext(), getCategories());
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.iv_more:
                    popCategoryMenu(view, position, mAdapter.getItem(position));
                    break;
            }
        });
        mAdapter.setOnItemClickListener(this);

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
    }

    private String getEmptySubTitle() {
        if (status == null) return null;
        switch (status) {
            case NORMAL:
                return getString(R.string.categories_list_empty_sub_normal);
            case TRASHED:
                return getString(R.string.categories_list_empty_sub_trashed);
            case ARCHIVED:
                return getString(R.string.categories_list_empty_sub_archived);
        }
        return getString(R.string.categories_list_empty_sub_normal);
    }

    private List<Category> getCategories() {
        return categoryStore.get(null, null);
    }

    public void reload() {
        mAdapter.setNewData(getCategories());

        /**
         * Notify the snagging list is changed. The activity need to record the message, and
         * use it when set result to caller. */
        if (getActivity() != null && getActivity() instanceof OnCategoriesInteractListener) {
            ((OnCategoriesInteractListener) getActivity()).onCategoryListChanged();
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
        categoryEditDialog = CategoryEditDialog.newInstance(getContext(), param, category -> {
            category.setContentChanged(true);
            mAdapter.notifyItemChanged(position);
            categoryStore.update(category);

            ToastUtils.makeToast(getContext(), R.string.text_save_successfully);
        });
        categoryEditDialog.show(getFragmentManager(), "CATEGORY_EDIT_DIALOG");
    }

    private void showDeleteDialog(int position, Category param) {
        new MaterialDialog.Builder(getContext())
                .title(R.string.text_warning)
                .content(R.string.category_delete_message)
                .positiveText(R.string.text_confirm)
                .onPositive((materialDialog, dialogAction) -> {
                    CategoryStore.getInstance(getContext()).update(param, Status.DELETED);
                    mAdapter.remove(position);
                })
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
            CategoryStore.getInstance(getContext()).updateOrders(mAdapter.getData());
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

    }

    public interface OnCategoriesInteractListener {

        /**
         * This method will be called, when the snagging list is changed. Do not try to call {@link #reload()}
         * This method as well as {@link NotesFragment.OnNotesInteractListener#onNoteListChanged()} is only used
         * to record the list change message and handle in future.
         *
         * @see NotesFragment.OnNotesInteractListener#onNoteListChanged()
         */
        default void onCategoryListChanged(){}
    }
}