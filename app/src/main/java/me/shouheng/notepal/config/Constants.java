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

    public final static String ACTION_TO_NOTE_FROM_THIRD_PART = "to_note_from_third_part";
    // endregion

    // region Attachment
    public final static String DATE_FORMAT_SORTABLE = "yyyyMMdd_HHmmss_SSS";
    public final static String MIME_TYPE_IMAGE = "image/jpeg";
    public final static String MIME_TYPE_AUDIO = "audio/amr";
    public final static String MIME_TYPE_VIDEO = "video/mp4";
    public final static String MIME_TYPE_SKETCH = "image/png";
    public final static String MIME_TYPE_FILES = "file/*";
    public final static String MIME_TYPE_IMAGE_EXTENSION = ".jpeg";
    public final static String MIME_TYPE_AUDIO_EXTENSION = ".amr";
    public final static String MIME_TYPE_VIDEO_EXTENSION = ".mp4";
    public final static String MIME_TYPE_SKETCH_EXTENSION = ".png";
    public final static String MIME_TYPE_CONTACT_EXTENSION = ".vcf";
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
    public final static String GITHUB_PAGE = "https://github.com/WngShhng/NotePal-Page";
    public final static String GITHUB_DEVELOPER = "https://github.com/WngShhng";
    public final static String GOOGLE_PLUS_URL = "https://plus.google.com/u/1/communities/102252970668657211916";
    public final static String WEIBO_PAGE = "https://weibo.com/5401152113/profile?rightmod=1&wvr=6&mod=personinfo";
    public final static String TWITTER_PAGE = "https://twitter.com/ShouhengWang";

    public final static String DEVELOPER_EMAIL = "shouheng2015@gmail.com";
    public final static String DEVELOPER_EMAIL_PREFIX = "【NotePal|%s】";
    public final static String DEVELOPER_EMAIL_EMAIL_PREFIX = "\nContact Email:";
    // endregion

    // region App Widgets
    public final static String INTENT_WIDGET = "widget_id";
    public final static String ACTION_WIDGET_LIST = "action_widget_list";
    public final static String ACTION_TAKE_PHOTO = "action_widget_take_photo";
    public final static String ACTION_ADD_NOTE = "action_widget_add_note";
    public final static String ACTION_ADD_MIND = "action_widget_add_mind";
    public final static String ACTION_CONFIG = "action_widget_config";
    public final static String ACTION_WIDGET_LAUNCH_APP = "action_widget_launch_app";

    public final static String PREFS_NAME = BuildConfig.APPLICATION_ID + "_preferences";
    public final static String PREF_WIDGET_SQL_PREFIX = "widget_sql_";
    public final static String PREF_WIDGET_TYPE_PREFIX = "widget_type_";
    // endregion
}
