package me.shouheng.notepal.dialog;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.DialogAdvancedPickerBinding;

/**
 * Created by shouh on 2018/3/25.*/
public class AdvancedPicker extends DialogFragment implements View.OnClickListener {

    private OnItemSelectedListener onItemSelectedListener;

    public static AdvancedPicker newInstance(OnItemSelectedListener onItemSelectedListener) {
        AdvancedPicker advancedPicker = new AdvancedPicker();
        advancedPicker.setOnItemSelectedListener(onItemSelectedListener);
        return advancedPicker;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DialogAdvancedPickerBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_advanced_picker, null, false);

        binding.tvInsertCheckbox.setOnClickListener(this);
        binding.tvInsertLink.setOnClickListener(this);
        binding.tvInsertMathJax.setOnClickListener(this);
        binding.tvInsertTable.setOnClickListener(this);

        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.text_advanced)
                .setView(binding.getRoot())
                .create();
    }

    @Override
    public void onClick(View view) {
        AdvancedItem advancedItem = null;
        switch (view.getId()) {
            case R.id.tv_insert_checkbox:
                advancedItem = AdvancedItem.INSERT_CHECKBOX;
                break;
            case R.id.tv_insert_link:
                advancedItem = AdvancedItem.INSERT_LINK;
                break;
            case R.id.tv_insert_math_jax:
                advancedItem = AdvancedItem.INSERT_MATH_JAX;
                break;
            case R.id.tv_insert_table:
                advancedItem = AdvancedItem.INSERT_TABLE;
                break;
        }
        if (onItemSelectedListener != null && advancedItem != null) {
            onItemSelectedListener.onItemSelected(advancedItem);
        }
        // dismiss dialog when option is selected
        dismiss();
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(AdvancedItem advancedItem);
    }

    public enum AdvancedItem {
        INSERT_TABLE,
        INSERT_LINK,
        INSERT_CHECKBOX,
        INSERT_MATH_JAX
    }
}
