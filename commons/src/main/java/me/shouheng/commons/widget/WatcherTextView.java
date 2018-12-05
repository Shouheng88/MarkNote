package me.shouheng.commons.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import me.shouheng.commons.R;
import me.shouheng.commons.utils.ColorUtils;

/**
 * Created by wangshouheng on 2017/8/27.*/
public class WatcherTextView extends android.support.v7.widget.AppCompatTextView {

    private Integer maxLength;

    private TextLengthWatcher textLengthWatcher;

    public WatcherTextView(Context context) {
        super(context);
    }

    public WatcherTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, -1);
    }

    public WatcherTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.WatcherTextView, 0, 0);

        setTextColor(ColorUtils.accentColor());

        int maxLength = attr.getInt(R.styleable.WatcherTextView_max_length, Integer.MAX_VALUE);
        this.maxLength = maxLength == Integer.MAX_VALUE ? null : maxLength;

        attr.recycle();
    }

    public void bindEditText(EditText editText) {
        if (editText == null) return;

        StringBuilder sb = new StringBuilder(TextUtils.isEmpty(editText.getText())
                ? String.valueOf(0) : String.valueOf(editText.getText().length()));
        if (this.maxLength != null) sb.append("/").append(maxLength);

        setText(sb);

        editText.addTextChangedListener(textLengthWatcher = new TextLengthWatcher(this, maxLength));
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
        this.textLengthWatcher.setWeakLength(maxLength);
    }

    private static class TextLengthWatcher implements TextWatcher {

        private WeakReference<Integer> weakLength;

        private WeakReference<TextView> weakTextView;

        TextLengthWatcher(TextView textView, Integer maxLength) {
            this.weakLength = new WeakReference<>(maxLength);
            this.weakTextView = new WeakReference<>(textView);
        }

        void setWeakLength(Integer maxLength) {
            this.weakLength = new WeakReference<>(maxLength);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (weakTextView.get() == null) return;
            if (weakLength.get() == null) {
                weakTextView.get().setText(String.valueOf(s.length()));
            } else {
                String sb = s.length() + "/" + weakLength.get();
                weakTextView.get().setText(sb);
            }
        }
    }
}
