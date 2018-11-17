package cn.glority.receipt.view.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.alibaba.android.arouter.launcher.ARouter;
import com.glority.commons.router.Router;
import com.test.generatedAPI.API.enums.PaymentType;

import cn.glority.receipt.R;
import cn.glority.receipt.common.activity.CommonDaggerActivity;
import cn.glority.receipt.common.config.RequestCode;
import cn.glority.receipt.common.util.FragmentHelper;
import cn.glority.receipt.databinding.ActivityContainerBinding;
import cn.glority.receipt.view.account.AboutUsFragment;
import cn.glority.receipt.view.account.ChangeEmailFragment;
import cn.glority.receipt.view.account.ChangePhoneFragment;
import cn.glority.receipt.view.account.FeedbackFragment;
import cn.glority.receipt.view.account.PersonInfoFragment;
import cn.glority.receipt.view.account.TermServiceFragment;
import cn.glority.receipt.view.corporate.CorporateFragment;
import cn.glority.receipt.view.main.interaction.BackEventResolver;

public class ContainerActivity extends CommonDaggerActivity<ActivityContainerBinding> {

    private final static String ACTION_CHANGE_EMAIL = "__action_change_email";

    private final static String ACTION_CHANGE_PHONE = "__action_change_phone";

    private final static String ACTION_TERM_SERVICE = "__action_term_of_service";

    private final static String ACTION_ABOUT_US = "__action_about_us";

    private final static String ACTION_CORPORATE_PAGE = "__action_corporation_page";

    private final static String ACTION_FEEDBACK_PAGE = "__action_feedback_page";

    private final static String ACTION_PERSON_INFORMATION = "__action_person_info";

    private final static String ACTION_PROJECT_PICKER = "__action_project_picker";
    private final static String ACTION_PROJECT_PICKER_ARG_KEY_PAYMENT = "__project_picker_key_payment";

    private final static String EXTRA_KEY_SHOW_RATE_APP = "__extra_key_show_rate_app_dialog";

    public static void changeEmail(Fragment fragment, boolean showRateApp) {
        Intent intent = new Intent(fragment.getContext(), ContainerActivity.class);
        intent.setAction(ACTION_CHANGE_EMAIL);
        intent.putExtra(EXTRA_KEY_SHOW_RATE_APP, showRateApp);
        fragment.startActivityForResult(intent, RequestCode.REQUEST_CHANGE_EMAIL);
    }

    public static void changePhone(Fragment fragment) {
        Intent intent = new Intent(fragment.getContext(), ContainerActivity.class);
        intent.setAction(ACTION_CHANGE_PHONE);
        fragment.startActivityForResult(intent, RequestCode.REQUEST_CHANGE_PHONE);
    }

    public static void termService(Activity activity) {
        Intent intent = new Intent(activity, ContainerActivity.class);
        intent.setAction(ACTION_TERM_SERVICE);
        activity.startActivityForResult(intent, RequestCode.REQUEST_TERM_SERVICE);
    }

    public static void aboutUs(Fragment fragment) {
        Intent intent = new Intent(fragment.getContext(), ContainerActivity.class);
        intent.setAction(ACTION_ABOUT_US);
        fragment.startActivityForResult(intent, RequestCode.REQUEST_ABOUT_US);
    }

    public static void corporate(Activity activity) {
        Intent intent = new Intent(activity, ContainerActivity.class);
        intent.setAction(ACTION_CORPORATE_PAGE);
        activity.startActivityForResult(intent, RequestCode.REQUEST_CORPORATE_PAGE);
    }

    public static void feedback(Fragment fragment) {
        Intent intent = new Intent(fragment.getContext(), ContainerActivity.class);
        intent.setAction(ACTION_FEEDBACK_PAGE);
        fragment.startActivityForResult(intent, RequestCode.REQUEST_FEEDBACK_PAGE);
    }

    public static void personInfo(Fragment fragment) {
        Intent intent = new Intent(fragment.getContext(), ContainerActivity.class);
        intent.setAction(ACTION_PERSON_INFORMATION);
        fragment.startActivityForResult(intent, RequestCode.REQUEST_PERSON_INFO);
    }

    public static void projectPicker(Fragment fragment, PaymentType paymentType) {
        Intent intent = new Intent(fragment.getContext(), ContainerActivity.class);
        intent.setAction(ACTION_PROJECT_PICKER);
        intent.putExtra(ACTION_PROJECT_PICKER_ARG_KEY_PAYMENT, paymentType);
        fragment.startActivityForResult(intent, RequestCode.REQUEST_PROJECT_PICKER);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_container;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String action = intent.getAction();
        if (ACTION_CHANGE_EMAIL.equals(action)) {
            boolean showRateApp = intent.getBooleanExtra(EXTRA_KEY_SHOW_RATE_APP, false);
            toFragment(ChangeEmailFragment.newInstance(showRateApp));
        } else if (ACTION_CHANGE_PHONE.equals(action)) {
            toFragment(ChangePhoneFragment.newInstance());
        } else if (ACTION_TERM_SERVICE.equals(action)) {
            toFragment(TermServiceFragment.newInstance());
        } else if (ACTION_ABOUT_US.equals(action)) {
            toFragment(AboutUsFragment.newInstance());
        } else if (ACTION_CORPORATE_PAGE.equals(action)) {
            toFragment(CorporateFragment.newInstance());
        } else if (ACTION_FEEDBACK_PAGE.equals(action)) {
            toFragment(FeedbackFragment.newInstance());
        } else if (ACTION_PERSON_INFORMATION.equals(action)) {
            toFragment(PersonInfoFragment.newInstance());
        } else if (ACTION_PROJECT_PICKER.equals(action)) {
            PaymentType paymentType = (PaymentType) intent.getSerializableExtra(ACTION_PROJECT_PICKER_ARG_KEY_PAYMENT);
            Fragment fragment = (Fragment) ARouter.getInstance()
                    .build(Router.FRAGMENT_PROJECT_PICKER)
                    .withSerializable(Router.FRAGMENT_PROJECT_PICKER_ARG_PAYMENT_TYPE, paymentType)
                    .navigation();
            toFragment(fragment);
        }
    }

    private void toFragment(Fragment fragment) {
        FragmentHelper.replace(this, fragment,  R.id.fragment_container);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getCurrentFragment(R.id.fragment_container);
        if (fragment != null && fragment instanceof BackEventResolver) {
            ((BackEventResolver) fragment).resolveBackEvent();
        } else {
            super.onBackPressed();
        }
    }
}
