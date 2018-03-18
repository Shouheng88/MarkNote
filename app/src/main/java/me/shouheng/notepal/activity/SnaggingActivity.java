package me.shouheng.notepal.activity;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.polaric.colorful.BaseActivity;

import java.util.Collections;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.dialog.AttachmentPickerDialog;
import me.shouheng.notepal.dialog.MindSnaggingDialog;
import me.shouheng.notepal.dialog.MindSnaggingDialog.OnLifeMethodCalledListener;
import me.shouheng.notepal.listener.OnAttachingFileListener;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.model.MindSnagging;
import me.shouheng.notepal.model.Model;
import me.shouheng.notepal.model.ModelFactory;
import me.shouheng.notepal.model.enums.ModelType;
import me.shouheng.notepal.util.AppWidgetUtils;
import me.shouheng.notepal.util.AttachmentHelper;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.PreferencesUtils;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.viewmodel.AttachmentViewModel;
import me.shouheng.notepal.viewmodel.SnaggingViewModel;

// todo remove the logic of snagging
public class SnaggingActivity extends BaseActivity implements OnAttachingFileListener {

    private final static int REQUEST_PASSWORD = 0x0016;

    private MindSnaggingDialog mindSnaggingDialog;

    private AttachmentViewModel attachmentViewModel;
    private SnaggingViewModel snaggingViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPassword();
    }

    private void checkPassword() {
        if (PreferencesUtils.getInstance(this).isPasswordRequired() && !PalmApp.isPasswordChecked()) {
            LockActivity.requireLaunch(this, REQUEST_PASSWORD);
        } else {
            init();
        }
    }

    private void init() {
        handleIntent(getIntent());

        snaggingViewModel = ViewModelProviders.of(this).get(SnaggingViewModel.class);
        attachmentViewModel = ViewModelProviders.of(this).get(AttachmentViewModel.class);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        LogUtils.d("action:" + action);

        if (TextUtils.isEmpty(action)) {
            finish();
            return;
        }

        switch (action) {
            case Constants.ACTION_WIDGET_LIST:
                Model model;
                if (intent.hasExtra(Constants.EXTRA_MODEL)
                        && (model = (Model) intent.getSerializableExtra(Constants.EXTRA_MODEL)) != null) {
                    if (model instanceof MindSnagging) {
                        LogUtils.d(model);
                        editMindSnagging((MindSnagging) model);
                    }
                }
                break;
            case Constants.ACTION_ADD_MIND:
                editMindSnagging(ModelFactory.getMindSnagging(this));
                break;
            default:
                finish();
        }
    }

    private void editMindSnagging(@NonNull MindSnagging param) {
        mindSnaggingDialog = new MindSnaggingDialog.Builder()
                .setMindSnagging(param)
                .setOnAddAttachmentListener(mindSnagging -> showAttachmentPicker())
                .setOnLifeMethodCalledListener(new OnLifeMethodCalledListener() {
                    @Override
                    public void onCancel() {
                        finish();
                    }

                    @Override
                    public void onDismiss() {
                        finish();
                    }
                })
                .setOnAttachmentClickListener(this::resolveAttachmentClick)
                .setOnConfirmListener(this::saveMindSnagging)
                .build();
        mindSnaggingDialog.show(getSupportFragmentManager(), "MIND SNAGGING");
    }

    private void resolveAttachmentClick(Attachment attachment) {
        AttachmentHelper.resolveClickEvent(
                this,
                attachment,
                Collections.singletonList(attachment),
                attachment.getName());
    }

    private void saveMindSnagging(MindSnagging mindSnagging, Attachment attachment) {
        if (attachment != null) {
            attachment.setModelCode(mindSnagging.getCode());
            attachment.setModelType(ModelType.MIND_SNAGGING);
            attachmentViewModel.saveIfNew(attachment).observe(this, attachmentResource -> {});
        }

        snaggingViewModel.saveOrUpdate(mindSnagging).observe(this, mindSnaggingResource -> {
            if (mindSnaggingResource == null) {
                ToastUtils.makeToast(R.string.text_failed_to_modify_data);
                return;
            }
            switch (mindSnaggingResource.status) {
                case SUCCESS:
                    ToastUtils.makeToast(R.string.text_save_successfully);
                    AppWidgetUtils.notifyAppWidgets(this);
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed_to_modify_data);
                    break;
            }
        });
    }

    private void showAttachmentPicker() {
        new AttachmentPickerDialog.Builder()
                .setAddLinkVisible(false)
                .setRecordVisible(false)
                .setVideoVisible(false)
                .build().show(getSupportFragmentManager(), "ATTACHMENT PICKER");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            AttachmentHelper.resolveResult(this,
                    requestCode,
                    data,
                    attachment -> mindSnaggingDialog.setAttachment(attachment));
        }
        switch (requestCode) {
            case REQUEST_PASSWORD:
                if (resultCode == RESULT_OK) {
                    init();
                } else {
                    finish();
                }
                break;
        }
    }

    @Override
    public void onAttachingFileErrorOccurred(Attachment attachment) {
        ToastUtils.makeToast(R.string.failed_to_save_attachment);
    }

    @Override
    public void onAttachingFileFinished(Attachment attachment) {
        if (AttachmentHelper.checkAttachment(attachment)) {
            mindSnaggingDialog.setAttachment(attachment);
        }
    }
}
