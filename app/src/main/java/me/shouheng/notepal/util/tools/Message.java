package me.shouheng.notepal.util.tools;

import java.util.List;

/**
 * Created by wang shouheng on 2018/1/25.*/
public class Message<T> {

    private boolean succeed;

    private T obj;

    private List<T> list;

    private String msg;

    public boolean isSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
