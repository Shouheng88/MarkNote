package me.shouheng.notepal.model;

import android.net.Uri;

import me.shouheng.notepal.provider.annotation.Column;
import me.shouheng.notepal.provider.annotation.Table;

/**
 * Created by wangshouheng on 2017/8/18. */
@Table(name = "gt_mind_snagging")
public class MindSnagging extends Model {

    @Column(name = "content")
    private String content;

    @Column(name = "picture")
    private Uri picture;

    // todo add mime type

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Uri getPicture() {
        return picture;
    }

    public void setPicture(Uri picture) {
        this.picture = picture;
    }

    @Override
    public String toString() {
        return "MindSnagging{" +
                "content='" + content + '\'' +
                ", picture=" + picture +
                "} " + super.toString();
    }
}
