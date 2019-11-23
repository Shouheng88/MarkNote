package me.shouheng.notepal.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import me.shouheng.commons.activity.ContainerActivity;
import me.shouheng.commons.activity.ThemedActivity;
import me.shouheng.commons.event.RxMessage;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.widget.recycler.CustomItemAnimator;
import me.shouheng.commons.widget.recycler.DividerItemDecoration;
import me.shouheng.mvvm.base.anno.ActivityConfiguration;
import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.NotesAdapter;
import me.shouheng.notepal.databinding.ActivitySearchBinding;
import me.shouheng.notepal.fragment.NoteViewFragment;
import me.shouheng.notepal.vm.SearchViewModel;
import me.shouheng.uix.rv.EmptyView;
import me.shouheng.utils.stability.LogUtils;
import me.shouheng.utils.ui.ToastUtils;

@ActivityConfiguration(layoutResId = R.layout.activity_search)
public class SearchActivity extends ThemedActivity<ActivitySearchBinding, SearchViewModel> implements SearchView.OnQueryTextListener {

    private NotesAdapter adapter;
    private SearchView mSearchView;

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        /* Config toolbar. */
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

        /* Config list. */
        adapter = new NotesAdapter(this, new LinkedList<>());
        EmptyView emptyView = getBinding().ev;
        getBinding().recyclerview.setEmptyView(emptyView);
        getBinding().recyclerview.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL_LIST, isDarkTheme()));
        getBinding().recyclerview.setItemAnimator(new CustomItemAnimator());
        getBinding().recyclerview.setLayoutManager(new LinearLayoutManager(this));
        getBinding().recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener((quickAdapter, view, position) -> {
            NotesAdapter.MultiItem item = (NotesAdapter.MultiItem) quickAdapter.getData().get(position);
            ContainerActivity.open(NoteViewFragment.class)
                    .put(NoteViewFragment.ARGS_KEY_NOTE, (Serializable) item.note)
                    .put(NoteViewFragment.ARGS_KEY_IS_PREVIEW, false)
                    .launch(this);
        });

        /* Add subscription. */
        addSubscriptions();
    }

    private void addSubscriptions() {
        getVM().getNotesLiveData().observe(this, resources -> {
            assert resources != null;
            switch (resources.status) {
                case SUCCESS:
                    List<NotesAdapter.MultiItem> multiItems = new LinkedList<>();
                    assert resources.data != null;
                    Disposable d = Observable.fromIterable(resources.data)
                            .forEach(note -> multiItems.add(new NotesAdapter.MultiItem(note)));
                    LogUtils.d(d);
                    adapter.setNewData(multiItems);
                    getBinding().ev.showEmpty();
                    break;
                case FAILED:
                    getBinding().ev.showEmpty();
                    ToastUtils.showShort(R.string.text_failed);
                    break;
                case LOADING:
                    getBinding().ev.showLoading();
                    break;
                default: // noop
            }
        });
        addSubscription(RxMessage.class, RxMessage.CODE_NOTE_DATA_CHANGED,
                rxMessage -> getVM().fetchSearchResults());
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
        if (item.getItemId() == android.R.id.home){
            finish();
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
        if (newText.equals(getVM().getQueryText())) {
            return true;
        }
        getVM().setQueryText(newText);
        if (!TextUtils.isEmpty(newText)) {
            getVM().fetchSearchResults();
        } else {
            getVM().notifyEmptyResult();
        }
        return true;
    }
}
