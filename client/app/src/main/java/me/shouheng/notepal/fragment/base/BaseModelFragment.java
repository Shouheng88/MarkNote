package me.shouheng.notepal.fragment.base;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;

import java.util.LinkedList;
import java.util.List;

import me.shouheng.commons.activity.CommonActivity;
import me.shouheng.commons.utils.LogUtils;
import me.shouheng.commons.utils.PermissionUtils;
import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.ContentActivity;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.dialog.CategoryEditDialog;
import me.shouheng.notepal.dialog.picker.CategoryPickerDialog;
import me.shouheng.notepal.model.Category;
import me.shouheng.notepal.model.Location;
import me.shouheng.notepal.model.Model;
import me.shouheng.notepal.model.ModelFactory;
import me.shouheng.notepal.model.data.Resource;
import me.shouheng.notepal.provider.CategoryStore;
import me.shouheng.notepal.util.AppWidgetUtils;
import me.shouheng.commons.utils.NetworkUtils;
import me.shouheng.notepal.util.ShortcutHelper;
import me.shouheng.commons.utils.ToastUtils;
import me.shouheng.commons.utils.ViewUtils;
import me.shouheng.notepal.viewmodel.BaseViewModel;
import me.shouheng.notepal.viewmodel.CategoryViewModel;
import me.shouheng.notepal.widget.FlowLayout;

/**
 * Created by wangshouheng on 2017/9/3.*/
public abstract class BaseModelFragment<T extends Model, V extends ViewDataBinding> extends BaseFragment<V> {

    // region edit structure
    /**
     * Field remark that is the content changed. */
    private boolean contentChanged = false;

    /**
     * Have we ever saved or updated the content. */
    private boolean savedOrUpdated = false;

    protected void setContentChanged() {
        this.contentChanged = true;
    }

    protected final boolean isContentChanged() {
        return contentChanged;
    }

    /**
     * Get the model to work with.
     *
     * @return the model to work with */
    protected abstract T getModel();

    /**
     * Get the view model to operate the model.
     *
     * @return the view model */
    protected abstract BaseViewModel<T> getViewModel();

    /**
     * Check the model content before save or update.
     *
     * @return true if the content is legal otherwise false. */
    protected boolean checkContent() {
        return true;
    }

    /**
     * This method will be called before save or update the model. */
    protected void beforeSaveOrUpdate(BeforePersistEventHandler handler) {}

