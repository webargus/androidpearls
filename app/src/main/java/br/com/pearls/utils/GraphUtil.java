package br.com.pearls.utils;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import br.com.pearls.DB.DomainRepository;
import br.com.pearls.DB.Graph;
import br.com.pearls.DB.GraphRepository;
import br.com.pearls.DB.Term;
import br.com.pearls.DB.TermRepository;
import br.com.pearls.DB.VertexRepository;
import br.com.pearls.ui.main.NewTermActivity;

public class GraphUtil {

    public static final String TAG = GraphUtil.class.getName();

    private static DomainRepository domainRepository;
    private static GraphRepository graphRepository;
    private static VertexRepository vertexRepository;
    private static TermRepository termRepository;
    private static List<GraphVertexWithTerm> vertices;
    private static Graph graph;
    private static OnGraphCreated graphCreated;

    public interface OnGraphCreated {
        void onGraphCreated(boolean graphOk);
    }

    public GraphUtil(Application application, NewTermActivity newTermActivity) {
        try {
            graphCreated = (OnGraphCreated) newTermActivity;
        } catch (ClassCastException ex) {
            Log.e(TAG, "You must implement the OnGraphCreated interface");
            return;
        }
        domainRepository = new DomainRepository(application);
        graphRepository = new GraphRepository(application);
        vertexRepository = new VertexRepository(application);
        termRepository = new TermRepository(application);
        graph = new Graph();
        vertices = new ArrayList<>();
    }

    public void add(@NonNull final GraphVertexWithTerm vertex) {
        this.vertices.add(vertex);
    }

    public void createGraph(@NonNull final long domain_ref) {
        graph.setDomain_ref(domain_ref);
        new GraphAsyncTask().execute();
    }

    private static class GraphAsyncTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            // Check if domain_ref exists, return false if not
            if(domainRepository.getDomain(graph.getDomain_ref()).length == 0) {
                return false;
            }
            graph.setId(graphRepository.insert(graph));
            long termId;
            for (GraphVertexWithTerm vertexWithTerm : vertices) {
                termId = termRepository.insert(vertexWithTerm.term);
                vertexWithTerm.term.setId(termId);
                vertexWithTerm.vertex.setTerm_ref(termId);
                vertexWithTerm.vertex.setGraph_ref(graph.getId());
                vertexRepository.insert(vertexWithTerm.vertex);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
          graphCreated.onGraphCreated(result);
        }
    }

    public class GraphVertexWithTerm extends VertexWithTerm {
        public GraphVertexWithTerm(Term term) { super(term); }
    }
}














