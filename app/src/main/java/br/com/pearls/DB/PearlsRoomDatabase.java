package br.com.pearls.DB;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Language.class, KnowledgeArea.class, Domain.class}, version = 3, exportSchema = false)
public abstract class PearlsRoomDatabase extends RoomDatabase {

    public abstract LanguagesDao languagesDao();
    public abstract AreasDao areasDao();
    public abstract DomainsDao domainsDao();

    private static volatile PearlsRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                LanguagesDao dao = INSTANCE.languagesDao();
                Language language = new Language();
                language.setLanguage("English");
                dao.insert(language);
                language = new Language();
                language.setLanguage("Portuguese");
                dao.insert(language);
                language = new Language();
                language.setLanguage("Spanish");
                dao.insert(language);
            });
        }

    };

    static PearlsRoomDatabase getDatabase(final Context context) {
        if(INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                       PearlsRoomDatabase.class,
                      "pearls").fallbackToDestructiveMigration()
                                    .addCallback(sRoomDatabaseCallback)
                                    .build();
        }
        return INSTANCE;
    }

}
