package me.shouheng.notepal.listener;

public enum SettingChangeType {
    DRAWER(0),
    FAB(1),
    NOTE_LIST_TYPE(2);

    public final int id;

    SettingChangeType(int id) {
        this.id = id;
    }
}
