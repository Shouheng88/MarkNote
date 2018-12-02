package me.shouheng.notepal;

import me.shouheng.commons.BaseConstants;

public interface Constants extends BaseConstants {

    /**
     * Shortcut action, used to create new note, registered in the shortcuts.xml.
     * Used fot app version after {@link android.os.Build.VERSION_CODES#N_MR1}
     */
    String SHORTCUT_ACTION_CREATE_NOTE = "me.shouheng.notepal.CREATE_NOTE";

    /**
     * Shortcut action, used to search note, registered in the shortcuts.xml.
     * Used fot app version after {@link android.os.Build.VERSION_CODES#N_MR1}
     */
    String SHORTCUT_ACTION_SEARCH_NOTE = "me.shouheng.notepal.SEARCH_NOTE";

    /**
     * Shortcut action, used to capture a photo fot note, registered in the shortcuts.xml.
     * Used fot app version after {@link android.os.Build.VERSION_CODES#N_MR1}
     */
    String SHORTCUT_ACTION_CAPTURE = "me.shouheng.notepal.CAPTURE";

    /**
     * Shortcut action, used to start a note view page, the intent contains information of the note.
     * @see #SHORTCUT_EXTRA_NOTE_CODE
     */
    String SHORTCUT_ACTION_VIEW_NOTE = "me.shouheng.notepal.VIEW_NOTE";

    /**
     * The intent extra key for {@link #SHORTCUT_ACTION_VIEW_NOTE} used to send the note code
     * to the MainActivity. The code will later be used to get the full note information.
     */
    String SHORTCUT_EXTRA_NOTE_CODE = "me.shouheng.notepal.intent.extras.NOTE_CODE";

    /**
     * Shortcut action, used to create quick note, registered in the shortcuts.xml
     */
    String SHORTCUT_ACTION_QUICK_NOTE = "me.shouheng.notepal.QUICK_NOTE";


    /**
     * The action for app widget, used to create a new note.
     */
    String APP_WIDGET_ACTION_CREATE_NOTE = "me.shouheng.notepal.widget.CREATE";

    /**
     * The action for app widget, used to take a photo, and then create a note.
     */
    String APP_WIDGET_ACTION_CAPTURE = "me.shouheng.notepal.widget.CAPTURE";

    /**
     * The action for app widget, used to quickly create a note.
     */
    String APP_WIDGET_ACTION_QUICK_NOTE = "me.shouheng.notepal.widget.QUICK_NOTE";

    /**
     * The action for app widget, used to launch the app.
     */
    String APP_WIDGET_ACTION_LAUNCH_APP = "me.shouheng.notepal.widget.LAUNCH";

    /**
     * Default note file encoding.
     */
    String NOTE_FILE_ENCODING = "utf-8";


    String EXTRA_MODEL = "extra_model";
    String EXTRA_CODE = "extra_code";
    String EXTRA_POSITION = "extra_position";
    String EXTRA_REQUEST_CODE = "extra_request_code";
    String ACTION_TO_NOTE_FROM_THIRD_PART = "to_note_from_third_part";


    // region REGION: Constant fields for URI

    /**
     * Schemas for Http and Https, used to detect the url type.
     */
    String URI_SCHEME_HTTPS = "https";
    String URI_SCHEME_HTTP = "http";

    String MIME_TYPE_OF_VIDEO = "video/*";
    String MIME_TYPE_OF_PDF = "application/pdf";
    String MIME_TYPE_OF_PLAIN_TEXT = "text/plain";
    String MIME_TYPE_OF_IMAGE = "image/*";
    String MIME_TYPE_OF_AUDIO = "audio/*";
    String MIME_TYPE_OTHERS = "*/*";

    String EXTENSION_3GP = ".3gp";
    String EXTENSION_MP4 = ".mp4";
    String EXTENSION_PDF = ".pdf";

    // endregion REGION: Fields for URI


    // region Action
    String ACTION_SHORTCUT = "ACTION_SHORTCUT";

