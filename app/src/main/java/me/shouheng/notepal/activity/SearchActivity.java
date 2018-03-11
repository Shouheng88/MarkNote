package me.shouheng.notepal.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.SearchItemsAdapter;
import me.shouheng.notepal.adapter.SearchItemsAdapter.OnItemSelectedListener;
import me.shouheng.notepal.dialog.MindSnaggingDialog;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.model.MindSnagging;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.model.enums.ModelType;
import me.shouheng.notepal.provider.AttachmentsStore;
import me.shouheng.notepal.provider.MindSnaggingStore;
import me.shouheng.notepal.provider.helper.QueryHelper;
import me.shouheng.notepal.util.AttachmentHelper;
import me.shouheng.notepal.util.GsonUtils;
import me.shouheng.notepal.util.PreferencesUtils;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.util.tools.SearchConditions;
import me.shouheng.notepal.widget.EmptySupportRecyclerView;
import me.shouheng.notepal.widget.tools.CustomItemAnimator;
import me.shouheng.notepal.widget.tools.DividerItemDecoration;

public class SearchActivity extends ThemedActivity implements OnQueryTextListener, OnItemSelectedListener {

    private static final String EXTRA_NAME_REQUEST_CODE = "extra.request.code";

    private final int REQUEST_FOR_NOTE = 20004;

//    private InputMethodManager mImm;

    private SearchItemsAdapter adapter;
    private SearchView mSearchView;

    private String queryString;
    private List searchResults = new LinkedList();
    private List<Note> notes = new LinkedList<>();
    private List<MindSnagging> minds = new LinkedList<>();

    private QueryHelper queryHelper;
    private SearchConditions conditions;

    public static void startActivityForResult(Activity mContext, int requestCode){
        Intent intent = new Intent(mContext, SearchActivity.class);
        intent.putExtra(EXTRA_NAME_REQUEST_CODE, requestCode);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        mContext.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (!isDarkTheme()) toolbar.setPopupTheme(R.style.AppTheme_PopupOverlay);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EmptySupportRecyclerView mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setEmptyView(findViewById(R.id.iv_empty));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, isDarkTheme()));
        mRecyclerView.setItemAnimator(new CustomItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SearchItemsAdapter(this, this);
        mRecyclerView.setAdapter(adapter);

        queryHelper = QueryHelper.newInstance(this);

        initSearchConditions();
    }

    private void initSearchConditions() {
        String searchConditions = PreferencesUtils.getInstance(this).getSearchConditions();
        if (TextUtils.isEmpty(searchConditions)) {
            conditions = SearchConditions.getDefaultConditions();
        } else {
            conditions = GsonUtils.toObject(searchConditions, SearchConditions.class);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.item_note).setChecked(conditions.isIncludeNote());
        menu.findItem(R.id.item_mind_snagging).setChecked(conditions.isIncludeMindSnagging());
        menu.findItem(R.id.item_include_tags).setChecked(conditions.isIncludeTags());
        menu.findItem(R.id.item_include_archived).setChecked(conditions.isIncludeArchived());
        menu.findItem(R.id.item_include_trashed).setChecked(conditions.isIncludeTrashed());
        queryHelper.setConditions(conditions);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        getMenuInflater().inflate(R.menu.filter_search_condition, menu);

        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint(getString(R.string.search_by_conditions));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setIconified(false);

        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search), new MenuItemCompat.OnActionExpandListener() {
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

        menu.findItem(R.id.action_search).expandActionView();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.item_note:
                conditions.setIncludeNote(!conditions.isIncludeNote());
                invalidateOptionsMenu();
                break;
            case R.id.item_mind_snagging:
                conditions.setIncludeMindSnagging(!conditions.isIncludeMindSnagging());
                invalidateOptionsMenu();
                break;
            case R.id.item_include_tags:
                conditions.setIncludeTags(!conditions.isIncludeTags());
                invalidateOptionsMenu();
                break;
            case R.id.item_include_archived:
                conditions.setIncludeArchived(!conditions.isIncludeArchived());
                invalidateOptionsMenu();
                break;
            case R.id.item_include_trashed:
                conditions.setIncludeTrashed(!conditions.isIncludeTrashed());
                invalidateOptionsMenu();
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
//            if (mImm != null) mImm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
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
            setupQueryResults();
        } else {
            searchResults.clear();
        }

        adapter.updateSearchResults(searchResults);
        adapter.notifyDataSetChanged();

        return true;
    }

    private void queryAll(String queryString) {
        queryNotes(queryString);
        queryMinds(queryString);
    }

    private void queryNotes(String queryString) {
        notes.clear();
        if (conditions.isIncludeNote()) {
            notes = queryHelper.getNotes(queryString);
        }
    }

    private void queryMinds(String queryString) {
        minds.clear();
        if (conditions.isIncludeMindSnagging()) {
            minds = queryHelper.getMindSnaggings(queryString);
        }
    }

    private void setupQueryResults() {
        searchResults.clear();
        if (notes != null && !notes.isEmpty()) {
            searchResults.add(getString(R.string.model_name_note));
            searchResults.addAll(notes);
        }
        if (minds != null && !minds.isEmpty()) {
            searchResults.add(getString(R.string.model_name_mind_snagging));
            searchResults.addAll(minds);
        }
    }

    @Override
    public void onNoteSelected(Note note, int position) {
        ContentActivity.viewNote(this, note, REQUEST_FOR_NOTE);
    }

    @Override
    public void onMindSnaggingSelected(MindSnagging mind, final int position) {
        new MindSnaggingDialog.Builder()
                .setMindSnagging(mind)
                .setOnConfirmListener((mindSnagging, attachment) ->
                        saveMindSnagging(position, mindSnagging, attachment))
                .setOnAttachmentClickListener(this::resolveAttachmentClick)
                .build()
                .show(getSupportFragmentManager(), "VIEW_MIND_SNAGGING");
    }

    private void saveMindSnagging(int position, MindSnagging mindSnagging, Attachment attachment) {
        if (attachment != null && AttachmentsStore.getInstance(this).isNewModel(attachment.getCode())) {
            attachment.setModelCode(mindSnagging.getCode());
            attachment.setModelType(ModelType.MIND_SNAGGING);
            AttachmentsStore.getInstance(this).saveModel(attachment);
        }

        if (MindSnaggingStore.getInstance(this).isNewModel(mindSnagging.getCode())) {
            MindSnaggingStore.getInstance(this).saveModel(mindSnagging);
        } else {
            MindSnaggingStore.getInstance(this).update(mindSnagging);
        }

        ToastUtils.makeToast(R.string.text_save_successfully);

        adapter.notifyItemChanged(position);
    }

    protected void resolveAttachmentClick(Attachment attachment) {
        AttachmentHelper.resolveClickEvent(
                this,
                attachment,
                Collections.singletonList(attachment),
                "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_FOR_NOTE:
                    queryNotes(queryString);
                    setupQueryResults();
                    adapter.updateSearchResults(searchResults);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
