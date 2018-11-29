package me.shouheng.data.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by shouh on 2018/3/31.
 */
public class Directory implements Serializable {

    private String id;

    private String name;

    private String path;

    private Date lastModifiedDateTime;

    public Directory() {}

    public Directory(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    public void setLastModifiedDateTime(Date lastModifiedDateTime) {
        this.lastModifiedDateTime = lastModifiedDateTime;
    }

    @Override
    public String toString() {
        return "Directory{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", lastModifiedDateTime=" + lastModifiedDateTime +
                '}';
    }
}
