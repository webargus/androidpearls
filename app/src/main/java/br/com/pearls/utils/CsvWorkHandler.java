package br.com.pearls.utils;

import android.os.Handler;
import android.os.Message;

public class CsvWorkHandler extends Handler {

    private static final String TAG = "CsvWorkHandler";

    public static final int PEARLS_CSV_READ_FILE = 100;
    public static final int PEARLS_CSV_SAVE_TERMS = 101;

    private static volatile boolean stopWork;

    private CsvThreadWorkIFace workIFace;

    // interface to return thread results
    public interface CsvThreadWorkIFace {
        void onCsvFileInputWorkFinished(CsvParams result);
        void onCsvTermSaveWorkFinished(boolean saved);
    }

    public CsvWorkHandler(CsvThreadWorkIFace workIFace) {
        this.workIFace = workIFace;
    }

    @Override
    public void handleMessage(Message msg) {
        stopWork = false;
        switch (msg.what) {
            case PEARLS_CSV_READ_FILE:
                workIFace.onCsvFileInputWorkFinished(processFile(msg.obj));
                break;
            case PEARLS_CSV_SAVE_TERMS:
                workIFace.onCsvTermSaveWorkFinished(saveTerms(msg.obj));
                break;
        }
    }

    public void stopProcesses() {
        stopWork = true;
    }

    public static boolean isStopWork() {
        return stopWork;
    }

    private CsvParams processFile(Object params) {
        return new CsvProcessInputFile((CsvParams) params).processInputStream();
    }

    private boolean saveTerms(Object params) {
        return new CsvSaveInputTerms((CsvParams) params).saveTerms();
    }
}
