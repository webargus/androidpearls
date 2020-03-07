package br.com.pearls.DB;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GraphSearchResultDao {
    @Query("SELECT DISTINCT graph_ref, domain AS domainName, area AS areaName FROM vertices " +
           "LEFT JOIN terms ON term_ref = terms.id " +
           "LEFT JOIN languages ON lang_ref = languages.id " +
           "LEFT JOIN graphs ON graph_ref = graphs.id " +
           "LEFT JOIN domains ON domain_ref = domains.id " +
           "LEFT JOIN areas ON area_ref = areas.id " +
           "WHERE languages.active = 1 AND term_ascii LIKE :term")
    public List<GraphSearchResult> loadGraphsForTermSearch(@NonNull final String term);

    static class GraphSearchResult {
        public long graph_ref;
        public String domainName;
        public String areaName;
    }
}
