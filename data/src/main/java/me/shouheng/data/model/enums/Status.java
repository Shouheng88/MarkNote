package me.shouheng.data.model.enums;

/**
 * Created by WngShhng on 2017/12/9.*/
public enum Status {
    NORMAL(0),
    ARCHIVED(1),
    TRASHED(2),
    DELETED(3);

    public final int id;

    Status(int id) {
        this.id = id;
    }

    public static Status getStatusById(int id) {
        for (Status status : values()) {
            if (status.id == id) {
                return status;
            }
        }
        return NORMAL;
    }
}
