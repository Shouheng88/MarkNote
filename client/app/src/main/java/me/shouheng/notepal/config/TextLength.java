package me.shouheng.notepal.config;

/**
 * 文字的长度的设置
 *
 * Created by wangshouheng on 2017/10/7. */
public enum TextLength {
    TITLE_TEXT_LENGTH(120),
    SUB_CONTENT_LENGTH(255),
    NOTE_CONTENT_PREVIEW_LENGTH(255),
    COMMENT_TEXT_LENGTH(255),
    LABEL_TEXT_LENGTH(20),
    LABELS_NUMBER(4),
    MAX_ATTACHMENT_NUMBER(6),
    LABELS_TOTAL_LENGTH(120),
    ROOM_TEXT_LENGTH(50),
    TEACHER_TEXT_LENGTH(50),
    TIMELINE_TITLE_LENGTH(120),
    SCHOOL_TEXT_LENGTH(250),
    MOTTO_TEXT_LENGTH(250),
    MAJOR_TEXT_LENGTH(250);

    public final int length;

    TextLength(int length) {
        this.length = length;
    }
}
