package me.shouheng.commons.event;

/**
 * @author shouh
 * @version $Id: RxMessage, v 0.1 2018/11/17 17:14 shouh Exp$
 */
public class RxMessage {

    /**
     * Float action buttons order has changed.
     */
    public final static int CODE_SORT_FLOAT_BUTTONS = 0;

    public final static int CODE_NOTE_LIST_STYLE_CHANGED = 2;

    /**
     * NOTE AND NOTEBOOK data has changed.
     */
    public final static int CODE_NOTE_DATA_CHANGED = 3;

    /**
     * Category data change event.
     */
    public final static int CODE_CATEGORY_DATA_CHANGED = 4;

    /**
     * Code identify that the password check has been passed.
     */
    public final static int CODE_PASSWORD_CHECK_PASSED = 5;

    /**
     * Code identify that the password check has been failed.
     */
    public final static int CODE_PASSWORD_CHECK_FAILED = 6;

    /**
     * The new password has benn set.
     */
    public final static int CODE_PASSWORD_SET_SUCCEED = 7;

    public final static int CODE_PASSWORD_SET_FAILED = 8;

    public final int code;

    public final Object object;

    public RxMessage(int code, Object object) {
        this.code = code;
        this.object = object;
    }
}
