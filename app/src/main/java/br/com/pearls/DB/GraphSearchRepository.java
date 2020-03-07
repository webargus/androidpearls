package br.com.pearls.DB;

import android.app.Application;

import androidx.annotation.NonNull;

import java.util.List;

import br.com.pearls.utils.GraphSearchResult;

public class GraphSearchRepository {

    private GraphSearchDao resultDao;

    public GraphSearchRepository(Application application) {
        PearlsRoomDatabase db = PearlsRoomDatabase.getDatabase(application);
        resultDao = db.graphSearchResultDao();
    }

    public List<GraphSearchResult> loadGraphsForTermSearch(@NonNull final String term) {
        return resultDao.loadGraphsForTermSearch(term);
    }
}
