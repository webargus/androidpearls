package br.com.pearls.DB;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import br.com.pearls.utils.GraphSearchResult;

@Dao
public interface GraphSearchDao {
    /* IMPORTANT! query must return results ordered by graph_ref for search script to work
        properly; see GraphSearchUtil.java
     */
    @Query("SELECT graph_ref, domain_ref, area_ref, domain AS domainName, area AS areaName, term_ascii " +
           "FROM vertices " +
           "LEFT JOIN terms ON term_ref = terms.id " +
           "LEFT JOIN languages ON lang_ref = languages.id " +
           "LEFT JOIN graphs ON graph_ref = graphs.id " +
           "LEFT JOIN domains ON domain_ref = domains.id " +
           "LEFT JOIN areas ON area_ref = areas.id " +
           "WHERE languages.active = 1 AND term_ascii LIKE :term_ascii " +
           "ORDER BY graph_ref ASC")
    List<GraphSearchResult> loadGraphsForTermSearch(@NonNull final String term_ascii);

}
