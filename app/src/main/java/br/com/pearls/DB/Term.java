package br.com.pearls.DB;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "terms")
public class Term {

    public static final String TAG = Term.class.getName();

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private long lang_ref;

    @NonNull
    private String term;

    @NonNull
    private String term_ascii;

    @NonNull
    @ColumnInfo(defaultValue = "0")
    private int synced;

    public long getId() { return this.id; }
    public void setId(final long id) { this.id = id; }
    public long getLang_ref() { return this.lang_ref; }
    public void setLang_ref(final long ref) { this.lang_ref = ref; }
    public String getTerm() { return this.term; }
    public void setTerm(final String term) { this.term = term; }
    public String getTerm_ascii() { return this.term_ascii; }
    public void setTerm_ascii(final String term_ascii) { this.term_ascii = term_ascii; }
    public int getSynced() { return this.synced; }
    public void setSynced(final int synced) { this.synced = synced; }

    public void debugDump() {
        Log.v(TAG, "Term dump:");
        Log.v(TAG, "id: " + id);
        Log.v(TAG, "term: " + term);
        Log.v(TAG, "term_ascii: " + term_ascii);
        Log.v(TAG, "lang_ref: " + lang_ref);
    }
}













