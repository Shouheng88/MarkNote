package me.shouheng.data.entity;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import me.shouheng.data.utils.annotation.Column;
import me.shouheng.data.utils.annotation.Table;
import me.shouheng.data.model.enums.ModelType;
import me.shouheng.data.model.enums.Status;
import me.shouheng.data.schema.AttachmentSchema;

/**
 * Created by wangshouheng on 2017/4/6.*/
@Table(name = AttachmentSchema.TABLE_NAME)
public class Attachment extends Model implements Parcelable {

    @Column(name = AttachmentSchema.MODEL_CODE)
    private long modelCode;

    @Column(name = AttachmentSchema.MODEL_TYPE)
    private ModelType modelType;

    @Column(name = AttachmentSchema.URI)
    private Uri uri;

    @Column(name = AttachmentSchema.PATH)
    private String path;

    @Column(name = AttachmentSchema.NAME)
    private String name;

    @Column(name = AttachmentSchema.SIZE)
    private long size;

    @Column(name = AttachmentSchema.LENGTH)
    private long length;

    @Column(name = AttachmentSchema.MINE_TYPE)
    private String mineType;

    @Column(name = AttachmentSchema.ONE_DRIVE_SYNC_TIME)
    private Date oneDriveSyncTime;

    @Column(name = AttachmentSchema.ONE_DRIVE_ITEM_ID)
    private String oneDriveItemId;

    // region Android端字段，不计入数据库

    /**
     * 不计入数据库的字段 */
    private boolean audioPlaying;

    /**
     * 判断当前的附件是否是新加入的附件 */
    private boolean isNew;

    private boolean fake;

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean isAudioPlaying() {
        return audioPlaying;
    }

    public void setAudioPlaying(boolean audioPlaying) {
        this.audioPlaying = audioPlaying;
    }

    public boolean isFake() {
        return fake;
    }

    public void setFake(boolean fake) {
        this.fake = fake;
    }

    // endregion

    public Attachment(){}

    private Attachment(Parcel in) {
        setId(in.readLong());
        setCode(in.readLong());
        setUserId(in.readLong());
        setAddedTime(new Date(in.readLong()));
        setLastModifiedTime(new Date(in.readLong()));
        setLastSyncTime(new Date(in.readLong()));
        setStatus(Status.getStatusById(in.readInt()));

        setId(in.readLong());
        setUri(Uri.parse(in.readString()));
        setMineType(in.readString());
        setUserId(in.readLong());
        setModelCode(in.readLong());
        setModelType(ModelType.getTypeById(in.readInt()));
        setCode(in.readLong());
    }

    public static final Creator<Attachment> CREATOR = new Creator<Attachment>() {

        public Attachment createFromParcel(Parcel in) {
            return new Attachment(in);
        }

        public Attachment[] newArray(int size) {
            return new Attachment[size];
        }
    };

    public long getModelCode() {
        return modelCode;
    }

    public void setModelCode(long modelCode) {
        this.modelCode = modelCode;
    }

    public ModelType getModelType() {
        return modelType;
    }

    public void setModelType(ModelType modelType) {
        this.modelType = modelType;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getMineType() {
        return mineType;
    }

    public void setMineType(String mineType) {
        this.mineType = mineType;
    }

    public Date getOneDriveSyncTime() {
        return oneDriveSyncTime;
    }

    public void setOneDriveSyncTime(Date oneDriveSyncTime) {
        this.oneDriveSyncTime = oneDriveSyncTime;
    }

    public String getOneDriveItemId() {
        return oneDriveItemId;
    }

    public void setOneDriveItemId(String oneDriveItemId) {
        this.oneDriveItemId = oneDriveItemId;
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

        dest.writeLong(getId());
        dest.writeString(getUri().toString());
        dest.writeString(getMineType());
        dest.writeLong(getUserId());
        dest.writeLong(getModelCode());
        dest.writeInt(getModelType().id);
        dest.writeLong(getCode());
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "id=" + id +
                ", code=" + code +
                ", userId=" + userId +
                ", modelCode=" + modelCode +
                ", addedTime=" + addedTime +
                ", modelType=" + modelType +
                ", lastModifiedTime=" + lastModifiedTime +
                ", uri=" + uri +
                ", lastSyncTime=" + lastSyncTime +
                ", path='" + path + '\'' +
                ", status=" + status +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", length=" + length +
                ", mineType='" + mineType + '\'' +
                ", oneDriveSyncTime=" + oneDriveSyncTime +
                ", oneDriveItemId='" + oneDriveItemId + '\'' +
                ", audioPlaying=" + audioPlaying +
                ", isNew=" + isNew +
                ", fake=" + fake +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Attachment that = (Attachment) o;

        return code == that.code;
    }

    @Override
    public int hashCode() {
        return (int) (code ^ (code >>> 32));
    }
}
