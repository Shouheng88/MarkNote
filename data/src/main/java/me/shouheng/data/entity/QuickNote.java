package me.shouheng.data.entity;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Date;

import me.shouheng.data.model.enums.Status;
import me.shouheng.data.utils.annotation.Column;
import me.shouheng.data.utils.annotation.Table;

/**
 * Created by WngShhng on 2017/8/18.
 */
@Table(name = "gt_mind_snagging")
public class QuickNote extends Model implements Parcelable {

    @Column(name = "content")
    private String content;

    @Column(name = "picture")
    private Uri picture;

    public QuickNote() {}

    protected QuickNote(Parcel in) {
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

    public static final Creator<QuickNote> CREATOR = new Creator<QuickNote>() {
        @Override
        public QuickNote createFromParcel(Parcel in) {
            return new QuickNote(in);
        }

        @Override
        public QuickNote[] newArray(int size) {
            return new QuickNote[size];
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

    @NonNull
    @Override
    public String toString() {
        return "QuickNote{" +
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
