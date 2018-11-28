package me.shouheng.data.model.enums;

/**
 * Created by shouh on 2018/3/19.*/
public enum NoteType {
    NORMAL(0),
    DIARY(1);

    public final int id;

    NoteType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static NoteType getTypeById(int id) {
        for (NoteType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        return NORMAL;
    }
}
