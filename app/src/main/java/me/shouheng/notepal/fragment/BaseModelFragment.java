package me.shouheng.notepal.fragment;

import android.app.Activity;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.baidu.location.BDLocation;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.CommonActivity;
import me.shouheng.notepal.activity.ContentActivity;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.config.TextLength;
import me.shouheng.notepal.dialog.SimpleEditDialog;
import me.shouheng.notepal.manager.LocationManager;
import me.shouheng.notepal.model.Model;
import me.shouheng.notepal.provider.BaseStore;
import me.shouheng.notepal.util.NetworkUtils;
import me.shouheng.notepal.util.PermissionUtils;
import me.shouheng.notepal.util.ShortcutHelper;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.util.ViewUtils;
import me.shouheng.notepal.widget.FlowLayout;


/**
 * Created by wangshouheng on 2017/9/3.*/
public abstract class BaseModelFragment<T extends Model, V extends ViewDataBinding> extends CommonFragment<V> {

    // region edit structure
    private Boolean isNewModel;

    private boolean contentChanged;

    private boolean savedOrUpdated;

    protected abstract boolean checkInputInfo();

    protected void beforeSaveOrUpdate(){}

    protected abstract void saveModel();

    protected abstract void updateModel();

    protected abstract BaseStore getStoreOfModel();

    protected abstract T getModel();

    protected void afterSaveOrUpdate() {}

    protected void setContentChanged() {
        this.contentChanged = true;
    }

    protected boolean isContentChanged() {
        return contentChanged;
    }

    protected boolean isNewModel() {
        if (isNewModel == null) {
            isNewModel = getStoreOfModel().isNewModel(getModel().getCode());
        }
        return isNewModel;
    }

    protected boolean saveOrUpdateData() {
        if (!checkInputInfo()) return false;

        beforeSaveOrUpdate();

        if (isNewModel()){
            saveModel();
        } else {
            updateModel();
        }
        ToastUtils.makeToast(getContext(), R.string.text_save_successfully);

        resetEditState();

        afterSaveOrUpdate();

        return true;
    }

    private void resetEditState() {
        contentChanged = false;
        savedOrUpdated = true;
        isNewModel = false;
    }

    protected void setResult() {
        if (!savedOrUpdated) getActivity().onBackPressed();

        Bundle args = getArguments();
        if (args != null && args.containsKey(Constants.EXTRA_REQUEST_CODE)){
            Intent intent = new Intent();
            intent.putExtra(Constants.EXTRA_MODEL, getModel());
            if (args.containsKey(Constants.EXTRA_POSITION)){
                intent.putExtra(Constants.EXTRA_POSITION, args.getInt(Constants.EXTRA_POSITION, 0));
            }
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        } else {
            getActivity().onBackPressed();
        }
    }

    protected void onBack(){
        if (isContentChanged()){
            new MaterialDialog.Builder(getContext())
                    .title(R.string.text_tips)
                    .content(R.string.text_save_or_discard)
                    .positiveText(R.string.text_save)
                    .negativeText(R.string.text_give_up)
                    .onPositive((materialDialog, dialogAction) -> {
                        if (!checkInputInfo()){
                            return;
                        }
                        saveOrUpdateData();
                        setResult();
                    })
                    .onNegative((materialDialog, dialogAction) -> getActivity().onBackPressed())
                    .show();
        } else {
            setResult();
        }
    }
    // endregion

    // region drawer
    protected void addShortcut(){
        if (isNewModel()) {
            new MaterialDialog.Builder(getContext())
                    .title(R.string.text_tips)
                    .content(R.string.text_save_and_retry_to_add_shortcut)
                    .positiveText(R.string.text_save_and_retry)
                    .negativeText(R.string.text_give_up)
                    .onPositive((materialDialog, dialogAction) -> {
                        if (!saveOrUpdateData()) return;
                        ShortcutHelper.addShortcut(getContext(), getModel());
                        ToastUtils.makeToast(getContext(), R.string.successfully_add_shortcut);
                    }).show();
        } else {
            ShortcutHelper.addShortcut(getActivity().getApplicationContext(), getModel());
            ToastUtils.makeToast(getContext(), R.string.successfully_add_shortcut);
        }
    }

