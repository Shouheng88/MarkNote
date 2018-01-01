package me.shouheng.notepal.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import me.shouheng.notepal.R;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.util.TimeUtils;

/**
 * Created by wang shouheng on 2017/12/23.*/
public class NotesAdapter extends BaseMultiItemQuickAdapter<NotesAdapter.MultiItem, BaseViewHolder> {

    private Context context;

    private int accentColor;
    private boolean isDarkTheme;

    public NotesAdapter(Context context, List<NotesAdapter.MultiItem> data) {
        super(data);
        this.context = context;
        addItemType(MultiItem.ITEM_TYPE_NOTE, R.layout.item_note);
        addItemType(MultiItem.ITEM_TYPE_NOTEBOOK, R.layout.item_note);

        accentColor = ColorUtils.accentColor(context);
        isDarkTheme = ColorUtils.isDarkTheme(context);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItem item) {
        if (isDarkTheme) helper.itemView.setBackgroundResource(R.color.dark_theme_foreground);
        switch (helper.getItemViewType()) {
            case MultiItem.ITEM_TYPE_NOTE:
                convertNote(helper, item.note);
                break;
            case MultiItem.ITEM_TYPE_NOTEBOOK:
                convertNotebook(helper, item.notebook);
                break;
        }
    }

    private void convertNote(BaseViewHolder helper, Note note) {
        helper.setText(R.id.tv_note_title, note.getTitle());
        helper.setText(R.id.tv_added_time, TimeUtils.getLongDateTime(context, note.getAddedTime()));
        helper.setImageDrawable(R.id.iv_icon, ColorUtils.tintDrawable(
                context.getResources().getDrawable(R.drawable.ic_doc_text_alpha), accentColor));
    }

    private void convertNotebook(BaseViewHolder helper, Notebook notebook) {
        int nbColor = notebook.getColor();
        helper.setText(R.id.tv_note_title, notebook.getTitle());
        String str = context.getResources().getQuantityString(R.plurals.notes_count,
                notebook.getCount(), notebook.getCount());
        helper.setText(R.id.tv_added_time, str);
        helper.setImageDrawable(R.id.iv_icon, ColorUtils.tintDrawable(
                context.getResources().getDrawable(R.drawable.ic_folder_black_24dp), nbColor));
    }

    public static class MultiItem implements MultiItemEntity {

        public static final int ITEM_TYPE_NOTE = 0;

        public static final int ITEM_TYPE_NOTEBOOK = 1;

        public int itemType;

        public Note note;

        public Notebook notebook;

        public MultiItem(Note note) {
            this.note = note;
            this.itemType = ITEM_TYPE_NOTE;
        }

        public MultiItem(Notebook notebook) {
            this.notebook = notebook;
            this.itemType = ITEM_TYPE_NOTEBOOK;
        }

        @Override
        public int getItemType() {
            return itemType;
        }
    }
}
