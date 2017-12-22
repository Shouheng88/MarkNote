package me.shouheng.notepal.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import me.shouheng.notepal.model.enums.Status;
import me.shouheng.notepal.provider.annotation.Column;
import me.shouheng.notepal.provider.annotation.Table;

/**
 * Created by wangshouheng on 2017/5/12.*/
@Table(name = "gt_note")
public class Note extends Model implements Parcelable {

    @Column(name = "parent_code")
    private long parentCode;

    @Column(name = "tree_path")
    private String treePath;

    @Column(name = "title")
    private String title;

    @Column(name = "content_code")
    private long contentCode;

    @Column(name = "class_code")
    private long classCode;

    @Column(name = "tags")
    private String tags;

    @Column(name = "purpose_code")
    private long purposeCode;

    // region Android端字段，不计入数据库

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // endregion

    public Note(){}

    private Note(Parcel in) {
        setId(in.readLong());
        setCode(in.readLong());
        setUserId(in.readLong());
        setAddedTime(new Date(in.readLong()));
        setLastModifiedTime(new Date(in.readLong()));
        setLastSyncTime(new Date(in.readLong()));
        setStatus(Status.getStatusById(in.readInt()));

        setParentCode(in.readLong());
        setTitle(in.readString());
        setContent(in.readString());
        setContentCode(in.readLong());
        setTags(in.readString());
        setClassCode(in.readLong());
        setPurposeCode(in.readLong());
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public long getParentCode() {
        return parentCode;
    }

    public void setParentCode(long parentCode) {
        this.parentCode = parentCode;
    }

    public String getTreePath() {
        return treePath;
    }

    public void setTreePath(String treePath) {
        this.treePath = treePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getContentCode() {
        return contentCode;
    }

    public void setContentCode(long contentCode) {
        this.contentCode = contentCode;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public long getClassCode() {
        return classCode;
    }

    public void setClassCode(long classCode) {
        this.classCode = classCode;
    }

    public long getPurposeCode() {
        return purposeCode;
    }

    public void setPurposeCode(long purposeCode) {
        this.purposeCode = purposeCode;
    }

    @Override
    public String toString() {
        return "Note{" +
                "parentCode=" + parentCode +
                ", treePath='" + treePath + '\'' +
                ", title='" + title + '\'' +
                ", contentCode=" + contentCode +
                ", classCode=" + classCode +
                ", tags='" + tags + '\'' +
                ", purposeCode=" + purposeCode +
                ", content='" + content + '\'' +
                "} " + super.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getId());
        dest.writeLong(getCode());
        dest.writeLong(getUserId());
        dest.writeLong(getAddedTime().getTime());
        dest.writeLong(getLastModifiedTime().getTime());
        dest.writeLong(getLastSyncTime().getTime());
        dest.writeInt(getStatus().id);

        dest.writeLong(getParentCode());
        dest.writeString(getTitle());
        dest.writeString(getContent());
        dest.writeLong(getContentCode());
        dest.writeString(getTags());
        dest.writeLong(getClassCode());
        dest.writeLong(getPurposeCode());
    }
}
