package me.shouheng.data.entity;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Date;

import me.shouheng.data.utils.annotation.Column;
import me.shouheng.data.utils.annotation.Table;
import me.shouheng.data.model.enums.NoteType;
import me.shouheng.data.model.enums.Status;
import me.shouheng.data.schema.NoteSchema;

/**
 * Created by wangshouheng on 2017/5/12.*/
@Table(name = NoteSchema.TABLE_NAME)
public class Note extends Model implements Parcelable {

    @Column(name = NoteSchema.PARENT_CODE)
    private long parentCode;

    @Column(name = NoteSchema.TREE_PATH)
    private String treePath;

    @Column(name = NoteSchema.TITLE)
    private String title;

    @Column(name = NoteSchema.CONTENT_CODE)
    private long contentCode;

    @Column(name = NoteSchema.TAGS)
    private String tags;

    @Column(name = NoteSchema.PREVIEW_IMAGE)
    private Uri previewImage;

    @Column(name = NoteSchema.NOTE_TYPE)
    private NoteType noteType;

    @Column(name = NoteSchema.PREVIEW_CONTENT)
    private String previewContent;

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

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
        setTreePath(in.readString());
        setTitle(in.readString());
        setContent(in.readString());
        setContentCode(in.readLong());
        setTags(in.readString());
        setNoteType(NoteType.getTypeById(in.readInt()));
        setPreviewContent(in.readString());
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

    public Uri getPreviewImage() {
        return previewImage;
    }

    public void setPreviewImage(Uri previewImage) {
        this.previewImage = previewImage;
    }

    public NoteType getNoteType() {
        return noteType;
    }

    public void setNoteType(NoteType noteType) {
        this.noteType = noteType;
    }

    public String getPreviewContent() {
        return previewContent;
    }

    public void setPreviewContent(String previewContent) {
        this.previewContent = previewContent;
    }

    @NonNull
    @Override
    public String toString() {
        return "Note{" +
                "parentCode=" + parentCode +
                ", treePath='" + treePath + '\'' +
                ", title='" + title + '\'' +
                ", contentCode=" + contentCode +
                ", tags='" + tags + '\'' +
                ", previewImage=" + previewImage +
                ", noteType=" + noteType +
                ", previewContent='" + previewContent + '\'' +
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
        dest.writeString(getTreePath());
        dest.writeString(getTitle());
        dest.writeString(getContent());
        dest.writeLong(getContentCode());
        dest.writeString(getTags());
        dest.writeInt(getNoteType().getId());
        dest.writeString(getPreviewContent());
    }
}
