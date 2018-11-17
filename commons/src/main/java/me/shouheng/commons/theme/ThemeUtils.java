package me.shouheng.commons.tools.preferences;

import me.shouheng.commons.R;
import me.shouheng.commons.colorful.ThemeStyle;

/**
 * @author shouh
 * @version $Id: BasePrefUtils, v 0.1 2018/9/4 7:42 shouh Exp$
 */
public class BasePrefUtils extends BasePref {

    private volatile static BasePrefUtils sInstance;

    protected BasePrefUtils() {
        super();
    }

    public static BasePrefUtils getInstance() {
        if (sInstance == null) {
            synchronized (BasePrefUtils.class) {
                if (sInstance == null){
                    sInstance = new BasePrefUtils();
                }
            }
        }
        return sInstance;
    }

    public ThemeStyle getThemeStyle() {
        return ThemeStyle.getThemeStyleById(getInt(R.string.key_theme_style_id, ThemeStyle.LIGHT_BLUE_THEME.id));
    }

    public void setThemeStyle(ThemeStyle themeStyle) {
        putInt(R.string.key_theme_style_id, themeStyle.id);
    }
}
