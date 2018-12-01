package me.shouheng.notepal.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import me.shouheng.commons.activity.CommonActivity;
import me.shouheng.commons.event.RxMessage;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.easymark.editor.Format;
import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.MenuSortAdapter;
import me.shouheng.notepal.databinding.ActivityMenuSortBinding;
import me.shouheng.commons.utils.ToastUtils;
import me.shouheng.notepal.util.preferences.PrefUtils;
import me.shouheng.commons.widget.recycler.DragSortRecycler;

public class MenuSortActivity extends CommonActivity<ActivityMenuSortBinding> {

    private MenuSortAdapter mAdapter;

    private boolean saved = true, everSaved = false;

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
        setSupportActionBar(getBinding().toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.custom_note_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(ColorUtils.tintDrawable(
                    R.drawable.ic_arrow_back_black_24dp,
                    getThemeStyle().isDarkTheme ? Color.WHITE : Color.BLACK));
        }
        getBinding().toolbar.setTitleTextColor(getThemeStyle().isDarkTheme ? Color.WHITE : Color.BLACK);
        if (getThemeStyle().isDarkTheme) {
            getBinding().toolbar.setPopupTheme(R.style.AppTheme_PopupOverlayDark);
        }

        getBinding().tvCustom.setTextColor(accentColor());

        configList();
    }

    private void configList() {
        mAdapter = new MenuSortAdapter(this, PrefUtils.getInstance().getMarkdownFormats());
        getBinding().rvFabs.setAdapter(mAdapter);

        DragSortRecycler dragSortRecycler = new DragSortRecycler();
        dragSortRecycler.setViewHandleId(R.id.iv_drag_handler);

        dragSortRecycler.setOnItemMovedListener((from, to) -> {
            saved = false;
            Format markdownFormat = mAdapter.getMarkdownFormatAt(from);
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
        getMenuInflater().inflate(R.menu.sort_editor_menu, menu);
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
        List<Format> markdownFormats = mAdapter.getMarkdownFormats();
        PrefUtils.getInstance().setMarkdownFormats(markdownFormats);
        ToastUtils.makeToast(R.string.menu_successfully_saved);
    }

    private void resetFabOrders() {
        saved = true;
        mAdapter.setMarkdownFormats(PrefUtils.getInstance().getMarkdownFormats());
        mAdapter.notifyDataSetChanged();
    }

    private void setResult() {
        if (everSaved) {
            postEvent(new RxMessage(RxMessage.CODE_SORT_EDIT_MENU, null));
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
