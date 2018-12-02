package me.shouheng.notepal.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.shouheng.easymark.EasyMarkEditor;
import me.shouheng.easymark.editor.Format;
import me.shouheng.easymark.editor.format.DayOneFormatHandler;
import me.shouheng.easymark.scroller.FastScrollScrollView;
import me.shouheng.easymark.tools.Utils;
import me.shouheng.notepal.R;
import me.shouheng.sil.BaseSoftInputLayout;

/**
 * @author WngShhng (shouheng2015@gmail.com)
 * @version $Id: MarkdownEditLayout, v 0.1 2018/11/26 22:59 shouh Exp$
 */
public class MDEditorLayout extends BaseSoftInputLayout {

    public static final int FORMAT_ID_LEFT = 0;
    public static final int FORMAT_ID_RIGHT = 1;
    public static final int FORMAT_ID_UP = 2;
    public static final int FORMAT_ID_DOWN = 3;

    private View frame;
    private View container;
    private EditText titleEditor;
    private EasyMarkEditor easyMarkEditor;
    private FastScrollScrollView fssv;
    private OnFormatClickListener onFormatClickListener;
    private OnCustomFormatClickListener onCustomFormatClickListener;
    private Adapter adapter;

    public MDEditorLayout(Context context) {
        super(context);
    }

    public MDEditorLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MDEditorLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MDEditorLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void doInitView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        LayoutInflater.from(context).inflate(R.layout.layout_markdown_editor, this, true);

        /* Get widgets */
        frame = findViewById(R.id.frame);
        container = findViewById(R.id.container);
        titleEditor = findViewById(R.id.et_title);
        easyMarkEditor = findViewById(R.id.eme);
        fssv = findViewById(R.id.fssv);

        /* Filter formats */
        List<Format> formats = new LinkedList<>();
        Disposable disposable = Observable.fromArray(Format.values())
                .filter(format -> format != Format.H2
                        && format != Format.H3
                        && format != Format.H4
                        && format != Format.H5
                        && format != Format.H6
                        && format != Format.NORMAL_LIST
                        && format != Format.INDENT
                        && format != Format.DEDENT
                        && format != Format.CHECKBOX)
                .toList()
                .subscribe((Consumer<List<Format>>) formats::addAll);

        RecyclerView rv = findViewById(R.id.rv);
        adapter = new Adapter(context, formats, onFormatClickListener);
        rv.setLayoutManager(new GridLayoutManager(context, 8));
        rv.setAdapter(adapter);
        easyMarkEditor.setFormatHandler(new CustomFormatHandler());

