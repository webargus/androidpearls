package br.com.pearls.DB;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "areas")
public class KnowledgeArea implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String area;

    @NonNull
    private String area_ascii;

    @NonNull
    @ColumnInfo(defaultValue = "0")
    private int synced;

    public KnowledgeArea() {
    }

    public KnowledgeArea(@NonNull String area, @NonNull String area_ascii) {
        this.area = area;
        this.area_ascii = area_ascii;
    }

    public KnowledgeArea(@NonNull String area, @NonNull String area_ascii, int synced) {
        this.area = area;
        this.area_ascii = area_ascii;
        this.synced = synced;
    }

    protected KnowledgeArea(Parcel in) {
        id = in.readLong();
        area = in.readString();
        area_ascii = in.readString();
        synced = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(area);
        dest.writeString(area_ascii);
        dest.writeInt(synced);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<KnowledgeArea> CREATOR = new Creator<KnowledgeArea>() {
        @Override
        public KnowledgeArea createFromParcel(Parcel in) {
            return new KnowledgeArea(in);
        }

        @Override
        public KnowledgeArea[] newArray(int size) {
            return new KnowledgeArea[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getArea() {
        return this.area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getArea_ascii() { return this.area_ascii; }

    public void setArea_ascii(String area_ascii) { this.area_ascii = area_ascii; }

    public int getSynced() {
        return this.synced;
    }

    public void setSynced(int synced) {
        this.synced = synced;
    }
}



















