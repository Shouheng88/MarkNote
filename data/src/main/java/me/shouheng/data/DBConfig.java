package me.shouheng.data;

/**
 * Created on 2018/11/28.
 */
public interface DBConfig {
    String DATABASE_NAME = "NotePal.db";
    int VERSION = 7;
    int TIMELINE_CONTENT_LENGTH = 250;
    int DAYS_OF_ADDED_MODEL = 7;
    String CATEGORY_SPLIT = ",";
}
