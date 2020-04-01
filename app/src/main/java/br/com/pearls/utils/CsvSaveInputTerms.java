package br.com.pearls.utils;

import android.app.Application;
import android.util.Log;

import java.util.List;
import java.util.Objects;

import br.com.pearls.DB.Graph;
import br.com.pearls.DB.GraphRepository;
import br.com.pearls.DB.Term;
import br.com.pearls.DB.TermRepository;
import br.com.pearls.DB.Vertex;
import br.com.pearls.DB.VertexRepository;

public class CsvSaveInputTerms {

    private static final String TAG = "CsvSaveInputTerms";

    CsvParams params;

    public CsvSaveInputTerms(CsvParams params) {
        this.params = params;
    }

    public boolean saveTerms() {
        TermRepository termRepository;
        VertexRepository vertexRepository;
        GraphRepository graphRepository;
        try {
            Application app = Objects.requireNonNull(params.activity).getApplication();
            termRepository = new TermRepository(app);
            vertexRepository = new VertexRepository(app);
            graphRepository = new GraphRepository(app);
        }catch (ClassCastException e) {
            Log.d(TAG, "****************** saveTerms: failed creating repositories");;
            return false;
        }

        for(int iy = 0; iy < params.rows.size(); iy++) {
            List<String> row = params.rows.get(iy);
            Graph graph = new Graph();
            graph.setDomain_ref(params.domainId);
            long graph_id = 0;
            for(int ix = 0; ix < row.size(); ix++) {

                // insert term
                Term term = new Term();                      // term to insert
                term.setTerm(row.get(ix));
                term.setTerm_ascii(RemoveDiacritics.removeDiacritics(row.get(ix)).toLowerCase());
                term.setLang_ref(params.languages.get(ix).getId());
                try {
                    term.setId(termRepository.insert(term));
                } catch (ClassCastException e) {
                    Log.d(TAG, "***************doInBackground: failed inserting term");
                    return false;
                }

                // insert vertex
                Vertex vertex = new Vertex();                  // vertex to insert
                vertex.setTerm_ref(term.getId());
                vertex.setUser_rank(0);
                vertex.setVertex_context("");
                if(graph_id == 0) {
                    // create graph once and reuse same graph_id for all next vertices of this graph
                    try {
                        graph_id = graphRepository.insert(graph);
                    } catch (ClassCastException e) {
                        Log.d(TAG, "**************saveTerms: failed inserting graph");
                        return false;
                    }
                }
                vertex.setGraph_ref(graph_id);
                try {
                    vertex.setId(vertexRepository.insert(vertex));
                } catch(ClassCastException e) {
                    Log.d(TAG, "****************saveTerms: failed inserting vertex");
                    return false;
                }
            }
        }
        return true;
    }
}
