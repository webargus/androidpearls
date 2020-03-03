package br.com.pearls.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.pearls.DB.Domain;
import br.com.pearls.DB.KnowledgeArea;
import br.com.pearls.DB.Language;
import br.com.pearls.DB.LanguagesViewModel;
import br.com.pearls.R;
import br.com.pearls.SearchActivity;
import br.com.pearls.utils.LiveDataUtil;

public class NewTermActivity extends AppCompatActivity {

    private static final String TAG = NewTermActivity.class.getName();

    private KnowledgeArea area;
    private Domain domain;
    private LanguagesViewModel languagesViewModel;
    Observer<List<Language>> languageObserver;

    private TextView tvCaption;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_term_activity);
        Intent input = getIntent();
        area = input.getParcelableExtra(SearchActivity.CURRENT_AREA);
        domain = input.getParcelableExtra(SearchActivity.CURRENT_DOMAIN);
        Log.v(TAG, "-------------> got area '" + area.getArea() + "'");

        tvCaption = findViewById(R.id.tv_term_form_caption);
        tvCaption.setText(area.getArea() + " > " + domain.getDomain());

        RecyclerView recyclerView = findViewById(R.id.rv_term_form);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RVTermFormAdapter adapter = new RVTermFormAdapter();
        recyclerView.setAdapter(adapter);

        languageObserver = new Observer<List<Language>>() {
            @Override
            public void onChanged(List<Language> languages) {
                adapter.setLanguages(languages);
            }
        };
        //LiveDataUtil.observeOnce(languagesViewModel.getActiveLanguages(), languageObserver);
        languagesViewModel = new ViewModelProvider(this).get(LanguagesViewModel.class);
        languagesViewModel.getActiveLanguages().observe(this, languageObserver);
    }
}









