package me.shouheng.notepal;

import me.shouheng.commons.BaseConstants;

public interface Constants extends BaseConstants {

    /**
     * Action: shortcut action, used to create new note, registered in the shortcuts.xml.
     * Used fot app version after {@link android.os.Build.VERSION_CODES#N_MR1}
     */
    String SHORTCUT_ACTION_CREATE_NOTE = "me.shouheng.notepal.CREATE_NOTE";

    /**
     * Action: shortcut action, used to search note, registered in the shortcuts.xml.
     * Used fot app version after {@link android.os.Build.VERSION_CODES#N_MR1}
     */
    String SHORTCUT_ACTION_SEARCH_NOTE = "me.shouheng.notepal.SEARCH_NOTE";

    /**
     * Action: shortcut action, used to capture a photo fot note, registered in the shortcuts.xml.
     * Used fot app version after {@link android.os.Build.VERSION_CODES#N_MR1}
     */
    String SHORTCUT_ACTION_CAPTURE = "me.shouheng.notepal.CAPTURE";

    /**
     * Action: shortcut action, used to start a note view page, the intent contains information of the note.
     * @see #SHORTCUT_EXTRA_NOTE_CODE
     */
    String SHORTCUT_ACTION_VIEW_NOTE = "me.shouheng.notepal.VIEW_NOTE";

    /**
     * Action: shortcut action, used to create quick note, registered in the shortcuts.xml
     */
    String SHORTCUT_ACTION_QUICK_NOTE = "me.shouheng.notepal.QUICK_NOTE";

    /**
     * Extra: the intent extra key for {@link #SHORTCUT_ACTION_VIEW_NOTE} used to send the note code
     * to the MainActivity. The code will later be used to get the full note information.
     */
    String SHORTCUT_EXTRA_NOTE_CODE = "me.shouheng.notepal.intent.extras.NOTE_CODE";


    /**
     * Action: action for app widget, used to create a new note.
     */
    String APP_WIDGET_ACTION_CREATE_NOTE = "me.shouheng.notepal.widget.CREATE";

    /**
     * Action: action for app widget, used to take a photo, and then create a note.
     */
    String APP_WIDGET_ACTION_CAPTURE = "me.shouheng.notepal.widget.CAPTURE";

    /**
     * Action: action for app widget, used to quickly create a note.
     */
    String APP_WIDGET_ACTION_QUICK_NOTE = "me.shouheng.notepal.widget.QUICK_NOTE";

    /**
     * Action: action for app widget, used to launch the app.
     */
    String APP_WIDGET_ACTION_LAUNCH_APP = "me.shouheng.notepal.widget.LAUNCH";

    /**
     * Action: action for app widget to create sketch.
     */
    String APP_WIDGET_ACTION_CREATE_SKETCH = "me.shouheng.notepal.widget.CREATE_SKETCH";

    /**
     * Action: action for app widget to config the list widget.
     */
    String APP_WIDGET_ACTION_CONFIG_LIST = "me.shouheng.notepal.widget.CONFIG_LIST";

    /**
     * Action: action for app widget to identify the list item click event.
     */
    String APP_WIDGET_ACTION_LIST_ITEM_CLICLED = "me.shouheng.notepal.widget.LIST_ITEM_CLICKED";

    /**
     * Extra: extra key for app widget, the app widget id.
     */
    String APP_WIDGET_EXTRA_WIDGET_ID = "me.shouheng.notepal.widget.WIDGET_ID";

    /**
     * Extra: extra key for app widget, the note in parcel.
     */
    String APP_WIDGET_EXTRA_NOTE = "me.shouheng.notepal.widget.NOTE";

    /**
     * Extra: extra key to identify is switching notebook allowed in the config activity.
     */
    String APP_WIDGET_EXTRA_ALLOW_SWITCH_NOTEBOOK = "me.shouheng.notepal.widget.ALLOW_SWITCH_NOTEBOOK";

    /**
     * App widget preferences file name.
     */
    String APP_WIDGET_PREFERENCES_NAME = BuildConfig.APPLICATION_ID + "_preferences";

    /**
     * Preference key prefix to get the notebook code of given widget.
     * The final preference key is : APP_WIDGET_PREFERENCE_KEY_NOTEBOOK_CODE_PREFIX + widgetId
     */
    String APP_WIDGET_PREFERENCE_KEY_NOTEBOOK_CODE_PREFIX = "me.shouheng.notepal.widget.NOTE_BOOK_CODE_";

    /**
     * Preference key prefix to get the sql of list of given widget.
     * The final preference key is : APP_WIDGET_PREFERENCE_KEY_SQL_PREFIX + widgetId
     */
    String APP_WIDGET_PREFERENCE_KEY_SQL_PREFIX = "me.shouheng.notepal.widget.WIDGET_SQL_";


    /**
     * Action from fab to capture and create note.
     */
    String FAB_ACTION_CAPTURE = "me.shouheng.notepal.fab.CAPTURE";

