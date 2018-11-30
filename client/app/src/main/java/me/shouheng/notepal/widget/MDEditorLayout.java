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

import me.shouheng.easymark.EasyMarkEditor;
import me.shouheng.easymark.editor.Format;
import me.shouheng.easymark.scroller.FastScrollScrollView;
import me.shouheng.easymark.tools.Utils;
import me.shouheng.notepal.R;
import me.shouheng.sil.BaseSoftInputLayout;

/**
 * @author WngShhng (shouheng2015@gmail.com)
 * @version $Id: MarkdownEditLayout, v 0.1 2018/11/26 22:59 shouh Exp$
 */
public class MDEditorLayout extends BaseSoftInputLayout {

    private View frame;
    private View container;
    private EditText titleEditor;
    private EasyMarkEditor easyMarkEditor;
    private FastScrollScrollView fssv;

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

        frame = findViewById(R.id.frame);
        container = findViewById(R.id.container);
        titleEditor = findViewById(R.id.et_title);
        easyMarkEditor = findViewById(R.id.eme);
        fssv = findViewById(R.id.fssv);

        RecyclerView rv = findViewById(R.id.rv);
        Adapter adapter = new Adapter(context, format -> easyMarkEditor.useFormat(format));
        rv.setLayoutManager(new GridLayoutManager(context, 8));
        rv.setAdapter(adapter);

        findViewById(R.id.iv_soft).setOnClickListener(v -> {
            if (isKeyboardShowing()) {
                hideSoftInputOnly();
            } else {
                showSoftInputOnly();
            }
        });
    }

    public final static class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        private Context context;

        private OnFormatClickListener onFormatClickListener;

        Adapter(Context context, OnFormatClickListener onFormatClickListener) {
            this.context = context;
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
            Format format = Format.values()[i];
            holder.iv.setImageDrawable(Utils.tintDrawable(context, format.drawableResId, Color.WHITE));
        }

        @Override
        public int getItemCount() {
            return Format.values().length;
        }

        class Holder extends RecyclerView.ViewHolder {

            ImageView iv;

            Holder(@NonNull View itemView) {
                super(itemView);

                iv = itemView.findViewById(R.id.iv);
                itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onFormatClickListener != null) {
                            onFormatClickListener.onFormatClick(Format.values()[getAdapterPosition()]);
                        }
                    }
                });
            }
        }

        public interface OnFormatClickListener {
            void onFormatClick(Format format);
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
}
