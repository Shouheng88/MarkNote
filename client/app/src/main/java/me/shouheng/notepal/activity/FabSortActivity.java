package me.shouheng.notepal.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import me.shouheng.commons.activity.ThemedActivity;
import me.shouheng.commons.event.RxMessage;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.data.model.enums.FabSortItem;
import me.shouheng.mvvm.base.anno.ActivityConfiguration;
import me.shouheng.mvvm.comn.EmptyViewModel;
import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.FabSortAdapter;
import me.shouheng.notepal.common.preferences.UserPreferences;
import me.shouheng.notepal.databinding.ActivityFabSortBinding;
import me.shouheng.uix.rv.decor.DragSortRecycler;
import me.shouheng.utils.ui.ToastUtils;

@ActivityConfiguration(layoutResId = R.layout.activity_fab_sort)
public class FabSortActivity extends ThemedActivity<ActivityFabSortBinding, EmptyViewModel> {

    private FabSortAdapter mAdapter;

    private boolean saved = true;
    private boolean everSaved = false;

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        setSupportActionBar(getBinding().toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.fab_sort_custom_fab);
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

        configFabList();
    }

    private void configFabList() {
        mAdapter = new FabSortAdapter(UserPreferences.getInstance().getFabSortResult());
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
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        getBinding().rvFabs.setLayoutManager(lm);
        getBinding().rvFabs.addOnItemTouchListener(dragSortRecycler);
        getBinding().rvFabs.addOnScrollListener(dragSortRecycler.getScrollListener());
        lm.scrollToPosition(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_editor_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!saved) {
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
            default:
                // noop
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!saved) {
            back();
        } else {
            setResult();
        }
    }

    private void saveFabOrders() {
        saved = true;
        everSaved = true;
        List<FabSortItem> fabSortItems = mAdapter.getFabSortItems();
        UserPreferences.getInstance().setFabSortResult(fabSortItems);
        ToastUtils.showShort(R.string.text_succeed);
    }

    private void resetFabOrders() {
        saved = true;
        mAdapter.setFabSortItems(UserPreferences.getInstance().getFabSortResult());
        mAdapter.notifyDataSetChanged();
    }

    private void setResult() {
        if (everSaved) {
            postEvent(new RxMessage(RxMessage.CODE_SORT_FLOAT_BUTTONS, null));
            finish();
        } else {
            finish();
        }
    }

    private void back() {
        new MaterialDialog.Builder(this)
                .content(R.string.text_save_or_discard)
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
