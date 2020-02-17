package br.com.pearls.DB;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class PearlsRepository {

    private LanguagesDao mLanguagesDao;
    private LiveData<List<Language>> mAllLanguages;

    PearlsRepository (Application application) {

        PearlsRoomDatabase db = PearlsRoomDatabase.getDatabase(application);
        mLanguagesDao = db.languagesDao();
        mAllLanguages = mLanguagesDao.getAllLanguages();
    }

    LiveData<List<Language>> getmAllLanguages() {
        return mAllLanguages;
    }

    void insertLanguage(Language language) {
        PearlsRoomDatabase.databaseWriteExecutor.execute(() -> {
            mLanguagesDao.insert(language);
        });
    }

}
