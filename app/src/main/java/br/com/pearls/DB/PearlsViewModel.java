package br.com.pearls.DB;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class PearlsViewModel extends AndroidViewModel {

    private PearlsRepository mRepository;

    private LiveData<List<Language>> mAllLanguages;

    public PearlsViewModel(Application application) {
        super(application);
        mRepository = new PearlsRepository(application);
        mAllLanguages = mRepository.getmAllLanguages();
    }

    public LiveData<List<Language>> getmAllLanguages() {
        return mAllLanguages;
    }

    public void insert(Language language) {
        mRepository.insertLanguage(language);
    }
}
