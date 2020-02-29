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
    private String domain_ascii;

    @NonNull
    private long area_ref;

    @NonNull
    @ColumnInfo(defaultValue = "0")
    private int synced;

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

}















