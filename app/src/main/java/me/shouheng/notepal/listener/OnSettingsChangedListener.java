package me.shouheng.notepal.listener;

/**
 * Created by shouh on 2018/3/21.*/
public interface OnSettingsChangedListener {

    void onDashboardSettingChanged(ChangedType changedType);

    enum ChangedType {
        DRAWER_CONTENT(0),
        NOTE_LIST_TYPE(1);

        public final int id;

        ChangedType(int id) {
            this.id = id;
        }
    }
}
