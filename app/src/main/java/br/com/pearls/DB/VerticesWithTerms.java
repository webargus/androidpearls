package br.com.pearls.DB;

import androidx.room.Embedded;
import androidx.room.Relation;

public class VerticesWithTerms {
    @Relation(entity        = TermAndLanguage.class,
              parentColumn  = "id",
              entityColumn  = "term_ref")
    public Vertex vertex;
}
