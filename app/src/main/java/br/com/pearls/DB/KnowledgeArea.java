package br.com.pearls.DB;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "areas")
public class KnowledgeArea {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String area;

    @NonNull
    private String area_ascii;

    @NonNull
    @ColumnInfo(defaultValue = "0")
    private int synced;

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



















