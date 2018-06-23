package me.shouheng.notepal.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.LinkedList;
import java.util.List;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.base.CommonActivity;
import me.shouheng.notepal.adapter.MenuSortAdapter;
import me.shouheng.notepal.databinding.ActivityMenuSortBinding;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.util.preferences.UserPreferences;
import me.shouheng.notepal.widget.tools.DragSortRecycler;
import my.shouheng.palmmarkdown.tools.MarkdownFormat;

public class MenuSortActivity extends CommonActivity<ActivityMenuSortBinding> {

    private List<MarkdownFormat> oldList = new LinkedList<>();

    private MenuSortAdapter mAdapter;

    private boolean saved = true, everSaved = false;

    private UserPreferences userPreferences;

    public static void start(Fragment fragment, int req) {
        Intent intent = new Intent(fragment.getContext(), MenuSortActivity.class);
        fragment.startActivityForResult(intent, req);
    }

    public static void start(android.app.Fragment fragment, int req) {
        Intent intent = new Intent(fragment.getActivity(), MenuSortActivity.class);
        fragment.startActivityForResult(intent, req);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_menu_sort;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        configToolbar();

        userPreferences = UserPreferences.getInstance();

        getBinding().tvCustom.setTextColor(primaryColor());

        oldList = userPreferences.getMarkdownFormats();

        configList();
    }

    private void configToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.custom_note_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }
        if (!isDarkTheme()) toolbar.setPopupTheme(R.style.AppTheme_PopupOverlay);
    }

    private void configList() {
        mAdapter = new MenuSortAdapter(this, oldList);
        getBinding().rvFabs.setAdapter(mAdapter);

        DragSortRecycler dragSortRecycler = new DragSortRecycler();
        dragSortRecycler.setViewHandleId(R.id.iv_drag_handler);

        dragSortRecycler.setOnItemMovedListener((from, to) -> {
            saved = false;
            MarkdownFormat markdownFormat = mAdapter.getMarkdownFormatAt(from);
            mAdapter.removeMarkdownFormatAt(from);
            mAdapter.addMarkdownFormatTo(to, markdownFormat);
            mAdapter.notifyDataSetChanged();
        });

        getBinding().rvFabs.addItemDecoration(dragSortRecycler);
        getBinding().rvFabs.addOnItemTouchListener(dragSortRecycler);
        getBinding().rvFabs.setLayoutManager(new LinearLayoutManager(this));
        getBinding().rvFabs.addOnItemTouchListener(dragSortRecycler);
        getBinding().rvFabs.addOnScrollListener(dragSortRecycler.getScrollListener());
        getBinding().rvFabs.getLayoutManager().scrollToPosition(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fab_sort, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (!saved){
                    back();
                } else {
                    setResult();
                }
                break;
            case R.id.action_save:
                saveFabOrders();
                break;
            case R.id.action_reset:
                resetFabOrders();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!saved){
            back();
        } else {
            setResult();
        }
    }

    private void saveFabOrders() {
        saved = true;
        everSaved = true;
        List<MarkdownFormat> markdownFormats = mAdapter.getMarkdownFormats();
        userPreferences.setMarkdownFormats(markdownFormats);
        ToastUtils.makeToast(R.string.menu_successfully_saved);
    }

    private void resetFabOrders() {
        saved = true;
        mAdapter.setMarkdownFormats(oldList);
        mAdapter.notifyDataSetChanged();
    }

    private void setResult() {
        if (everSaved) {
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else {
            finish();
        }
    }

    private void back() {
        new MaterialDialog.Builder(this)
                .title(R.string.fab_sort_save)
                .content(R.string.fab_sort_save_or_lose)
                .positiveText(R.string.text_save)
                .negativeText(R.string.text_give_up)
                .onPositive((materialDialog, dialogAction) -> {
                    saveFabOrders();
                    setResult();
                })
                .onNegative((materialDialog, dialogAction) -> finish())
                .show();
    }
}
