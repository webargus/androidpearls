package br.com.pearls.DB;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Language.class, KnowledgeArea.class, Domain.class, Term.class, Graph.class, Vertex.class},
          version = 1, exportSchema = false)
public abstract class PearlsRoomDatabase extends RoomDatabase {

    public abstract LanguagesDao languagesDao();
    public abstract AreasDao areasDao();
    public abstract DomainsDao domainsDao();
    public abstract TermDao termDao();
    public abstract GraphDao graphDao();
    public abstract GraphSearchDao graphSearchResultDao();
    public abstract VertexDao vertexDao();
    public abstract GraphSearchVerticesDao graphSearchVerticesDao();

    private static volatile PearlsRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                LanguagesDao dao = INSTANCE.languagesDao();
                Language language = new Language();
                language.setLanguage("English");
                language.setStatus(1);
                language.setActive(1);
                dao.insert(language);
                language = new Language();
                language.setLanguage("Portuguese");
                language.setActive(1);
                language.setStatus(2);
                dao.insert(language);
                language = new Language();
                language.setLanguage("Spanish");
                language.setActive(1);
                language.setStatus(3);
                dao.insert(language);
                language.setLanguage("French");
                language.setActive(1);
                language.setStatus(4);
                dao.insert(language);
                language.setLanguage("German");
                language.setActive(1);
                language.setStatus(5);
                dao.insert(language);
                language.setLanguage("Italian");
                language.setActive(1);
                language.setStatus(6);
                dao.insert(language);
            });
        }

    };

    public static PearlsRoomDatabase getDatabase(final Context context) {
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
