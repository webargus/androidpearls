package br.com.pearls.DB;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import br.com.pearls.utils.SearchVertex;

@Dao
public interface GraphSearchVerticesDao {
    @Query("SELECT vertices.id AS vertex_id, graph_ref, term_ref, user_rank, vertex_context, term, lang_ref, language " +
           "FROM vertices LEFT JOIN terms ON term_ref = terms.id " +
           "LEFT JOIN languages ON lang_ref = languages.id " +
           "WHERE languages.active = 1 AND graph_ref = :graph_ref")
    List<SearchVertex> fetchVerticesForGraph(@NonNull long graph_ref);

}

