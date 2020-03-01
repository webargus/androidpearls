package br.com.pearls;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.reflect.Type;

import br.com.pearls.DB.Domain;
import br.com.pearls.DB.KnowledgeArea;
import br.com.pearls.Settings.SettingsActivity;
import br.com.pearls.ui.main.AreasDomainsTabFragment;
import br.com.pearls.ui.main.SectionsPagerAdapter;

public class SearchActivity extends AppCompatActivity implements AreasDomainsTabFragment.OnDomainSelectedListener {

    private static final String TAG = SearchActivity.class.getName();

    private static final String SEARCH_PREFERENCES = "search preferences";
    private static final String CURRENT_AREA = "current area";
    private static final String CURRENT_DOMAIN = "current domain";

    private KnowledgeArea currentArea;
    private Domain currentDomain;
    private TextView tvCaption;

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
        Log.v(TAG, "UUUUUUUUUUUUUUUUUUUUUUUUUU ->->-> onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        tvCaption = (TextView) findViewById(R.id.area_domain_caption);
        loadPreferences();
        if(currentArea != null) {
            tvCaption.setText(currentArea.getArea() + " > " + currentDomain.getDomain());
        }

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        ImageButton settingsButton = findViewById(R.id.action_settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingsIntent = new Intent(SearchActivity.this, SettingsActivity.class);
                SearchActivity.this.startActivity(settingsIntent);
            }
        });
    }

    private void savePreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SEARCH_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String domain = gson.toJson(currentDomain);
        String area = gson.toJson(currentArea);
        editor.putString(CURRENT_DOMAIN, domain);
        editor.putString(CURRENT_AREA, area);
        editor.apply();
    }

    private void loadPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SEARCH_PREFERENCES, MODE_PRIVATE);
        Gson gson = new Gson();
        String domainJson = sharedPreferences.getString(CURRENT_DOMAIN, null);
        String areaJson = sharedPreferences.getString(CURRENT_AREA, null);
        Type domainType = new TypeToken<Domain>() {}.getType();
        currentDomain = gson.fromJson(domainJson, domainType);
        Type areaType = new TypeToken<KnowledgeArea>() {}.getType();
        currentArea = gson.fromJson(areaJson, areaType);
    }
}




















