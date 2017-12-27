package me.shouheng.notepal.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.ModelsPickerAdapter;
import me.shouheng.notepal.model.Model;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.widget.EmptySupportRecyclerView;
import me.shouheng.notepal.widget.EmptyView;
import me.shouheng.notepal.widget.tools.CustomItemAnimator;
import me.shouheng.notepal.widget.tools.DividerItemDecoration;


/**
 * Created by wangshouheng on 2017/10/5.*/
public abstract class BasePickerDialog<T extends Model> extends DialogFragment {

    private ModelsPickerAdapter<T> modelsPickerAdapter;
    private View dialogView;
    private EmptySupportRecyclerView mRecyclerView;

    private OnItemSelectedListener<T> onItemSelectedListener;
    protected OnNewModelCreatedListener<T> onNewModelCreatedListener;

    protected abstract ModelsPickerAdapter<T> prepareAdapter();

    @NonNull @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_models_picker_layout_new, null, false);

        EmptyView emptyView = dialogView.findViewById(R.id.iv_empty);

        mRecyclerView = dialogView.findViewById(R.id.rv_models);
        mRecyclerView.setEmptyView(emptyView);
        mRecyclerView.setItemAnimator(new CustomItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL_LIST, ColorUtils.isDarkTheme(getContext())));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerView.setAdapter(getAdapter());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setView(dialogView);
        onCreateDialog(builder, emptyView);
        return builder.create();
    }

    protected abstract void onCreateDialog(AlertDialog.Builder builder, EmptyView emptyView);

    protected ModelsPickerAdapter<T> getAdapter() {
        if (modelsPickerAdapter == null) modelsPickerAdapter = prepareAdapter();
        modelsPickerAdapter.setOnItemSelectedListener((item, position) -> onItemSelectedListener.onItemSelected(BasePickerDialog.this, item, position));
        return modelsPickerAdapter;
    }

    protected View getDialogView() {
        return dialogView;
    }

    protected EmptySupportRecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void addItemToEnd(T item) {
        getAdapter().addItemToEnd(item);
        int position = getAdapter().getEndPosition();
        mRecyclerView.smoothScrollToPosition(position == 0 ? 0 : position - 1);
    }

    public void addItemToPosition(T item, int position) {
        getAdapter().addItemToPosition(item, position);
        mRecyclerView.smoothScrollToPosition(position);
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
