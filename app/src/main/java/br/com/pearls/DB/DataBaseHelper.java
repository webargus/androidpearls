package br.com.pearls.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final Pattern DIACRITICS_AND_FRIENDS
            = Pattern.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");

    //Constants for Database name, table name, and column names
    public static final String DB_NAME = "pearls";
    public static final String TABLE_NAME = "languages";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LANGUAGE = "language";
    public static final String COLUMN_NO_DIACRITICS = "no_diacritics";
    public static final String COLUMN_ACTIVE = "active";
    public static final String COLUMN_STATUS = "status";

    //database version
    private static final int DB_VERSION = 1;

    //Constructor
    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //creating the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME +" (" +
                      COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                      COLUMN_LANGUAGE + " VARCHAR, " +
                      COLUMN_NO_DIACRITICS + "VARCHAR, " +
                      COLUMN_ACTIVE + " TINYINT, " +
                      COLUMN_STATUS + " TINYINT);";
        db.execSQL(sql);
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
    public boolean addLanguage(String lang, int active,  int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_LANGUAGE, lang);
        contentValues.put(COLUMN_NO_DIACRITICS, stripDiacritics(lang));
        contentValues.put(COLUMN_ACTIVE, active);
        contentValues.put(COLUMN_STATUS, status);

        db.insert(TABLE_NAME, null, contentValues);
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
        db.update(TABLE_NAME, contentValues, COLUMN_ID + "=" + id, null);
        db.close();
        return true;
    }

    /*
     * Fetch all DB stored languages
     * */
    public Cursor getLanguages() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_LANGUAGE + " ASC;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    /*
     * Get languages out of sync for syncing with remote MySql DB
     * */
    public Cursor getLanguagesToSync() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + " = 0;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    private static String stripDiacritics(String str) {
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        str = DIACRITICS_AND_FRIENDS.matcher(str).replaceAll("");
        return str;
    }
}