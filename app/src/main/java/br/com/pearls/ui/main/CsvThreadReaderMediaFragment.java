package br.com.pearls.ui.main;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import br.com.pearls.DB.Domain;
import br.com.pearls.DB.Language;
import br.com.pearls.R;
import br.com.pearls.utils.CsvParams;
import br.com.pearls.utils.CsvWorkHandler;
import br.com.pearls.utils.CsvWorkHeaderThread;
import br.com.pearls.utils.InputFilterIntRange;

import static br.com.pearls.utils.CsvWorkHandler.PEARLS_CSV_READ_FILE;
import static br.com.pearls.utils.CsvWorkHandler.PEARLS_CSV_SAVE_TERMS;

public class CsvThreadReaderMediaFragment extends Fragment implements CsvWorkHandler.CsvThreadWorkIFace {

    public static final String TAG = CsvThreadReaderMediaFragment.class.getName();

    private RadioButton radioComma, radioTab, radioDoubleQuotes, radioNoQuotes, radioOther;
    private RadioGroup separatorGroup, quotesGroup;
    private EditText sepOtherEdit, initLineEdit, endLineEdit;
    private Button sepOtherBtn, saveButton;
    private WebView webView;
    private ProgressBar progressBar;
    private Uri streamUri;
    private CsvWorkHeaderThread csvWorkerThread;

    private CsvMediaParentDataIFace parentIFace;
    private CsvParams postProcessParams;

    public interface CsvMediaParentDataIFace {
        void onCsvMediaFragmentException();
        Uri getStreamUri();
        String getStreamType();
        Domain csvMediaFragmentGetDomain();
        void onCsvMediaFragmentFinished();
    }

    public CsvThreadReaderMediaFragment() {}

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
        progressBar = view.findViewById(R.id.csv_progress_bar);

        if((csvWorkerThread == null) || !csvWorkerThread.isAlive()) {
            csvWorkerThread = new CsvWorkHeaderThread(this);
            csvWorkerThread.start();
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        saveButton.setEnabled(false);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean notSaving = saveTerms();
                saveButton.setEnabled(notSaving);
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
                if(checkedId == R.id.radio_button_other) {
                    saveButton.setEnabled(false);
                    sepOtherBtn.setEnabled(true);
                    sepOtherEdit.setEnabled(true);
                } else {
                    sepOtherEdit.setEnabled(false);
                    sepOtherBtn.setEnabled(false);
                    sendHandlerThreadMessage();
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
                sendHandlerThreadMessage();
            }
        });

        sepOtherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sepOtherEdit.setEnabled(false);
                sepOtherBtn.setEnabled(false);
                sendHandlerThreadMessage();
            }
        });

    }

    private void sendHandlerThreadMessage() {
        saveButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        CsvParams asyncParams = new CsvParams();
        asyncParams.activity = getActivity();
        asyncParams.initTable = getResources().getString(R.string.csv_table_init);
        asyncParams.streamUri = streamUri;
        asyncParams.separator = getSeparatorString(separatorGroup.getCheckedRadioButtonId());
        asyncParams.quotes = getQuoteString(quotesGroup.getCheckedRadioButtonId());
        CsvWorkHandler handler = csvWorkerThread.getHandler();
        // raise stop flag should cause any running process to stop
        handler.stopProcesses();
        // remove all enqueued processes
        handler.removeCallbacksAndMessages(null);
        Message message = Message.obtain();
        message.what = PEARLS_CSV_READ_FILE;
        message.obj = asyncParams;
        // postpone processing half a sec to allow for running processes to quit
        handler.sendMessageDelayed(message, 500);
    }

    private String getSeparatorString(int checkedId) {
        switch (checkedId) {
            case R.id.radio_button_comma:
                return ",";
            case R.id.radio_button_semicolon:
                return ";";
            case R.id.radio_button_tab:
                return "\t";
            case R.id.radio_button_other:
                return sepOtherEdit.getText().toString().trim();
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

    private void updateWebView(CsvParams params) {
        progressBar.setVisibility(View.INVISIBLE);
        if(separatorGroup.getCheckedRadioButtonId() == R.id.radio_button_other) {
            sepOtherEdit.setEnabled(true);
            sepOtherBtn.setEnabled(true);
        }
        webView.loadUrl("about:blank");     // clear web view
        if(params == null) {        // some exception occurred while processing input stream
            parentIFace.onCsvMediaFragmentException();
            return;
        } else if(params.html == null) { // work interrupted via CsvWorkHandler.stopProcesses()
            return;
        }
        webView.loadData(params.html, "text/html;charset=utf-8", "utf-8");

        initLineEdit.setText(1 + "");
        int maxLine = params.rows.size();
        endLineEdit.setText(maxLine + "");
        InputFilterIntRange filter = new InputFilterIntRange(1, maxLine);
        initLineEdit.setFilters(new InputFilter[]{filter});
        initLineEdit.setOnFocusChangeListener(filter);
        endLineEdit.setFilters(new InputFilter[]{filter});
        endLineEdit.setOnFocusChangeListener(filter);

        postProcessParams = params;
        saveButton.setEnabled(true);
    }

        /*
        @return true => didn't start thread, abort, but keep save button enabled
        @return false => enter term save thread, so disable save button
     */
    private boolean saveTerms() {
        Domain domain = parentIFace.csvMediaFragmentGetDomain();
        if(domain == null) {
            Toast.makeText(getContext(),
                    "You must select a knowledge area/domain...", Toast.LENGTH_SHORT).show();
            return true;
        }
        postProcessParams.domainId = domain.getId();

        int line1 = 1, line2 = 1;
        try {
            line1 = Integer.parseInt(initLineEdit.getText().toString());
            line2 = Integer.parseInt(endLineEdit.getText().toString());
        } catch (NumberFormatException e) {
            Log.d(TAG, "saveTerms: *************************************");
            Log.d(TAG, "saveTerms: " + e.getMessage());
            Toast.makeText(getContext(),
                    "Please, review your initial and end line entries", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(line1 > line2) {
            int swap = line1;
            line1 = line2;
            line2 = swap;
        }
        line1--;
        postProcessParams.rows = postProcessParams.rows.subList(line1, line2);
        csvWorkerThread.getHandler().stopProcesses();
        Message message = Message.obtain();
        message.what = PEARLS_CSV_SAVE_TERMS;
        message.obj = postProcessParams;
        csvWorkerThread.getHandler().sendMessage(message);
        progressBar.setVisibility(View.VISIBLE);
        return false;       // -> thread called
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

    @Override
    public void onCsvFileInputWorkFinished(CsvParams result) {

        try {
            Handler threadHandler = new Handler(Looper.getMainLooper());
            threadHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateWebView(result);
                }
            });
//                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
//                        updateWebView(result);
//                    });
        } catch (Exception e) {
            Log.v(TAG, "******************* onCsvFileInputWorkFinished: " + e.getMessage());
        }
    }

    @Override
    public void onCsvTermSaveWorkFinished(boolean saved) {
        progressBar.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
                csvWorkerThread.quit();
                if(!saved) {
                    parentIFace.onCsvMediaFragmentException();
                }
                Toast.makeText(getContext(), "Terms saved successfully", Toast.LENGTH_SHORT).show();
                parentIFace.onCsvMediaFragmentFinished();
            }
        });
    }

    /*
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(csvWorkerThread == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            csvWorkerThread.quitSafely();
        } else {
            csvWorkerThread.quit();
        }
    }
     */
}



