package me.shouheng.notepal.config;

import me.shouheng.commons.BaseConstants;
import me.shouheng.notepal.BuildConfig;

public interface Constants extends BaseConstants {
    // region Extras
    String EXTRA_MODEL = "extra_model";
    String EXTRA_CODE = "extra_code";
    String EXTRA_POSITION = "extra_position";
    String EXTRA_REQUEST_CODE = "extra_request_code";
    String EXTRA_IS_GOOGLE_NOW = "extra_is_from_google_now";

    String ACTION_TO_NOTE_FROM_THIRD_PART = "to_note_from_third_part";
    // endregion

    String VIDEO_MIME_TYPE = "video/*";
    String SCHEME_HTTPS = "https";
    String SCHEME_HTTP = "http";
    String PDF_MIME_TYPE = "application/pdf";
    String _3GP = ".3gp";
    String _MP4 = ".mp4";
    String _PDF = ".pdf";
    // endregion

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

    String TITLE_REGEX = "^(#+)(.*)";
    String IMAGE_REGEX = "!\\[.*]\\(.+.\\)";

    String HTML_EXPORT_DIR_NAME = "ExportedHtml";
    String TEXT_EXPORT_DIR_NAME = "ExportedText";
    String EXPORTED_TEXT_EXTENSION = ".txt";
    String EXPORTED_HTML_EXTENSION = ".html";

    String BACKUP_DIR_NAME = "NotePal";
    String FILES_BACKUP_DIR_NAME = "files";
}
