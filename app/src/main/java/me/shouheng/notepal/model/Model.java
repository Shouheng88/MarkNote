package me.shouheng.notepal.model;

import java.io.Serializable;
import java.util.Date;

import me.shouheng.notepal.model.enums.Status;
import me.shouheng.notepal.provider.annotation.Column;
import me.shouheng.notepal.util.TimeUtils;


/**
 * Created by wangshouheng on 2017/3/13. */
public class Model implements Serializable {

    @Column(name = "id")
    private long id;

    @Column(name = "code")
    private long code;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "added_time")
    private Date addedTime;

    @Column(name = "last_modified_time")
    private Date lastModifiedTime;

    @Column(name = "last_sync_time")
    private Date lastSyncTime;

    @Column(name = "status")
    private Status status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Date getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(Date addedTime) {
        this.addedTime = addedTime;
    }

    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public Date getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(Date lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Model{" +
                "id=" + id +
                ", code=" + code +
                ", userId=" + userId +
                ", addedTime=" + TimeUtils.formatDate(addedTime, TimeUtils.DateFormat.YYYY_MMM_dd_E_hh_mm_a) +
                ", lastModifiedTime=" +  TimeUtils.formatDate(lastModifiedTime, TimeUtils.DateFormat.YYYY_MMM_dd_E_hh_mm_a) +
                ", lastSyncTime=" + TimeUtils.formatDate(lastSyncTime, TimeUtils.DateFormat.YYYY_MMM_dd_E_hh_mm_a) +
                ", status=" + (status == null ? "null" : status.name()) +
                '}';
    }
}
