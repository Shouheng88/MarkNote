package me.shouheng.notepal.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.notepal.R;
import me.shouheng.notepal.widget.CircleImageView;
import my.shouheng.palmmarkdown.tools.MarkdownFormat;

public class MenuSortAdapter extends RecyclerView.Adapter<MenuSortAdapter.ViewHolder> {

    private List<MarkdownFormat> markdownFormats;

    private int accentColor;

    private boolean isDarkTheme;

    private Context context;

    public MenuSortAdapter(Context context, List<MarkdownFormat> markdownFormats) {
        this.markdownFormats = markdownFormats;
        this.context = context;
        accentColor = ColorUtils.accentColor(context);
        isDarkTheme = ColorUtils.isDarkTheme(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fab_drag_sort, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuSortAdapter.ViewHolder holder, int position) {
        MarkdownFormat markdownFormat = markdownFormats.get(position);
        holder.ivFabIcon.setImageDrawable(ColorUtils.tintDrawable(context.getResources().getDrawable(markdownFormat.drawableResId), Color.WHITE));
        holder.tvFabName.setText(markdownFormat.name());
        holder.civFabIconBG.setFillingCircleColor(accentColor);
        holder.ivHandler.setImageResource(isDarkTheme ? R.drawable.ic_menu_white : R.drawable.ic_menu_black);
    }

    @Override
    public int getItemCount() {
        if (null != markdownFormats){
            return markdownFormats.size();
        }
        return 0;
    }

    public List<MarkdownFormat> getMarkdownFormats() {
        return markdownFormats;
    }

    public void setMarkdownFormats(List<MarkdownFormat> markdownFormats) {
        this.markdownFormats = markdownFormats;
    }

    public MarkdownFormat getMarkdownFormatAt(int position) {
        return markdownFormats.get(position);
    }

    public void addMarkdownFormatTo(int position, MarkdownFormat markdownFormat) {
        markdownFormats.add(position, markdownFormat);
    }

    public void removeMarkdownFormatAt(int position) {
        markdownFormats.remove(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        ImageView ivFabIcon;
        TextView tvFabName;
        CircleImageView civFabIconBG;
        ImageView ivHandler;

        ViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            this.ivFabIcon = itemView.findViewById(R.id.iv_fab_icon);
            this.tvFabName = itemView.findViewById(R.id.tv_fab_name);
            this.civFabIconBG = itemView.findViewById(R.id.civ_fab_icon_background);
            this.ivHandler = itemView.findViewById(R.id.iv_drag_handler);
        }
    }
}
