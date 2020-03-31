package br.com.pearls.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import br.com.pearls.DB.LanguagesRepository;

public class CsvWorkHandler extends Handler {

    private static final String TAG = "CsvWorkHandler";

    public static final int PEARLS_CSV_READ_FILE = 100;
    public static final int PEARLS_CSV_SAVE_TERMS = 101;

    private OnCsvWorkFinished workFinished;

    // interface to return thread results
    public interface OnCsvWorkFinished {
        void onCsvWorkFinished(int workId, CsvParams result);
    }

    public CsvWorkHandler(OnCsvWorkFinished workFinished) {
        this.workFinished = workFinished;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case PEARLS_CSV_READ_FILE:
                workFinished.onCsvWorkFinished(PEARLS_CSV_READ_FILE, processFile(msg.obj));
                break;
            case PEARLS_CSV_SAVE_TERMS:
                workFinished.onCsvWorkFinished(PEARLS_CSV_SAVE_TERMS, saveTerms(msg.obj));
                break;
        }
    }

    private CsvParams processFile(Object params) {
        return new CsvProcessInputFile((CsvParams) params).processInputStream();
    }

    private CsvParams saveTerms(Object params) {
        return null;
    }
}
