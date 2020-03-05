package br.com.pearls.DB;

import android.app.Application;

public class VertexRepository {

    private VertexDao vertexDao;

    public VertexRepository(Application application) {
        PearlsRoomDatabase db = PearlsRoomDatabase.getDatabase(application);
        vertexDao = db.vertexDao();
    }

    public long insert(Vertex vertex) { return vertexDao.insert(vertex); }
}
