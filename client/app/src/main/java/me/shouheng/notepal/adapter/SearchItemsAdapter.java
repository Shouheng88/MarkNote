package me.shouheng.notepal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.notepal.R;
import me.shouheng.data.entity.Note;
import me.shouheng.commons.utils.TimeUtils;

/**
 * Created by wangshouheng on 2017/5/8.
 */
public class SearchItemsAdapter extends RecyclerView.Adapter<SearchItemsAdapter.ViewHolder>{

    private List searchResults = new LinkedList();

    private Context context;

    private boolean isDarkTheme;
    private int accentColor, primaryColor;

    private final int NOTE = 2, STRING = 5;

    private OnItemSelectedListener onItemSelectedListener;

    public SearchItemsAdapter(Context context, OnItemSelectedListener onItemSelectedListener) {
        this.context = context;

        this.isDarkTheme = ColorUtils.isDarkTheme(context);
        this.accentColor = ColorUtils.accentColor(context);
        this.primaryColor = ColorUtils.primaryColor(context);
        this.onItemSelectedListener = onItemSelectedListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_note, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (isDarkTheme) holder.itemView.setBackgroundResource(R.color.dark_theme_foreground);

        if (viewType == NOTE) {
            onBindNote(holder, position);
        }
    }

    private void onBindNote(ViewHolder holder, final int position) {
        Note note = (Note) searchResults.get(position);
        holder.tvNoteTitle.setText(note.getTitle());
        holder.tvAddedTime.setText(TimeUtils.getLongDateTime(context, note.getAddedTime()));
        holder.ivIcon.setImageDrawable(ColorUtils.tintDrawable(context.getResources().getDrawable(R.drawable.ic_doc_text_alpha), accentColor));
    }

    @Override
    public int getItemCount() {
        if (null != searchResults){
            return searchResults.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (searchResults.get(position) instanceof Note) {
            return NOTE;
        }
        return STRING;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View itemView;

        TextView tvSubTitle;

        TextView tvNoteTitle;
        ImageView ivIcon;
        ImageView ivMore;

        TextView tvTitle;

        TextView tvAddedTime;

        ViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;

            ivMore = itemView.findViewById(R.id.iv_more);
            if (ivMore != null) ivMore.setVisibility(View.GONE);

            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSubTitle = itemView.findViewById(R.id.tv_sub_title);
            ivIcon = itemView.findViewById(R.id.iv_icon);

            tvNoteTitle = itemView.findViewById(R.id.tv_note_title);
            tvAddedTime = itemView.findViewById(R.id.tv_added_time);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            switch (getItemViewType()) {
                case NOTE:
                    if (onItemSelectedListener != null) {
                        onItemSelectedListener.onNoteSelected((Note) searchResults.get(position), position);
                    }
                    break;
            }
        }
    }

    public void updateSearchResults(List searchResults) {
        this.searchResults = searchResults;
    }

    public interface OnItemSelectedListener {
        void onNoteSelected(Note note, int position);
    }
}
