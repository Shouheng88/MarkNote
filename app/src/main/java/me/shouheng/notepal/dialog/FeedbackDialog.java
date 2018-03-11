package me.shouheng.notepal.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import java.lang.ref.WeakReference;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.model.Feedback;
import me.shouheng.notepal.model.ModelFactory;
import me.shouheng.notepal.model.enums.FeedbackType;
import me.shouheng.notepal.util.ModelHelper;
import me.shouheng.notepal.util.StringUtils;
import me.shouheng.notepal.util.ToastUtils;
import me.shouheng.notepal.widget.WatcherTextView;

/**
 * Created by wangshouheng on 2017/12/3.*/
@SuppressLint("ValidFragment")
public class FeedbackDialog extends DialogFragment implements AdapterView.OnItemSelectedListener {

    private Context context;

    private Feedback feedback;

    private OnSendClickListener onSendClickListener;

    private AppCompatEditText etEmail, etQuestion;

    public static FeedbackDialog newInstance(Context context, OnSendClickListener onSendClickListener) {
        Bundle args = new Bundle();
        FeedbackDialog fragment = new FeedbackDialog(context, onSendClickListener);
        fragment.setArguments(args);
        return fragment;
    }

    public FeedbackDialog(Context context, OnSendClickListener onSendClickListener) {
        this.context = context;
        this.onSendClickListener = onSendClickListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_feedback_layout, null);

        feedback = feedback == null ? ModelFactory.getFeedback(context) : feedback;
        feedback.setFeedbackType(FeedbackType.ABRUPT_CRASH);

        etEmail = rootView.findViewById(R.id.et_email);
        TextInputLayout tilEmail = rootView.findViewById(R.id.til_email);
        etEmail.addTextChangedListener(new EmailFormatWatcher(tilEmail));
        etQuestion = rootView.findViewById(R.id.et_question);
        WatcherTextView wtQuestion = rootView.findViewById(R.id.wt_question);
        wtQuestion.bindEditText(etQuestion);
        AppCompatSpinner spFeedbackTypes = rootView.findViewById(R.id.sp_feedback_types);
        spFeedbackTypes.setOnItemSelectedListener(this);

        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.feedback)
                .setView(rootView)
//                .setNegativeButton(R.string.text_other_ways, (dialog, i) -> {})
                .setPositiveButton(R.string.text_send, (dialog, which) -> {
                    if (!checkInput()) return;
                    if (onSendClickListener != null) onSendClickListener.onSend(FeedbackDialog.this, feedback);
                })
                .setNegativeButton(R.string.text_cancel, null)
                .create();
    }

    private void copyContentIfNecessary() {
        if (!TextUtils.isEmpty(etQuestion.getText().toString())) {
            ModelHelper.copyToClipboard(getActivity(), etQuestion.getText().toString());
            ToastUtils.makeToast(R.string.content_was_copied_to_clipboard);
        }
    }

    private boolean checkInput() {
        String email, details;
        if (TextUtils.isEmpty(email = etEmail.getText().toString())) {
            ToastUtils.makeToast(R.string.connect_email_required);
            copyContentIfNecessary();
            return false;
        }
        if (!StringUtils.validate(email)) {
            ToastUtils.makeToast(R.string.illegal_email_format);
            copyContentIfNecessary();
            return false;
        }
        feedback.setEmail(email);
        if (TextUtils.isEmpty(details = etQuestion.getText().toString())) {
            ToastUtils.makeToast(R.string.details_required);
            return false;
        }
        feedback.setQuestion(details);
        return true;
    }

    private static class EmailFormatWatcher implements TextWatcher {

        private WeakReference<TextInputLayout> weakEt;

        public EmailFormatWatcher(TextInputLayout et) {
            this.weakEt = new WeakReference<>(et);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            TextInputLayout et = weakEt.get();
            if (StringUtils.validate(s.toString())) {
                if (et != null) et.setErrorEnabled(false);
                return;
            }
            if (et != null) {
                et.setErrorEnabled(true);
                et.setError(PalmApp.getContext().getString(R.string.illegal_email_format));
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:feedback.setFeedbackType(FeedbackType.ABRUPT_CRASH);break;
            case 1:feedback.setFeedbackType(FeedbackType.FUNCTION_IMPROVEMENT);break;
            case 2:feedback.setFeedbackType(FeedbackType.FUNCTION_REQUIREMENT);break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    public interface OnSendClickListener {
        void onSend(FeedbackDialog dialog, Feedback feedback);
    }
}
