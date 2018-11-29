package me.shouheng.commons.widget.theme;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;

import me.shouheng.commons.R;
import me.shouheng.commons.theme.ThemeUtils;

/**
 * @author shouh
 * @version $Id: SupportAppBarLayout, v 0.1 2018/9/1 12:15 shouh Exp$
 */
@CoordinatorLayout.DefaultBehavior(AppBarLayout.Behavior.class)
public class SupportAppBarLayout extends AppBarLayout {

    public SupportAppBarLayout(Context context) {
        this(context, null);
    }

    public SupportAppBarLayout(Context context, AttributeSet attrs) {
        super(new ContextThemeWrapper(context,
                ThemeUtils.getInstance().getThemeStyle().isDarkTheme ?
                        R.style.AppTheme_AppBarOverlayDark : R.style.AppTheme_AppBarOverlay), attrs);
    }
}
