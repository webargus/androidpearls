package br.com.pearls.DB;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class DomainsViewModel extends AndroidViewModel {

    private DomainRepository mRepository;

    private LiveData<List<Domain>> mAllDomains;

    public DomainsViewModel(Application application) {
        super(application);
        mRepository = new DomainRepository(application);
        mAllDomains = mRepository.getmAllDomains();
    }

    public LiveData<List<Domain>> getmAllDomains() { return mAllDomains; }

    public LiveData<Domain[]> getDomainByName(String domain, long area_ref) {
        return mRepository.getmDomainByName(domain, area_ref);
    }

    public void insert(Domain domain) { mRepository.insertDomain(domain); }
}
