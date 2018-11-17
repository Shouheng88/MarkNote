package me.shouheng.dailykeep.view.dialog;

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

import me.shouheng.commons.colorful.ThemeStyle;
import me.shouheng.dailykeep.R;
import me.shouheng.dailykeep.databinding.DialogThemePickBinding;
import me.shouheng.data.preference.PrefUtils;
import me.shouheng.data.preference.UserUtils;
import me.shouheng.data.preference.model.User;

/**
 * @author shouh
 * @version $Id: ThemePickDialog, v 0.1 2018/9/19 21:07 shouh Exp$
 */
public class ThemePickDialog extends DialogFragment {

    private DialogThemePickBinding binding;
    private ThemeStyle themeStyle;

    public static ThemePickDialog newInstance() {
        Bundle args = new Bundle();
        ThemePickDialog fragment = new ThemePickDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_theme_pick, null, false);

        User user = UserUtils.getInstance().getUser();
        themeStyle = PrefUtils.getInstance().getThemeStyle();

        Glide.with(Objects.requireNonNull(getContext())).load(user.getAvatar()).into(binding.ivBg);

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
    }

    private void switchToTheme(ThemeStyle themeStyle) {
        PrefUtils.getInstance().setThemeStyle(themeStyle);
        if (getActivity() != null) {
            getActivity().recreate();
        }
    }
}
