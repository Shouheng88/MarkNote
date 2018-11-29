package me.shouheng.notepal.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import me.shouheng.data.model.enums.Status;
import me.shouheng.notepal.provider.annotation.Column;
import me.shouheng.notepal.provider.annotation.Table;

/**
 * Created by wangshouheng on 2017/8/18. */
@Table(name = "gt_mind_snagging")
public class MindSnagging extends Model implements Parcelable {

    @Column(name = "content")
    private String content;

    @Column(name = "picture")
    private Uri picture;

    public MindSnagging() {}

    protected MindSnagging(Parcel in) {
        setId(in.readLong());
        setCode(in.readLong());
        setUserId(in.readLong());
        setAddedTime(new Date(in.readLong()));
        setLastModifiedTime(new Date(in.readLong()));
        setLastSyncTime(new Date(in.readLong()));
        setStatus(Status.getStatusById(in.readInt()));

        content = in.readString();
        picture = Uri.parse(in.readString());
    }

    public static final Creator<MindSnagging> CREATOR = new Creator<MindSnagging>() {
        @Override
        public MindSnagging createFromParcel(Parcel in) {
            return new MindSnagging(in);
        }

        @Override
        public MindSnagging[] newArray(int size) {
            return new MindSnagging[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(getId());
        parcel.writeLong(getCode());
        parcel.writeLong(getUserId());
        parcel.writeLong(getAddedTime().getTime());
        parcel.writeLong(getLastModifiedTime().getTime());
        parcel.writeLong(getLastSyncTime().getTime());
        parcel.writeInt(getStatus().id);

        parcel.writeString(content);
        parcel.writeString(getPicture() == null ? "" : getPicture().toString());
    }
}
