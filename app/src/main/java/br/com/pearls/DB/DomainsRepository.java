package br.com.pearls.DB;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class DomainsRepository {

    private DomainsDao mDomainsDao;
    private LiveData<List<Domain>> mAllDomains;

    DomainsRepository(Application application) {

        PearlsRoomDatabase db = PearlsRoomDatabase.getDatabase(application);
        mDomainsDao = db.domainsDao();
        mAllDomains = mDomainsDao.getAllDomains();
    }

    LiveData<List<Domain>> getmAllDomains() { return mAllDomains; }

    LiveData<Domain[]> getmDomainByName(String domain, long area_ref) {
        return  mDomainsDao.getDomainByName(domain, area_ref);
    }

    void insertDomain(Domain domain) {
        PearlsRoomDatabase.databaseWriteExecutor.execute(() -> {
            mDomainsDao.insert(domain);
        });
    }
}











