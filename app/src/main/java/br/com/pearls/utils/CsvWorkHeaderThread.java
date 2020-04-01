package br.com.pearls.utils;

import android.os.HandlerThread;
import android.os.Process;

public class CsvWorkHeaderThread extends HandlerThread{

    private CsvWorkHandler handler;
    private CsvWorkHandler.CsvThreadWorkIFace workFinished;

    public CsvWorkHeaderThread(CsvWorkHandler.CsvThreadWorkIFace workFinished) {
        super("CsvWorkHeaderThread", Process.THREAD_PRIORITY_DEFAULT);
        this.workFinished = workFinished;
    }

    @Override
    protected void onLooperPrepared() {
        handler = new CsvWorkHandler(workFinished);
    }

    public CsvWorkHandler getHandler() { return handler; }
}
