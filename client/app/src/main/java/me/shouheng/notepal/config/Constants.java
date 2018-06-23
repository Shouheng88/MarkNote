package me.shouheng.notepal.config;

import me.shouheng.notepal.BuildConfig;

public class Constants {
    public static final String DEFAULT_LOG_TAG = "NotePal";

    // region Extras
    public final static String EXTRA_MODEL = "extra_model";
    public final static String EXTRA_CODE = "extra_code";
    public final static String EXTRA_POSITION = "extra_position";
    public final static String EXTRA_REQUEST_CODE = "extra_request_code";
    public final static String EXTRA_START_TYPE = "extra_start_type";
    public final static String VALUE_START_VIEW = "value_start_view";
    public final static String VALUE_START_EDIT = "value_start_edit";
    public final static String EXTRA_FRAGMENT = "extra_fragment";
    public final static String VALUE_FRAGMENT_NOTE = "value_fragment_note";
    public final static String EXTRA_IS_GOOGLE_NOW = "extra_is_from_google_now";
    public final static String EXTRA_IS_PREVIEW = "extra_is_preview";

    public final static String ACTION_TO_NOTE_FROM_THIRD_PART = "to_note_from_third_part";
    // endregion

    // region Attachment
    public final static String MIME_TYPE_IMAGE = "image/jpeg";
    public final static String MIME_TYPE_AUDIO = "audio/amr";
    public final static String MIME_TYPE_VIDEO = "video/mp4";
    public final static String MIME_TYPE_SKETCH = "image/png";
    public final static String MIME_TYPE_FILES = "file/*";
    public final static String MIME_TYPE_HTML = "text/html";
    public final static String MIME_TYPE_IMAGE_EXTENSION = ".jpeg";
    public final static String MIME_TYPE_AUDIO_EXTENSION = ".amr";
    public final static String MIME_TYPE_VIDEO_EXTENSION = ".mp4";
    public final static String MIME_TYPE_SKETCH_EXTENSION = ".png";
    public final static String MIME_TYPE_CONTACT_EXTENSION = ".vcf";

    public final static String VIDEO_MIME_TYPE = "video/*";
    public final static String SCHEME_HTTPS = "https";
    public final static String SCHEME_HTTP = "http";
    public final static String PDF_MIME_TYPE = "application/pdf";
    public final static String _3GP = ".3gp";
    public final static String _MP4 = ".mp4";
    public final static String _PDF = ".pdf";
    // endregion

    // region Action
    public final static String ACTION_SHORTCUT = "ACTION_SHORTCUT";
    public final static String ACTION_NOTIFICATION = "ACTION_NOTIFICATION";
    public final static String INTENT_GOOGLE_NOW = "com.google.android.gm.action.AUTO_SEND";

    public final static String ACTION_RESTART_APP = "action_restart_app";

    public final static String ACTION_NOTE_CHANGE_BROADCAST = "action_broadcast_notes_changed";
    // endregion

    // region Urls
    public final static String GOOGLE_PLAY_WEB_PAGE = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
    public final static String MARKET_PAGE = "market://details?id=" + BuildConfig.APPLICATION_ID;
    public final static String GITHUB_PAGE = "https://github.com/Shouheng88/NotePal";
    public final static String GITHUB_DEVELOPER = "https://github.com/Shouheng88";
    public final static String GOOGLE_PLUS_URL = "https://plus.google.com/u/1/communities/102252970668657211916";
    public final static String WEIBO_PAGE = "https://weibo.com/5401152113/profile?rightmod=1&wvr=6&mod=personinfo";
    public final static String TWITTER_PAGE = "https://twitter.com/ShouhengWang";
    public final static String WIKI = "https://github.com/Shouheng88/MarkNote/wiki";

    public final static String DEVELOPER_EMAIL = "shouheng2015@gmail.com";
    public final static String DEVELOPER_EMAIL_PREFIX = "【NotePal|%s】";
    public final static String DEVELOPER_EMAIL_EMAIL_PREFIX = "\nContact Email:";

    public final static String DEFAULT_USER_INFO_BG = "https://cdn1.tnwcdn.com/wp-content/blogs.dir/1/files/2014/06/wallpaper_51.jpg";
    // endregion

    // region App Widgets
    public final static String INTENT_WIDGET = "widget_id";
    public final static String ACTION_WIDGET_LIST = "action_widget_list";
    public final static String ACTION_TAKE_PHOTO = "action_widget_take_photo";
    public final static String ACTION_ADD_SKETCH = "action_widget_add_sketch";
    public final static String ACTION_ADD_FILES = "action_widget_add_files";
    public final static String ACTION_ADD_NOTE = "action_widget_add_note";
    public final static String ACTION_ADD_MIND = "action_widget_add_mind";
    public final static String ACTION_CONFIG = "action_widget_config";
    public final static String EXTRA_CONFIG_SWITCH_ENABLE = "extra_widget_switch_enable";
    public final static String ACTION_WIDGET_LAUNCH_APP = "action_widget_launch_app";

    public final static String PREFS_NAME = BuildConfig.APPLICATION_ID + "_preferences";
    public final static String PREF_WIDGET_SQL_PREFIX = "widget_sql_";
    public final static String PREF_WIDGET_TYPE_PREFIX = "widget_type_";
    public final static String PREF_WIDGET_NOTEBOOK_CODE_PREFIX = "widget_notebook_code";
    // endregion

    public final static String REA_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQD0diKVSZ/U/KHuxZFYac3lLq7K\n" +
            "edqc+uOKSJgq26tgy4wmELCw8gJkempBm8NPf+uSOdWPlPLWijSf3W2KfzMMvZQ2\n" +
            "tfNQPQu+gXgdXuZC+fhqVqNgYtWVRMIspveSm3AK+52AxxzTlfAU1fpCEFOf4AHc\n" +
            "/E33toB493pf9gS2xwIDAQAB";

    public final static String TITLE_REGEX = "^(#+)(.*)";
    public final static String IMAGE_REGEX = "!\\[.*]\\(.+.\\)";

    public final static String HTML_EXPORT_DIR_NAME = "ExportedHtml";
    public final static String TEXT_EXPORT_DIR_NAME = "ExportedText";
    public final static String EXPORTED_TEXT_EXTENSION = ".txt";
    public final static String EXPORTED_MARKDOWN_EXTENSION = ".md";
    public final static String EXPORTED_HTML_EXTENSION = ".html";

    public final static String BACKUP_DIR_NAME = "NotePal";
    public final static String FILES_BACKUP_DIR_NAME = "files";
}
