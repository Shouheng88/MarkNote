package me.shouheng.commons.event;

/**
 * Created Wngshhng (shouheng2015@gmail.com) on 2018/12/6.
 */
public interface UMEvent {
    /* click : main menu drawer */
    String MAIN_MENU_ITEM_NOTEBOOKS = "main_menu_item_notebooks";
    String MAIN_MENU_ITEM_CATEGORIES = "main_menu_item_categories";
    String MAIN_MENU_ITEM_ARCHIVED = "main_menu_item_archived";
    String MAIN_MENU_ITEM_TRASHED = "main_menu_item_trashed";
    String MAIN_MENU_ITEM_SETTINGS = "main_menu_item_settings";
    String MAIN_MENU_ITEM_SUPPORT = "main_menu_item_support";
    String MAIN_MENU_ITEM_STATISTIC = "main_menu_item_statistics";
    String MAIN_MENU_ITEM_TIMELINE = "main_menu_item_timeline";
    String MAIN_MENU_ITEM_SHARE_APP = "main_menu_item_share_app";

    /* click : donate */
    String SUPPORT_DONATE_ALIPAY = "support_donate_alipay";
    String SUPPORT_DONATE_WECHAT = "support_donate_wechat";

    /* click : fab */
    String FAB_SORT_ITEM_NOTE = "fab_sort_item_note";
    String FAB_SORT_ITEM_NOTEBOOK = "fab_sort_item_notebook";
    String FAB_SORT_ITEM_CATEGORY = "fab_sort_item_category";
    String FAB_SORT_ITEM_IMAGE = "fab_sort_item_image";
    String FAB_SORT_ITEM_CAPTURE = "fab_sort_item_capture";
    String FAB_SORT_ITEM_DRAFT = "fab_sort_item_draft";
    String FAB_SORT_ITEM_QUICK_NOTE = "fab_sort_item_quick_note";

    /* intent : main */
    String INTENT_SHORTCUT_ACTION_SEARCH_NOTE = "intent_shortcut_action_search_note";
    String INTENT_SHORTCUT_ACTION_CAPTURE = "intent_shortcut_action_capture";
    String INTENT_SHORTCUT_ACTION_CREATE_NOTE = "intent_shortcut_action_create_note";
    String INTENT_SHORTCUT_ACTION_VIEW_NOTE = "intent_shortcut_action_view_note";
    String INTENT_ACTION_SEND = "intent_action_send";
    String INTENT_ACTION_VIEW = "intent_action_view";
    String INTENT_APP_WIDGET_ACTION_CREATE_NOTE = "intent_app_widget_action_create_note";
    String INTENT_APP_WIDGET_ACTION_LIST_ITEM_CLICKED = "intent_app_widget_action_list_item_clicked";
    String INTENT_APP_WIDGET_ACTION_LAUNCH_APP = "intent_app_widget_action_launch_app";
    String INTENT_APP_WIDGET_ACTION_CAPTURE = "intent_app_widget_action_capture";
    String INTENT_APP_WIDGET_ACTION_CREATE_SKETCH = "intent_app_widget_action_create_sketch";
    String INTENT_ACTION_RESTART_APP = "intent_action_restart_app";
}
