package br.com.pearls.utils;

import android.app.Application;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import br.com.pearls.DB.Graph;
import br.com.pearls.DB.GraphRepository;
import br.com.pearls.DB.Term;
import br.com.pearls.DB.TermRepository;
import br.com.pearls.DB.Vertex;
import br.com.pearls.DB.VertexRepository;

public class GraphUtil {

    private static GraphRepository graphRepository;
    private static VertexRepository vertexRepository;
    private static TermRepository termRepository;
    private static List<VertexWithTerm> vertices;
    private static Graph graph;
    private static Application application;

    public GraphUtil(Application application) {
        graphRepository = new GraphRepository(application);
        vertexRepository = new VertexRepository(application);
        termRepository = new TermRepository(application);
        this.application = application;
        graph = new Graph();
        vertices = new ArrayList<>();
    }

    public void add(@NonNull final VertexWithTerm vertex) {
        this.vertices.add(vertex);
    }

    public void createGraph(@NonNull final long domain_ref) {
        graph.setDomain_ref(domain_ref);
        new GraphAsyncTask().execute();
    }

    private static class GraphAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            // TODO: check if domain_ref exists
            graph.setId(graphRepository.insert(graph));
            long termId;
            for (VertexWithTerm vertexWithTerm : vertices) {
                termId = termRepository.insert(vertexWithTerm.term);
                vertexWithTerm.term.setId(termId);
                vertexWithTerm.vertex.setTerm_ref(termId);
                vertexWithTerm.vertex.setGraph_ref(graph.getId());
                vertexRepository.insert(vertexWithTerm.vertex);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void nothing) {
            // Toast graph creation
            Toast.makeText(application, "Terms saved successfully", Toast.LENGTH_SHORT).show();
        }
    }

    public class VertexWithTerm {

        private Vertex vertex;
        private Term term;

        public VertexWithTerm(@NonNull final Term term) {
            vertex = new Vertex();
            vertex.setTerm_ref(term.getId());
            this.term = term;
        }

        public void setVertexParams(final String vertexContext, final int userRank) {
            this.vertex.setVertex_context(vertexContext);
            this.vertex.setUser_rank(userRank);
        }
    }
}














