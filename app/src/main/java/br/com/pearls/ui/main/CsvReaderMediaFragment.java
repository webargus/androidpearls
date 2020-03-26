package br.com.pearls.ui.main;

import android.content.Context;
import android.content.Intent;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.pearls.DB.Domain;
import br.com.pearls.DB.KnowledgeArea;
import br.com.pearls.DB.Language;
import br.com.pearls.DB.LanguagesRepository;
import br.com.pearls.R;
import br.com.pearls.utils.GraphUtil;
import br.com.pearls.utils.GraphVertex;
import br.com.pearls.utils.InputFilterIntRange;

public class CsvReaderMediaFragment extends Fragment {

    public static final String TAG = CsvReaderMediaFragment.class.getName();

    private RadioButton radioOther;
    private EditText sepOtherEdit, initLine, endLine;
    private Button sepOtherBtn, saveButton;
    private WebView webView;
    private String separator, quotes, streamType;
    private Uri streamUri;
    private List<Language> languages;
    private List<List<String>> rows;

    private CsvMediaParentDataIFace parentIFace;

    public interface CsvMediaParentDataIFace {
        void onCsvMediaFragmentException();
        Uri getStreamUri();
        String getStreamType();
        Domain csvMediaFragmentGetDomain();
    }

    public CsvReaderMediaFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.csv_reader_media_fragment, container, false);
        webView = view.findViewById(R.id.csv_web_view);

        RadioGroup separatorGroup = view.findViewById(R.id.separator_radio_group);
        RadioButton radioComma = view.findViewById(R.id.radio_button_comma);
        RadioButton radioTab = view.findViewById(R.id.radio_button_tab);
        RadioGroup quotesGroup = view.findViewById(R.id.quote_radio_group);
        RadioButton radioDoubleQuotes = view.findViewById(R.id.radio_double_quotes);
        RadioButton radioNoQuotes = view.findViewById(R.id.radio_no_quotes);
        radioOther = view.findViewById(R.id.radio_button_other);
        sepOtherEdit = view.findViewById(R.id.edit_other_separator);
        sepOtherBtn = view.findViewById(R.id.btn_separator_other);
        initLine = view.findViewById(R.id.csv_init_line);
        endLine = view.findViewById(R.id.csv_end_line);
        saveButton = view.findViewById(R.id.csv_btn_save);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTerms();
            }
        });

        streamType = parentIFace.getStreamType();
        streamUri = parentIFace.getStreamUri();
        if((streamUri == null) || (streamType == null)) {
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

    private void saveTerms() {
        Domain domain = parentIFace.csvMediaFragmentGetDomain();
        if(domain == null) {
            Toast.makeText(getContext(),
                    "You must select a knowledge area/domain...", Toast.LENGTH_SHORT).show();
            return;
        }

        int line1 = Integer.parseInt(initLine.getText().toString());
        int line2 = Integer.parseInt(endLine.getText().toString());
        if(line1 > line2) {
            int swap = line1;
            line1 = line2;
            line2 = swap;
        }
        line1--;
        GraphUtil graphUtil = new GraphUtil(getActivity().getApplication());
        List<GraphVertex> vertices = new ArrayList<>();
        long domainId = domain.getId();
        for(int iy = line1; iy < line2; iy++) {
            List<String> row = rows.get(iy);
            vertices.clear();
            for(int ix = 0; ix < row.size(); ix++) {
                GraphVertex vertex = new GraphVertex();
                vertex.term = row.get(ix);
                vertex.lang_ref = languages.get(ix).getId();
                vertex.language = languages.get(ix).getLanguage();
                vertex.user_rank = 0;
                vertex.vertex_context = "";
                vertices.add(vertex);
            }
            graphUtil.createGraph(domainId, vertices);
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

    class ProcessStreamInputAsync extends AsyncTask<Void, Void, String> {

        private int maxColumns, lineId;

        @Override
        protected void onPostExecute(String html) {
            webView.loadUrl("about:blank");     // clear web view
            webView.loadData(html, "text/html;charset=utf-8", "utf-8");
            if(radioOther.isChecked()) {
                sepOtherEdit.setEnabled(true);
                sepOtherBtn.setEnabled(true);
            }
            initLine.setText(1 + "");
            endLine.setText(lineId + "");
            InputFilterIntRange filter = new InputFilterIntRange(1, lineId);
            initLine.setFilters(new InputFilter[]{filter});
            initLine.setOnFocusChangeListener(filter);
            endLine.setFilters(new InputFilter[]{filter});
            endLine.setOnFocusChangeListener(filter);
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
            fetchLanguages();
            InputStream inputStream;
            inputStream = getContext().getContentResolver().openInputStream(streamUri);

            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader reader=new BufferedReader(streamReader);
            String line;
            String html = "";
            lineId = 0;
            maxColumns = 0;
            rows = new ArrayList<>();
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
            rows.add(columns);
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
}



