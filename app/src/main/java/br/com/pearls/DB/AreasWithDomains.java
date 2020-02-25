package br.com.pearls.DB;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class AreasWithDomains {

    @Embedded public KnowledgeArea area;

    @Relation(
        parentColumn = "id",
        entityColumn = "area_ref"
    )
    public List<Domain> domains;
}
