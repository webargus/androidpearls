package br.com.pearls;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

public class CsvReaderActivity extends AppCompatActivity {

    public static final String TAG = CsvReaderActivity.class.getName();

    WebView wvCSV;
    private String tvHTML;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csv_reader);

        wvCSV = findViewById(R.id.csv_web_view);

        Intent intent = getIntent();
        String action = intent.getAction();
        Log.v(TAG, "################################ intent action: '" + action + "'");
        String scheme = intent.getScheme();

        if(action.equals(Intent.ACTION_SEND)) {
            String type = intent.getType();
            Log.v(TAG, "type: " + type);
            // type is set to application/octet-stream for .prl (former desktop pearls app format) files
            // and text/csv for .csv files; checked to be true for chrome download, gmail app and whatsapp.
            if(type != null && (type.equals("text/csv") || type.equals("application/octet-stream"))) {
                Bundle bundle = intent.getExtras();
                if(bundle != null && bundle.size() > 0 && bundle.containsKey(Intent.EXTRA_STREAM)) {
                    logDebug(bundle);               // debug
                    Uri uri;
                    try {
                        uri = (Uri) bundle.get(Intent.EXTRA_STREAM);
                    } catch (ClassCastException e) {
                        notifyUserOnException();
                        return;
                    }
                    InputStream inputStream= null;
                    try {
                        inputStream = getContentResolver().openInputStream(uri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        notifyUserOnException();
                        return;
                    }
                    InputStreamReader streamReader = new InputStreamReader(inputStream);
                    BufferedReader reader=new BufferedReader(streamReader);
                    String line;
                    tvHTML = getResources().getString(R.string.csv_table_init);
                    int lineId = 0;
                    while (true){
                        try {
                            if ((line=reader.readLine()) == null) {
                                break;
                            }
                            lineId ++;
                            processLineHTML(lineId, line);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    tvHTML += "</div>\n";
                    wvCSV.loadData(tvHTML, "text/html;charset=utf-8", "utf-8");
                }
            } else {
                Log.v(TAG, "--------------->-----------> No type string supplied");
                notifyUserOnException();
            }
        }
    }

    private void processLineHTML(int lineId, String line) {
        tvHTML += "<div class='tableRow'>\n";
        tvHTML += "<div class='tableCell'>" + lineId + "</div>";
        String[] fields = line.split(",");
        String field;
        for(int ix = 0; ix < fields.length; ix++) {
            field = fields[ix];
            tvHTML += "<div class='tableCell'>";
            tvHTML += field;
            tvHTML += "</div>\n";
        }
        tvHTML += "</div>\n";
    }

    private void notifyUserOnException() {
        Toast.makeText(this, "Couldn't read file content", Toast.LENGTH_SHORT);
    }

    private void logDebug(Bundle bundle) {
        Log.v(TAG, "\n-----------------------------------------------------");
        Set<String> keys = bundle.keySet();
        for(String key: keys) {
            Log.v(TAG, key + ": " + bundle.get(key));
        }
        Log.v(TAG, "------------------------------------------------------");
    }

}
