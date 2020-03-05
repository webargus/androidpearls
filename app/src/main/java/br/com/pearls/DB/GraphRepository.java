package br.com.pearls.DB;

import android.app.Application;

public class GraphRepository {

    private GraphDao graphDao;

    public GraphRepository(Application application) {
        PearlsRoomDatabase db = PearlsRoomDatabase.getDatabase(application);
        graphDao = db.graphDao();
    }

    public long insert(Graph graph) { return graphDao.insert(graph); }
}
