package br.com.pearls.DB;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;

@Dao
public interface TermDao {

    @Insert
    long insert(Term term);

    @Update
    void update(Term term);
}
