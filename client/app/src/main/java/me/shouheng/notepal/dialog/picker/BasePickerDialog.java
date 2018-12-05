package me.shouheng.notepal.dialog.picker;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.ModelsPickerAdapter;
import me.shouheng.data.entity.Model;
import me.shouheng.data.utils.Selectable;
import me.shouheng.commons.widget.recycler.EmptySupportRecyclerView;
import me.shouheng.commons.widget.recycler.EmptyView;
import me.shouheng.commons.widget.recycler.CustomItemAnimator;
import me.shouheng.commons.widget.recycler.DividerItemDecoration;

/**
 * Created by wangshouheng on 2017/10/5.*/
public abstract class BasePickerDialog<T extends Model & Selectable> extends DialogFragment {

    private ModelsPickerAdapter<T> modelsPickerAdapter;

    private View dialogView;

    private EmptySupportRecyclerView mRecyclerView;

    private OnItemSelectedListener<T> onItemSelectedListener;

    protected OnNewModelCreatedListener<T> onNewModelCreatedListener;

    protected abstract ModelsPickerAdapter<T> prepareAdapter();

    @NonNull @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialogView = LayoutInflater.from(getContext()).inflate(
                R.layout.dialog_models_picker_layout, null, false);

        EmptyView emptyView = dialogView.findViewById(R.id.iv_empty);

        mRecyclerView = dialogView.findViewById(R.id.rv_models);
        mRecyclerView.setEmptyView(emptyView);
        mRecyclerView.setItemAnimator(new CustomItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL_LIST, ColorUtils.isDarkTheme()));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerView.setAdapter(getAdapter());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setView(dialogView);
        onCreateDialog(builder, emptyView);
        return builder.create();
    }

    protected abstract void onCreateDialog(AlertDialog.Builder builder, EmptyView emptyView);

    protected ModelsPickerAdapter<T> getAdapter() {
        if (modelsPickerAdapter == null) modelsPickerAdapter = prepareAdapter();
        modelsPickerAdapter.setOnItemClickListener((adapter, view, position) -> {
            // Change the item data and performance
            T t = modelsPickerAdapter.getItem(position);
            assert t != null;
            t.setSelected(!t.isSelected());
            modelsPickerAdapter.notifyItemChanged(position);
            // Call back
            if (onItemSelectedListener != null) {
                onItemSelectedListener.onItemSelected(
                        BasePickerDialog.this,
                        modelsPickerAdapter.getItem(position),
                        position);
            }
        });
        return modelsPickerAdapter;
    }

    protected List<T> getSelected() {
        List<T> ret = new LinkedList<>();
        for (T t : modelsPickerAdapter.getData()) {
            if (t.isSelected()) {
                ret.add(t);
            }
        }
        return ret;
    }

    protected View getDialogView() {
        return dialogView;
    }

    protected EmptySupportRecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public BasePickerDialog<T> setOnItemSelectedListener(OnItemSelectedListener<T> onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
        return this;
    }

    public BasePickerDialog<T> setOnNewModelCreatedListener(OnNewModelCreatedListener<T> onNewModelCreatedListener) {
        this.onNewModelCreatedListener = onNewModelCreatedListener;
        return this;
    }

    public interface OnNewModelCreatedListener<T extends Model> {
        void onNewModelCreated(BasePickerDialog dialog, T model);
    }

    public interface OnItemSelectedListener<T extends Model> {
        void onItemSelected(BasePickerDialog dialog, T item, int position);
    }
}
