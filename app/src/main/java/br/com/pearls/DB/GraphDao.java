package br.com.pearls.DB;

import androidx.room.Dao;
import androidx.room.Insert;

@Dao
public interface GraphDao {

    @Insert
    long insert(Graph graph);
}
