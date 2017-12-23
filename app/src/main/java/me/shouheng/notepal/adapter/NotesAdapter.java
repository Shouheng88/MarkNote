package me.shouheng.notepal.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import me.shouheng.notepal.R;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.model.Notebook;

/**
 * Created by wang shouheng on 2017/12/23.*/
public class NotesAdapter extends BaseMultiItemQuickAdapter<NotesAdapter.MultiItem, BaseViewHolder> {

    public NotesAdapter(Context context, List data) {
        super(data);
        addItemType(MultiItem.ITEM_TYPE_NOTE, R.layout.item_note);
        addItemType(MultiItem.ITEM_TYPE_NOTEBOOK, R.layout.item_note);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItem item) {
        switch (helper.getItemViewType()) {
            case MultiItem.ITEM_TYPE_NOTE:
                convertNote(helper, item.note);
                break;
            case MultiItem.ITEM_TYPE_NOTEBOOK:
                convertNotebook(helper, item.notebook);
                break;
        }
    }

    private void convertNote(BaseViewHolder helper, Note note) {}

    private void convertNotebook(BaseViewHolder helper, Notebook notebookm) {}

    public static class MultiItem implements MultiItemEntity {

        static final int ITEM_TYPE_NOTE = 0;

        static final int ITEM_TYPE_NOTEBOOK = 1;

        int itemType;

        Note note;

        Notebook notebook;

        @Override
        public int getItemType() {
            return itemType;
        }
    }
}
