package me.shouheng.notepal.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.color.ColorChooserDialog;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.CommonActivity;
import me.shouheng.notepal.model.Category;
import me.shouheng.notepal.model.enums.Portrait;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.widget.CircleImageView;
import me.shouheng.notepal.widget.WatcherTextView;

/**
 * Created by wangshouheng on 2017/4/2.*/
public class CategoryEditDialog extends DialogFragment {

    private OnConfirmCategoryEditListener onConfirmCategoryEditListener;

    private EditText etCategoryName;
    private LinearLayout llNameEditBG;
    private CircleImageView civCategoryColor, civPortraitBG;
    private ImageView ivPortrait;

    private int primaryColor;
    private int iCategoryColor;

    private Category category;

    public static CategoryEditDialog newInstance(Context context, Category category, OnConfirmCategoryEditListener onPositiveButtonClickListener){
        return new CategoryEditDialog(context, category, onPositiveButtonClickListener);
    }

    public CategoryEditDialog(Context context, Category category, OnConfirmCategoryEditListener onConfirmCategoryEditListener) {
        this.onConfirmCategoryEditListener = onConfirmCategoryEditListener;
        this.primaryColor = ColorUtils.primaryColor(context);
        this.category = category;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_category_editor_layout, null);

        etCategoryName = rootView.findViewById(R.id.et_category_name);
        llNameEditBG = rootView.findViewById(R.id.ll_title_background);
        civCategoryColor = rootView.findViewById(R.id.civ_category_color);
        civPortraitBG = rootView.findViewById(R.id.civ_portrait_background);
        ivPortrait = rootView.findViewById(R.id.iv_portrait);

        rootView.findViewById(R.id.ll_category_color).setOnClickListener(v -> showColorPickerDialog());
        rootView.findViewById(R.id.fl_category_portrait).setOnClickListener(v -> showPortraitPickerDialog());

        WatcherTextView wtv = rootView.findViewById(R.id.wtv);
        wtv.bindEditText(etCategoryName);

        etCategoryName.setText(category.getName());
        updateUIBySelectedColor(category.getColor());
        ivPortrait.setImageResource(category.getPortrait().iconRes);

        return new AlertDialog.Builder(getContext())
                .setView(rootView)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    if (TextUtils.isEmpty(etCategoryName.getText())){
                        ToastUtils.makeToast(getContext(), R.string.title_required);
                        return;
                    }
                    category.setName(etCategoryName.getText().toString());
                    if (onConfirmCategoryEditListener != null){
                        onConfirmCategoryEditListener.onConfirmCategory(category);
                    }
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    public void updateUIBySelectedColor(int color){
        iCategoryColor = color;
        category.setColor(color);
        llNameEditBG.setBackgroundColor(color);
        civPortraitBG.setFillingCircleColor(color);
        civCategoryColor.setFillingCircleColor(color);
    }

    private void showPortraitPickerDialog(){
        String SHOW_PORTRAIT_DIALOG = "SHOW_PORTRAIT_DIALOG";
        PortraitPickerDialog.newInstance(iCategoryColor, (portraitId, portraitRes) -> {
            category.setPortrait(Portrait.getPortraitById(portraitId));
            ivPortrait.setImageResource(portraitRes);
        }).show(getFragmentManager(), SHOW_PORTRAIT_DIALOG);
    }

    private void showColorPickerDialog() {
        assert getActivity() != null;
        new ColorChooserDialog.Builder((CommonActivity) getActivity(), R.string.pick_category_color)
                .preselect(primaryColor)
                .accentMode(false)
                .titleSub(R.string.pick_category_color)
                .backButton(R.string.back)
                .doneButton(R.string.done_label)
                .cancelButton(R.string.cancel)
                .show();
    }

    public interface OnConfirmCategoryEditListener {
        void onConfirmCategory(Category category);
    }
}