        /* Add the bottom buttons click event. */
        ImageView ivSoft = findViewById(R.id.iv_soft);
        ivSoft.setOnClickListener(v -> {
            if (isKeyboardShowing()) {
                hideSoftInputOnly();
                ivSoft.animate().rotation(180).setDuration(500).start();
            } else {
                showSoftInputOnly();
                ivSoft.animate().rotation(0).setDuration(500).start();
            }
        });
        findViewById(R.id.iv_left).setOnClickListener(v -> performCustomButtonClick(FORMAT_ID_LEFT));
        findViewById(R.id.iv_right).setOnClickListener(v -> performCustomButtonClick(FORMAT_ID_RIGHT));
        findViewById(R.id.iv_dedent).setOnClickListener(v -> performCustomButtonClick(Format.DEDENT.id));
        findViewById(R.id.iv_indent).setOnClickListener(v -> performCustomButtonClick(Format.INDENT.id));
    }

    private void performCustomButtonClick(int formatId) {
        if (onCustomFormatClickListener != null) {
            onCustomFormatClickListener.onCustomFormatClick(formatId);
        }
    }

    public final static class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        private Context context;

        private OnFormatClickListener onFormatClickListener;

        private List<Format> formats;

        Adapter(Context context, List<Format> formats, OnFormatClickListener onFormatClickListener) {
            this.context = context;
            this.formats = formats;
            this.onFormatClickListener = onFormatClickListener;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View root = LayoutInflater.from(context).inflate(R.layout.item_format, null, false);
            return new Holder(root);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int i) {
            Format format = formats.get(i);
            holder.iv.setImageDrawable(Utils.tintDrawable(context, format.drawableResId, Color.WHITE));
        }

        @Override
        public int getItemCount() {
            return formats.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            ImageView iv;

            Holder(@NonNull View itemView) {
                super(itemView);

                iv = itemView.findViewById(R.id.iv);
                itemView.setOnClickListener(v -> {
                    if (onFormatClickListener != null) {
                        onFormatClickListener.onFormatClick(formats.get(getAdapterPosition()));
                    }
                });
            }
        }

        void setOnFormatClickListener(OnFormatClickListener onFormatClickListener) {
            this.onFormatClickListener = onFormatClickListener;
        }
    }

    @Override
    protected View getFrame() {
        return frame;
    }

    @Override
    protected View getContainer() {
        return container;
    }

    /**
     * Get the markdown editor.
     *
     * @return the editor
     */
    @Override
    public EasyMarkEditor getEditText() {
        return easyMarkEditor;
    }

    public EditText getTitleEditor() {
        return titleEditor;
    }

    /**
     * Get the fast scroll view.
     *
     * @return the fast scroll view.
     */
    public FastScrollScrollView getFastScrollView() {
        return fssv;
    }

    /**
     * Set the format click event callback.
     *
     * @param onFormatClickListener the format click callback
     */
    public void setOnFormatClickListener(OnFormatClickListener onFormatClickListener) {
        this.onFormatClickListener = onFormatClickListener;
        if (adapter != null) {
            adapter.setOnFormatClickListener(onFormatClickListener);
        }
    }

    /**
     * Set the custom format callback, the format id will be send the the listener.
     *
     * @param onCustomFormatClickListener the format listener
     */
    public void setOnCustomFormatClickListener(OnCustomFormatClickListener onCustomFormatClickListener) {
        this.onCustomFormatClickListener = onCustomFormatClickListener;
    }

    /**
     * The custom format handler
     */
    public static class CustomFormatHandler extends DayOneFormatHandler {

        @Override
        public void handle(int formatId,
                           @org.jetbrains.annotations.Nullable String source,
                           int selectionStart,
                           int selectionEnd,
                           @org.jetbrains.annotations.Nullable String selection,
                           @org.jetbrains.annotations.Nullable EditText editor,
                           @NotNull Object... params) {
            switch (formatId) {
                case FORMAT_ID_LEFT: {
                    assert editor != null;
                    int pos = selectionStart - 1;
                    editor.setSelection(pos < 0 ? 0 : pos);
                    break;
                }
                case FORMAT_ID_RIGHT: {
                    assert editor != null;
                    int pos = selectionStart + 1;
                    assert source != null;
                    int length = source.length();
                    editor.setSelection(pos >= length ? length : pos);
                    break;
                }
                case FORMAT_ID_UP:
                    break;
                case FORMAT_ID_DOWN:
                    break;
            }
            Format format = Format.getFormat(formatId);
            if (format != null && editor != null) {
                switch (format) {
                    case IMAGE:
                        if (params.length == 2) {
                            String title = (String) params[0];
                            String url = (String) params[1];
                            String result = "\n![" + title + "](" + url + ")\n";
                            editor.getText().insert(selectionStart, result);
                            editor.setSelection(selectionStart + result.length());
                            return;
                        }
                        break;
                    case LINK:
                        if (params.length == 2) {
                            String title = (String) params[0];
                            String url = (String) params[1];
                            String result = "\n[" + title + "](" + url + ")\n";
                            editor.getText().insert(selectionStart, result);
                            editor.setSelection(selectionStart + result.length());
                            return;
                        }
                        break;
                }
            }
            super.handle(formatId, source, selectionStart, selectionEnd, selection, editor, params);
        }
    }

    public interface OnFormatClickListener {
        void onFormatClick(Format format);
    }

    public interface OnCustomFormatClickListener {
        void onCustomFormatClick(int formatId);
    }
}
