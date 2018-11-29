package me.shouheng.notepal.activity;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import me.shouheng.commons.activity.CommonActivity;
import me.shouheng.commons.activity.ContainerActivity;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.utils.LogUtils;
import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.SearchItemsAdapter;
import me.shouheng.notepal.adapter.SearchItemsAdapter.OnItemSelectedListener;
import me.shouheng.notepal.databinding.ActivitySearchBinding;
import me.shouheng.notepal.fragment.NoteViewFragment;
import me.shouheng.data.entity.Note;
import me.shouheng.commons.utils.ToastUtils;
import me.shouheng.notepal.util.tools.SearchConditions;
import me.shouheng.notepal.viewmodel.SearchViewModel;
import me.shouheng.commons.widget.recycler.CustomItemAnimator;
import me.shouheng.commons.widget.recycler.DividerItemDecoration;

// TODO 搜索界面的列表的样式修改
public class SearchActivity extends CommonActivity<ActivitySearchBinding>
        implements OnItemSelectedListener, SearchView.OnQueryTextListener {

    private final static int REQUEST_FOR_NOTE = 20004;

    private SearchItemsAdapter adapter;

    private String queryString;
    private SearchViewModel viewModel;
    private boolean isContentChanged = false;
    private SearchView mSearchView;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_search;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(this).get(SearchViewModel.class);

        setSupportActionBar(getBinding().toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.text_search);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(ColorUtils.tintDrawable(R.drawable.ic_arrow_back_black_24dp,
                    isDarkTheme() ? Color.WHITE : Color.BLACK));
        }
        getBinding().toolbar.setTitleTextColor(isDarkTheme() ? Color.WHITE : Color.BLACK);
        if (isDarkTheme()) {
            getBinding().toolbar.setPopupTheme(R.style.AppTheme_PopupOverlayDark);
        }

        adapter = new SearchItemsAdapter(this, this);

        getBinding().recyclerview.setEmptyView(findViewById(R.id.iv_empty));
        getBinding().recyclerview.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL_LIST, isDarkTheme()));
        getBinding().recyclerview.setItemAnimator(new CustomItemAnimator());
        getBinding().recyclerview.setLayoutManager(new LinearLayoutManager(this));
        getBinding().recyclerview.setAdapter(adapter);

        SearchConditions conditions = SearchConditions.getDefaultConditions();
        viewModel.setConditions(conditions);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);

        MenuItem itemSearch = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) itemSearch.getActionView();
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint(getString(R.string.text_search_with));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setIconified(false);
        itemSearch.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                finish();
                return false;
            }
        });
        itemSearch.expandActionView();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        onQueryTextChange(query);
        hideInputManager();
        return true;
    }

    private void hideInputManager() {
        if (mSearchView != null) {
            mSearchView.clearFocus();
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.equals(queryString)) {
            return true;
        }

        queryString = newText;

        if (!TextUtils.isEmpty(queryString)) {
            queryAll(queryString);
        } else {
            setupQueryResults(null);
        }

        return true;
    }

    private void queryAll(String queryText) {
        getBinding().topMpb.setVisibility(View.VISIBLE);
        viewModel.getSearchResult(queryText).observe(this, searchResultResource -> {
            LogUtils.d(searchResultResource);
            getBinding().topMpb.setVisibility(View.GONE);
            if (searchResultResource == null) {
                ToastUtils.makeToast(R.string.text_error_when_save);
                return;
            }
            switch (searchResultResource.status) {
                case SUCCESS:
                    if (searchResultResource.data != null) {
                        setupQueryResults(searchResultResource.data.getNotes());
                    }
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_error_when_save);
                    break;
            }
        });
    }

    private void setupQueryResults(List<Note> notes) {
        List searchResults = new LinkedList();
        if (notes != null && !notes.isEmpty()) {
            searchResults.addAll(notes);
        }
        adapter.updateSearchResults(searchResults);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onNoteSelected(Note note, int position) {
        ContainerActivity.open(NoteViewFragment.class)
                .put(NoteViewFragment.ARGS_KEY_NOTE, (Serializable) note)
                .put(NoteViewFragment.ARGS_KEY_IS_PREVIEW, false)
                .launch(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_FOR_NOTE:
                    queryAll(queryString);
                    isContentChanged = true;
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (isContentChanged) {
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
