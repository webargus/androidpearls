package br.com.pearls.ui.main;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.pearls.DB.Domain;
import br.com.pearls.DB.Language;
import br.com.pearls.DB.LanguagesRepository;
import br.com.pearls.R;
import br.com.pearls.utils.GraphUtil;
import br.com.pearls.utils.GraphVertex;
import br.com.pearls.utils.InputFilterIntRange;

public class CsvReaderMediaFragment extends Fragment {

    public static final String TAG = CsvReaderMediaFragment.class.getName();

    private RadioButton radioComma, radioTab, radioDoubleQuotes, radioNoQuotes, radioOther;
    private RadioGroup separatorGroup, quotesGroup;
    private EditText sepOtherEdit, initLineEdit, endLineEdit;
    private Button sepOtherBtn, saveButton;
    private WebView webView;
    private Uri streamUri;

    private CsvMediaParentDataIFace parentIFace;
    private PostProcessAsyncIFace postProcessIFace;
    private AsyncParams postProcessParams;

    public interface CsvMediaParentDataIFace {
        void onCsvMediaFragmentException();
        Uri getStreamUri();
        String getStreamType();
        Domain csvMediaFragmentGetDomain();
        void onCsvMediaFragmentFinished();
    }

    interface PostProcessAsyncIFace {
        void displayResults(@Nullable AsyncParams asyncParams);
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
        initLineEdit = view.findViewById(R.id.csv_init_line);
        endLineEdit = view.findViewById(R.id.csv_end_line);
        saveButton = view.findViewById(R.id.csv_btn_save);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        postProcessIFace = new PostProcessAsyncIFace() {
            @Override
            public void displayResults(@Nullable AsyncParams asyncParams) {
                postProcessParams = asyncParams;
                displayResultsInUI();
            }
        };

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTerms();
                parentIFace.onCsvMediaFragmentFinished();
            }
        });

        String streamType = parentIFace.getStreamType();
        streamUri = parentIFace.getStreamUri();
        if((streamUri == null) || (streamType == null)) {
            parentIFace.onCsvMediaFragmentException();
            return;
        }

        separatorGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                sepOtherBtn.setEnabled(false);
                sepOtherEdit.setEnabled(false);
                AsyncParams asyncParams = new AsyncParams();
                asyncParams.quotes = getQuoteString(quotesGroup.getCheckedRadioButtonId());
                asyncParams.separator = getSeparatorString(checkedId);
                if(asyncParams.separator == null) {
                    sepOtherBtn.setEnabled(true);
                    sepOtherEdit.setEnabled(true);
                } else {
                    new ProcessStreamInputAsync(CsvReaderMediaFragment.this, asyncParams).execute();
                }
            }
        });

        // set quotes radio buttons to their default values before setting their change listener;
        // this prevents listeners from calling async thread to process medium input
        // twice (setting separator radio buttons below already calls async thread)
        if(streamType.equals("text/csv")) {
            radioDoubleQuotes.setChecked(true);
            radioComma.setChecked(true);
        } else {
            radioNoQuotes.setChecked(true);
            radioTab.setChecked(true);
        }

        // quotes radio buttons
        quotesGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                AsyncParams asyncParams = new AsyncParams();
                asyncParams.quotes = getQuoteString(checkedId);
                asyncParams.separator = getSeparatorString(separatorGroup.getCheckedRadioButtonId());
                new ProcessStreamInputAsync(CsvReaderMediaFragment.this, asyncParams).execute();
            }
        });

        sepOtherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncParams asyncParams = new AsyncParams();
                asyncParams.separator = sepOtherEdit.getText().toString().trim();
                asyncParams.quotes = getQuoteString(quotesGroup.getCheckedRadioButtonId());
                sepOtherEdit.setEnabled(false);
                sepOtherBtn.setEnabled(false);
                new ProcessStreamInputAsync(CsvReaderMediaFragment.this, asyncParams).execute();
            }
        });

    }

    private String getSeparatorString(int checkedId) {
        switch (checkedId) {
            case R.id.radio_button_comma:
                return ",";
            case R.id.radio_button_semicolon:
                return ";";
            case R.id.radio_button_tab:
                return "\t";
        }
        return null;
    }

    private String getQuoteString(int checkedId) {
        switch (checkedId) {
            case R.id.radio_double_quotes:
                return "\"";
            case R.id.radio_single_quotes:
                return "'";
        }
        return "";
    }

    private class AsyncParams {
        List<List<String>> rows;            // raw row values to save
        List<Language> languages;           // fresh db lang list (user may have changed them)
        String quotes, separator, html;     //
    }

    private void displayResultsInUI() {

        webView.loadUrl("about:blank");     // clear web view
        if(postProcessParams == null) {
            parentIFace.onCsvMediaFragmentException();
            return;
        }
        webView.loadData(postProcessParams.html, "text/html;charset=utf-8", "utf-8");
        if(radioOther.isChecked()) {
            sepOtherEdit.setEnabled(true);
            sepOtherBtn.setEnabled(true);
        }
        initLineEdit.setText(1 + "");
        int maxLine = postProcessParams.rows.size();
        endLineEdit.setText(maxLine + "");
        InputFilterIntRange filter = new InputFilterIntRange(1, maxLine);
        initLineEdit.setFilters(new InputFilter[]{filter});
        initLineEdit.setOnFocusChangeListener(filter);
        endLineEdit.setFilters(new InputFilter[]{filter});
        endLineEdit.setOnFocusChangeListener(filter);
    }

    private static class ProcessStreamInputAsync extends AsyncTask<Void, Void, Void> {

        WeakReference<CsvReaderMediaFragment> weakReference;
        private int maxColumns;
        AsyncParams asyncParams;
        private static String initTable;

        ProcessStreamInputAsync(CsvReaderMediaFragment fragment, AsyncParams asyncParams) {
            weakReference = new WeakReference<>(fragment);
            this.asyncParams = asyncParams;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CsvReaderMediaFragment fragment = weakReference.get();
            if(fragment == null || fragment.isRemoving() || fragment.isDetached()) {
                return;
            }
            initTable = fragment.getResources().getString(R.string.csv_table_init);
            // We'll return the rows member filled in with the terms we read from the file rows
            asyncParams.rows = new ArrayList<>();
            asyncParams.html = "";
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            asyncParams = null;
            CsvReaderMediaFragment fragment = weakReference.get();
            if(fragment == null || fragment.isRemoving() || fragment.isDetached()) {
                return;
            }
            fragment.parentIFace.onCsvMediaFragmentException();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                processStreamInput();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                cancel(true);
            }
            return null;
        }

        private void processStreamInput() throws FileNotFoundException {
            CsvReaderMediaFragment fragment = weakReference.get();
            if(fragment == null || fragment.isRemoving() || fragment.isDetached()) {
                cancel(true);
                return;
            }
            // we must initialize the languages param of the asyncParam member in this thread
            // because the Room model does not execute database queries on the main thread
            LanguagesRepository repository =
                    new LanguagesRepository(Objects.requireNonNull(fragment.getActivity()).getApplication());
            // Set AsyncParams member languages with db languages, which we'll need when saving terms
            asyncParams.languages = repository.getAllLanguages();

            // let's try to read from file
            InputStream inputStream;
            // if fragment is no longer available here, a FileNotFoundException will be thrown
            // and caught by code above; so, we're safe :D
            inputStream = Objects.requireNonNull(fragment.getContext())
                                 .getContentResolver().openInputStream(fragment.streamUri);

            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(streamReader);
            String line;
            int lineId = 0;     // the line no. to be shown in the left-most column of html table
            maxColumns = 0;
            while (true) {
                try {
                    if ((line=reader.readLine()) == null) {
                        break;
                    }
                    lineId ++;
                    asyncParams.html += processLineHTML(lineId, line);
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
            asyncParams.html += "</div>\n";
            asyncParams.html = initTable + languagesRow() + asyncParams.html;
        }

        private String processLineHTML(int lineId, String line) {
            String html = "<div class='tableRow'>\n";
            html += "<div class='tableCell'>" + lineId + "</div>";
            String quotes = asyncParams.quotes;
            String sep = quotes+asyncParams.separator+quotes;
            String[] fields = line.split(sep, asyncParams.languages.size());
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
            asyncParams.rows.add(columns);
            html += "</div>\n";
            return html;
        }

        private String languagesRow() {
            String row = "<div class='tableRow'>";
            row += "<div class='tableCell'>Row</div>";
            for(int ix = 0; ix < maxColumns; ix++) {
                row += "<div class='tableCell'>";
                row += asyncParams.languages.get(ix).getLanguage();
                row += "</div>";
            }
            row += "</div>";
            return row;
        }

        @Override
        protected void onPostExecute(Void v) {
            CsvReaderMediaFragment fragment = weakReference.get();
            if(fragment == null || fragment.isRemoving() || fragment.isDetached()) {
                return;
            }
            fragment.postProcessIFace.displayResults(asyncParams);
        }
    }

    private void saveTerms() {
        Domain domain = parentIFace.csvMediaFragmentGetDomain();
        if(domain == null) {
            Toast.makeText(getContext(),
                    "You must select a knowledge area/domain...", Toast.LENGTH_SHORT).show();
            return;
        }

        int line1 = 1, line2 = 1;
        try {
            line1 = Integer.parseInt(initLineEdit.getText().toString());
            line2 = Integer.parseInt(endLineEdit.getText().toString());
        } catch (NumberFormatException e) {
            Log.d(TAG, "saveTerms: *************************************");
            Log.d(TAG, "saveTerms: " + e.getMessage());
            Toast.makeText(getContext(),
                    "Please, review your initial and end line entries", Toast.LENGTH_SHORT).show();
            return;
        }
        if(line1 > line2) {
            int swap = line1;
            line1 = line2;
            line2 = swap;
        }
        line1--;
        postProcessParams.rows = postProcessParams.rows.subList(line1, line2);
        new saveAsyncTask(domain.getId(), postProcessParams, this).execute();
    }

    private static class saveAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<CsvReaderMediaFragment> weakReference;
        private long domainId;
        private AsyncParams saveParams;

        saveAsyncTask(long domainId, AsyncParams saveParams, CsvReaderMediaFragment fragment) {
            weakReference = new WeakReference<>(fragment);
            this.domainId = domainId;
            this.saveParams = saveParams;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            CsvReaderMediaFragment fragment = weakReference.get();
            if(fragment == null || fragment.isRemoving() || fragment.isDetached()) {
                return false;
            }

            GraphUtil graphUtil = new GraphUtil(Objects.requireNonNull(fragment.getActivity()).getApplication());

            List<GraphVertex> vertices = new ArrayList<>();
            for(int iy = 0; iy < saveParams.rows.size(); iy++) {
                List<String> row = saveParams.rows.get(iy);
                vertices.clear();
                for(int ix = 0; ix < row.size(); ix++) {
                    GraphVertex vertex = new GraphVertex();
                    vertex.term = row.get(ix);
                    vertex.lang_ref = saveParams.languages.get(ix).getId();
                    vertex.language = saveParams.languages.get(ix).getLanguage();
                    vertex.user_rank = 0;
                    vertex.vertex_context = "";
                    vertices.add(vertex);
                }
                graphUtil.createGraph(domainId, vertices);
            }
            return true;
        }
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
}