    protected void showColorPickerDialog(int titleRes) {
        if (!(getActivity() instanceof ContentActivity)) {
            throw new IllegalArgumentException("The associated activity must be content!");
        }
        new ColorChooserDialog.Builder((ContentActivity) getActivity(), titleRes)
                .preselect(primaryColor())
                .accentMode(false)
                .titleSub(titleRes)
                .backButton(R.string.text_back)
                .doneButton(R.string.done_label)
                .cancelButton(R.string.text_cancel)
                .show();
    }
    // endregion

    // region tags
    protected void showTagEditDialog() {
        SimpleEditDialog.newInstance("", tag -> {
            if (TextUtils.isEmpty(tag)) return;

            if (tag.indexOf(';') != -1) {
                ToastUtils.makeToast(getContext(), R.string.text_illegal_label);
                return;
            }

            String tags = (TextUtils.isEmpty(getTags()) ? "" : getTags()) + tag + ";";
            if (tags.length() > TextLength.LABELS_TOTAL_LENGTH.length) {
                ToastUtils.makeToast(getContext(), R.string.text_total_labels_too_long);
                return;
            }

            onGetTags(tags);

            addTagToLayout(tag);
        }).setMaxLength(TextLength.LABEL_TEXT_LENGTH.length).show(getFragmentManager(), "SHOW_ADD_LABELS_DIALOG");
    }

    protected void showTagsEditDialog() {
        SimpleEditDialog.newInstance(TextUtils.isEmpty(getTags()) ? "" : getTags(), content -> {
            content = TextUtils.isEmpty(content) ? "" : content;
            if (!content.endsWith(";")) content = content + ";";

            onGetTags(content);

            addTagsToLayout(content);
        }).setMaxLength(TextLength.LABELS_TOTAL_LENGTH.length).show(getFragmentManager(), "TAGS_EDITOR");
    }

    protected FlowLayout getTagsLayout(){
        return null;
    }

    protected String getTags() {
        return "";
    }

    protected void onGetTags(String tags) {}

    protected void addTagsToLayout(String stringTags){
        if (getTagsLayout() == null) return;
        getTagsLayout().removeAllViews();
        if (TextUtils.isEmpty(stringTags)) return;
        String[] tags = stringTags.split(";");
        for (String tag : tags) addTagToLayout(tag);
    }

    protected void addTagToLayout(String tag){
        if (getTagsLayout() == null) return;

        int margin = ViewUtils.dp2Px(getContext(), 2f);
        int padding = ViewUtils.dp2Px(getContext(), 5f);
        TextView tvLabel = new TextView(getContext());
        tvLabel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(tvLabel.getLayoutParams());
        params.setMargins(margin, margin, margin, margin);
        tvLabel.setLayoutParams(params);
        tvLabel.setPadding(padding, 0, padding, 0);
        tvLabel.setBackgroundResource(R.drawable.label_background);
        tvLabel.setText(tag);

        getTagsLayout().addView(tvLabel);
    }
    // endregion

    // region location
    protected void tryToLocate() {
        if (!NetworkUtils.isNetworkAvailable(getActivity())){
            ToastUtils.makeToast(getActivity(), R.string.check_network_availability);
            return;
        }
        if (getActivity() != null) {
            PermissionUtils.checkLocationPermission((CommonActivity) getActivity(), this::locate);
        }
    }

    private void locate(){
        ToastUtils.makeToast(getContext(), R.string.trying_to_get_location);
        LocationManager.getInstance(getContext()).locate(this::onGetLocation);
    }

    protected void onGetLocation(BDLocation bdLocation) {}
    // endregion
}
