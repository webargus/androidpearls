package br.com.pearls;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CsvReaderActivity extends AppCompatActivity {

    public static final String TAG = CsvReaderActivity.class.getName();

    private RadioGroup separatorGroup, quotesGroup;
    private RadioButton radioComma, radioTab, radioDoubleQuotes, radioNoQuotes;
    private EditText sepOtherEdit;
    private Button sepOtherBtn;
    private WebView webView;
    private String wvHTML, separator, quotes, streamType;
    private Uri streamUri;
    private Pattern patternRepeatedQuotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csv_reader);

        webView = findViewById(R.id.csv_web_view);

        separatorGroup = findViewById(R.id.separator_radio_group);
        radioComma = findViewById(R.id.radio_button_comma);
        radioTab = findViewById(R.id.radio_button_tab);
        quotesGroup = findViewById(R.id.quote_radio_group);
        radioDoubleQuotes = findViewById(R.id.radio_double_quotes);
        radioNoQuotes = findViewById(R.id.radio_no_quotes);
        sepOtherEdit = findViewById(R.id.edit_other_separator);
        sepOtherBtn = findViewById(R.id.btn_separator_other);

        getStreamUri();

        sepOtherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                separator = sepOtherEdit.getText().toString().trim();
                processStreamInput();
            }
        });

        separatorGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                sepOtherBtn.setEnabled(false);
                sepOtherEdit.setEnabled(false);
                switch (checkedId) {
                    case R.id.radio_button_comma:
                        separator = ",";
                        processStreamInput();
                        break;
                    case R.id.radio_button_semicolon:
                        separator = ";";
                        processStreamInput();
                        break;
                    case R.id.radio_button_tab:
                        separator = "\t";
                        processStreamInput();
                        break;
                    case R.id.radio_button_other:
                        sepOtherBtn.setEnabled(true);
                        sepOtherEdit.setEnabled(true);
                        break;
                }
            }
        });

        // set quotes radio btns to their default values before setting their change listener;
        // this is to prevent stream input processing from running twice needlessly;
        // setting separator radios triggers call to stream input processing processStreamInput()
        if(streamType.equals("text/csv")) {
            quotes = "\"";
            radioDoubleQuotes.setChecked(true);
            radioComma.setChecked(true);
        } else {
            quotes = "";
            radioNoQuotes.setChecked(true);
            radioTab.setChecked(true);
        }

        quotesGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_double_quotes:
                        quotes = "\"";
                        break;
                    case R.id.radio_single_quotes:
                        quotes = "'";
                        break;
                    case R.id.radio_no_quotes:
                        quotes = "";
                        break;
                }
                processStreamInput();
            }
        });
    }

    // get stream type and stream Uri if we're apt to process this stream
    private void getStreamUri() {
        Intent intent = getIntent();
        String action = intent.getAction();
        Log.v(TAG, "################################ intent action: '" + action + "'");

        if(action.equals(Intent.ACTION_SEND)) {
            streamType= intent.getType();
            Log.v(TAG, "type: " + streamType);
            // type is set to application/octet-stream for .prl (former desktop pearls app format) files
            // and text/csv for .csv files; checked to be true for chrome download, gmail app and whatsapp.
            if(streamType != null && (streamType.equals("text/csv") || streamType.equals("application/octet-stream"))) {
                Bundle bundle = intent.getExtras();
                if(bundle != null && bundle.size() > 0 && bundle.containsKey(Intent.EXTRA_STREAM)) {
                    logDebug(bundle);               // debug
                    try {
                        streamUri = (Uri) bundle.get(Intent.EXTRA_STREAM);
                    } catch (ClassCastException e) {
                        notifyUserOnException();
                    }
                }
            } else {
                Log.v(TAG, "--------------->-----------> No type string supplied");
                notifyUserOnException();
            }
        } else {
            notifyUserOnException();
        }
    }

    class processStreamInputAsync extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(Void... voids) {
            return null;
        }
    }
    private void processStreamInput() {
        InputStream inputStream= null;
        try {
            inputStream = getContentResolver().openInputStream(streamUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            notifyUserOnException();
            return;
        }
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        BufferedReader reader=new BufferedReader(streamReader);
        String line;
        webView.loadUrl("about:blank");
        wvHTML = getResources().getString(R.string.csv_table_init);
        int lineId = 0;
        patternRepeatedQuotes = Pattern.compile(quotes + quotes);
        while (true) {
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
        wvHTML += "</div>\n";
        webView.loadData(wvHTML, "text/html;charset=utf-8", "utf-8");
    }

    private void processLineHTML(int lineId, String line) {
        wvHTML += "<div class='tableRow'>\n";
        wvHTML += "<div class='tableCell'>" + lineId + "</div>";
        String[] fields = line.split(quotes+separator+quotes);
        for(String field: fields) {
            wvHTML += "<div class='tableCell'>";
            // replace repeated quotes by one quote only ("" -> ", '' -> ', ** -> *, ...)
            if(quotes.length() > 0) {
                Matcher matcher = patternRepeatedQuotes.matcher(field);
                field = matcher.replaceAll(quotes);
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
            wvHTML += field;
            wvHTML += "</div>\n";
        }
        wvHTML += "</div>\n";
    }

    private void notifyUserOnException() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Oops!");
        alertDialog.setMessage("Couldn't open or read from file");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        alertDialog.show();
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
