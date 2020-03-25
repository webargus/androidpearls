package br.com.pearls.DB;

import android.app.Application;

import androidx.annotation.NonNull;

import java.util.List;

import br.com.pearls.utils.GraphVertex;

public class GraphSearchVerticesRepository {

    private GraphSearchVerticesDao resultDao;

    public GraphSearchVerticesRepository(Application application) {
        PearlsRoomDatabase db = PearlsRoomDatabase.getDatabase(application);
        resultDao = db.graphSearchVerticesDao();
    }

    public List<GraphVertex> fetchVerticesForGraph(@NonNull long graph_ref) {
        return resultDao.fetchVerticesForGraph(graph_ref);
    }

}
