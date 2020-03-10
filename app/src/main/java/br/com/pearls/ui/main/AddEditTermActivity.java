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

public class AddEditTermActivity extends AppCompatActivity {

    private static final String TAG = AddEditTermActivity.class.getName();

    public static final String PEARLS_KEY_VERTICES = "vertices";
    public static final String PEARLS_KEY_DOMAIN = "domain";
    public static final String PEARLS_KEY_AREA = "area";

    private KnowledgeArea area;
    private Domain domain;
    private ArrayList<SearchVertex> vertices;
    private LanguagesViewModel languagesViewModel;
    private AddEditTermAdapter adapter;
    private RecyclerView recyclerView;
    private Observer<List<Language>> languageObserver;
    private TextView tvCaption;
    private GraphUtil graphUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_term_activity);


        recyclerView = findViewById(R.id.rv_term_form);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AddEditTermAdapter();
        recyclerView.setAdapter(adapter);

        Intent input = getIntent();
        this.area = input.getParcelableExtra(PEARLS_KEY_AREA);
        this.domain = input.getParcelableExtra(PEARLS_KEY_DOMAIN);
        if(input.hasExtra(PEARLS_KEY_VERTICES)) {      // edit
            setTitle("Edit term");
            this.vertices = input.getParcelableArrayListExtra(PEARLS_KEY_VERTICES);
            adapter.setData(vertices);
        } else { // new term
            setTitle("Add term");
            languageObserver = new Observer<List<Language>>() {
                @Override
                public void onChanged(List<Language> languages) {
                    vertices = new ArrayList<>();
                    SearchVertex vertex;
                    for(Language lang: languages) {
                        vertex = new SearchVertex();
                        vertex.language = lang.getLanguage();
                        vertex.lang_ref = lang.getId();
                        vertex.user_rank = 0;
                        vertex.term = "";
                        vertex.vertex_context = "";
                        vertex.term_ref = 0;
                        vertex.vertex_id = 0;
                        vertex.graph_ref = 0;   // IMPORTANT! tells insert/edit script it's an insert op.
                        vertices.add(vertex);
                    }
                    adapter.setData(vertices);
                }
            };
            languagesViewModel = new ViewModelProvider(this).get(LanguagesViewModel.class);
            languagesViewModel.getActiveLanguages().observe(this, languageObserver);
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

        ((FloatingActionButton) findViewById(R.id.btn_save_graph)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGraph();
            }
        });

        graphUtil = new GraphUtil(getApplication());
        graphUtil.setOnGraphCreated(new GraphUtil.OnGraphCreated() {
            @Override
            public void onGraphCreated(boolean graphOk) {
                clearForm();
                String msg = "Terms created and saved successfully!";
                if(!graphOk) {
                    msg = "Term creation failed...";
                }
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveGraph() {
        // validate
        boolean isEmpty = true;
        SearchVertex vertex;
        for (int position = 0; position < adapter.getItemCount(); position++) {
            AddEditTermAdapter.TermFormHolder itemHolder =
                    (AddEditTermAdapter.TermFormHolder) recyclerView.findViewHolderForAdapterPosition(position);
            String termString = itemHolder.etTerm.getText().toString().trim();
            if (!termString.isEmpty()) {
                isEmpty = false;
            }
            vertex = vertices.get(position);
            vertex.term = termString;
            vertex.vertex_context = itemHolder.etContext.getText().toString().trim();
            vertex.user_rank = (int)itemHolder.rbRank.getRating();
        }

        if (isEmpty) {
            Toast.makeText(this, "You can't have all terms blank", Toast.LENGTH_SHORT).show();
            return;
        }

        graphUtil.createGraph(domain.getId(), vertices);
    }

    private void clearForm() {
        for (int position = 0; position < adapter.getItemCount(); position++) {
            AddEditTermAdapter.TermFormHolder itemHolder =
                    (AddEditTermAdapter.TermFormHolder) recyclerView.findViewHolderForAdapterPosition(position);
            itemHolder.etTerm.setText("");
            if(position == 0) {
                itemHolder.etTerm.requestFocus();
            }
            itemHolder.etContext.setText("");
        }
    }
}









