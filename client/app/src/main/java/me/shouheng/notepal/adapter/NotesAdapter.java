package me.shouheng.notepal.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.util.FileHelper;
import me.shouheng.notepal.util.TimeUtils;
import me.shouheng.notepal.util.preferences.NotePreferences;
import me.shouheng.notepal.widget.tools.BubbleTextGetter;

/**
 * Created by wang shouheng on 2017/12/23.*/
public class NotesAdapter extends BaseMultiItemQuickAdapter<NotesAdapter.MultiItem, BaseViewHolder> implements
        BubbleTextGetter {

    private Context context;

    private int accentColor;
    private boolean isDarkTheme;
    private boolean isExpanded;

    public NotesAdapter(Context context, List<NotesAdapter.MultiItem> data) {
        super(data);

        this.isExpanded = NotePreferences.getInstance().isNoteExpanded();
        this.context = context;
        addItemType(MultiItem.ITEM_TYPE_NOTE, isExpanded ? R.layout.item_note_expanded : R.layout.item_note);
        addItemType(MultiItem.ITEM_TYPE_NOTEBOOK, R.layout.item_note);

        accentColor = ColorUtils.accentColor(context);
        isDarkTheme = ColorUtils.isDarkTheme(context);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItem item) {
        if (isDarkTheme) helper.itemView.setBackgroundResource(R.color.dark_theme_background);
        switch (helper.getItemViewType()) {
            case MultiItem.ITEM_TYPE_NOTE:
                if (isExpanded) {
                    convertNoteExpanded(helper, item.note);
                } else {
                    convertNote(helper, item.note);
                }
                break;
            case MultiItem.ITEM_TYPE_NOTEBOOK:
                convertNotebook(helper, item.notebook);
                break;
        }
        helper.addOnClickListener(R.id.iv_more);
    }

    private void convertNote(BaseViewHolder helper, Note note) {
        helper.setText(R.id.tv_note_title, note.getTitle());
        helper.setText(R.id.tv_added_time, TimeUtils.getLongDateTime(context, note.getAddedTime()));
        helper.setImageDrawable(R.id.iv_icon, ColorUtils.tintDrawable(
                context.getResources().getDrawable(R.drawable.ic_doc_text_alpha), accentColor));
    }

    private void convertNoteExpanded(BaseViewHolder holder, Note note) {
        holder.itemView.setBackgroundColor(PalmApp.getColorCompact(isDarkTheme ?
                R.color.dark_theme_background : R.color.light_theme_background));
        holder.setText(R.id.tv_note_title, note.getTitle());
        holder.setText(R.id.tv_content, note.getPreviewContent());
        holder.setText(R.id.tv_time, TimeUtils.getPrettyTime(note.getAddedTime()));
        holder.setTextColor(R.id.tv_time, accentColor);
        if (note.getPreviewImage() != null) {
            holder.getView(R.id.iv_image).setVisibility(View.VISIBLE);
            Uri thumbnailUri = FileHelper.getThumbnailUri(context, note.getPreviewImage());
            Glide.with(PalmApp.getContext())
                    .load(thumbnailUri)
                    .centerCrop()
                    .crossFade()
                    .into((ImageView) holder.getView(R.id.iv_image));
        } else {
            holder.getView(R.id.iv_image).setVisibility(View.GONE);
        }
    }

    private void convertNotebook(BaseViewHolder helper, Notebook notebook) {
        int nbColor = notebook.getColor();
        helper.setText(R.id.tv_note_title, notebook.getTitle());
        String str = context.getResources().getQuantityString(R.plurals.notes_number, notebook.getCount(), notebook.getCount());
        helper.setText(R.id.tv_added_time, str);
        helper.setImageDrawable(R.id.iv_icon, ColorUtils.tintDrawable(
                context.getResources().getDrawable(R.drawable.ic_folder_black_24dp), nbColor));
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        try {
            MultiItem multiItem = getItem(pos);
            if (multiItem.itemType == MultiItem.ITEM_TYPE_NOTE) {
                return String.valueOf(multiItem.note.getTitle().charAt(0));
            } else if (multiItem.itemType == MultiItem.ITEM_TYPE_NOTEBOOK) {
                return String.valueOf(multiItem.notebook.getTitle().charAt(0));
            }
        } catch (Exception e) {
            return "";
        }
        return "";
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
