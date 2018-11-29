package me.shouheng.commons.activity.interaction;

import android.view.KeyEvent;

/**
 * @author shouh
 * @version $Id: FragmentKeyDown, v 0.1 2018/11/17 20:50 shouh Exp$
 */
public interface FragmentKeyDown {

    /**
     * The interaction method when the fragment key down, used to interact with webview fragment
     *
     * @param keyCode the key code
     * @param event the key event
     * @return is event handled
     */
    boolean onFragmentKeyDown(int keyCode, KeyEvent event);
}
