package br.com.pearls.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import br.com.pearls.DB.Domain;
import br.com.pearls.DB.KnowledgeArea;
import br.com.pearls.DB.Language;
import br.com.pearls.DB.LanguagesViewModel;
import br.com.pearls.DB.Term;
import br.com.pearls.R;
import br.com.pearls.utils.GraphSearchResult;
import br.com.pearls.utils.GraphUtil;
import br.com.pearls.utils.RemoveDiacritics;
import br.com.pearls.utils.SearchVertex;

import static br.com.pearls.SearchActivity.PEARLS_KEY_CURRENT_AREA;
import static br.com.pearls.SearchActivity.PEARLS_KEY_CURRENT_DOMAIN;
import static br.com.pearls.SearchActivity.PEARLS_KEY_GRAPH;
import static br.com.pearls.SearchActivity.PEARLS_KEY_VERTICES;

public class NewTermActivity extends AppCompatActivity implements GraphUtil.OnGraphCreated {

    private static final String TAG = NewTermActivity.class.getName();

    private KnowledgeArea area;
    private Domain domain;
    private ArrayList<SearchVertex> vertices;
    private GraphSearchResult graph;
    private LanguagesViewModel languagesViewModel;
    private RVTermFormAdapter adapter;
    private RecyclerView recyclerView;
    private Observer<List<Language>> languageObserver;
    private TextView tvCaption;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_term_activity);

        Intent input = getIntent();
        if(input.hasExtra(PEARLS_KEY_GRAPH)) {      // edit
            this.graph = input.getParcelableExtra(PEARLS_KEY_GRAPH);
            this.area = new KnowledgeArea();
            area.setId(graph.area_ref);
            area.setArea(graph.areaName);
            this.domain = new Domain();
            domain.setArea_ref(graph.area_ref);
            domain.setDomain(graph.domainName);
            domain.setId(graph.domain_ref);
            this.vertices = input.getParcelableArrayListExtra(PEARLS_KEY_VERTICES);
            if (vertices != null) {
                for (SearchVertex v : vertices) {       // debug
                    Log.v(TAG, "language: " + v.language + "; term: " + v.term);
                }
            }
        } else { // new term
            if (input.hasExtra(PEARLS_KEY_CURRENT_AREA)) {
                this.area = input.getParcelableExtra(PEARLS_KEY_CURRENT_AREA);
                this.domain = input.getParcelableExtra(PEARLS_KEY_CURRENT_DOMAIN);
            }
        }

        tvCaption = findViewById(R.id.tv_term_form_caption);
        if(domain != null) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(area.getArea());
            buffer.append(" > ");
            buffer.append(domain.getDomain());
            tvCaption.setText(buffer);
        }else{
            Log.v(TAG, "--------------------xxxxxxxxxxxx domain is null");
        }

        recyclerView = findViewById(R.id.rv_term_form);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RVTermFormAdapter();
        recyclerView.setAdapter(adapter);

        languageObserver = new Observer<List<Language>>() {
            @Override
            public void onChanged(List<Language> languages) {
                adapter.setLanguages(languages);
            }
        };
        languagesViewModel = new ViewModelProvider(this).get(LanguagesViewModel.class);
        languagesViewModel.getActiveLanguages().observe(this, languageObserver);

        ((FloatingActionButton) findViewById(R.id.btn_save_graph)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGraph();
            }
        });
    }

    private void saveGraph() {
        // validate
        GraphUtil graphUtil = new GraphUtil(getApplication(), this);
        Term term;
        boolean isEmpty = true;
        for (int position = 0; position < adapter.getItemCount(); position++) {
            RVTermFormAdapter.TermFormHolder itemHolder =
                    (RVTermFormAdapter.TermFormHolder) recyclerView.findViewHolderForAdapterPosition(position);
            term = new Term();
            String termString = itemHolder.etTerm.getText().toString().trim();
            if (!termString.isEmpty()) {
                isEmpty = false;
            }
            term.setTerm(termString);
            term.setTerm_ascii(RemoveDiacritics.removeDiacritics(termString));
            term.setLang_ref(itemHolder.language.getId());
            GraphUtil.GraphVertexWithTerm vertex = graphUtil.new GraphVertexWithTerm(term);
            String termContext = itemHolder.etContext.getText().toString().trim();
            vertex.setVertexParams(termContext, (int)itemHolder.rbRank.getRating());
            graphUtil.add(vertex);
        }

        if (isEmpty) {
            Toast.makeText(this, "You can't have all terms blank", Toast.LENGTH_SHORT).show();
            return;
        }

        graphUtil.createGraph(domain.getId());
    }

    @Override
    public void onGraphCreated(boolean graphOk) {
        clearForm();
        String msg = "Terms created and saved successfully!";
        if(!graphOk) {
            msg = "Term creation failed...";
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void clearForm() {
        for (int position = 0; position < adapter.getItemCount(); position++) {
            RVTermFormAdapter.TermFormHolder itemHolder =
                    (RVTermFormAdapter.TermFormHolder) recyclerView.findViewHolderForAdapterPosition(position);
            itemHolder.etTerm.setText("");
            if(position == 0) {
                itemHolder.etTerm.requestFocus();
            }
            itemHolder.etContext.setText("");
        }
    }
}









