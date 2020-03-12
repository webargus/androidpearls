package br.com.pearls.DB;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "vertices")
public class Vertex {

    public static final String TAG = Vertex.class.getName();

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private long graph_ref;

    @NonNull
    private long term_ref;

    @NonNull
    private int user_rank;

    private String vertex_context;

    @NonNull
    private int synced;

    public long getId() { return this.id; }
    public void setId(final long id) { this.id = id; }
    public long getGraph_ref() { return this.graph_ref; }
    public void setGraph_ref(final long ref) { this.graph_ref = ref; }
    public long getTerm_ref() { return this.term_ref; }
    public void setTerm_ref(final long ref) { this.term_ref = ref; }
    public int getUser_rank() { return this.user_rank; }
    public void setUser_rank(final int rank) { this.user_rank = rank; }
    public String getVertex_context() { return this.vertex_context; }
    public void setVertex_context(final String context) { this.vertex_context = context; }
    public int getSynced() { return this.synced; }
    public void setSynced(final int synced) { this.synced = synced; }

    public void debugDump() {
        Log.v(TAG, "Vertex dump: ");
        Log.v(TAG, "id: " + id);
        Log.v(TAG, "graph_ref: " + graph_ref);
        Log.v(TAG, "term_ref: " + term_ref);
        Log.v(TAG, "user_rank: " + user_rank);
        Log.v(TAG, "vertex_context: " + vertex_context);
    }
}












