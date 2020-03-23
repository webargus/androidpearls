package br.com.pearls.ui.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import br.com.pearls.DB.Language;
import br.com.pearls.DB.LanguagesRepository;
import br.com.pearls.R;

public class CsvReaderMediaFragment extends Fragment {

    public static final String TAG = CsvReaderMediaFragment.class.getName();

    private RadioGroup separatorGroup, quotesGroup;
    private RadioButton radioComma, radioTab, radioOther, radioDoubleQuotes, radioNoQuotes;
    private EditText sepOtherEdit;
    private Button sepOtherBtn;
    private WebView webView;
    private String separator, quotes, streamType;
    private Uri streamUri;

    private CsvMediaParentDataIFace parentIFace;

    public interface CsvMediaParentDataIFace {
        void onCsvMediaFragmentException();
        Intent csvMediaFragmentGetIntent();
    }

    public CsvReaderMediaFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.csv_reader_media_fragment, container, false);
        webView = view.findViewById(R.id.csv_web_view);

        separatorGroup = view.findViewById(R.id.separator_radio_group);
        radioComma = view.findViewById(R.id.radio_button_comma);
        radioTab = view.findViewById(R.id.radio_button_tab);
        quotesGroup = view.findViewById(R.id.quote_radio_group);
        radioDoubleQuotes = view.findViewById(R.id.radio_double_quotes);
        radioNoQuotes = view.findViewById(R.id.radio_no_quotes);
        radioOther = view.findViewById(R.id.radio_button_other);
        sepOtherEdit = view.findViewById(R.id.edit_other_separator);
        sepOtherBtn = view.findViewById(R.id.btn_separator_other);

        try {
            getStreamUri();
        } catch (RuntimeException e) {
            parentIFace.onCsvMediaFragmentException();
            return null;
        }

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

        // set quotes radio buttons to their default values before setting their change listener;
        // this prevents listeners from calling async thread to process medium input
        // twice (setting separator radio buttons below already calls async thread)
        if(streamType.equals("text/csv")) {
            quotes = "\"";
            radioDoubleQuotes.setChecked(true);
            radioComma.setChecked(true);
        } else {
            quotes = "";
            radioNoQuotes.setChecked(true);
            radioTab.setChecked(true);
        }

        // quotes radio buttons
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

        sepOtherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                separator = sepOtherEdit.getText().toString().trim();
                sepOtherEdit.setEnabled(false);
                sepOtherBtn.setEnabled(false);
                new ProcessStreamInputAsync().execute();
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            parentIFace = (CsvMediaParentDataIFace) context;
        } catch (ClassCastException e) {
            throw new RuntimeException("You must implement the CsvMediaParentDataIFace...");
        }
    }

    // get stream type and stream Uri if we're apt to process this stream
    private void getStreamUri() throws ClassCastException {
        // get intent from parent activity through i-face
        Intent intent = parentIFace.csvMediaFragmentGetIntent();
        String action = intent.getAction();
        Log.v(TAG, "################################ intent action: '" + action + "'");

        if (action.equals(Intent.ACTION_SEND)) {
            // save stream type to local member
            streamType = intent.getType();
            Log.v(TAG, "type: " + streamType);          // debug
            // type is set to application/octet-stream for .prl (former desktop pearls app format) files
            // and text/csv for .csv files; checked to be true for chrome download, gmail app and whatsapp.
            if (streamType != null && (streamType.equals("text/csv") || streamType.equals("application/octet-stream"))) {
                Bundle bundle = intent.getExtras();
                if (bundle != null && bundle.size() > 0 && bundle.containsKey(Intent.EXTRA_STREAM)) {
                    logDebug(bundle);               // debug
                    try {
                        streamUri = (Uri) bundle.get(Intent.EXTRA_STREAM);
                    } catch (ClassCastException e) {
                        throw new RuntimeException("Failed to get stream uri");
                    }
                }
            } else {        // abort if sender didn't declare their media type
                Log.v(TAG, "--------------->-----------> No type string supplied");
                throw new RuntimeException("No type string supplied");
            }
        } else {    // abort if script does not handle action
            throw new RuntimeException("Unhandled intent action");
        }
    }

    class ProcessStreamInputAsync extends AsyncTask<Void, Void, String> {

        private int maxColumns;
        private List<Language> languages;

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
            parentIFace.onCsvMediaFragmentException();
        }

        private String processStreamInput() throws FileNotFoundException {
            if(languages == null) {
                fetchLanguages();
            }
            InputStream inputStream;
            inputStream = getContext().getContentResolver().openInputStream(streamUri);

            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader reader=new BufferedReader(streamReader);
            String line;
            String html = "";
            int lineId = 0;
            maxColumns = 0;
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
            html = getResources().getString(R.string.csv_table_init) + languagesRow() + html;
            return html;
        }

        private String processLineHTML(int lineId, String line) {
            String html = "<div class='tableRow'>\n";
            html += "<div class='tableCell'>" + lineId + "</div>";
            String[] fields = line.split(quotes+separator+quotes, languages.size());
            // save the greatest no. of columns found so far
            if(fields.length > maxColumns) {
                maxColumns = fields.length;
            }
            // create row cells
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

        private void fetchLanguages() {
            LanguagesRepository repository = new LanguagesRepository(getActivity().getApplication());
            languages = repository.getAllLanguages();
        }

        private String languagesRow() {
            String row = "<div class='tableRow'>";
            row += "<div class='tableCell'>Row</div>";
            for(int ix = 0; ix < maxColumns; ix++) {
                row += "<div class='tableCell'>";
                row += languages.get(ix).getLanguage();
                row += "</div>";
            }
            row += "</div>";
            return row;
        }

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