    String ACTION_RESTART_APP = "action_restart_app";

    String ACTION_NOTE_CHANGE_BROADCAST = "action_broadcast_notes_changed";
    // endregion

    String GITHUB_PAGE = "https://github.com/Shouheng88/NotePal";
    String GOOGLE_PLUS_URL = "https://plus.google.com/u/1/communities/102252970668657211916";
    String WEIBO_PAGE = "https://weibo.com/5401152113/profile?rightmod=1&wvr=6&mod=personinfo";
    String TWITTER_PAGE = "https://twitter.com/ShouhengWang";
    String AVATAR_WNGSHHNG = "https://github.com/Shouheng88/MarkNote/blob/master/resources/images/avatar_wngshhng.jpg?raw=true";
    String USER_WNGSHHNG = "https://github.com/Shouheng88";
    String GUIDE_PAGE = "https://github.com/Shouheng88/MarkNote/blob/master/resources/%E7%94%A8%E6%88%B7%E6%89%8B%E5%86%8C.md";
    String PRIVACY_PAGE = "https://github.com/Shouheng88/MarkNote/blob/master/resources/%E9%9A%90%E7%A7%81%E6%94%BF%E7%AD%96.md";
    String FEEDBACK_CHINESE = "http://fnlefu0fqyo8miz7.mikecrm.com/IR50hog";
    String FEEDBACK_ENGLISH = "http://fnlefu0fqyo8miz7.mikecrm.com/nwGEX3r";
    String DEVELOPER_EMAIL = "shouheng2015@gmail.com";

    // region App Widgets
    String INTENT_WIDGET = "widget_id";
    String ACTION_WIDGET_LIST = "action_widget_list";
    String ACTION_TAKE_PHOTO = "action_widget_take_photo";
    String ACTION_ADD_SKETCH = "action_widget_add_sketch";
    String ACTION_ADD_FILES = "action_widget_add_files";
    String ACTION_ADD_NOTE = "action_widget_add_note";
    String ACTION_ADD_MIND = "action_widget_add_mind";
    String ACTION_CONFIG = "action_widget_config";
    String EXTRA_CONFIG_SWITCH_ENABLE = "extra_widget_switch_enable";
    String ACTION_WIDGET_LAUNCH_APP = "action_widget_launch_app";

    String PREFS_NAME = BuildConfig.APPLICATION_ID + "_preferences";
    String PREF_WIDGET_SQL_PREFIX = "widget_sql_";
    String PREF_WIDGET_TYPE_PREFIX = "widget_type_";
    String PREF_WIDGET_NOTEBOOK_CODE_PREFIX = "widget_notebook_code";
    // endregion

    String REA_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQD0diKVSZ/U/KHuxZFYac3lLq7K\n" +
            "edqc+uOKSJgq26tgy4wmELCw8gJkempBm8NPf+uSOdWPlPLWijSf3W2KfzMMvZQ2\n" +
            "tfNQPQu+gXgdXuZC+fhqVqNgYtWVRMIspveSm3AK+52AxxzTlfAU1fpCEFOf4AHc\n" +
            "/E33toB493pf9gS2xwIDAQAB";

    // region regex expression

    /**
     * The note title regex expression, used to get the note title from the note content.
     */
    String REGEX_NOTE_TITLE = "^(#+)(.*)";

    /**
     * The note image regex expression, used to get the note image from the note content.
     */
    String REGEX_NOTE_PREVIEW_IMAGE = "!\\[.*]\\(.+.\\)";

    // endregion regex expression




    String HTML_EXPORT_DIR_NAME = "ExportedHtml";
    String TEXT_EXPORT_DIR_NAME = "ExportedText";
    String EXPORTED_TEXT_EXTENSION = ".txt";
    String EXPORTED_HTML_EXTENSION = ".html";

    String BACKUP_DIR_NAME = "NotePal";
    String FILES_BACKUP_DIR_NAME = "files";
}
