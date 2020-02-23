package br.com.pearls.DB;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class AreasRepository {

    private AreasDao mAreasDao;
    private LiveData<List<KnowledgeArea>> mAllAreas;

    AreasRepository(Application application) {

        PearlsRoomDatabase db = PearlsRoomDatabase.getDatabase(application);
        mAreasDao = db.areasDao();
        mAllAreas = mAreasDao.getAllAreas();

    }

    LiveData<List<KnowledgeArea>> getmAllAreas() {
        return mAllAreas;
    }

    void insertArea(KnowledgeArea area) {
        PearlsRoomDatabase.databaseWriteExecutor.execute( () -> {
            mAreasDao.insert(area);
        });
    }
}














