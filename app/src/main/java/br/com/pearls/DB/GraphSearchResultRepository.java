package br.com.pearls.DB;

import android.app.Application;

import androidx.annotation.NonNull;

import java.util.List;

public class GraphSearchResultRepository {

    private GraphSearchResultDao resultDao;

    public GraphSearchResultRepository(Application application) {
        PearlsRoomDatabase db = PearlsRoomDatabase.getDatabase(application);
        resultDao = db.graphSearchResultDao();
    }

    public List<GraphSearchResultDao.GraphSearchResult> loadGraphsForTermSearch(@NonNull final String term) {
        return resultDao.loadGraphsForTermSearch(term);
    }
}
