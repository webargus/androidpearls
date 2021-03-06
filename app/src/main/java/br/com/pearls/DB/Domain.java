package br.com.pearls.DB;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "domains")
public class Domain implements Parcelable {

    public static final String TAG = Domain.class.getName();

    public Domain() {}

    @Ignore
    public Domain(Domain other) {
        this.id = other.getId();
        this.area_ref = other.getArea_ref();
        this.domain = other.getDomain();
        this.domain_ascii = other.getDomain_ascii();
        this.synced = other.getSynced();
    }

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String domain;

    @NonNull
    private String domain_ascii;

    @NonNull
    private long area_ref;

    @NonNull
    @ColumnInfo(defaultValue = "0")
    private int synced;

    protected Domain(Parcel in) {
        id = in.readLong();
        domain = in.readString();
        domain_ascii = in.readString();
        area_ref = in.readLong();
        synced = in.readInt();
    }

    public static final Creator<Domain> CREATOR = new Creator<Domain>() {
        @Override
        public Domain createFromParcel(Parcel in) {
            return new Domain(in);
        }

        @Override
        public Domain[] newArray(int size) {
            return new Domain[size];
        }
    };

    public long getId() { return this.id; }

    public void setId(long id) { this.id = id; }

    public String getDomain() { return this.domain; }

    public void setDomain(String domain) { this.domain = domain; }

    public String getDomain_ascii() { return this.domain_ascii; }

    public void setDomain_ascii(String domain_ascii) { this.domain_ascii = domain_ascii; }

    public long getArea_ref() { return this.area_ref; }

    public void setArea_ref(long area_ref) { this.area_ref = area_ref; }

    public int getSynced() { return this.synced; }

    public void setSynced(int synced) { this.synced = synced; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(domain);
        dest.writeString(domain_ascii);
        dest.writeLong(area_ref);
        dest.writeInt(synced);
    }

    public void debugDump() {
        Log.v(TAG, "Domain dump:");
        Log.v(TAG, "id: " + getId());
        Log.v(TAG, "domain: " + getDomain());
        Log.v(TAG, "domain_ascii: " + getDomain_ascii());
        Log.v(TAG, "area_ref: " + getArea_ref());
    }
}















