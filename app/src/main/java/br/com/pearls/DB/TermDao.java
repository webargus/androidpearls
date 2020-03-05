package br.com.pearls.DB;

import androidx.room.Dao;
import androidx.room.Insert;

@Dao
public interface TermDao {

    @Insert
    long insert(Term term);
}
