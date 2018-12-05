package me.shouheng.notepal.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.notepal.R;
import me.shouheng.data.model.enums.FabSortItem;
import me.shouheng.commons.widget.CircleImageView;


/**
 * Created by wangshouheng on 2017/3/12.
 */
public class FabSortAdapter extends RecyclerView.Adapter<FabSortAdapter.ViewHolder>{

    private List<FabSortItem> fabSortItems;

    private int accentColor;

    private boolean isDarkTheme;

    public FabSortAdapter(Context context, List<FabSortItem> fabSortItems) {
        this.fabSortItems = fabSortItems;
        accentColor = ColorUtils.accentColor();
        isDarkTheme = ColorUtils.isDarkTheme();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_fab_drag_sort, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FabSortItem fabSortItem = fabSortItems.get(position);
        holder.ivFabIcon.setImageDrawable(ColorUtils.tintDrawable(fabSortItem.iconRes, Color.WHITE));
        holder.tvFabName.setText(fabSortItem.nameRes);
        holder.civFabIconBG.setFillingCircleColor(accentColor);
        holder.ivHandler.setImageResource(isDarkTheme ? R.drawable.ic_menu_white : R.drawable.ic_menu_black);
    }

    @Override
    public int getItemCount() {
        if (null != fabSortItems){
            return fabSortItems.size();
        }
        return 0;
    }

    public List<FabSortItem> getFabSortItems() {
        return fabSortItems;
    }

    public void setFabSortItems(List<FabSortItem> fabSortItems) {
        this.fabSortItems = fabSortItems;
    }

    public FabSortItem getFabSortItemAt(int position) {
        return fabSortItems.get(position);
    }

    public void addFabSortItemTo(int position, FabSortItem fabSortItem) {
        fabSortItems.add(position, fabSortItem);
    }

    public void removeFabSortItemAt(int position) {
        fabSortItems.remove(position);
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