    /**
     * Save the model to db if it is new, otherwise update the existed one. */
    protected void doPersist(PersistEventHandler handler) {
        getViewModel().saveOrUpdate(getModel()).observe(this, tResource -> {
            if (tResource == null) {
                ToastUtils.makeToast(R.string.text_error_when_save);
                return;
            }
            switch (tResource.status) {
                case SUCCESS:
                    ToastUtils.makeToast(R.string.text_save_successfully);
                    updateState();
                    afterSaveOrUpdate();
                    if (handler != null) handler.onGetEventResult(true);
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_error_when_save);
                    if (handler != null) handler.onGetEventResult(false);
                    break;
            }
        });
    }

    protected void afterSaveOrUpdate() {
        AppWidgetUtils.notifyAppWidgets(getContext());
    }

    protected LiveData<Resource<Boolean>> isNewModel() {
        return getViewModel().isNewModel(getModel().getCode());
    }

    protected final void saveOrUpdateData(PersistEventHandler handler) {
        if (!checkContent()) {
            if (handler != null) {
                handler.onGetEventResult(false);
            }
            return;
        }

        beforeSaveOrUpdate(succeed -> {
            if (succeed) {
                doPersist(handler);
            }
        });
    }

    private void updateState() {
        contentChanged = false;
        savedOrUpdated = true;
    }

    protected final void onBack() {
        if (getActivity() == null) {
            // the activity is not attached
            LogUtils.e("Error! Activity is not attached when go back!");
            return;
        }

        CommonActivity activity = (CommonActivity) getActivity();
        if (isContentChanged()){
            new MaterialDialog.Builder(getContext())
                    .title(R.string.text_tips)
                    .content(R.string.text_save_or_discard)
                    .positiveText(R.string.text_save)
                    .negativeText(R.string.text_give_up)
                    .onPositive((materialDialog, dialogAction) -> {
                        if (!checkContent()){
                            return;
                        }
                        saveOrUpdateData(succeed -> setResult());
                    })
                    .onNegative((materialDialog, dialogAction) -> activity.superOnBackPressed())
                    .show();
        } else {
            setResult();
        }
    }

    protected final void setResult() {
        // If the activity is null, do nothing
        if (getActivity() == null) return;

        CommonActivity activity = (CommonActivity) getActivity();

        // The model didn't change.
        if (!savedOrUpdated) {
            activity.superOnBackPressed();
        }

        // If the argument has request code, return it, otherwise just go back
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

    public interface BeforePersistEventHandler {
        void onGetEventResult(boolean succeed);
    }

    public interface PersistEventHandler {
        void onGetEventResult(boolean succeed);
    }
    // endregion

    // region drawer
    protected void addShortcut(){
        if (getActivity() == null) return;

        isNewModel().observe(this, booleanResource -> {
            if (booleanResource == null) {
                LogUtils.e("Error! booleanResource is null when query is new model!");
                return;
            }
            LogUtils.d(booleanResource);
            switch (booleanResource.status) {
                case SUCCESS:
                    if (booleanResource.data != null && !booleanResource.data) {
                        ShortcutHelper.addShortcut(getActivity().getApplicationContext(), getModel());
                        ToastUtils.makeToast(R.string.successfully_add_shortcut);
                    } else {
                        new MaterialDialog.Builder(getContext())
                                .title(R.string.text_tips)
                                .content(R.string.text_save_and_retry_to_add_shortcut)
                                .positiveText(R.string.text_save_and_retry)
                                .negativeText(R.string.text_give_up)
                                .onPositive((materialDialog, dialogAction) -> saveOrUpdateData(succeed -> {
                                    if (succeed) {
                                        ShortcutHelper.addShortcut(getContext(), getModel());
                                        ToastUtils.makeToast(R.string.successfully_add_shortcut);
                                    }
                                })).show();
                    }
                    break;
            }
        });
    }

    protected void showColorPickerDialog(int titleRes) {
        if (!(getActivity() instanceof ContentActivity)) {
            throw new IllegalArgumentException("The associated activity must be content!");
        }
        new ColorChooserDialog.Builder(getActivity(), titleRes)
                .preselect(primaryColor())
                .accentMode(false)
                .titleSub(titleRes)
                .backButton(R.string.text_back)
                .doneButton(R.string.text_done)
                .cancelButton(R.string.text_cancel)
                .show(getChildFragmentManager());
    }
    // endregion

    // region Base logic about category
    private List<Category> allCategories;
    private List<Category> cs = new LinkedList<>();

    private CategoryEditDialog categoryEditDialog;

    /**
     * Call this method and override {@link #onGetSelectedCategories(List)} to implement
     * the logic of getting categories.
     *
     * @param selected selected categories */
    protected void showCategoriesPicker(List<Category> selected) {
        List<Category> all = getAllCategories();

        // try to avoid NPE
        if (selected == null) selected = new LinkedList<>();
        cs = selected;

        for (Category c : selected) {
            for (Category a : all) {
                if (c.getCode() == a.getCode()) {
                    a.setSelected(true);
                }
            }
        }

        CategoryPickerDialog dialog = CategoryPickerDialog.newInstance(all);
        dialog.setOnConfirmClickListener(this::onGetSelectedCategories);
        dialog.setOnAddClickListener(this::showCategoryEditor);
        dialog.show(getFragmentManager(), "CATEGORY_PICKER");
    }

    private void showCategoryEditor() {
        categoryEditDialog = CategoryEditDialog.newInstance(ModelFactory.getCategory(), this::saveCategory);
        categoryEditDialog.show(getFragmentManager(), "CATEGORY_EDIT_DIALOG");
    }

    private void saveCategory(Category category) {
        ViewModelProviders.of(this).get(CategoryViewModel.class)
                .saveModel(category).observe(this, categoryResource -> {
            if (categoryResource == null) {
                ToastUtils.makeToast(R.string.text_error_when_save);
                return;
            }
            switch (categoryResource.status) {
                case SUCCESS:
                    ToastUtils.makeToast(R.string.text_save_successfully);
                    allCategories.add(0, category);
                    showCategoriesPicker(cs);
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_error_when_save);
                    break;
            }
        });
    }

    protected void onGetSelectedCategories(List<Category> categories) {}

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
        String[] tags = stringTags.split(CategoryViewModel.CATEGORY_SPLIT);
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
//        LocationManager.getInstance(getContext()).locate(bdLocation -> {
//            if (bdLocation != null && !TextUtils.isEmpty(bdLocation.getCity())){
//                Location location = ModelFactory.getLocation();
//                location.setLongitude(bdLocation.getLongitude());
//                location.setLatitude(bdLocation.getLatitude());
//                location.setCountry(bdLocation.getCountry());
//                location.setProvince(bdLocation.getProvince());
//                location.setCity(bdLocation.getCity());
//                location.setDistrict(bdLocation.getDistrict());
//                onGetLocation(location);
//            } else {
//                ToastUtils.makeToast(R.string.failed_to_get_location);
//            }
//        });
    }

    protected void onGetLocation(Location location) {}
    // endregion

    public void onColorSelection(@ColorInt int i) {
        if (categoryEditDialog != null) {
//            categoryEditDialog.updateUIBySelectedColor(i);
        }
    }
}
