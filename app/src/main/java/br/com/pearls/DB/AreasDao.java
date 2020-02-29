package br.com.pearls.DB;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface AreasDao {

    @Insert
    void insert(KnowledgeArea area);

    @Query("SELECT * FROM areas")
    LiveData<List<KnowledgeArea>> getAllAreas();

    @Query("SELECT * FROM areas WHERE synced = 1")
    LiveData<List<KnowledgeArea>> getSyncedAreas();

    @Transaction
    @Query("SELECT * FROM areas")
    LiveData<List<AreasWithDomains>> getAreasWithDomains();

    @Query("SELECT * FROM areas WHERE area_ascii = :area")
    LiveData<KnowledgeArea[]> getAreaByName(String area);

}