    /**
     * Action from fab to pick images from album and create note.
     */
    String FAB_ACTION_PICK_IMAGE = "me.shouheng.notepal.fab.PICK_FROM_ALBUM";

    /**
     * Actiuon from fab to create a sketch and create note.
     */
    String FAB_ACTION_CREATE_SKETCH = "me.shouheng.notepal.fab.CREATE_SKETCH";


    String ACTION_RESTART_APP = "me.shouheng.notepal.RESTART";


    /**
     * Default note file encoding.
     */
    String NOTE_FILE_ENCODING = "UTF-8";

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


    /* Web: Weibo */
    String PAGE_WEIBO = "https://weibo.com/5401152113/profile?rightmod=1&wvr=6&mod=personinfo";
    /* Web: Twitter */
    String PAGE_TWITTER = "https://twitter.com/shouheng_wang";
    /* Web: Github repository page */
    String PAGE_GITHUB_REPOSITORY = "https://github.com/Shouheng88/NotePal";
    /* Web: Github developer page  */
    String PAGE_GITHUB_DEVELOPER = "https://github.com/Shouheng88";
    /* Web: User Guide */
    String PAGE_GUIDE = "https://github.com/Shouheng88/MarkNote/blob/master/resources/%E7%94%A8%E6%88%B7%E6%89%8B%E5%86%8C.md";
    /* Web: Privacy */
    String PAGE_PRIVACY = "https://github.com/Shouheng88/MarkNote/blob/master/resources/%E9%9A%90%E7%A7%81%E6%94%BF%E7%AD%96.md";
    /* Web: Change logs */
    String PAGE_CHANGE_LOGS = "https://github.com/Shouheng88/MarkNote/blob/master/resources/%E6%9B%B4%E6%96%B0%E6%97%A5%E5%BF%97.md";
    /* Web: Update plan */
    String PAGE_UPDATE_PLAN = "https://github.com/Shouheng88/MarkNote/blob/master/resources/%E6%9B%B4%E6%96%B0%E8%AE%A1%E5%88%92.md";
    /* Web: Translate */
    String PAGE_TRANSLATE = "https://github.com/Shouheng88/MarkNote/blob/master/resources/%E5%8D%8F%E5%8A%A9%E7%BF%BB%E8%AF%91.md";
    /* Web: Online about */
    String PAGE_ABOUT = "https://github.com/Shouheng88/MarkNote/blob/master/resources/%E5%85%B3%E4%BA%8E%E5%BA%94%E7%94%A8.md";
    /* Web: Feedback Chinese */
    String PAGE_FEEDBACK_CHINESE = "http://fnlefu0fqyo8miz7.mikecrm.com/IR50hog";
    /* Web: Feedback English */
    String PAGE_FEEDBACK_ENGLISH = "http://fnlefu0fqyo8miz7.mikecrm.com/nwGEX3r";
    /* Email: developer */
    String EMAIL_DEVELOPER = "shouheng2015@gmail.com";
    /* Image: developer avatar */
    String IMAGE_AVATAR_DEVELOPER = "https://raw.githubusercontent.com/Shouheng88/MarkNote/master/resources/images/avatar_wngshhng.jpg";
    /* Image: light theme header */
    String IMAGE_HEADER_LIGHT = "https://raw.githubusercontent.com/Shouheng88/MarkNote/master/resources/images/bg1.jpg";
    /* Image: dark theme header */
    String IMAGE_HEADER_DARK = "https://raw.githubusercontent.com/Shouheng88/MarkNote/master/resources/images/bg2.jpg";
    /* Web: Google play download page */
    String GOOGLE_PLAY_WEB_PAGE = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
    /* Web: CoolApk download page */
    String COOL_APK_DOWNLOAD_PAGE = "https://www.coolapk.com/apk/178276";

    /**
     * The note title regex expression, used to get the note title from the note content.
     */
    String REGEX_NOTE_TITLE = "^(#+)(.*)";

    /**
     * The note image regex expression, used to get the note image from the note content.
     */
    String REGEX_NOTE_PREVIEW_IMAGE = "!\\[.*]\\(.+.\\)";


    String HTML_EXPORT_DIR_NAME = "ExportedHtml";
    String TEXT_EXPORT_DIR_NAME = "ExportedText";
    String EXPORTED_TEXT_EXTENSION = ".txt";
    String EXPORTED_HTML_EXTENSION = ".html";

    String BACKUP_DIR_NAME = "NotePal";
    String FILES_BACKUP_DIR_NAME = "files";

    String SHARE_IMAGE_FILE_PATH = "Share";
    String SHARE_IMAGE_NAME_1 = "MN2";
    String SHARE_IMAGE_ASSETS_NAME_1 = "share.jpg";

    long AGAIN_EXIT_TIME_INTERVAL = 2000;
}
