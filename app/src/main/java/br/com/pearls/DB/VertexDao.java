package br.com.pearls.DB;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

@Dao
public interface VertexDao {

    @Insert
    long insert(Vertex vertex);

    @Update
    void update(Vertex vertex);
}
