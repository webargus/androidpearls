package br.com.pearls.utils;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

import br.com.pearls.DB.DomainRepository;
import br.com.pearls.DB.Graph;
import br.com.pearls.DB.GraphRepository;
import br.com.pearls.DB.Term;
import br.com.pearls.DB.TermRepository;
import br.com.pearls.DB.Vertex;
import br.com.pearls.DB.VertexRepository;

public class GraphUtil {

    public static final String TAG = GraphUtil.class.getName();

    private static DomainRepository domainRepository;
    private static GraphRepository graphRepository;
    private static VertexRepository vertexRepository;
    private static TermRepository termRepository;
    private static List<SearchVertex> vertices;
    private static Graph graph;
    private static OnGraphCreated graphCreated;

    public interface OnGraphCreated {
        void onGraphCreated(boolean graphOk);
    }

    public void setOnGraphCreated(OnGraphCreated graphCreated) {
        this.graphCreated = graphCreated;
    }

    public GraphUtil(Application application) {
        domainRepository = new DomainRepository(application);
        graphRepository = new GraphRepository(application);
        vertexRepository = new VertexRepository(application);
        termRepository = new TermRepository(application);
        graph = new Graph();
    }

    public void createGraph(@NonNull final long domain_ref, @NonNull final List<SearchVertex> vertices) {
        graph.setDomain_ref(domain_ref);
        this.vertices = vertices;
        new GraphAsyncTask().execute();
    }

    private static class GraphAsyncTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            // Check if domain_ref exists, return false if not
            if (domainRepository.getDomain(graph.getDomain_ref()).length == 0) {
                return false;
            }
            Vertex vertex;
            Term term;
            long graph_id = 0;
            for(SearchVertex searchVertex : vertices) {
                vertex = new Vertex();                  // vertex to insert or update
                term = new Term();                      // term to insert or update
                // set term fields according to user's GUI input
                term.setTerm(searchVertex.term);
                term.setTerm_ascii(RemoveDiacritics.removeDiacritics(searchVertex.term).toLowerCase());
                if(searchVertex.term_ref == 0) {    // insert term
                    term.setLang_ref(searchVertex.lang_ref);
                    vertex.setTerm_ref(termRepository.insert(term));
                } else {        // update term
                    termRepository.update(term);
                }
                // set vertex fields according to user's input
                vertex.setUser_rank(searchVertex.user_rank);
                vertex.setVertex_context(searchVertex.vertex_context);
                if(searchVertex.graph_ref == 0) {       // => we're inserting
                    if(graph_id == 0) {
                        // create graph once and reuse same graph_id for all new vertices
                        graph_id = graphRepository.insert(graph);
                        searchVertex.graph_ref = graph_id;
                    }
                    vertex.setGraph_ref(graph_id);
                    Log.v(TAG, "inserting " + vertex.getId());
                    searchVertex.vertex_id = vertexRepository.insert(vertex);
                } else {                                // => we're updating
                    vertex.setId(searchVertex.vertex_id);
                    vertex.setGraph_ref(searchVertex.graph_ref);
                    vertex.setTerm_ref(searchVertex.term_ref);
                    Log.v(TAG, "updating " + vertex.getId());
                    vertexRepository.update(vertex);
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (graphCreated != null) {
                graphCreated.onGraphCreated(result);
            }
        }
    }
}














