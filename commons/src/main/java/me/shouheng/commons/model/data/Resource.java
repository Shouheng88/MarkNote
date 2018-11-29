package me.shouheng.commons.model.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static me.shouheng.commons.model.data.Status.FAILED;
import static me.shouheng.commons.model.data.Status.LOADING;
import static me.shouheng.commons.model.data.Status.SUCCESS;

/**
 * Created by wang shouheng on 2018/3/13. */
public class Resource<T> {

    @NonNull
    public Status status;

    @Nullable
    public T data;

    @Nullable
    public String message;

    /**
     * reserved field */
    private Long udf1;

    private Resource(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Resource<T> success(@NonNull T data) {
        return new Resource<>(SUCCESS, data, null);
    }

    public static <T> Resource<T> error(String msg, @Nullable T data) {
        return new Resource<>(FAILED, data, msg);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(LOADING, data, null);
    }

    public Long getUdf1() {
        return udf1;
    }

    public void setUdf1(Long udf1) {
        this.udf1 = udf1;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "status=" + status +
                ", data=" + data +
                ", message='" + message + '\'' +
                ", udf1=" + udf1 +
                '}';
    }
}
