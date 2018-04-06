package me.shouheng.notepal.async.onedrive;

/**
 * Created by shouh on 2018/4/6.*/
public enum ConflictBehavior {
    FAIL("fail"),
    REPLACE("replace"),
    RENAME("rename");

    public final String value;

    ConflictBehavior(String value) {
        this.value = value;
    }
}
