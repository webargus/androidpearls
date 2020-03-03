package br.com.pearls.DB;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class LanguagesRepository {

    private LanguagesDao mLanguagesDao;
    private LiveData<List<Language>> mAllLanguages;
    private LiveData<List<Language>> mActiveLanguages;

    LanguagesRepository(Application application) {

        PearlsRoomDatabase db = PearlsRoomDatabase.getDatabase(application);
        mLanguagesDao = db.languagesDao();
        mAllLanguages = mLanguagesDao.getAllLanguages();
        mActiveLanguages = mLanguagesDao.getActiveLanguages(1);
    }

    LiveData<List<Language>> getmAllLanguages() {
        return mAllLanguages;
    }

    LiveData<List<Language>> getmActiveLanguages() { return mActiveLanguages; }

    void insertLanguage(Language language) {
        PearlsRoomDatabase.databaseWriteExecutor.execute(() -> {
            mLanguagesDao.insert(language);
        });
    }

}
