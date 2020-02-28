package me.shouheng.notepal.dialog;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.Glide;

import java.util.Objects;

import me.shouheng.commons.theme.ThemeUtils;
import me.shouheng.commons.theme.ThemeStyle;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.notepal.Constants;
import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.DialogThemePickBinding;

/**
 * @author shouh
 * @version $Id: ThemePickDialog, v 0.1 2018/9/19 21:07 shouh Exp$
 */
public class ThemePickDialog extends DialogFragment {

    private DialogThemePickBinding binding;
    private ThemeStyle themeStyle;

    public static ThemePickDialog newInstance() {
        return new ThemePickDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_theme_pick, null, false);

        themeStyle = ThemeUtils.getInstance().getThemeStyle();

        switchUIToTheme(themeStyle);

        binding.llLightBlue.setOnClickListener(v -> {
            this.themeStyle = ThemeStyle.LIGHT_BLUE_THEME;
            switchUIToTheme(ThemeStyle.LIGHT_BLUE_THEME);
        });
        binding.llLightRed.setOnClickListener(v -> {
            this.themeStyle = ThemeStyle.LIGHT_RED_THEME;
            switchUIToTheme(ThemeStyle.LIGHT_RED_THEME);
        });
        binding.llDarkBlue.setOnClickListener(v -> {
            this.themeStyle = ThemeStyle.DARK_THEME;
            switchUIToTheme(ThemeStyle.DARK_THEME);
        });

        return new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                .setView(binding.getRoot())
                .setPositiveButton(R.string.text_confirm, (dialog, which) -> {
                    switchToTheme(themeStyle);
                    dismiss();
                })
                .setNegativeButton(R.string.text_cancel, null)
                .create();
    }

    private void switchUIToTheme(ThemeStyle themeStyle) {
        binding.ivLightBlue.setVisibility(themeStyle == ThemeStyle.LIGHT_BLUE_THEME ? View.VISIBLE : View.GONE);
        binding.ivLightRed.setVisibility(themeStyle == ThemeStyle.LIGHT_RED_THEME ? View.VISIBLE : View.GONE);
        binding.ivDarkBlue.setVisibility(themeStyle == ThemeStyle.DARK_THEME ? View.VISIBLE : View.GONE);
        Glide.with(getActivity())
                .load(themeStyle.isDarkTheme ? Constants.IMAGE_HEADER_DARK : Constants.IMAGE_HEADER_LIGHT)
                .into(binding.ivBg);
    }

    private void switchToTheme(ThemeStyle themeStyle) {
        ThemeUtils.getInstance().setThemeStyle(themeStyle);
        ColorUtils.updateTheme();
        if (getActivity() != null) {
            getActivity().recreate();
        }
    }
}
