package br.com.pearls.DB;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "graphs")
public class Graph {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private long domain_ref;

    @NonNull
    @ColumnInfo(defaultValue = "0")
    private int synced;

    public long getId() { return this.id; }
    public void setId(final long id) { this.id = id; }
    public long getDomainRef() { return this.domain_ref; }
    public void setDomainRef(final long ref) { this.domain_ref = ref; }
    public int getSynced() { return this.synced; }
    public void setSynced(final int synced) { this.synced = synced; }
}













