package br.com.pearls.DB;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class AreasRepository {

    private AreasDao mAreasDao;
    private LiveData<List<KnowledgeArea>> mAllAreas;
    private LiveData<List<AreasWithDomains>> mAreasWithDomains;

    AreasRepository(Application application) {

        PearlsRoomDatabase db = PearlsRoomDatabase.getDatabase(application);
        mAreasDao = db.areasDao();
        mAllAreas = mAreasDao.getAllAreas();
        mAreasWithDomains = mAreasDao.getAreasWithDomains();
    }

    LiveData<List<KnowledgeArea>> getmAllAreas() {
        return mAllAreas;
    }

    LiveData<List<AreasWithDomains>> getmAreasWithDomains() { return mAreasWithDomains; }

    LiveData<KnowledgeArea[]> getmAreaByName(String area) {
        return mAreasDao.getAreaByName(area);
    }

    long insert(KnowledgeArea area) {
        AtomicLong insertId = new AtomicLong();
        PearlsRoomDatabase.databaseWriteExecutor.execute( () -> {
            insertId.set(mAreasDao.insert(area));
        });
        return insertId.get();
    }

    void update(KnowledgeArea area) {
        PearlsRoomDatabase.databaseWriteExecutor.execute( () -> {
            mAreasDao.update(area);
        });
    }
}














