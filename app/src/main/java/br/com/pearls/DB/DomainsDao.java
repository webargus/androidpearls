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

    @Query("SELECT * FROM domains ORDER BY domain ASC")
    LiveData<List<Domain>> getAllDomains();

    @Query("SELECT * FROM domains WHERE synced = 1")
    LiveData<List<Domain>> getSyncedDomains();

    @Query("SELECT * FROM domains WHERE domain_ascii = :domain AND area_ref = :area_ref")
    LiveData<Domain[]> getDomainByName(String domain, long area_ref);

    @Query("SELECT id FROM domains WHERE id = :domain_id")
    long[] getDomain(long domain_id);
}
