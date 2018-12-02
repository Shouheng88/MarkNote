package me.shouheng.commons.event;

/**
 * @author shouh
 * @version $Id: RxMessage, v 0.1 2018/11/17 17:14 shouh Exp$
 */
public class RxMessage {

    public final static int CODE_SORT_FLOAT_BUTTONS = 0;
    public final static int CODE_SORT_EDIT_MENU = 1;
    public final static int CODE_NOTE_LIST_STYLE_CHANGED = 2;

    /**
     * The code to identify the NOTE AND NOTEBOOK data change event.
     */
    public final static int CODE_NOTE_DATA_CHANGED = 3;

    public final static int CODE_CATEGORY_DATA_CHANGED = 4;

    public final int code;

    public final Object object;

    public RxMessage(int code, Object object) {
        this.code = code;
        this.object = object;
    }
}
