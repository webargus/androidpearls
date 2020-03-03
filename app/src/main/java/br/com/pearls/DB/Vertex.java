package br.com.pearls.DB;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "vertices")
public class Vertex {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private long graph_ref;

    @NonNull
    private long term_ref;

    @NonNull
    private int user_rank;

    private String context;

    @NonNull
    private int synced;

    public long getId() { return this.id; }
    public void setId(final long id) { this.id = id; }
    public long getGraphRef() { return this.graph_ref; }
    public void setGraphRef(final long ref) { this.graph_ref = ref; }
    public long getTermRef() { return this.term_ref; }
    public void setTermRef(final long ref) { this.term_ref = ref; }
    public int getUserRank() { return this.user_rank; }
    public void setUserRank(final int rank) { this.user_rank = rank; }
    public String getTermContext() { return this.context; }
    public void setTermContext(final String context) { this.context = context; }
    public int getSynced() { return this.synced; }
    public void setSynced(final int synced) { this.synced = synced; }
}












