package br.com.pearls.DB;

import android.app.Application;

public class TermRepository {

    private TermDao termDao;

    public TermRepository(Application application) {
        PearlsRoomDatabase db = PearlsRoomDatabase.getDatabase(application);
        termDao = db.termDao();
    }

    public long insert(Term term) { return termDao.insert(term); }

}
