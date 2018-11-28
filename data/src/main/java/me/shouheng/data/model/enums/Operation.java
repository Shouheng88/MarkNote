package me.shouheng.data.model.enums;

import android.support.annotation.StringRes;

import me.shouheng.data.R;

/**
 * Created by wangshouheng on 2017/8/13.*/
public enum Operation {
    NONE(0, R.string.operation_none),
    ADD(1, R.string.operation_added),
    DELETE(2, R.string.operation_deleted),
    UPDATE(3, R.string.operation_modified),
    ARCHIVE(4, R.string.operation_archived),
    TRASH(5, R.string.operation_trashed),
    COMPLETE(6, R.string.operation_completed),
    SYNCED(7, R.string.operation_synced),
    INCOMPLETE(8, R.string.operation_uncompleted),
    RECOVER(10, R.string.operation_recover);

    public final int id;

    @StringRes
    public final int operationName;

    Operation(int id, int operationName) {
        this.id = id;
        this.operationName = operationName;
    }

    public static Operation getTypeById(int id){
        for (Operation type : values()){
            if (type.id == id){
                return type;
            }
        }
        return NONE;
    }
}
