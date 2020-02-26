package br.com.pearls.DB;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class AreasViewModel extends AndroidViewModel {

    private AreasRepository mRepository;

    private LiveData<List<KnowledgeArea>> mAllAreas;
    private LiveData<List<AreasWithDomains>> mAreasWithDomains;

    public AreasViewModel(Application application) {
        super(application);
        mRepository = new AreasRepository(application);
        mAllAreas = mRepository.getmAllAreas();
        mAreasWithDomains = mRepository.getmAreasWithDomains();
    }

    public LiveData<List<KnowledgeArea>> getmAllAreas() {
        return mAllAreas;
    }

    public LiveData<List<AreasWithDomains>> getmAreasWithDomains() { return mAreasWithDomains; }

    public LiveData<KnowledgeArea[]> getAreaByName(String area) {
        return mRepository.getmAreaByName(area);
    }

    public void insert(KnowledgeArea area) {
        mRepository.insertArea(area);
    }

}














