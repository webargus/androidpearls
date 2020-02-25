package br.com.pearls.DB;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class DomainsViewModel extends AndroidViewModel {

    private DomainsRepository mRepository;

    private LiveData<List<Domain>> mAllDomains;

    public DomainsViewModel(Application application) {
        super(application);
        mRepository = new DomainsRepository(application);
        mAllDomains = mRepository.getmAllDomains();
    }

    public LiveData<List<Domain>> getmAllDomains() { return mAllDomains; }

    public void insert(Domain domain) { mRepository.insertDomain(domain); }
}
