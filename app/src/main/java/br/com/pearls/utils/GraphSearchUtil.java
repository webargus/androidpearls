package br.com.pearls.utils;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import br.com.pearls.DB.Domain;
import br.com.pearls.DB.GraphSearchRepository;
import br.com.pearls.DB.GraphSearchVerticesRepository;

public class GraphSearchUtil {

    public static final String TAG = GraphSearchUtil.class.getName();

    private static TreeMap<GraphSearchResult, List<SearchVertex>> treeMap;
    private static GraphSearchRepository graphSearchRepository;
    private static GraphSearchVerticesRepository graphSearchVerticesRepository;
    private static SearchUtilIFace searchUtilIFace;
    private static String searchTerm;

    public interface SearchUtilIFace {
        void fetchGraphSearchResults(TreeMap<GraphSearchResult, List<SearchVertex>> results);
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
        searchTerm = RemoveDiacritics.removeDiacritics(term);
        new GraphSearchAsyncTask().execute(searchTerm);
    }

    /*  Comparator class for TreeMap -> rank results according to score
    *   calculated in getScore method
    * */
    private static class CompareGraphs implements Comparator<GraphSearchResult> {

        private Domain domain;

        CompareGraphs(Domain domain) {
            this.domain = domain;
        }

        @Override
        public int compare(GraphSearchResult o1, GraphSearchResult o2) {
            int score = getScore(o2) - getScore(o1);
            if(score == 0) {
                return -1;
            }
            return score;
        }

        private int getScore(GraphSearchResult obj) {
            int score = 0;
            if (domain != null) {
                if(obj.area_ref == domain.getArea_ref()) {
                    score += 3;
                }
                if(obj.domain_ref == domain.getId()) {
                    score += 4;
                }
            }
            if(obj.term_ascii == searchTerm) {
                score += 2;
            }
            return score;
        }
    }

    /* IMPORTANT! GraphSearchDao must return results ordered by graph_ref
    *  reason: we skip graphs in a row if they come repeated
    *  Useless to have DISTINCT added to SELECT statement (see GraphSearchDao.java)
    *  because the db query has to return the matched terms in order for us to be able
    *  to sort and rank search results properly
    * */
    private static void processGraphResults(List<GraphSearchResult> results) {
        // get current area and domain
        Domain domain = searchUtilIFace.getDomain();
        treeMap = new TreeMap<>(new CompareGraphs(domain));
        VertexAsyncSearch.Result result;
        long prevGraphId = 0;
        for(int ix = 0; ix < results.size(); ix ++) {
            result = new VertexAsyncSearch.Result();
            result.graph = results.get(ix);
            result.lastGraphId = prevGraphId;
            prevGraphId = result.graph.graph_ref;
            if(ix == results.size() - 1) {
                result.lastResult = true;
            }
            new VertexAsyncSearch().execute(result);
        }
    }

    private static void addVerticesToMap(VertexAsyncSearch.Result result) {
        if(result.vertices != null) {
            treeMap.put(result.graph, result.vertices);
        }
        if(result.lastResult) {
            Log.v(TAG, "sending treeMap to search fragment; size: " + treeMap.size());
            searchUtilIFace.fetchGraphSearchResults(treeMap);
        }
    }

    /* async classes to fetch search results from db
       efforts made to merge both classes into one failed because TreeMap is not
       synchronized and results return messed up unless we sort them them in main thread
     */
    private static class VertexAsyncSearch
            extends AsyncTask<VertexAsyncSearch.Result, Void, VertexAsyncSearch.Result> {

        private static class Result {
            GraphSearchResult graph;
            List<SearchVertex> vertices;
            /*  flag that tells addVerticesToMap that this is the last result
                to be processed in the GraphSearchResult list; addVerticesToMap
                needs this information when deciding to send TreeMap back to
                search fragment through the searchUtilIFace interface.
            */
            boolean lastResult;
            long lastGraphId;

            Result() {
                lastResult = false;
                lastGraphId = 0;
            }
        }

        @Override
        protected Result doInBackground(Result... results) {
            Result result = results[0];
            if(result.graph.graph_ref == result.lastGraphId) {
                result.vertices = null;
            } else {
                result.vertices = graphSearchVerticesRepository.fetchVerticesForGraph(result.graph.graph_ref);
            }
            return result;
        }

        @Override
        protected void onPostExecute(Result result) {
            addVerticesToMap(result);
        }
    }

    private static class GraphSearchAsyncTask
            extends AsyncTask<String, Void, List<GraphSearchResult>> {

        @Override
        protected List<GraphSearchResult> doInBackground(String... strings) {
            return  graphSearchRepository.loadGraphsForTermSearch(strings[0]);
        }

        @Override
        protected void onPostExecute(List<GraphSearchResult> graphSearchResults) {
            Log.v(TAG, "got results, sending them to process");
            processGraphResults(graphSearchResults);
        }
    }
}










