package me.shouheng.notepal.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.polaric.colorful.Colorful;

import java.util.Arrays;
import java.util.List;

import me.shouheng.notepal.R;
import me.shouheng.notepal.listener.OnThemeSelectedListener;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.widget.tools.DividerItemDecoration;

/**
 * Created by wangshouheng on 2017/8/6. */
public class ThemesListAdapter extends RecyclerView.Adapter<ThemesListAdapter.ViewHolder>{

    private String title1, title2;
    private List<Colorful.ThemeColor> themes1, themes2;

    private Context context;
    private boolean isDarkTheme;
    private OnThemeSelectedListener onThemeSelectedListener;

    private ThemesAdapter adapter1, adapter2;

    public ThemesListAdapter(Context context, OnThemeSelectedListener onThemeSelectedListener) {
        this.context = context;
        this.onThemeSelectedListener = onThemeSelectedListener;

        List<Colorful.ThemeColor> themeColors = Arrays.asList(Colorful.ThemeColor.values());
        int count = themeColors.size();
        this.title1 = context.getString(R.string.my_youth);
        this.title2 = context.getString(R.string.noble);
        this.themes1 = themeColors.subList(0, 13);
        this.themes2 = themeColors.subList(13, count);
        this.isDarkTheme = ColorUtils.isDarkTheme(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_themes_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0) {
            holder.tvListTitle.setText(title1);
            adapter1 = new ThemesAdapter(context, themes1, ColorUtils.getThemeColor());
            holder.rvThemes.setAdapter(adapter1);
            adapter1.setOnItemClickListener((adapter, view, position1) -> {
                if (onThemeSelectedListener != null) {
                    onThemeSelectedListener.onThemeSelected(adapter1.getItem(position1));
                }
            });
        } else if (position == 1) {
            holder.tvListTitle.setText(title2);
            adapter2 = new ThemesAdapter(context, themes2, ColorUtils.getThemeColor());
            holder.rvThemes.setAdapter(adapter2);
            adapter2.setOnItemClickListener((adapter, view, position1) -> {
                if (onThemeSelectedListener != null) {
                    onThemeSelectedListener.onThemeSelected(adapter2.getItem(position1));
                }
            });
        }
        holder.cardView.setBackgroundResource(isDarkTheme ? R.color.dark_theme_foreground : R.color.light_theme_background);
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public void setSelectionChanged(Colorful.ThemeColor themeColor) {
        if (adapter1 != null) {
            adapter1.setSelectedTheme(themeColor);
            adapter1.notifyDataSetChanged();
        }
        if (adapter2 != null) {
            adapter2.setSelectedTheme(themeColor);
            adapter2.notifyDataSetChanged();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvListTitle;
        RecyclerView rvThemes;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            tvListTitle = itemView.findViewById(R.id.list_tile);
            rvThemes = itemView.findViewById(R.id.rv_themes);
            cardView = itemView.findViewById(R.id.cardView);

            rvThemes.setLayoutManager(new LinearLayoutManager(context));
            rvThemes.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST, isDarkTheme));
        }
    }
}
