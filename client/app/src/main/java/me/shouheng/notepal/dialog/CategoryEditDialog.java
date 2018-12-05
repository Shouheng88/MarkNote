package me.shouheng.notepal.dialog;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;

import com.afollestad.materialdialogs.color.ColorChooserDialog;

import me.shouheng.commons.helper.DialogHelper;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.data.model.enums.Portrait;
import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.DialogCategoryEditBinding;
import me.shouheng.data.entity.Category;
import me.shouheng.commons.utils.ToastUtils;

/**
 * Created by WngShhng on 2017/4/2.
 */
public class CategoryEditDialog extends DialogFragment implements ColorChooserDialog.ColorCallback {

    private OnConfirmListener onConfirmListener;

    private int categoryColor;
    private Category category;

    private DialogCategoryEditBinding binding;

    public static CategoryEditDialog newInstance(Category category, OnConfirmListener listener) {
        CategoryEditDialog dialog = DialogHelper.open(CategoryEditDialog.class).get();
        dialog.setOnConfirmListener(listener);
        dialog.setCategory(category);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_category_edit, null, false);

        binding.setIsDarkTheme(ColorUtils.isDarkTheme());

        categoryColor = category.getColor();
        binding.vColor.setOnClickListener(v -> showColorPickerDialog());
        binding.flCategoryPortrait.setOnClickListener(v -> showPortraitPickerDialog());

        binding.wtv.bindEditText(binding.etCategoryName);
        binding.etCategoryName.setText(category.getName());
        binding.ivPortrait.setImageResource(category.getPortrait().iconRes);
        updateUIBySelectedColor(category.getColor());

        return new AlertDialog.Builder(getContext())
                .setView(binding.getRoot())
                .setPositiveButton(R.string.text_confirm, (dialog, which) -> {
                    if (TextUtils.isEmpty(binding.etCategoryName.getText())){
                        ToastUtils.makeToast(R.string.text_title_required);
                        return;
                    }
                    category.setName(binding.etCategoryName.getText().toString());
                    if (onConfirmListener != null) {
                        onConfirmListener.onConfirmCategory(category);
                    }
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.text_cancel, null)
                .create();
    }

    private void setCategory(Category category) {
        this.category = category;
    }

    private void updateUIBySelectedColor(int color) {
        categoryColor = color;
        category.setColor(color);
        binding.iv.setBackgroundColor(color);
        binding.civPortraitBackground.setFillingCircleColor(color);
    }

    private void showPortraitPickerDialog() {
        String SHOW_PORTRAIT_DIALOG = "SHOW_PORTRAIT_DIALOG";
        PortraitPickerDialog.newInstance(categoryColor, (portraitId, portraitRes) -> {
            category.setPortrait(Portrait.getPortraitById(portraitId));
            binding.ivPortrait.setImageResource(portraitRes);
        }).show(getFragmentManager(), SHOW_PORTRAIT_DIALOG);
    }

    private void showColorPickerDialog() {
        assert getContext() != null;
        new ColorChooserDialog.Builder(getContext(), R.string.notebook_color_picker_title)
                .preselect(categoryColor)
                .accentMode(false)
                .presetsButton(R.string.text_presets)
                .cancelButton(R.string.text_cancel)
                .customButton(R.string.text_custom)
                .backButton(R.string.text_back)
                .doneButton(R.string.text_done)
                .show(getChildFragmentManager());
    }

    private void setOnConfirmListener(OnConfirmListener listener) {
        this.onConfirmListener = listener;
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, int selectedColor) {
        updateUIBySelectedColor(selectedColor);
    }

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog dialog) { }

    public interface OnConfirmListener {
        void onConfirmCategory(Category category);
    }
}
