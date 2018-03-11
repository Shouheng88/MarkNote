package me.shouheng.notepal.fragment.base;

import android.app.Activity;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;

import org.polaric.colorful.PermissionUtils;

import java.util.LinkedList;
import java.util.List;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.base.CommonActivity;
import me.shouheng.notepal.activity.ContentActivity;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.manager.LocationManager;
import me.shouheng.notepal.model.Category;
import me.shouheng.notepal.model.Location;
import me.shouheng.notepal.model.Model;
import me.shouheng.notepal.model.ModelFactory;
import me.shouheng.notepal.provider.BaseStore;
import me.shouheng.notepal.provider.CategoryStore;
import me.shouheng.notepal.util.AppWidgetUtils;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.NetworkUtils;
import me.shouheng.notepal.util.ShortcutHelper;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.util.ViewUtils;
import me.shouheng.notepal.widget.FlowLayout;


/**
 * Created by wangshouheng on 2017/9/3.*/
public abstract class BaseModelFragment<T extends Model, V extends ViewDataBinding> extends BaseFragment<V> {

    // region edit structure
    private Boolean isNewModel;

    private boolean contentChanged;

    private boolean savedOrUpdated;

    protected abstract boolean checkInputInfo();

    protected void beforeSaveOrUpdate(){}

    protected abstract void saveModel();

    protected abstract void updateModel();

    protected abstract BaseStore<T> getStoreOfModel();

    protected abstract T getModel();

    protected void afterSaveOrUpdate() {
        AppWidgetUtils.notifyAppWidgets(getContext());
    }

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
        ToastUtils.makeToast(R.string.text_save_successfully);

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
        assert getActivity() != null;
        CommonActivity activity = (CommonActivity) getActivity();
        if (!savedOrUpdated) {
            activity.superOnBackPressed();
        }

        Bundle args = getArguments();
        if (args != null && args.containsKey(Constants.EXTRA_REQUEST_CODE)){
            Intent intent = new Intent();
            intent.putExtra(Constants.EXTRA_MODEL, getModel());
            if (args.containsKey(Constants.EXTRA_POSITION)){
                intent.putExtra(Constants.EXTRA_POSITION, args.getInt(Constants.EXTRA_POSITION, 0));
            }
            getActivity().setResult(Activity.RESULT_OK, intent);
            activity.superOnBackPressed();
        } else {
            activity.superOnBackPressed();
        }
    }

    protected void onBack() {
        assert getActivity() != null;
        CommonActivity activity = (CommonActivity) getActivity();
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
                    .onNegative((materialDialog, dialogAction) -> activity.superOnBackPressed())
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
                        ToastUtils.makeToast(R.string.successfully_add_shortcut);
                    }).show();
        } else {
            ShortcutHelper.addShortcut(getActivity().getApplicationContext(), getModel());
            ToastUtils.makeToast(R.string.successfully_add_shortcut);
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

    // region Base logic about category
    private List<Category> allCategories;

    /**
     * Call this method and override {@link #onGetSelectedCategories(List)} to implement
     * the logic of getting categories.
     *
     * @param selected selected categories */
    protected void showCategoriesPicker(List<Category> selected) {
        List<Category> all = getAllCategories();

        if (all == null || all.isEmpty()) {
            showCategoryEmptyDialog();
            return;
        }

        int len = all.size();
        String[] items = new String[len];
        boolean[] checked = new boolean[len];
        for (int i=0; i<len; i++) {
            Category current = all.get(i);
            items[i] = current.getName();
            for (Category category : selected) {
                if (category.getCode() == current.getCode()) {
                    checked[i] = true;
                    break;
                }
            }
        }

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.text_add_tags)
                .setMultiChoiceItems(items, checked, (dialog, which, isChecked) -> checked[which] = isChecked)
                .setPositiveButton(R.string.text_confirm, (dialog, which) -> {
                    LogUtils.d(checked);
                    List<Category> ret = new LinkedList<>();
                    for (int i=0; i<len; i++) if (checked[i]) ret.add(all.get(i));
                    onGetSelectedCategories(ret);
                })
                .setNegativeButton(R.string.text_cancel, null)
                .show();
    }

    protected void onGetSelectedCategories(List<Category> categories) {}

    private void showCategoryEmptyDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.text_tips)
                .setMessage(R.string.no_selectable_tag)
                .setPositiveButton(R.string.text_confirm, null)
                .create()
                .show();
    }

    private List<Category> getAllCategories() {
        if (allCategories == null) {
            allCategories = CategoryStore.getInstance(getContext()).get(null, null);
        }
        return allCategories;
    }

    /**
     * Call this method and override {@link #getTagsLayout()} to implement the logic of showing tags.
     *
     * @param stringTags tags string */
    protected void addTagsToLayout(String stringTags) {
        if (getTagsLayout() == null) return;
        getTagsLayout().removeAllViews();
        if (TextUtils.isEmpty(stringTags)) return;
        String[] tags = stringTags.split(CategoryStore.CATEGORY_SPLIT);
        for (String tag : tags) addTagToLayout(tag);
    }

    protected FlowLayout getTagsLayout() {
        return null;
    }

    private void addTagToLayout(String tag) {
        if (getTagsLayout() == null) return;

        int margin = ViewUtils.dp2Px(getContext(), 2f);
        int padding = ViewUtils.dp2Px(getContext(), 5f);
        TextView tvLabel = new TextView(getContext());
        tvLabel.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
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
            ToastUtils.makeToast(R.string.check_network_availability);
            return;
        }
        if (getActivity() != null) {
            PermissionUtils.checkLocationPermission((CommonActivity) getActivity(), this::baiduLocate);
        }
    }

    private void baiduLocate() {
        ToastUtils.makeToast(R.string.trying_to_get_location);
        LocationManager.getInstance(getContext()).locate(bdLocation -> {
            if (bdLocation != null && !TextUtils.isEmpty(bdLocation.getCity())){
                Location location = ModelFactory.getLocation(getContext());
                location.setLongitude(bdLocation.getLongitude());
                location.setLatitude(bdLocation.getLatitude());
                location.setCountry(bdLocation.getCountry());
                location.setProvince(bdLocation.getProvince());
                location.setCity(bdLocation.getCity());
                location.setDistrict(bdLocation.getDistrict());
                onGetLocation(location);
            } else {
                ToastUtils.makeToast(R.string.failed_to_get_location);
            }
        });
    }

    protected void onGetLocation(Location location) {}
    // endregion

}
