package br.com.pearls;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import br.com.pearls.DB.Domain;
import br.com.pearls.DB.KnowledgeArea;
import br.com.pearls.Settings.SettingsActivity;
import br.com.pearls.ui.main.AreasDomainsTabFragment;
import br.com.pearls.ui.main.CsvReaderMediaFragment;
import br.com.pearls.ui.main.LanguagesActivity;
import br.com.pearls.ui.main.AddEditTermActivity;
import br.com.pearls.ui.main.SearchTabFragment;
import br.com.pearls.ui.main.SearchPagerAdapter;
import br.com.pearls.utils.GraphVertex;

public class SearchActivity extends AppCompatActivity
        implements AreasDomainsTabFragment.OnDomainSelectedListener,
                   SearchTabFragment.SearchTabIFace {

    private static final String TAG = SearchActivity.class.getName();
    private static final int CSV_FILE_CHOOSER_REQUEST = 53;

    private static final String PEARLS_SEARCH_PREFERENCES = "search preferences";
    public static final String PEARLS_CURRENT_AREA = "current area";
    public static final String PEARLS_CURRENT_DOMAIN = "current domain";
    public static final int PEARLS_KEY_EDIT = 2020;
    public static final int PEARLS_KEY_ADD = 2021;

    private KnowledgeArea currentArea;
    private Domain currentDomain;
    private TextView tvCaption;

    private Uri streamUri;
    private String streamType;

    @Override
    public void setSelectedDomain(KnowledgeArea area, Domain domain) {
        currentArea = area;
        currentDomain = domain;
        Log.v(TAG, "***********Clicked on domain '" +
                    domain.getDomain() + "' in area '" + area.getArea() + "'");
        tvCaption.setText(area.getArea() + " > " + domain.getDomain());
        savePreferences();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setTitle(R.string.title_activity_search);

        tvCaption = (TextView) findViewById(R.id.area_domain_caption);
        loadPreferences();
        if(currentArea != null) {
            tvCaption.setText(currentArea.getArea() + " > " + currentDomain.getDomain());
        }

        SearchPagerAdapter searchPagerAdapter = new SearchPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(searchPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                openSettingsActivity();
                return true;
            case R.id.action_csv_files:
                browseForFile();
                return true;
            case R.id.action_languages:
                openLanguagesActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void browseForFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        String [] mimeTypes = {"text/csv", "application/octet-stream"};
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, CSV_FILE_CHOOSER_REQUEST);
    }

    private void openLanguagesActivity() {
        Intent languagesIntent = new Intent(SearchActivity.this, LanguagesActivity.class);
        SearchActivity.this.startActivity(languagesIntent);
    }

    private void openSettingsActivity() {
        Intent settingsIntent = new Intent(SearchActivity.this, SettingsActivity.class);
        SearchActivity.this.startActivity(settingsIntent);
    }

    private void savePreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PEARLS_SEARCH_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String domain = gson.toJson(currentDomain);
        String area = gson.toJson(currentArea);
        editor.putString(PEARLS_CURRENT_DOMAIN, domain);
        editor.putString(PEARLS_CURRENT_AREA, area);
        editor.apply();
    }

    private void loadPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PEARLS_SEARCH_PREFERENCES, MODE_PRIVATE);
        Gson gson = new Gson();
        String domainJson = sharedPreferences.getString(PEARLS_CURRENT_DOMAIN, null);
        String areaJson = sharedPreferences.getString(PEARLS_CURRENT_AREA, null);
        Type domainType = new TypeToken<Domain>() {}.getType();
        currentDomain = gson.fromJson(domainJson, domainType);
        Type areaType = new TypeToken<KnowledgeArea>() {}.getType();
        currentArea = gson.fromJson(areaJson, areaType);
    }

    /* SearchTabFragment.SearchTabIFace overrides */

    @Override
    public void onNewTermFABClick() {
        Log.v(TAG, "got click from SearchTabFragment FAB");
        if(currentArea == null) {
            Toast.makeText(this,
                        "You must create/select a Knowldedge area and domain first",
                            Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(SearchActivity.this, AddEditTermActivity.class);
        intent.putExtra(AddEditTermActivity.PEARLS_KEY_DOMAIN, currentDomain);
        intent.putExtra(AddEditTermActivity.PEARLS_KEY_AREA, currentArea);
        startActivityForResult(intent, PEARLS_KEY_ADD);
    }

    @Override
    public Domain getDomain() { return currentDomain; }

    @Override
    public void onEditTerm(String stringId, List<GraphVertex> vertices) {
        ArrayList<GraphVertex> arrayList = new ArrayList<>(vertices);
        Intent intent = new Intent(SearchActivity.this, AddEditTermActivity.class);
        intent.putExtra(AddEditTermActivity.PEARLS_KEY_AREA, currentArea);
        intent.putExtra(AddEditTermActivity.PEARLS_KEY_DOMAIN, currentDomain);
        intent.putParcelableArrayListExtra(AddEditTermActivity.PEARLS_KEY_VERTICES, arrayList);
        intent.putExtra(AddEditTermActivity.PEARLS_KEY_ID, stringId);
        startActivityForResult(intent, PEARLS_KEY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if((resultCode == RESULT_OK) && (data != null)) {
            switch (requestCode) {
                case PEARLS_KEY_EDIT:
                    ArrayList<GraphVertex> vertices =
                            data.getParcelableArrayListExtra(AddEditTermActivity.PEARLS_KEY_VERTICES);
                    if (vertices == null) {
                        Toast.makeText(getApplicationContext(), "Couldn't update terms", Toast.LENGTH_LONG).show();
                        return;
                    }
                    String stringId = data.getStringExtra(AddEditTermActivity.PEARLS_KEY_ID);
                    SearchTabFragment stf = (SearchTabFragment) (getSupportFragmentManager()
                            .getFragments().get(SearchPagerAdapter.PEARLS_SEARCH_TAB_FRAGMENT_POSITION));
                    stf.updateRecyclerViewItem(stringId, vertices);
                    Toast.makeText(getApplicationContext(), "Terms updated successfully", Toast.LENGTH_SHORT).show();
                    break;
                case CSV_FILE_CHOOSER_REQUEST:
                    Uri uri = data.getData();
                    String type = getContentResolver().getType(uri);
                    launchCsvReaderActivity(uri, type);
                    break;
            }
        }
    }

    private void launchCsvReaderActivity(Uri streamUri, String streamType) {
        Intent intent = new Intent(SearchActivity.this, CsvReaderActivity.class);
        intent.putExtra(CsvReaderActivity.CSV_READER_ACTIVITY_URI, streamUri.toString());
        intent.putExtra(CsvReaderActivity.CSV_READER_ACTIVITY_TYPE, streamType);
        startActivity(intent);
    }
}




















