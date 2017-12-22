package me.shouheng.notepal.model.enums;

import android.support.annotation.StringRes;

import me.shouheng.notepal.R;


/**
 * Created by wangshouheng on 2017/12/3.*/
public enum FeedbackType {
    ABRUPT_CRASH(0, R.string.feedback_abrupt_crash),
    FUNCTION_IMPROVEMENT(1, R.string.feedback_function_improvement),
    FUNCTION_REQUIREMENT(2, R.string.feedback_function_requirement);

    public final int id;

    @StringRes
    public final int typeNameRes;

    FeedbackType(int id, int typeNameRes) {
        this.id = id;
        this.typeNameRes = typeNameRes;
    }

    public FeedbackType getTypeById(int mId) {
        for (FeedbackType type : values()) {
            if (type.id == mId) {
                return type;
            }
        }
        return ABRUPT_CRASH;
    }
}
