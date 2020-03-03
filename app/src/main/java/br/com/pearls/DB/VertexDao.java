package br.com.pearls.DB;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public interface VertexDao {

    @Transaction
    @Query("SELECT * FROM vertices WHERE graph_ref = :graph_ref")
    LiveData<VerticesWithTerms> getVerticesFor(final long graph_ref);

}
