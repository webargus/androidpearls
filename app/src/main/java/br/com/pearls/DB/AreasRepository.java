package br.com.pearls.DB;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

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

    void insertArea(KnowledgeArea area) {
        PearlsRoomDatabase.databaseWriteExecutor.execute( () -> {
            mAreasDao.insert(area);
        });
    }

}














