package me.shouheng.notepal.listener;

public enum SettingChangeType {
    DRAWER(0),
    FAB(1),
    NOTE_LIST_TYPE(2),
    FAST_SCROLLER(3);

    public final int id;

    SettingChangeType(int id) {
        this.id = id;
    }
}
