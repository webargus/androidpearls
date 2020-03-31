package br.com.pearls.utils;

import android.app.Activity;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Process;

public class CsvWorkHeaderThread extends HandlerThread implements CsvWorkHandler.OnCsvWorkFinished{

    private CsvWorkHandler handler;

    public CsvWorkHeaderThread() {
        super("CsvWorkHeaderThread", Process.THREAD_PRIORITY_URGENT_DISPLAY);
    }

    @Override
    protected void onLooperPrepared() {
        handler = new CsvWorkHandler(this);
    }

    public CsvWorkHandler getHandler() { return handler; }

    @Override
    public void onCsvWorkFinished(int workId, CsvParams result) {

    }
}
