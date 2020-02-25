package br.com.pearls.DB;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "domains")
public class Domain {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String domain;

    @NonNull
    private long area_ref;

    @NonNull
    @ColumnInfo(defaultValue = "0")
    private int status;

    public long getId() { return this.id; }

    public void setId(long id) { this.id = id; }

    public String getDomain() { return this.domain; }

    public void setDomain(String domain) { this.domain = domain; }

    public long getArea_ref() { return this.area_ref; }

    public void setArea_ref(long area_ref) { this.area_ref = area_ref; }

    public int getStatus() { return this.status; }

    public void setStatus(int status) { this.status = status; }

}















