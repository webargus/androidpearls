package br.com.pearls.utils;

import android.app.Activity;
import android.net.Uri;

import java.util.List;

import br.com.pearls.DB.Language;

public class CsvParams {
    public String quotes, separator, html, initTable;
    public List<Language> languages;
    public Activity activity;
    public Uri streamUri;
    public List<List<String >> rows;
    public long domainId;
}
