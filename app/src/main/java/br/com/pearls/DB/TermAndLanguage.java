package br.com.pearls.DB;

import androidx.room.Embedded;
import androidx.room.Relation;

public class TermAndLanguage {
    @Embedded public Term term;
    @Relation(parentColumn = "lang_ref", entityColumn = "id")
    public Language language;
}
