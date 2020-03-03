package br.com.pearls.DB;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "terms")
public class Term {

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
    public long getLangRef() { return this.lang_ref; }
    public void setLangRef(final long ref) { this.lang_ref = ref; }
    public String getTerm() { return this.term; }
    public void setTerm(final String term) { this.term = term; }
    public String getTermAscii() { return this.term_ascii; }
    public void setTermAscii(final String term_ascii) { this.term_ascii = term_ascii; }
    public int getSynced() { return this.synced; }
    public void setSynced(final int synced) { this.synced = synced; }
}












