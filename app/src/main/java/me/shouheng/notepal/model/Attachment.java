package me.shouheng.notepal.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import me.shouheng.notepal.model.enums.ModelType;
import me.shouheng.notepal.model.enums.Status;
import me.shouheng.notepal.provider.annotation.Column;
import me.shouheng.notepal.provider.annotation.Table;


/**
 * Created by wangshouheng on 2017/4/6.*/
@Table(name = "gt_attachment")
public class Attachment extends Model implements Parcelable {

    @Column(name = "model_code")
    private long modelCode;

    @Column(name = "model_type")
    private ModelType modelType;

    @Column(name = "uri")
    private Uri uri;

    @Column(name = "path")
    private String path;

    @Column(name = "name")
    private String name;

    @Column(name = "size")
    private long size;

    @Column(name = "length")
    private long length;

    @Column(name = "mine_type")
    private String mineType;

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
                "modelCode=" + modelCode +
                ", uri=" + uri +
                ", path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", length=" + length +
                ", mineType='" + mineType + '\'' +
                "} " + super.toString();
    }
}
