package br.com.pearls;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import br.com.pearls.DB.Domain;
import br.com.pearls.DB.KnowledgeArea;
import br.com.pearls.ui.main.AreasDomainsTabFragment;
import br.com.pearls.ui.main.CsvReaderMediaFragment;
import br.com.pearls.ui.main.CsvReaderViewPagerAdapter;

public class CsvReaderActivity extends AppCompatActivity
        implements AreasDomainsTabFragment.OnDomainSelectedListener,
                   CsvReaderMediaFragment.CsvMediaParentDataIFace {

    public static final String TAG = CsvReaderActivity.class.getName();
    public static final String CSV_READER_ACTIVITY_URI = "pearls csv reader uri";
    public static final String CSV_READER_ACTIVITY_TYPE = "pearls csv reader type";

    TextView textViewCaption;
    AlertDialog alertDialog;
    CsvReaderViewPagerAdapter adapter;
    KnowledgeArea selectedArea;
    Domain selectedDomain;
    String streamType;
    Uri streamUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csv_reader);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCancelable(false);       // make it modal!
        alertDialog.setTitle(getString(R.string.oops));
        alertDialog.setMessage(getString(R.string.csv_reader_alert_msg));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.csv_reader_alert_btn),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        CsvReaderActivity.this.finish();
                    }
                });
        Intent intent = getIntent();
        if(intent.hasExtra(CSV_READER_ACTIVITY_URI)) {
            streamUri = Uri.parse(intent.getStringExtra(CSV_READER_ACTIVITY_URI));
            streamType = intent.getStringExtra(CSV_READER_ACTIVITY_TYPE);
        } else {
            try {
                getStreamUriAndType();
            } catch (RuntimeException e) {
                alertDialog.show();
                return;
            }
        }

        textViewCaption = findViewById(R.id.csv_reader_caption);

        adapter = new CsvReaderViewPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.csv_reader_view_pager);
        viewPager.setAdapter(adapter);
        TabLayout tabs = findViewById(R.id.csv_reader_tab_layout);
        tabs.setupWithViewPager(viewPager);
    }

    // get stream type and stream Uri if we're apt to process this stream
    private void getStreamUriAndType() throws RuntimeException {
        // get intent from parent activity through i-face
        Intent intent = getIntent();
        String action = intent.getAction();

        if (action.equals(Intent.ACTION_SEND)) {
            // save stream type to local member
            streamType = intent.getType();
            Log.v(TAG, "type: " + streamType);          // debug
            // type is set to application/octet-stream for .prl (former desktop pearls app format) files
            // and text/csv for .csv files; checked to be true for chrome download, gmail app and whatsapp.
            if (streamType != null && (streamType.equals("text/csv") || streamType.equals("application/octet-stream"))) {
                Bundle bundle = intent.getExtras();
                if (bundle != null && bundle.size() > 0 && bundle.containsKey(Intent.EXTRA_STREAM)) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setSelectedDomain(KnowledgeArea area, Domain domain) {
        selectedArea = area;
        selectedDomain = domain;
        textViewCaption.setText(String.format("%s > %s", area.getArea(), domain.getDomain()));
    }

    @Override
    public void onCsvMediaFragmentException() {
        alertDialog.show();
    }

    @Override
    public Uri getStreamUri() {
        return streamUri;
    }

    @Override
    public String getStreamType() {
        return streamType;
    }

    @Override
    public Domain csvMediaFragmentGetDomain() {
        return selectedDomain;
    }

    @Override
    public void onCsvMediaFragmentFinished() {
        System.out.println("*************** finishing CsvReaderActivity***********");
        finish();
    }

    // preventing 'has leaked window com.android.internal.policy.impl...' exceptions:
    @Override
    protected void onPause() {
        if((alertDialog != null) && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if((alertDialog != null) && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        super.onDestroy();
    }
}
