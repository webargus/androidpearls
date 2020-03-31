package br.com.pearls.utils;

import android.app.Activity;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Process;

public class CsvWorkHeaderThread extends HandlerThread{

    private CsvWorkHandler handler;
    private CsvWorkHandler.OnCsvWorkFinished workFinished;

    public CsvWorkHeaderThread(CsvWorkHandler.OnCsvWorkFinished workFinished) {
        super("CsvWorkHeaderThread", Process.THREAD_PRIORITY_URGENT_DISPLAY);
        this.workFinished = workFinished;
    }

    @Override
    protected void onLooperPrepared() {
        handler = new CsvWorkHandler(workFinished);
    }

    public CsvWorkHandler getHandler() { return handler; }
}
