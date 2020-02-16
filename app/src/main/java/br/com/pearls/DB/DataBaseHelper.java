package br.com.pearls.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import br.com.pearls.R;

public class DataBaseHelper extends SQLiteOpenHelper {

    //Constants for Database name, table name, and column names
    public static final String DB_NAME = "pearls";
    public static final String TABLE_LANGUAGES = "languages";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_COUNTRY_CODE = "country_code";
    public static final String COLUMN_LANGUAGE = "language";
    public static final String COLUMN_ACTIVE = "active";
    public static final String COLUMN_STATUS = "status";

    //database version
    private static final int DB_VERSION = 1;
    private Context cntxt;

    //Constructor
    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        cntxt = context;
    }

    //creating the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_LANGUAGES +" (" +
                      COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                      COLUMN_COUNTRY_CODE + " TEXT," +
                      COLUMN_LANGUAGE + " VARCHAR, " +
                      COLUMN_ACTIVE + " TINYINT, " +
                      COLUMN_STATUS + " TINYINT);";
        db.execSQL(sql);
        setInitialLanguages();
    }

    //upgrading the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS languages";
        db.execSQL(sql);
        onCreate(db);
    }

    /*
     * This method takes three arguments
     * one is the language that is to be saved
     * second one is the active flag
     * third is the status:
     * 0 means the language has already been synced with the server
     * 1 means the language has to be synced with the server yet
     * */
    public boolean addLanguage(String country, String lang, int active,  int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_COUNTRY_CODE, country);
        contentValues.put(COLUMN_LANGUAGE, lang);
        contentValues.put(COLUMN_ACTIVE, active);
        contentValues.put(COLUMN_STATUS, status);

        db.insert(TABLE_LANGUAGES, null, contentValues);
        db.close();
        return true;
    }

    /*
     * This method takes 2 arguments
     * the id of the language for which
     * we have to update the sync status
     * and the new status
     * */
    public boolean updateLanguageStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_STATUS, status);
        db.update(TABLE_LANGUAGES, contentValues, COLUMN_ID + "=" + id, null);
        db.close();
        return true;
    }

    /*
     * Fetch all DB stored languages
     * */
    public Cursor getLanguages(String where, String[] selectionArgs, String orderBy) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_LANGUAGES, null, where, selectionArgs, null, null, orderBy);
        return c;
    }

    /*
     * Get languages out of sync for syncing with remote MySql DB
     * */
    public Cursor getLanguagesToSync() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_LANGUAGES + " WHERE " + COLUMN_STATUS + " = 0;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    // TODO: download flag imgs files + langs from server; change table 'languages'
    // TODO: and include blob column to save img bitmap; stop using R.drawable for flags
    // TODO: and r.array for language country codes and names; do corresponding changes in
    // TODO: LanguagesListAdapter class;
    private void setInitialLanguages() {
        String[] languages = cntxt.getResources().getStringArray(R.array.language_codes);
        String[] code_lang;
        for(Integer ix = 0; ix < languages.length; ix++) {
            code_lang = languages[ix].split(";");
            addLanguage(code_lang[0], code_lang[1], 1, 1);
        }

    }

}