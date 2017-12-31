package me.shouheng.notepal.util.enums;

/**
 * Created by Wang Shouheng on 2017/12/31.*/
public enum MindSnaggingListType {
    ONE_COL(0),
    TWO_COLS(1);

    public final int id;

    MindSnaggingListType(int id) {
        this.id = id;
    }

    public static MindSnaggingListType getTypeById(int id) {
        for (MindSnaggingListType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        return ONE_COL;
    }
}
