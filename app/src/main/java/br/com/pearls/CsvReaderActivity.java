package br.com.pearls;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

import br.com.pearls.DB.Domain;
import br.com.pearls.DB.KnowledgeArea;
import br.com.pearls.DB.Language;
import br.com.pearls.ui.main.AreasDomainsTabFragment;
import br.com.pearls.ui.main.CsvReaderLanguagesFragment;
import br.com.pearls.ui.main.CsvReaderMediaFragment;
import br.com.pearls.ui.main.CsvReaderViewPagerAdapter;

public class CsvReaderActivity extends AppCompatActivity
        implements AreasDomainsTabFragment.OnDomainSelectedListener,
                   CsvReaderMediaFragment.CsvMediaParentDataIFace {

    public static final String TAG = CsvReaderActivity.class.getName();

    TextView textViewCaption;
    AlertDialog alertDialog;
    CsvReaderViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csv_reader);

        adapter = new CsvReaderViewPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.csv_reader_view_pager);
        viewPager.setAdapter(adapter);
        TabLayout tabs = findViewById(R.id.csv_reader_tab_layout);
        tabs.setupWithViewPager(viewPager);

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

        textViewCaption = findViewById(R.id.csv_reader_caption);
    }

    @Override
    public void setSelectedDomain(KnowledgeArea area, Domain domain) {
        textViewCaption.setText(area.getArea() + " > " + domain.getDomain());
    }

    @Override
    public void onCsvMediaFragmentException() {
        notifyUserOnException();
    }

    @Override
    public Intent csvMediaFragmentGetIntent() {
        return getIntent();
    }

    private void notifyUserOnException() {
        alertDialog.show();
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
