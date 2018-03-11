package me.shouheng.notepal.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.FabSortAdapter;
import me.shouheng.notepal.databinding.ActivityFabSortBinding;
import me.shouheng.notepal.model.enums.FabSortItem;
import me.shouheng.notepal.util.PreferencesUtils;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.widget.tools.DragSortRecycler;


public class FabSortActivity extends CommonActivity<ActivityFabSortBinding> {

    private PreferencesUtils preferencesUtils;

    private List<FabSortItem> oldFabSortItems = new ArrayList<>();

    private FabSortAdapter mAdapter;

    private boolean saved = true, everSaved = false;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_fab_sort;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        configToolbar();

        preferencesUtils = PreferencesUtils.getInstance(getApplicationContext());

        getBinding().tvCustom.setTextColor(primaryColor());

        prepareFabSortItems();

        configFabList();
    }

    private void configToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.fab_sort_custom_fab);
            ab.setDisplayHomeAsUpEnabled(true);
        }
        if (!isDarkTheme()) toolbar.setPopupTheme(R.style.AppTheme_PopupOverlay);
    }

    private void prepareFabSortItems() {
        oldFabSortItems.clear();
        oldFabSortItems = preferencesUtils.getFabSortResult();
        FabSortItem[] allItems = FabSortItem.values();
        for (FabSortItem fabSortItem : allItems) {
            if (!oldFabSortItems.contains(fabSortItem)){
                oldFabSortItems.add(fabSortItem);
            }
        }
    }

    private void configFabList() {
        mAdapter = new FabSortAdapter(this, new ArrayList<>(oldFabSortItems));
        getBinding().rvFabs.setAdapter(mAdapter);

        DragSortRecycler dragSortRecycler = new DragSortRecycler();
        dragSortRecycler.setViewHandleId(R.id.iv_drag_handler);

        dragSortRecycler.setOnItemMovedListener((from, to) -> {
            saved = false;
            FabSortItem fabSortItem = mAdapter.getFabSortItemAt(from);
            mAdapter.removeFabSortItemAt(from);
            mAdapter.addFabSortItemTo(to, fabSortItem);
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

    private void saveFabOrders(){
        saved = true;
        everSaved = true;
        List<FabSortItem> fabSortItems = mAdapter.getFabSortItems();
        preferencesUtils.setFabSortResult(fabSortItems);
        ToastUtils.makeToast(R.string.fab_sort_save_successfully);
    }

    private void resetFabOrders(){
        saved = true;
        mAdapter.setFabSortItems(oldFabSortItems);
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

    private void back(){
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
