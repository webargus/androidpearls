package br.com.pearls.utils;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import br.com.pearls.DB.Domain;
import br.com.pearls.DB.GraphSearchRepository;
import br.com.pearls.DB.GraphSearchVerticesRepository;

public class GraphSearchUtil {

    public static final String TAG = GraphSearchUtil.class.getName();

    private static TreeMap<GraphSearchRated, List<SearchVertex>> treeMap;
    private static GraphSearchRepository graphSearchRepository;
    private static GraphSearchVerticesRepository graphSearchVerticesRepository;
    private static SearchUtilIFace searchUtilIFace;
    private static String searchTerm;
    private static List<GraphSearchRated> graphs;
    private static int graphPointer;
    private static long prevGraphRef;
    private static Domain mDomain;

    public interface SearchUtilIFace {
        void fetchGraphSearchResults(TreeMap<GraphSearchRated, List<SearchVertex>> results);
        Domain getDomain();
    }

    public GraphSearchUtil(Application application, Fragment fragment) {
        graphSearchRepository = new GraphSearchRepository(application);
        graphSearchVerticesRepository = new GraphSearchVerticesRepository(application);
        try {
            searchUtilIFace = (SearchUtilIFace) fragment;
        } catch (ClassCastException e) {
            throw new NullPointerException("You must implement the SearchUtilIFace interface...");
        }
    }

    public void asyncSearchForTerm(@NonNull final String term) {
        Log.v(TAG, "searching term: " + term);
        searchTerm = RemoveDiacritics.removeDiacritics(term).toLowerCase();
        mDomain = searchUtilIFace.getDomain();
        new GraphSearchAsyncTask().execute(searchTerm);
    }

    /*  Comparator class for TreeMap -> rank results according to score
    *   calculated in getScore method
    * */
    private static class CompareGraphs implements Comparator<GraphSearchRated> {

        @Override
        public int compare(GraphSearchRated o1, GraphSearchRated o2) {
            int rank = o2.getScore() - o1.getScore();
            if(rank == 0) {
                return -1;
            }
            return rank;
        }
    }

    /* IMPORTANT! GraphSearchDao must return results ordered by graph_ref
    *  reason: we skip graphs in a row if they come repeated
    *  Useless to have DISTINCT added to SELECT statement (see GraphSearchDao.java)
    *  because the db query has to return the matched terms in order for us to be able
    *  to sort and rank search results properly, based on term matches
    * */
    private static void processGraphResults(List<GraphSearchRated> results) {
        graphs = results;
        if (graphs != null) {
            if (graphs.size() > 0) {
                treeMap = new TreeMap<>(new CompareGraphs());
                graphPointer = 0;
                prevGraphRef = 0;
                fetchVertices();
                return;
            }
        }
        searchUtilIFace.fetchGraphSearchResults(null);
    }

    private static void fetchVertices() {
        new VertexAsyncSearch().execute(graphs.get(graphPointer).graph.graph_ref);
    }

    private static void addVerticesToMap(List<SearchVertex> vertices) {
        if(graphPointer >= graphs.size()) {
            searchUtilIFace.fetchGraphSearchResults(treeMap);
            return;
        }
        GraphSearchRated result = graphs.get(graphPointer);
        if(result.graph.graph_ref != prevGraphRef) {
            prevGraphRef = result.graph.graph_ref;
            treeMap.put(result, vertices);
        }
        graphPointer++;
        if(graphPointer >= graphs.size()) {
            searchUtilIFace.fetchGraphSearchResults(treeMap);
            return;
        }
        fetchVertices();
    }

    /* async classes to fetch search results from db
       efforts made to merge both classes into one failed because TreeMap is not
       synchronized and results return messed up unless we sort them them in main thread
     */
    private static class VertexAsyncSearch
            extends AsyncTask<Long, Void, List<SearchVertex>> {

        @Override
        protected List<SearchVertex> doInBackground(Long... graph_refs) {
            return graphSearchVerticesRepository.fetchVerticesForGraph(graph_refs[0]);
        }

        @Override
        protected void onPostExecute(List<SearchVertex> vertices) {
            addVerticesToMap(vertices);
        }
    }

    private static class GraphSearchAsyncTask
            extends AsyncTask<String, Void, List<GraphSearchRated>> {

        @Override
        protected List<GraphSearchRated> doInBackground(String... strings) {
            List<GraphSearchResult> results = graphSearchRepository.loadGraphsForTermSearch(strings[0]);
            List<GraphSearchRated> ratedList = new ArrayList<>();
            for(GraphSearchResult result: results) {
                GraphSearchRated rated = new GraphSearchRated(result);
                String term = searchTerm;
                try {
                    term = term.substring(1);          // strip % from begin and end
                    term = term.substring(0, term.length() - 1);
                } catch (IndexOutOfBoundsException e) {
                    Log.v(TAG, "----------------->>> " +  e.getMessage());
                }
                rated.setScore(mDomain, term);
                ratedList.add(rated);
            }
            return ratedList;
        }

        @Override
        protected void onPostExecute(List<GraphSearchRated> graphSearchResults) {
            processGraphResults(graphSearchResults);
        }
    }
}










