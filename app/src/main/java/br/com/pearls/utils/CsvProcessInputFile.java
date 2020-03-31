package br.com.pearls.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.pearls.DB.LanguagesRepository;

public class CsvProcessInputFile {

    private static final String TAG = "CsvProcessInputFile";

    private CsvParams params;
    private int maxColumns;

    public CsvProcessInputFile(CsvParams params) {
        this.params = params;
    }

    public CsvParams processInputStream() {
        // Set CsvParams member languages with db languages, which we'll need when saving terms;
        // Wrap repository call with try catch for we never know if Activity still running;
        try {
            LanguagesRepository repository =
                new LanguagesRepository(Objects.requireNonNull(params.activity).getApplication());
            params.languages = repository.getAllLanguages();
        } catch (ClassCastException e) {
            Log.d(TAG, "processInputStream: failed fetching languages from DB: " + e.getMessage());
            return null;
        }

        // let's try to read from file
        InputStream inputStream;
        try {
            inputStream = Objects.requireNonNull(params.activity.getApplicationContext())
                    .getContentResolver().openInputStream(params.streamUri);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "processInputStream: couldn't open input stream: " + e.getMessage());
            return null;
        } catch (ClassCastException e) {
            Log.d(TAG, "processInputStream: failed creating input stream: " + e.getMessage());
            return null;
        }

        InputStreamReader streamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(streamReader);
        String line;
        int lineId = 0;     // the line no. to be shown in the left-most column of html table
        maxColumns = 0;
        params.html = "";
        params.rows = new ArrayList<>();
        while (true) {
            try {
                if ((line=reader.readLine()) == null) {
                    break;
                }
                lineId ++;
                params.html += processLineHTML(lineId, line);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        params.html += "</div>\n";
        params.html = params.initTable + languagesRow() + params.html;
        return params;
    }

    private String processLineHTML(int lineId, String line) {
        String html = "<div class='tableRow'>\n";
        html += "<div class='tableCell'>" + lineId + "</div>";
        String quotes = params.quotes;
        String sep = quotes+params.separator+quotes;
        String[] fields = line.split(sep, params.languages.size());
        // save the greatest no. of columns found so far
        if(fields.length > maxColumns) {
            maxColumns = fields.length;
        }
        // create row cells
        List<String> columns = new ArrayList<>();
        for(String field: fields) {
            html += "<div class='tableCell'>";
            // replace repeated quotes by one quote only ("" -> ", '' -> ', ** -> *, ...)
            // and remove residual quotes trailing and starting fields
            if(quotes.length() > 0) {
                field = field.replaceAll(quotes+quotes, quotes);
                if (field.length() > 0) {
                    if (field.indexOf(quotes) == 0) {
                        field = field.substring(1);
                    }
                    int length = field.length();
                    if (length > 0) {
                        if (field.lastIndexOf(quotes) == length - 1) {
                            field = field.substring(0, length - 1);
                        }
                    }
                }
            }
            columns.add(field);
            html += field;
            html += "</div>\n";
        }
        params.rows.add(columns);
        html += "</div>\n";
        return html;
    }

    private String languagesRow() {
        String row = "<div class='tableRow'>";
        row += "<div class='tableCell'>Row</div>";
        for(int ix = 0; ix < maxColumns; ix++) {
            row += "<div class='tableCell'>";
            row += params.languages.get(ix).getLanguage();
            row += "</div>";
        }
        row += "</div>";
        return row;
    }
}
