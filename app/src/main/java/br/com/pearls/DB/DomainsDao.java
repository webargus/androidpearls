package br.com.pearls.DB;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DomainsDao {

    @Insert
    void insert(Domain domain);

    @Query("SELECT * FROM domains")
    LiveData<List<Domain>> getAllDomains();

    @Query("SELECT * FROM domains WHERE status = 1")
    LiveData<List<Domain>> getSyncedDomains();

}
