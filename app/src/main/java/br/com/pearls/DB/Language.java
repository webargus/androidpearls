package br.com.pearls.DB;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "languages")
public class Language {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String language;

    @ColumnInfo(defaultValue = "1")
    private int active;

    private int status;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        // do nothing, just created this method for compiling to work
        this.id = id;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getActive() {
        return this.active;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }
}



















