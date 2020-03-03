package br.com.pearls.DB;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LanguagesDao {

    @Insert
    void insert(Language language);

    @Query("SELECT * FROM languages")
    LiveData<List<Language>> getAllLanguages();

    @Query("SELECT * FROM languages WHERE active = :active")
    LiveData<List<Language>> getActiveLanguages(final int active);


}








