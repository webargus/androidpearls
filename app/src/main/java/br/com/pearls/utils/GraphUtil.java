package br.com.pearls.utils;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
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

    private DomainRepository domainRepository;
    private GraphRepository graphRepository;
    private VertexRepository vertexRepository;
    private TermRepository termRepository;
    private Graph graph;
    private OnGraphCreated graphCreated;

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
    }

    public void createGraph(@NonNull final long domain_ref, @NonNull final List<GraphVertex> vertices) {
        graph = new Graph();
        graph.setDomain_ref(domain_ref);
        /* Lesson for a newbie:
            we MUST pass a brand new deep copy of the vertices argument to the async task
            whenever calling this method multiple times in a row, otherwise the async task
            will see only the last List sent and we'll end up inserting or
            updating the same List over and over again.
            As far as our understanding goes, all the assignment
                List<GraphVertex> vv = vertices;
            does is just tagging a shallow copy of vertices to vv; hence, when vertices changes,
            vv changes too!
            Another potential issue that may arise here concerns what would happen if we flood
            this method with too many vertices at once; Since each method call creates a new
            asynchronous task, we may end up too many threads running simultaneously, which
            may overcome some thread limit, as to how many background threads we're allowed
            to run at the same time in parallel. We shall test it when importing very long
            lists into the db, though.
         */
        List<GraphVertex> vv = new ArrayList<>();
        // Log.v(TAG, "******************1st dump");        // debug
        for(GraphVertex v: vertices) {
            // v.debugDump();                               // debug
            vv.add(new GraphVertex(v));
        }
        new GraphAsyncTask(this).execute(vv);
    }

    private static class GraphAsyncTask extends AsyncTask<List<GraphVertex>, Void, Boolean> {

        WeakReference<GraphUtil> weakReference;
        GraphAsyncTask(GraphUtil util) {
            weakReference = new WeakReference<>(util);
        }

        @Override
        protected Boolean doInBackground(List<GraphVertex>... vertices) {
            // Check if domain_ref exists, return false if not
            if (weakReference.get().domainRepository.getDomain(weakReference.get().graph.getDomain_ref()).length == 0) {
                return false;
            }
            Vertex vertex;
            Term term;
            long graph_id = 0;
            // Log.v(TAG, "******************************* 2nd dump:");         // debug
            for(GraphVertex graphVertex : vertices[0]) {
                // graphVertex.debugDump();                                     // debug
                vertex = new Vertex();                  // vertex to insert or update
                term = new Term();                      // term to insert or update
                // set term fields according to user's GUI input
                term.setTerm(graphVertex.term);
                term.setTerm_ascii(RemoveDiacritics.removeDiacritics(graphVertex.term).toLowerCase());
                // lang_ref != 0 always, either in insert or edit mode
                term.setLang_ref(graphVertex.lang_ref);
                if(graphVertex.term_ref == 0) {    // insert term
                    term.setId(weakReference.get().termRepository.insert(term));
                    vertex.setTerm_ref(term.getId());
                } else {        // update term
                    term.setId(graphVertex.term_ref);
                    vertex.setTerm_ref(graphVertex.term_ref);
                    weakReference.get().termRepository.update(term);
                }
                // set vertex fields with user inputs
                vertex.setUser_rank(graphVertex.user_rank);
                vertex.setVertex_context(graphVertex.vertex_context);
                if(graphVertex.graph_ref == 0) {       // => we're inserting
                    if(graph_id == 0) {
                        // create graph once and reuse same graph_id for all new vertices
                        graph_id = weakReference.get().graphRepository.insert(weakReference.get().graph);
                    }
                    vertex.setGraph_ref(graph_id);
                    // Log.v(TAG, "inserting vertex");       // debug
                    vertex.setId(weakReference.get().vertexRepository.insert(vertex));
                } else {                                // => we're updating
                    vertex.setId(graphVertex.vertex_id);
                    vertex.setGraph_ref(graphVertex.graph_ref);
                    vertex.setTerm_ref(graphVertex.term_ref);
                    // Log.v(TAG, "updating vertex id " + vertex.getId());  // debug
                    weakReference.get().vertexRepository.update(vertex);
                }
//                vertex.debugDump();                       // debug
//                term.debugDump();                         // debug
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (weakReference.get().graphCreated != null) {
                weakReference.get().graphCreated.onGraphCreated(result);
            }
        }
    }
}














