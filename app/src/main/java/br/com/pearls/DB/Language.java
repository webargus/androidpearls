package br.com.pearls.DB;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "languages")
public class Language {

    private static final String TAG = "Language";

    public Language() {}

    @Ignore
    public Language(Language other) {
        id = other.id;
        language = other.language;
        active = other.active;
        status = other.status;
    }

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

    public void debugDump() {
        Log.d(TAG, "*************debugDump() called");
        Log.w(TAG, "debugDump: id=" + id);
        Log.w(TAG, "debugDump: language=" + language);
        Log.i(TAG, "debugDump: active=" + active);
        Log.i(TAG, "debugDump: status=" + status);
    }
}



















