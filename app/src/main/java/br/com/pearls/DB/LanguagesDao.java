package br.com.pearls.DB;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface LanguagesDao {

    @Insert
    void insert(Language language);

    @Query("SELECT * FROM languages ORDER BY status ASC")
    LiveData<List<Language>> getmAllLanguages();

    @Query("SELECT * FROM languages WHERE active = :active")
    LiveData<List<Language>> getActiveLanguages(final int active);

    @Query("SELECT * FROM languages ORDER BY status ASC")
    List<Language> getAllLanguages();

    @Update
    void update(List<Language> languages);
}








