package br.com.pearls;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

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

import br.com.pearls.DB.Domain;
import br.com.pearls.DB.KnowledgeArea;
import br.com.pearls.ui.main.AreasDomainsTabFragment;
import br.com.pearls.ui.main.CsvReaderLanguagesSetDialog;

public class CsvReaderActivity extends AppCompatActivity
        implements AreasDomainsTabFragment.OnDomainSelectedListener {

    public static final String TAG = CsvReaderActivity.class.getName();

    private RadioGroup separatorGroup, quotesGroup;
    private RadioButton radioComma, radioTab, radioOther, radioDoubleQuotes, radioNoQuotes;
    private EditText sepOtherEdit;
    private Button sepOtherBtn, setLanguagesBtn;
    private WebView webView;
    private String separator, quotes, streamType;
    private Uri streamUri;

    AreasDomainsTabFragment areasDomainsTabFragment;

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
        radioOther = findViewById(R.id.radio_button_other);
        sepOtherEdit = findViewById(R.id.edit_other_separator);
        sepOtherBtn = findViewById(R.id.btn_separator_other);
        setLanguagesBtn = findViewById(R.id.btn_csv_set_languages);

        try {
            getStreamUri();
        } catch (ClassCastException e) {
            notifyUserOnException();
            return;
        }

        sepOtherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                separator = sepOtherEdit.getText().toString().trim();
                sepOtherEdit.setEnabled(false);
                sepOtherBtn.setEnabled(false);
                new ProcessStreamInputAsync().execute();
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
                        new ProcessStreamInputAsync().execute();
                        break;
                    case R.id.radio_button_semicolon:
                        separator = ";";
                        new ProcessStreamInputAsync().execute();
                        break;
                    case R.id.radio_button_tab:
                        separator = "\t";
                        new ProcessStreamInputAsync().execute();
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
        // setting separator radios triggers call to async stream input processing thread
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
                new ProcessStreamInputAsync().execute();
            }
        });

        areasDomainsTabFragment = new AreasDomainsTabFragment();

        setLanguagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCsvLanguagesDialog();
            }
        });
    }

    private void openCsvLanguagesDialog() {
//        CsvReaderLanguagesSetDialog dialog = new CsvReaderLanguagesSetDialog();
//        dialog.show(getSupportFragmentManager(), "CSV Languages Set Dialog");
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, areasDomainsTabFragment, "areas domains")
                .addToBackStack(null)
                .commit();
    }

    // get stream type and stream Uri if we're apt to process this stream
    private void getStreamUri() throws ClassCastException {
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
                        throw new ClassCastException("Failed to get stream uri");
                    }
                }
            } else {
                Log.v(TAG, "--------------->-----------> No type string supplied");
                throw new ClassCastException("No type string supplied");
            }
        } else {
            throw new ClassCastException("Unhandled intent action");
        }
    }

    @Override
    public void setSelectedDomain(KnowledgeArea area, Domain domain) {

    }

    class ProcessStreamInputAsync extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPostExecute(String html) {
            webView.loadUrl("about:blank");     // clear web view
            webView.loadData(html, "text/html;charset=utf-8", "utf-8");
            if(radioOther.isChecked()) {
                sepOtherEdit.setEnabled(true);
                sepOtherBtn.setEnabled(true);
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                return processStreamInput();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            notifyUserOnException();
        }

        private String processStreamInput() throws FileNotFoundException {
            InputStream inputStream;
            inputStream = getContentResolver().openInputStream(streamUri);

            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader reader=new BufferedReader(streamReader);
            String line;
            String html = getResources().getString(R.string.csv_table_init);
            int lineId = 0;
            while (true) {
                try {
                    if ((line=reader.readLine()) == null) {
                        break;
                    }
                    lineId ++;
                    html += processLineHTML(lineId, line);
                } catch (IOException e) {
                    e.printStackTrace();
                    cancel(true);
                }
            }
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            html += "</div>\n";
            return html;
        }

        private String processLineHTML(int lineId, String line) {
            String html = "<div class='tableRow'>\n";
            html += "<div class='tableCell'>" + lineId + "</div>";
            String[] fields = line.split(quotes+separator+quotes, 6);
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
                html += field;
                html += "</div>\n";
            }
            html += "</div>\n";
            return html;
        }

    }

    private void notifyUserOnException() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Oops!");
        alertDialog.setMessage("Perls couldn't handle the medium you sent and will abort after you dismiss this dialog");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Abort",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        CsvReaderActivity.this.finish();
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
