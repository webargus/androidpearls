package br.com.pearls.DB;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class LanguagesRepository {

    private LanguagesDao mLanguagesDao;
    private LiveData<List<Language>> mAllLanguages;

    LanguagesRepository(Application application) {

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
