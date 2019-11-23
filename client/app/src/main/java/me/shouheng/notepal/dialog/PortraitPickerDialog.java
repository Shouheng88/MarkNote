package me.shouheng.notepal.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Objects;

import me.shouheng.data.model.enums.Portrait;
import me.shouheng.notepal.R;
import me.shouheng.uix.image.CircleImageView;
import me.shouheng.uix.rv.decor.SpaceItemDecoration;
import me.shouheng.utils.ui.ViewUtils;

/**
 * Created by wangshouheng on 2017/4/3.
 */
@SuppressLint("ValidFragment")
public class PortraitPickerDialog extends DialogFragment {

    private final Portrait[] portraits = Portrait.values();

    private OnPortraitSelectedListener onPortraitSelectedListener;

    private int selectedColor;

    public static PortraitPickerDialog newInstance(int selectedColor, OnPortraitSelectedListener listener) {
        Bundle args = new Bundle();
        PortraitPickerDialog fragment = new PortraitPickerDialog(selectedColor, listener);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("ValidFragment")
    private PortraitPickerDialog(int selectedColor, OnPortraitSelectedListener listener) {
        this.onPortraitSelectedListener = listener;
        this.selectedColor = selectedColor;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_portrait_seletor_layout, null);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 5));
        PortraitAdapter adapter = new PortraitAdapter();
        recyclerView.setAdapter(adapter);
        int padding = ViewUtils.dp2px(2);
        recyclerView.addItemDecoration(new SpaceItemDecoration(padding, padding, padding, padding));

        return new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                .setTitle(R.string.portrait_picker_title)
                .setNegativeButton(R.string.text_cancel, null)
                .setView(rootView)
                .create();
    }

    public class PortraitAdapter extends RecyclerView.Adapter<PortraitAdapter.PortraitViewHolder> {

        @Override
        @NonNull
        public PortraitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PortraitViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_portrait, parent, false));
        }

        @Override
        public void onBindViewHolder(PortraitViewHolder holder, int position) {
            Portrait portrait = portraits[position];
            holder.ivIcon.setImageResource(portrait.iconRes);
            holder.civ.setFillingCircleColor(selectedColor);
        }

        @Override
        public int getItemCount() {
            return portraits.length;
        }

        class PortraitViewHolder extends RecyclerView.ViewHolder {
            ImageView ivIcon;
            CircleImageView civ;

            PortraitViewHolder(View itemView) {
                super(itemView);

                this.ivIcon = itemView.findViewById(R.id.iv_portrait);
                this.civ = itemView.findViewById(R.id.civ);

                this.itemView.setOnClickListener(v -> {
                    if (onPortraitSelectedListener != null) {
                        Portrait portrait = portraits[getAdapterPosition()];
                        onPortraitSelectedListener.onPortraitSelected(portrait.id, portrait.iconRes);
                        dismiss();
                    }
                });
            }
        }
    }

    public interface OnPortraitSelectedListener{
        void onPortraitSelected(int portraitId, int portraitRes);
    }
}
