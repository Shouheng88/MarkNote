package me.shouheng.notepal.dialog;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;

import com.afollestad.materialdialogs.color.ColorChooserDialog;

import me.shouheng.commons.helper.DialogHelper;
import me.shouheng.commons.theme.ThemeUtils;
import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.DialogNotebookEditBinding;
import me.shouheng.data.entity.Notebook;
import me.shouheng.commons.utils.ToastUtils;

/**
 * Created by wangshouheng on 2017/7/23.
 */
public class NotebookEditDialog extends DialogFragment implements ColorChooserDialog.ColorCallback {

    public final static String ARG_KEY_NOTEBOOK = "__arg_key_notebook";

    @ColorInt
    private int notebookColor;
    private String notebookName;

    private DialogNotebookEditBinding binding;

    private OnConfirmListener onConfirmListener;

    public static NotebookEditDialog newInstance(Notebook notebook, OnConfirmListener onConfirmListener) {
        NotebookEditDialog dialog = DialogHelper.open(NotebookEditDialog.class)
                .put(ARG_KEY_NOTEBOOK, notebook)
                .get();
        dialog.setOnConfirmListener(onConfirmListener);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        assert args != null;
        Notebook notebook = (Notebook) args.getSerializable(ARG_KEY_NOTEBOOK);
        assert notebook != null;
        this.notebookName = notebook.getTitle();
        this.notebookColor = notebook.getColor();

        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_notebook_edit, null, false);
        binding.setIsDarkTheme(ThemeUtils.getInstance().getThemeStyle().isDarkTheme);

        binding.etNotebookName.setText(notebookName);
        binding.watcher.bindEditText(binding.etNotebookName);
        binding.vColor.setOnClickListener(v -> showColorPickerDialog());

        updateUIBySelectedColor(notebookColor);

        return new AlertDialog.Builder(getContext())
                .setView(binding.getRoot())
                .setPositiveButton(R.string.text_confirm, (dialog, which) -> {
                    if (TextUtils.isEmpty(binding.etNotebookName.getText())){
                        ToastUtils.makeToast(R.string.text_title_required);
                        return;
                    }
                    notebookName = binding.etNotebookName.getText().toString();
                    if (onConfirmListener != null){
                        onConfirmListener.onConfirm(notebookName, notebookColor);
                    }
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.text_cancel, null)
                .create();
    }

    private void showColorPickerDialog() {
        assert getContext() != null;
        new ColorChooserDialog.Builder(getContext(), R.string.notebook_color_picker_title)
                .preselect(notebookColor)
                .accentMode(false)
                .presetsButton(R.string.text_presets)
                .cancelButton(R.string.text_cancel)
                .customButton(R.string.text_custom)
                .backButton(R.string.text_back)
                .doneButton(R.string.text_done)
                .show(getChildFragmentManager());
    }

    private void updateUIBySelectedColor(@ColorInt int color) {
        notebookColor = color;
        binding.iv.setBackgroundColor(color);
    }

    private void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, int selectedColor) {
        updateUIBySelectedColor(selectedColor);
    }

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog dialog) { }

    public interface OnConfirmListener {
        void onConfirm(String categoryName, int notebookColor);
    }
}
